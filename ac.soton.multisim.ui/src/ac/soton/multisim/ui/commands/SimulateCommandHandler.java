/**
 * Copyright (c) 2014 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ac.soton.multisim.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.services.ISourceProviderService;

import ac.soton.multisim.ComponentDiagram;
import ac.soton.multisim.diagram.part.ValidateAction;
import ac.soton.multisim.master.Master;
import ac.soton.multisim.ui.MultisimUIActivator;
import ac.soton.multisim.ui.dialogs.SimulationSettingsDialog;
import ac.soton.multisim.ui.providers.SimulationStateSourceProvider;

/**
 * Simulation command handler.
 * 
 * @author vitaly
 *
 */
public class SimulateCommandHandler extends AbstractHandler {

	private static final String JOB_NAME = "Multi-simulation";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (validate(editor) == false)
			return null;
		
		final ComponentDiagram diagram = (ComponentDiagram) ((DiagramEditor) editor).getDiagram().getElement();
		
		// input dialog for entering simulation parameters
		SimulationSettingsDialog simulationInputDialog = new SimulationSettingsDialog(Display.getCurrent().getActiveShell(), diagram);
		if (simulationInputDialog.open() != InputDialog.OK)
			return null;
		
		// get output path
		IResource res = (IResource) editor.getEditorInput().getAdapter(IResource.class);
		final String outputPath = res.getLocation().removeLastSegments(1).append("results.csv").toOSString();
		
		// source provider service
	    ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil
	        .getActiveWorkbenchWindow(event).getService(ISourceProviderService.class);
	    final SimulationStateSourceProvider simulationStateService = (SimulationStateSourceProvider) sourceProviderService
	        .getSourceProvider(SimulationStateSourceProvider.STATE_SIMULATING);
	    
	    // simulation job
		final Job job = new Job(JOB_NAME) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return Master.simulate(diagram, outputPath, monitor);
			}
			@Override
			public boolean belongsTo(Object family) {
				return JOB_NAME.equals(getName());
			}
		};
		job.setUser(true);												// user UI job
		job.setPriority(Job.LONG);										// long-running job scheduling (lower priority than interactive and short, but higher than build)
		job.setProperty(IProgressConstants.KEEPONE_PROPERTY, true);		// keep only one job in progress monitor
		job.setProperty(IProgressConstants.ICON_PROPERTY, 
				MultisimUIActivator.getDefault().getImageRegistry().getDescriptor(MultisimUIActivator.IMAGE_MULTISIM));	// job icon
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						// update UI state on finish
						simulationStateService.setSimulating(false);
					}
				});
			}
		});
		job.setProperty(IProgressConstants.ACTION_PROPERTY, new Action() {
			Job jb = job;
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						// results dialog on finish
						MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Simulation Results", jb.getResult().getMessage());
					}
				});
			}
		});
		job.schedule();
		
		// update UI state to simulating
	    simulationStateService.setSimulating(true);
	    
		return null;
	}
	
	/**
	 * Validates the editor content.
	 * Returns true if validation has found no problems, otherwise false.
	 * 
	 * @param diagramEditor
	 */
	public static boolean validate(IEditorPart diagramEditor) {
		Action validateAction = new ValidateAction(diagramEditor.getSite().getPage());
		validateAction.run();

		// show error markers if added
		IResource resource = (IResource) diagramEditor.getEditorInput().getAdapter(IResource.class);
		if (resource == null)
			return false;
		IMarker[] problems = null;
		int depth = IResource.DEPTH_INFINITE;
		try {
			problems = resource.findMarkers(IMarker.PROBLEM, true, depth);
			if (problems.length > 0) {
				new MessageDialog(diagramEditor.getSite().getShell(),
						"Validation", null,
						"Fix the validation problems first.",
						MessageDialog.ERROR, new String[] { "OK" }, 0).open();
				return false;
			}
		} catch (CoreException e) {
			return false;
		}
		return true;
	}

}
