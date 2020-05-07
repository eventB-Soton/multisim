/**
 * Copyright (c) 2014 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ac.soton.multisim.master;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import ac.soton.multisim.Component;
import ac.soton.multisim.ComponentDiagram;
import ac.soton.multisim.EventBComponent;
import ac.soton.multisim.exception.ModelException;
import ac.soton.multisim.exception.SimulationException;
import ac.soton.multisim.util.SimulationStatus;
import ac.soton.multisim.util.SimulationUtil;

/**
 * @author vitaly
 *
 */
public class Master {

	/**
	 * Simulate the diagram.
	 * 
	 * @param diagram
	 * @param outputPath
	 *            file path of the file for recording output values in a CSV
	 *            format, or null if no recording required
	 * @param monitor
	 *            progress monitor
	 * @return
	 * @throws IOException
	 * @throws ModelException
	 * @throws SimulationException
	 */
	public static IStatus simulate(final ComponentDiagram diagram, final String outputPath, IProgressMonitor monitor) {
		IStatus status = SimulationStatus.OK_STATUS;
		long tMasterStart = System.currentTimeMillis();

		// data
		int tStart = diagram.getStartTime();
		int tStop = diagram.getStopTime();

		List<EventBComponent> compB = new ArrayList<EventBComponent>();
		List<Component> compNotB = new ArrayList<Component>();

		for (Component c : diagram.getComponents()) {
			if (c instanceof EventBComponent) {
				compB.add((EventBComponent) c);
			} else {
				compNotB.add(c);
			}
		}

		BufferedWriter resultWriter = null;

		try {

			// recording
			if (diagram.isRecordOutputs() && outputPath != null)
				resultWriter = SimulationUtil.createOutputWriter(new File(outputPath));

			// instantiation & initialisation
			for (Component c : diagram.getComponents()) {
				c.instantiate();
				c.initialise(tStart, tStop);
			}

			// initial IO
			for (Component c : compB)
				c.writeOutputs();
			for (Component c : compNotB) {
				c.readInputs();
				c.writeOutputs();
			}
			for (Component c : compB)
				c.readInputs();

			// record header and initial values
			if (resultWriter != null) {
				SimulationUtil.writeColumns(diagram, resultWriter);
				SimulationUtil.writeOutput(diagram, tStart, resultWriter);
			}

			// first eval
			Map<Component, Integer> updateList = new HashMap<Component, Integer>(compB.size() + compNotB.size());
			for (EventBComponent c : compB)
				updateList.put(c, tStart + c.getStepSize());
			for (Component c : compNotB)
				updateList.put(c, tStart);

			List<EventBComponent> evalList = new ArrayList<EventBComponent>(compB.size());

			// simulation loop
			for (int tCurrent = tStart + 1; tCurrent <= tStop; ++tCurrent) {
				if (monitor.isCanceled()) {
					status = SimulationStatus.CANCEL_STATUS;
					break;
				}

				// update eval list
				for (EventBComponent c : compB)
					if (updateList.get(c) == tCurrent)
						evalList.add(c);

				// evaluate
				if (!evalList.isEmpty()) {
					// XXX: in this semantics tCurrent and tStep do not matter
					// for
					// the doStep(...) call on Event-B components
					for (EventBComponent c : evalList) {
						c.doStep(tCurrent, 0);
						updateList.put(c, tCurrent + c.getStepSize()); // update
																		// the
																		// next
																		// evaluation
																		// time
																		// for
																		// the
																		// evaluated
																		// Event-B
																		// component
					}
					for (Component c : compNotB) {
						int tLast = updateList.get(c);
						c.doStep(tLast, tCurrent - tLast);
						updateList.put(c, tCurrent); // update non-B component's
														// last
														// evaluation times
					}

					// IO
					for (Component c : evalList)
						c.writeOutputs();
					for (Component c : compNotB) {
						c.readInputs();
						c.writeOutputs();
					}
					for (Component c : evalList)
						c.readInputs();

					// record if any Event-B is evaluated (possible changes
					// occurred)
					// XXX: output may be better recorded at fixed intervals
					// irrespective of evaluation
					if (resultWriter != null)
						SimulationUtil.writeOutput(diagram, tCurrent, resultWriter);

					evalList.clear();
				}
			}

		} catch (IOException | SimulationException | ModelException e) {
			status = SimulationStatus.createErrorStatus("Simulation terminated: " + e.getMessage(), e);
		}

		// termination
		for (Component c : diagram.getComponents())
			c.terminate();

		long tMasterEnd = System.currentTimeMillis();

		// stop recording
		boolean saved = false;
		if (resultWriter != null) {
			try {
				resultWriter.close();
				saved = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (status.getSeverity() == IStatus.OK) {
			status = SimulationStatus.createOKStatus("Finished in " + (tMasterEnd - tMasterStart) + "ms"
					+ (saved ? "\nOutput saved to:\n\t" + outputPath + "\n" : "") + diagram.createStatusReport());
		}

		return status;
	}
}
