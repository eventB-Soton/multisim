/**
 * Copyright (c) 2013 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ac.soton.multisim.diagram.sheet.custom.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eventb.emf.core.EventBNamedCommentedElement;
import org.eventb.emf.core.machine.Parameter;
import org.eventb.emf.core.machine.Variable;

import ac.soton.multisim.EventBPort;
import ac.soton.multisim.MultisimFactory;
import ac.soton.multisim.VariableCausality;
import ac.soton.multisim.VariableType;

/**
 * New EventB port creation dialog.
 * Allows to specify the name and type of the new port,
 * as well as a parameter for provided read/write event.
 * Also provides the basic name validation based on the source
 * component: disallows empty name or the existing port name.
 * 
 * @author vitaly
 *
 */
public class EventBPortDialog extends SelectionDialog {

	private Text nameText;
	private Combo typeCombo;
	private Combo elementCombo;
	private Spinner precisionSpinner;
	private Map<String, EventBNamedCommentedElement> elementMap;
	private DecoratedInputValidator elementValidator;
	private boolean elementValid;
	private EventBPort port;
	private VariableCausality causality;
	private Set<EventBNamedCommentedElement> filterElements;
	
	/**
	 * Creates Event-B port dialog.
	 * 
	 * @param shell
	 * @param causality
	 * @param elements
	 * @param list 
	 */
	public EventBPortDialog(Shell shell, VariableCausality causality,
			Collection<? extends EventBNamedCommentedElement> elements,
			List<EventBPort> filterPorts) {
		super(shell);
		
		this.setTitle("New " + causality.getName() + " Port");
		this.causality = causality;
		
		elementMap = new HashMap<String, EventBNamedCommentedElement>(elements.size());
		for (EventBNamedCommentedElement el : elements) {
			elementMap.put(el.getName(), el);
		}
		
		if (filterPorts != null) {
			filterElements = new HashSet<EventBNamedCommentedElement>(filterPorts.size());
			for (EventBPort port : filterPorts) {
				EventBNamedCommentedElement element = port.getCausality() == VariableCausality.INPUT ? port.getParameter() : port.getVariable();
				if (element != null)
					filterElements.add(element);
			}
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout());
		
		Group plate = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		plate.setLayout(layout);
		plate.setText("Event-B port attributes");
		
		// name text
		Label label = new Label(plate, SWT.NONE);
		label.setText("Name:");
		nameText = new Text(plate, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		nameText.setToolTipText("Port name (empty = Event-B element name)");
		
		// event parameter combo
		String comboLabel = causality == VariableCausality.INPUT ? "Parameter:" : "Variable:";
		if (elementMap != null) {
			elementCombo = createCombo(plate, comboLabel, elementMap, new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					validateLinkedElement();
				}
			});
		}
		elementCombo.setToolTipText("Linked Event-B element");
		
		// type label and combo
		label = new Label(plate, SWT.NONE);
		label.setText("Type:");
		typeCombo = new Combo(plate, SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		typeCombo.setToolTipText("Type of the signal");
		typeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validateType();
			}
		});
		{
			String[] items = new String[VariableType.VALUES.size()];
			for (int i = 0; i < VariableType.VALUES.size(); i++) {
				items[i] = VariableType.VALUES.get(i).getName();
			}
			typeCombo.setItems(items);
			typeCombo.select(VariableType.BOOLEAN.getValue());
		}
		
		// int to real precision spinner/label
		label = new Label(plate, SWT.NONE);
		label.setText("Precision:");
		precisionSpinner = new Spinner(plate, SWT.BORDER);
		precisionSpinner.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		precisionSpinner.setToolTipText("Order of conversion from FMI Real type to Event-B Integer and back");
		precisionSpinner.setMinimum(0);
		precisionSpinner.setMaximum(10);
		precisionSpinner.setSelection(0);
		precisionSpinner.setIncrement(1);
		
		// validators & initial validation
		createValidators();
		validateType();
		
		plate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return composite;
	}

	/**
	 * Creates input validators.
	 */
	private void createValidators() {
		elementValidator = new DecoratedInputValidator(
				DecoratedInputValidator.createDecorator(
						elementCombo,
						FieldDecorationRegistry.DEC_ERROR, 
						false)) {
			@Override
			public String isValidInput(String elementName) {
				if (elementName == null || elementName.isEmpty())
					return "Selection cannot be empty";
				
				// check if a port for this element already exists
				if (filterElements != null) {
					EventBNamedCommentedElement element = elementMap.get(elementName);
					if (element != null && filterElements.contains(element))
						return "Port for this element already exists";
				}
				return null;
			}
		};
	}

	/**
	 * Creates and returns a combo.
	 * 
	 * @param parent parent
	 * @param label label of the combo
	 * @param map map of elements to populate the combo
	 * @param listener combo selection listener
	 * @return
	 */
	private Combo createCombo(Composite parent, String label, Map<String, ? extends Object> map, SelectionListener listener) {
		Label variableLabel = new Label(parent, SWT.NONE);
		variableLabel.setText(label);
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		combo.addSelectionListener(listener);
		{
			String[] items = new String[map.size() + 1];
			int i = 0;
			items[i++] = ""; // empty item
			for (String name : map.keySet())
				items[i++] = name;
			Arrays.sort(items);
			combo.setItems(items);
		}
		return combo;
	}
	

	@Override
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);
		// only here the buttons are available, so that's where OK can be disabled for initial state
		update();
		return contents;
	}

	/**
	 * Validates type.
	 */
	protected void validateType() {
		precisionSpinner.setEnabled(VariableType.REAL.getName().equals(typeCombo.getText()));
	}

	/**
	 * Validates linked element.
	 */
	protected void validateLinkedElement() {
		if (elementValidator != null) {
			elementValid = elementValidator.isValid(elementCombo.getText()) == null;
		} else {
			elementValid = true;
		}
		update();
	}

	/**
	 * Updates the buttons based on validation.
	 */
	private void update() {
		Control button = getButton(IDialogConstants.OK_ID);
		if (button != null) {
			button.setEnabled(elementValid);
		}
	}

	@Override
	protected void okPressed() {
		String typeStr = typeCombo.getItem(typeCombo.getSelectionIndex());
		String elementStr = elementCombo.getItem(elementCombo.getSelectionIndex());
		EventBNamedCommentedElement element = elementMap.get(elementStr);
		String name = nameText.getText();
		if (name == null || name.trim().isEmpty())
			name = element.getName();
		
		port = MultisimFactory.eINSTANCE.createEventBPort();
		port.setName(name);
		port.setType(VariableType.getByName(typeStr));
		port.setCausality(causality);
		port.setIntToReal(precisionSpinner.getSelection());
		port.setComment(element.getComment());

		if (causality == VariableCausality.INPUT) {
			port.setParameter((Parameter) element);
		} else if (causality == VariableCausality.OUTPUT) {
			port.setVariable((Variable) element);
		}
		
		// either variable or port has to be present
		assert port.getVariable() != null || port.getParameter() != null;
		
		super.okPressed();
	}

	@Override
	public Object[] getResult() {
		return Collections.singleton(port).toArray();
	}


}
