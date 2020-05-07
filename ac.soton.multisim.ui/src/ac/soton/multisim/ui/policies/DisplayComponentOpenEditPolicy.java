/**
 * Copyright (c) 2014 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ac.soton.multisim.ui.policies;

import info.monitorenter.gui.chart.Chart2D;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.OpenEditPolicy;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import ac.soton.multisim.DisplayComponent;
import ac.soton.multisim.diagram.edit.parts.DisplayComponentEditPart;
import ac.soton.multisim.exception.ModelException;
import ac.soton.multisim.exception.SimulationException;
import ac.soton.multisim.ui.MultisimUIActivator;

/**
 * Open edit policy for Display edit part to show the chart on double-click.
 *  
 * @author vitaly
 *
 */
public final class DisplayComponentOpenEditPolicy extends OpenEditPolicy {

	@Override
	protected Command getOpenCommand(Request request) {
		final DisplayComponentEditPart part = (DisplayComponentEditPart) getHost();
		return new Command() {
			@Override
			public void execute() {
				final DisplayComponent component = (DisplayComponent) part.resolveSemanticElement();
				assert component != null;

				if (component.getChart() == null) {
					try {
						component.instantiate();
					} catch (SimulationException | ModelException e) {
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Display Error", "Failed to instantiate the Display. See error log for details.");
						MultisimUIActivator.getDefault().logError("Display instantiation failed", e);
						return;
					}
				}
				
				final Chart2D chart = component.getChart();

				// display the chart if not visible yet
				if (!chart.isVisible()) {
					JPanel container = new JPanel();
					container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
					container.setBackground(java.awt.Color.WHITE);
					container.setLayout(new java.awt.BorderLayout());
					container.add(chart, java.awt.BorderLayout.CENTER);

					final JFrame frame = new JFrame("Display");
					frame.getContentPane().add(container);
					frame.setSize(300, 300);
					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							frame.dispose();
							chart.setVisible(false);
						}
					});
					chart.setVisible(true);
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
				}
			}
		};
	}
}