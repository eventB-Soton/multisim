/**
 * 
 */
package ac.soton.multisim.master;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;
import org.ptolemy.fmi.FMIModelDescription;
import org.ptolemy.fmi.FMUFile;

import ac.soton.multisim.FMUComponent;
import ac.soton.multisim.FMUPort;
import ac.soton.multisim.MultisimFactory;
import ac.soton.multisim.VariableCausality;
import ac.soton.multisim.VariableType;
import ac.soton.multisim.common.AbstractEventBProjectTest;
import ac.soton.multisim.exception.ModelException;
import ac.soton.multisim.exception.SimulationException;

/**
 * @author vitaly
 *
 */
public class FMUComponentTest extends AbstractEventBProjectTest {

	private FMUComponent component;

	@Before
	public void setUp() {
		super.setUp();
		
		String fmuFilename = "ct.fmu";
//		InputStream is = this.getClass().getResourceAsStream(fmuFilename);
//		assertNotNull(is);
//
//		IProject project = getEventBProject().getRodinProject().getProject();
//		String fmuFileName = project.getFile(fmuFilename).getLocation().toOSString();
//		try {
//			File fmuFile = new File(fmuFileName);
//			Files.copy(is, fmuFile.toPath());
//			System.err.println(fmuFile.getAbsolutePath());
//			is.close();
//		} catch (IOException e) {
//			fail("Cannot copy FMU to the project");
//		}
//
//		FMIModelDescription description = null;
//		try {
//			description = FMUFile.parseFMUFile(fmuFileName);
//			assertNotNull(description);
//		} catch (IOException e) {
//			fail("Cannot parse the FMU file: " + fmuFileName);
//		}
//
//		assertEquals("ct", description.modelName);

		InputStream is = this.getClass().getResourceAsStream("/" + fmuFilename);
		assertNotNull(is);

		IProject project = getEventBProject().getRodinProject().getProject();
		String fmuFileName = project.getFile(fmuFilename).getLocation().toOSString();
		File fmuFile = new File(fmuFileName);
		try {
			Files.copy(is, fmuFile.toPath());
			is.close();
		} catch (IOException e) {
			fail("Failed to copy the FMU for testing");
		}

		component = MultisimFactory.eINSTANCE.createFMUComponent();
		component.setPath(fmuFile.getAbsolutePath());
		FMUPort output = MultisimFactory.eINSTANCE.createFMUPort();
		output.setType(VariableType.INTEGER);
		output.setCausality(VariableCausality.OUTPUT);
		output.setName("y");
		component.getOutputs().add(output);
	}

//	@Test
//	public void testFMUSimulation() {
//		try {
//			component.instantiate();
//		} catch (SimulationException | ModelException e) {
//			fail("Failed to instantiate FMU: "+e.getMessage());
//		}
//
//		int tStart = 0;
//		int tStop = 12;
//		try {
//			component.initialise(tStart, tStop);
//		} catch (SimulationException e) {
//			fail("Failed to initialise FMU: "+e.getMessage());
//		}
//
//		try {
//			for (int t = tStart; t < tStop; t += 1000)
//				component.doStep(t, 1000);
//		} catch (ModelException | SimulationException e) {
//			fail("doStep() failed: "+e.getMessage());
//		}
//		
//		component.writeOutputs();
//		assertEquals(12, component.getOutputs().get(0).getValue());
//	}

	@Override
	protected String getProjectName() {
		return "ftest";
	}
}
