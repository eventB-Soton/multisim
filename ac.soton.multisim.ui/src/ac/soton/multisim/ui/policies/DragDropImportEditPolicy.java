/**
 * Copyright (c) 2014 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ac.soton.multisim.ui.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.diagram.core.edithelpers.CreateElementRequestAdapter;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SemanticCreateCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.DiagramDragDropEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest.ViewAndElementDescriptor;
import org.eclipse.gmf.runtime.diagram.ui.requests.DropObjectsRequest;
import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eventb.core.basis.MachineRoot;

import ac.soton.multisim.Component;
import ac.soton.multisim.diagram.part.MultisimDiagramEditorUtil;
import ac.soton.multisim.diagram.providers.MultisimElementTypes;
import ac.soton.multisim.util.FMUResource;

/**
 * Drag & drop import edit policy for component diagram.
 * Accepts the drop of:
 * - existing components
 * - machine root
 * - FMU resource wrapper (FMUResource)
 * 
 * @author vitaly
 *
 */
public class DragDropImportEditPolicy extends DiagramDragDropEditPolicy {

	@Override
	public Command getDropObjectsCommand(DropObjectsRequest dropRequest) {
		
		// disallow multiple selection
		if (dropRequest.getObjects().size() != 1)
			return null;
		
		Object dropObject = dropRequest.getObjects().get(0);
		IElementType type = null;
		
		// filter allowed object types
		if (dropObject instanceof Component) {
			type = ElementTypeRegistry.getInstance().getElementType(dropObject);
		} else if (dropObject instanceof MachineRoot) {
			type = MultisimElementTypes.EventBComponent_2006;
		} else if (dropObject instanceof FMUResource) {
			type = MultisimElementTypes.FMUComponent_2005;
		} else {
			return null;
		}
			
		TransactionalEditingDomain editingDomain = ((GraphicalEditPart) getHost()).getEditingDomain();
		EObject container = ((View) getHost().getModel()).getElement();
		
		// requests and adapters
		CreateElementRequest createElementRequest = new CreateElementRequest(editingDomain, container, type);
		CreateElementRequestAdapter adapter = new CreateElementRequestAdapter(createElementRequest);
		ViewAndElementDescriptor descriptor = new ViewAndElementDescriptor(adapter, Node.class, ((IHintedType) type).getSemanticHint(), 
				((GraphicalEditPart) getHost()).getDiagramPreferencesHint());

		// element command (customised to return an existing element)
		Command createElementCommand = new ICommandProxy(new ComponentCreateCommand(createElementRequest, dropObject));

		// semantic command
		SemanticCreateCommand semanticCommand = new SemanticCreateCommand(adapter, createElementCommand);

		// view command
		CreateCommand viewCommand = new CreateCommand(editingDomain, descriptor, (View) (getHost().getModel())) {

			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				CommandResult result = super.doExecuteWithResult(monitor, info);
				
				// if success, add component to palette
				if (result.getStatus().isOK()) {
					Object value = getCommandResult().getReturnValue();
					if (value instanceof ViewAndElementDescriptor) {
						Object comp = ((ViewAndElementDescriptor) value).getElementAdapter().getAdapter(Component.class);
						if (comp instanceof Component)
							MultisimDiagramEditorUtil.addPaletteComponent(getHost().getViewer().getEditDomain(), (Component) comp);
					}
				}
				return result;
			}};

		// relocate command
		Point dropLocation = dropRequest.getLocation().getCopy();
		getHostFigure().translateToRelative(dropLocation);
		AdjustSizeAndPortsCommand relocateCommand = new AdjustSizeAndPortsCommand(editingDomain, "Set location", descriptor, dropLocation);

		// compound command
		CompositeCommand cc = new CompositeCommand(semanticCommand.getLabel());
		cc.compose(semanticCommand);
		cc.compose(viewCommand);
		cc.compose(relocateCommand);
		return new ICommandProxy(cc);
	}
}