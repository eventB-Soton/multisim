/**
 * 
 */
package ac.soton.multisim.master;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IMachineRoot;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

import ac.soton.multisim.common.AbstractEventBProjectTest;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.IEvalResult;
import de.prob.statespace.Trace;

/**
 * @author vitaly
 *
 */
public class EventBComponentTest extends AbstractEventBProjectTest {

	/**
	 * Tests if ProB animation works in the unit test.
	 */
	@Test
	public void testEventBAnimation() {
		// create machine file
		IMachineRoot machine = null;
		try {
			machine = createSampleMachine(getEventBProject(), "m0");
			assertNotNull(machine);
			saveOpenable(machine.getOpenable());
			assertTrue(machine.getResource().exists());
		} catch (RodinDBException e1) {
			fail("Cannot create a test Event-B machine");
		}

		// build workspace
		try {
			workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		} catch (CoreException e) {
			fail("Failed to build the test workspace");
		}

		// animate machine
		Trace trace = getAnimationTrace(machine);
		assertNotNull(trace);
		
		trace = trace.anyEvent(null);
		IEvalResult evalResult = trace.getCurrentState().eval("var");
		assertTrue(evalResult instanceof EvalResult);
		
		String varValue = ((EvalResult) evalResult).getValue();
		assertEquals("TRUE", varValue);
		
		trace = trace.anyEvent(null);
		evalResult = trace.getCurrentState().eval("var");
		assertTrue(evalResult instanceof EvalResult);
		
		varValue = ((EvalResult) evalResult).getValue();
		assertEquals("FALSE", varValue);
	}

	/**
	 * Creates a sample Event-B machine.
	 * The machine contains a set of variables and events to be able
	 * to run a simple animation via e.g. ProB.
	 * 
	 * @param eventBProject Event-B project to contain a created machine
	 * @param machineName name of the created Event-B machine
	 * @return sample machine
	 * @throws RodinDBException if creating Event-B machine failed
	 */
	protected IMachineRoot createSampleMachine(IEventBProject eventBProject, String machineName) throws RodinDBException {
		IMachineRoot machine = createMachine(eventBProject, machineName);
		
		createVariable(machine, "var");
		createInvariant(machine, "inv1", "var " + MEMBER_OF + " BOOL", false);
		
		IEvent event = createEvent(machine, IEvent.INITIALISATION);
		createAction(event, "act1", "var " + ASSIGN + " TRUE");
		
		event = createEvent(machine, "evt1");
		createAction(event, "act2", "var " + ASSIGN + " FALSE");
		
		return machine;
	}

	@Override
	protected String getProjectName() {
		return "btest";
	}
}
