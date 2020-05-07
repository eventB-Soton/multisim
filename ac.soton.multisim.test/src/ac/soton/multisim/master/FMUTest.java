package ac.soton.multisim.master;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.eclipse.core.resources.IProject;
import org.junit.Test;
import org.ptolemy.fmi.FMIModelDescription;
import org.ptolemy.fmi.FMUFile;

import ac.soton.multisim.common.AbstractEventBProjectTest;

/**
 * @author vitaly
 *
 */
public class FMUTest extends AbstractEventBProjectTest {

	/**
	 * Tests if the FMU loading works in the unit test.
	 */
	@Test
	public void testFMULoad() {
		InputStream is = this.getClass().getResourceAsStream("/ct.fmu");
		assertNotNull(is);

		IProject project = getEventBProject().getRodinProject().getProject();
		String fmuFileName = project.getFile("ct.fmu").getLocation().toOSString();
		try {
			File fmuFile = new File(fmuFileName);
			Files.copy(is, fmuFile.toPath());
			is.close();
		} catch (IOException e) {
			fail("Cannot copy FMU to the project");
		}

		FMIModelDescription description = null;
		try {
			description = FMUFile.parseFMUFile(fmuFileName);
			assertNotNull(description);
		} catch (IOException e) {
			fail("Cannot parse the FMU file: " + fmuFileName);
		}

		assertEquals("ct", description.modelName);
	}

	@Override
	protected String getProjectName() {
		return "fmutest";
	}
}
