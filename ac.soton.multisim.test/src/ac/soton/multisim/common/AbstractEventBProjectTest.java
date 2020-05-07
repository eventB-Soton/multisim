/**
 * 
 */
package ac.soton.multisim.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eventb.core.IEventBProject;
import org.junit.After;
import org.junit.Before;
import org.rodinp.core.IRodinProject;

/**
 * Abstract test for code requiring an Event-B project in the workspace.
 * <p>
 * Project name must be provided for the project to be created.
 * <p>
 * Creates a workspace project prior test and clears it afterwards.
 * @author vitaly
 *
 */
public abstract class AbstractEventBProjectTest extends AbstractEventBTest {

	private IEventBProject eventBProject;
	
	protected abstract String getProjectName();
	
	/**
	 * Creates a temporary workspace project to contain the models used by the
	 * tests.
	 */
	@Before
	public void setUp() {
		try {
			super.setUp();
			eventBProject = createEventBProject(getProjectName());
			assertNotNull(getEventBProject());

			IRodinProject rodinProject = getEventBProject().getRodinProject();
			saveOpenable(rodinProject);
			assertTrue(rodinProject.exists());
		} catch (Exception e) {
			fail("Cannot create a test Rodin project");
		}
	}

	/**
	 * Deletes the test project for the resources used by tests.
	 */
	@After
	public void tearDown() {
		try {
			if (getEventBProject() != null && getEventBProject().getRodinProject().exists())
				getEventBProject().getRodinProject().getProject().delete(true, monitor);
		} catch (Exception e) {
			fail("Failed to delete the test project");
		}
		
		try {
			super.tearDown();
		} catch (Exception e) {
			fail("Failed to clean up after test");
		}
	}

	/**
	 * Returns Event-B project
	 * @return the Event-B project
	 */
	public IEventBProject getEventBProject() {
		return eventBProject;
	}
}
