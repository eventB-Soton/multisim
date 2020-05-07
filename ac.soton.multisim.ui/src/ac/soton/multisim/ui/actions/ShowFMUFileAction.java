/**
 * 
 */
package ac.soton.multisim.ui.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import ac.soton.multisim.FMUComponent;
import ac.soton.multisim.diagram.edit.parts.FMUComponentEditPart;

/**
 * Shows the FMU file of the FMU component in system explorer.
 * <p>
 * The logic of opening a file in system explorer depending on
 * the target platform is copied from the original code in 
 * <i>org.eclipse.ui.internal.ide.handlers.ShowInSystemExplorerHandler</i>
 * </p>
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p>
 * Contributors:<br>
 *     IBM Corporation - initial API and implementation
 * </p>
 * 
 * @author vitaly
 *
 */
public class ShowFMUFileAction implements IObjectActionDelegate {

	private static final String VARIABLE_RESOURCE = "${selected_resource_loc}"; //$NON-NLS-1$
	private static final String VARIABLE_RESOURCE_URI = "${selected_resource_uri}"; //$NON-NLS-1$
	private static final String VARIABLE_FOLDER = "${selected_resource_parent_loc}"; //$NON-NLS-1$

	private FMUComponentEditPart selectedElement;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// nothing to do here
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		selectedElement = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.getFirstElement() instanceof FMUComponentEditPart)
				selectedElement = (FMUComponentEditPart) structuredSelection.getFirstElement();
		}
	}
	
	@Override
	public void run(IAction action) {
		final FMUComponent component = (FMUComponent) selectedElement.resolveSemanticElement();
		
		String fmuFilePath = component.getPath();
		if (fmuFilePath == null) {
			
		}

//		String logMsgPrefix;
//		try {
//			logMsgPrefix = action.getCommand().getName() + ": "; //$NON-NLS-1$
//		} catch (NotDefinedException e) {
//			// will used id instead...
//			logMsgPrefix = event.getCommand().getId() + ": "; //$NON-NLS-1$
//		}

		try {
			File canonicalPath = new Path(fmuFilePath).toFile();
			if (canonicalPath == null) {
//				StatusManager
//						.getManager()
//						.handle(new Status(
//								IStatus.ERROR,
//								IDEWorkbenchPlugin.getDefault().getBundle()
//										.getSymbolicName(),
//								logMsgPrefix
//										+ IDEWorkbenchMessages.ShowInSystemExplorerHandler_notDetermineLocation),
//								StatusManager.SHOW | StatusManager.LOG);
				return;
			}
			String launchCmd = formShowInSytemExplorerCommand(canonicalPath);

			if ("".equals(launchCmd)) { //$NON-NLS-1$
//				StatusManager
//						.getManager()
//						.handle(new Status(
//								IStatus.ERROR,
//								IDEWorkbenchPlugin.getDefault().getBundle()
//										.getSymbolicName(),
//								logMsgPrefix
//										+ IDEWorkbenchMessages.ShowInSystemExplorerHandler_commandUnavailable),
//								StatusManager.SHOW | StatusManager.LOG);
				return;
			}

			File dir = canonicalPath.getParentFile();
			Process p;
			if (Util.isLinux() || Util.isMac()) {
				p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", launchCmd }, null, dir); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				p = Runtime.getRuntime().exec(launchCmd, null, dir);
			}
//			int retCode = p.waitFor();
//			if (retCode != 0 && !Util.isWindows()) {
//				log.log(new Status(IStatus.ERROR, IDEWorkbenchPlugin
//						.getDefault().getBundle().getSymbolicName(),
//						logMsgPrefix + "Execution of '" + launchCmd //$NON-NLS-1$
//								+ "' failed with return code: " + retCode)); //$NON-NLS-1$
//			}
		} catch (Exception e) {
//			log.log(new Status(IStatus.ERROR, IDEWorkbenchPlugin.getDefault()
//					.getBundle().getSymbolicName(), logMsgPrefix
//					+ "Unhandled failure.", e)); //$NON-NLS-1$
//			throw new ExecutionException("Show in Explorer command failed.", e); //$NON-NLS-1$
		}
		return;
	}
	
	/**
	 * Prepare command for launching system explorer to show a path
	 * 
	 * @param path
	 *            the path to show
	 * @return the command that shows the path
	 */
	private String formShowInSytemExplorerCommand(File path) throws IOException {
		String command = getShowInSystemExplorerCommand();
		
		command = Util.replaceAll(command, VARIABLE_RESOURCE, quotePath(path.getCanonicalPath()));
		command = Util.replaceAll(command, VARIABLE_RESOURCE_URI, path.getCanonicalFile().toURI().toString());
		File parent = path.getParentFile();
		if (parent != null) {
			command = Util.replaceAll(command, VARIABLE_FOLDER, quotePath(parent.getCanonicalPath()));
		}
		return command;
	}
	
	private String quotePath(String path) {
		if (Util.isLinux() || Util.isMac()) {
			// Quote for usage inside "", man sh, topic QUOTING:
			path = path.replaceAll("[\"$`]", "\\\\$0"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// Windows: Can't quote, since explorer.exe has a very special command line parsing strategy.
		return path;
	}

	
	/**
	 * The default command for launching the system explorer on this platform.
	 * <p>
	 * Copied from original <i>org.eclipse.ui.internal.ide.IDEPreferenceInitializer</i>
	 * </p>
	 * Copyright (c) 2004, 2014 IBM Corporation and others.
	 * All rights reserved. This program and the accompanying materials
	 * are made available under the terms of the Eclipse Public License v1.0
	 * which accompanies this distribution, and is available at
	 * http://www.eclipse.org/legal/epl-v10.html
	 * <p>
	 * Contributors:<br>
	 *     IBM Corporation - initial API and implementation<br>
	 *     Dina Sayed, dsayed@eg.ibm.com, IBM -  bug 269844
	 * </p>
	 * @return The default command which launches the system explorer on this system, or an empty
	 *         string if no default exists
	 * @see ShowInSystemExplorerHandler#getDefaultCommand()
	 */
	public static String getShowInSystemExplorerCommand() {
		// See https://bugs.eclipse.org/419940 why it is implemented in here and not in ShowInSystemExplorerHandler#getDefaultCommand()
		if (Util.isGtk()) {
			return "dbus-send --print-reply --dest=org.freedesktop.FileManager1 /org/freedesktop/FileManager1 org.freedesktop.FileManager1.ShowItems array:string:\"${selected_resource_uri}\" string:\"\""; //$NON-NLS-1$
		} else if (Util.isWindows()) {
			return "explorer /E,/select=${selected_resource_loc}"; //$NON-NLS-1$
		} else if (Util.isMac()) {
			return "open -R \"${selected_resource_loc}\""; //$NON-NLS-1$
		}

		// if all else fails, return empty default
		return ""; //$NON-NLS-1$
	}
}
