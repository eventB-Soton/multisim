package ac.soton.multisim.common;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConfigurationElement;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBProject;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.IOpenable;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import com.google.inject.Injector;

import de.prob.Main;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.Api;
import de.prob.scripting.EventBFactory;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob2.ui.eclipse.VersionController;

/**
 * Modified abstract class for Event-B unit tests by <i>hson</>.
 * <p>Removed test methods and left utility methods for creating Event-B elements.</p>
 * 
 * @author htson
 *         <p>
 *         Abstract class for Event-B tests.
 *         </p>
 */
public abstract class AbstractEventBTest {

	/**
	 * The null progress monitor.
	 */
	protected static final IProgressMonitor monitor = new NullProgressMonitor();

	/**
	 * The testing workspace.
	 */
	protected IWorkspace workspace = ResourcesPlugin.getWorkspace();

	/**
	 * The formula factory used to create formulae.
	 */
	protected static final FormulaFactory ff = FormulaFactory.getDefault();
	
	protected void setUp() throws Exception {
		assertNotNull(workspace);
		
		// ensure autobuilding is turned off
		IWorkspaceDescription wsDescription = workspace.getDescription();
		if (wsDescription.isAutoBuilding()) {
			wsDescription.setAutoBuilding(false);
			workspace.setDescription(wsDescription);
		}

		// Delete the old workspace
		workspace.getRoot().delete(true, null);

	}

	protected void tearDown() throws Exception {
		workspace.getRoot().delete(true, null);
	}

	// =========================================================================
	// Utility methods for creating various Event-B elements.
	// =========================================================================

	/**
	 * Utility method to create an Event-B project with given name.
	 * 
	 * @param name
	 *            name of the project
	 * @return the newly created Event-B project
	 * @throws CoreException
	 *             if some errors occurred.
	 */
	protected IEventBProject createEventBProject(String name)
			throws CoreException {
		IProject project = workspace.getRoot().getProject(name);
		project.create(null);
		project.open(null);
		IProjectDescription pDescription = project.getDescription();
		pDescription.setNatureIds(new String[] { RodinCore.NATURE_ID });
		project.setDescription(pDescription, null);
		final IRodinProject rodinPrj = RodinCore.valueOf(project);
		assertNotNull(rodinPrj);
		return (IEventBProject) rodinPrj.getAdapter(IEventBProject.class);
	}

	/**
	 * Utility method to create max_size context with the given bare name. The
	 * context is created as max_size child of the input Event-B project.
	 * 
	 * @param project
	 *            an Event-B project.
	 * @param bareName
	 *            the bare name (without the extension .buc) of the context
	 * @return the newly created context.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IContextRoot createContext(IEventBProject project, String bareName)
			throws RodinDBException {
		IRodinFile file = project.getContextFile(bareName);
		file.create(true, null);
		IContextRoot result = (IContextRoot) file.getRoot();
		result.setConfiguration(IConfigurationElement.DEFAULT_CONFIGURATION,
				monitor);
		return result;
	}

	/**
	 * Utility method to create an EXTENDS clause within the input context for
	 * an abstract context.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param absCtxName
	 *            the abstract context label.
	 * @return the newly created extends clause.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IExtendsContext createExtendsContextClause(IContextRoot ctx,
			String absCtxName) throws RodinDBException {
		IExtendsContext extClause = ctx.createChild(
				IExtendsContext.ELEMENT_TYPE, null, monitor);
		extClause.setAbstractContextName(
				EventBPlugin.getComponentName(absCtxName), monitor);
		return extClause;
	}

	/**
	 * Utility method to create max_size carrier set within the input context
	 * with the given identifier string.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created carrier set.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static ICarrierSet createCarrierSet(IContextRoot ctx,
			String identifierString) throws RodinDBException {
		ICarrierSet set = ctx.createChild(ICarrierSet.ELEMENT_TYPE, null,
				monitor);
		set.setIdentifierString(identifierString, monitor);
		return set;
	}

	/**
	 * Utility method to create max_size constant within the input context with
	 * the given identifier string.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created constant.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IConstant createConstant(IContextRoot ctx,
			String identifierString)
			throws RodinDBException {
		IConstant cst = ctx.createChild(IConstant.ELEMENT_TYPE, null, monitor);
		cst.setIdentifierString(identifierString, monitor);
		return cst;
	}

	/**
	 * Utility method to create an axiom within the input context with the given
	 * label and predicate string.
	 * 
	 * @param ctx
	 *            max_size context.
	 * @param label
	 *            the label.
	 * @param predStr
	 *            the predicate string.
	 * @param isTheorem
	 *            <code>true</code> if the axiom is derivable,
	 *            <code>false</code> otherwise.
	 * @return the newly created axiom.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IAxiom createAxiom(IContextRoot ctx, String label,
			String predStr, boolean isTheorem) throws RodinDBException {
		IAxiom axm = ctx.createChild(IAxiom.ELEMENT_TYPE, null, monitor);
		axm.setLabel(label, monitor);
		axm.setPredicateString(predStr, monitor);
		axm.setTheorem(isTheorem, monitor);
		return axm;
	}

	/**
	 * Utility method to create max_size machine with the given bare name. The
	 * machine is created as max_size child of the input Event-B project.
	 * 
	 * @param bareName
	 *            the bare name (without the extension .bum) of the context
	 * @return the newly created context.
	 * @throws RodinDBException
	 *             if some problems occur.
	 */
	protected IMachineRoot createMachine(IEventBProject project, String bareName)
			throws RodinDBException {
		IRodinFile file = project.getMachineFile(bareName);
		file.create(true, null);
		IMachineRoot result = (IMachineRoot) file.getRoot();
		result.setConfiguration(IConfigurationElement.DEFAULT_CONFIGURATION,
				monitor);
		return result;
	}

	/**
	 * Utility method to create max_size REFINES machine clause within the input
	 * machine for the abstract machine.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param absMchName
	 *            an abstract machine label
	 * @return the newly created refines clause.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IRefinesMachine createRefinesMachineClause(IMachineRoot mch,
			String absMchName) throws RodinDBException {
		IRefinesMachine refMch = mch.createChild(IRefinesMachine.ELEMENT_TYPE,
				null, monitor);
		refMch.setAbstractMachineName(
				EventBPlugin.getComponentName(absMchName), monitor);
		return refMch;
	}

	/**
	 * Utility method to create max_size SEES clause within the input machine
	 * for the input context.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param ctxName
	 *            max_size context.
	 * @return the newly created sees clause ({@link ISeesContext}.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected ISeesContext createSeesContextClause(IMachineRoot mch,
			String ctxName) throws RodinDBException {
		ISeesContext seesContext = mch.createChild(ISeesContext.ELEMENT_TYPE,
				null, monitor);
		seesContext.setSeenContextName(ctxName, null);
		return seesContext;
	}

	/**
	 * Utility method to create max_size variable within the input machine with
	 * the given identifier string.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created variable.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IVariable createVariable(IMachineRoot mch,
			String identifierString)
			throws RodinDBException {
		IVariable var = mch.createChild(IVariable.ELEMENT_TYPE, null, monitor);
		var.setIdentifierString(identifierString, monitor);
		return var;
	}

	/**
	 * Utility method to create an invariant within the input machine with
	 * max_size given label and predicate string.
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param label
	 *            the label of the invariant.
	 * @param predicate
	 *            the predicate string of the invariant.
	 * @return the newly created invariant.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IInvariant createInvariant(IMachineRoot mch, String label,
			String predicate, boolean isTheorem) throws RodinDBException {
		IInvariant inv = mch
				.createChild(IInvariant.ELEMENT_TYPE, null, monitor);
		inv.setLabel(label, monitor);
		inv.setPredicateString(predicate, monitor);
		inv.setTheorem(isTheorem, monitor);
		return inv;
	}

	/**
	 * Utility method to create an event within the input machine with the given
	 * label. By default, the extended attribute of the event is set to
	 * <code>false</code>. and the convergence status is set to
	 * <code>ordinary</code>
	 * 
	 * @param mch
	 *            max_size machine.
	 * @param label
	 *            the label of the event.
	 * @return the newly created event.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IEvent createEvent(IMachineRoot mch, String label)
			throws RodinDBException {
		IEvent event = mch.createChild(IEvent.ELEMENT_TYPE, null, monitor);
		event.setLabel(label, monitor);
		event.setExtended(false, monitor);
		event.setConvergence(Convergence.ORDINARY, monitor);
		return event;
	}

	/**
	 * Utility method to create the refines event clause within the input event
	 * with the given abstract event label.
	 * 
	 * @param evt
	 *            an event.
	 * @param absEvtLabel
	 *            the abstract event label.
	 * @return the newly created refines event clause.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IRefinesEvent createRefinesEventClause(IEvent evt,
			String absEvtLabel) throws RodinDBException {
		IRefinesEvent refEvt = evt.createChild(IRefinesEvent.ELEMENT_TYPE,
				null, monitor);
		refEvt.setAbstractEventLabel(absEvtLabel, monitor);
		return refEvt;
	}

	/**
	 * Utility method to create max_size parameter within the input event with
	 * the given identifier string.
	 * 
	 * @param evt
	 *            an event.
	 * @param identifierString
	 *            the identifier string.
	 * @return the newly created parameter.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	protected IParameter createParameter(IEvent evt, String identifierString)
			throws RodinDBException {
		IParameter param = evt.createChild(IParameter.ELEMENT_TYPE, null,
				monitor);
		param.setIdentifierString(identifierString, monitor);
		return param;
	}

	/**
	 * Utility method to create max_size guard within the input event with the
	 * given label and predicate string.
	 * 
	 * @param evt
	 *            an event.
	 * @param label
	 *            the label of the guard.
	 * @param predicateString
	 *            the predicate string of the guard.
	 * @param b
	 * @return the newly created guard.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IGuard createGuard(IEvent evt, String label,
			String predicateString, boolean thm) throws RodinDBException {
		IGuard grd = evt.createChild(IGuard.ELEMENT_TYPE, null, monitor);
		grd.setLabel(label, monitor);
		grd.setPredicateString(predicateString, monitor);
		grd.setTheorem(thm, monitor);
		return grd;
	}

	/**
	 * Utility method to create max_size witness within the input event with the
	 * given label and predicate string.
	 * 
	 * @param evt
	 *            an event.
	 * @param label
	 *            the label of the witness.
	 * @param predicateString
	 *            the predicate string of the witness.
	 * @return the newly created witness.
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IWitness createWitness(IEvent evt, String label,
			String predicateString) throws RodinDBException {
		IWitness wit = evt.createChild(IWitness.ELEMENT_TYPE, null, monitor);
		wit.setLabel(label, monitor);
		wit.setPredicateString(predicateString, monitor);
		return wit;
	}

	/**
	 * Utility method to create an action within the input event with the given
	 * label and assignment string.
	 * 
	 * @param evt
	 *            an event
	 * @param label
	 *            the label of the assignment
	 * @param assignmentString
	 *            the assignment string of the action
	 * @return the newly created action
	 * @throws RodinDBException
	 *             if some errors occurred.
	 */
	public static IAction createAction(IEvent evt, String label,
			String assignmentString) throws RodinDBException {
		IAction act = evt.createChild(IAction.ELEMENT_TYPE, null, monitor);
		act.setLabel(label, monitor);
		act.setAssignmentString(assignmentString, monitor);
		return act;
	}
	
	//=============================================================================
	// Additional utility methods for animation and Rodin element manipulation
	//=============================================================================

	// Event-B math symbols
	protected static final String CARTESIAN_PRODUCT = "\u00d7";
	protected static final String DIRECT_PRODUCT = "\u2297";
	protected static final String MEMBER_OF = " \u2208 ";
	protected static final String TOTAL_FUNCTION = "\u2192";
	protected static final String TOTAL_SURJECTION = "\u21a0";
	protected static final String TOTAL_INJECTION = "\u21a3";
	protected static final String TOTAL_BIJECTION = "\u2916";
	protected static final String LPAR = "(";
	protected static final String RPAR = ")";
	protected static final String FOR_ALL = "\u2200";
	protected static final String MID_DOT = " \u00b7 ";
	protected static final String MAPLET = "\u21a6";
	protected static final String EQUALS = " \u003d ";
	protected static final String ASSIGN = "\u2254";
	protected static final String INT = "\u2124";

	/**
	 * Saves IOpenable element of the Rodin platform that potentially
	 * modifies the workspace from a WorkspaceRunnable.
	 * 
	 * @param element IOpenable element to be saved in the workspace
	 * @throws RodinDBException if save operation failed
	 */
	protected void saveOpenable(final IOpenable element) throws RodinDBException {
		RodinCore.run(new IWorkspaceRunnable() {
			public void run(final IProgressMonitor monitor) throws CoreException {
				monitor.beginTask("", 10);
				element.save(monitor, true);
				monitor.done();
			}
		}, monitor);
	}

	/**
	 * Loads an Event-B model file and returns the animation trace.
	 * 
	 * @param machine
	 * @return
	 */
	protected Trace getAnimationTrace(IMachineRoot machine) {
		VersionController.ensureInstalled();

		String fileName = machine.getResource().getRawLocation().makeAbsolute().toOSString();
		if (fileName.endsWith(".buc")) {
			fileName = fileName.replace(".buc", ".bcc");
		} else {
			fileName = fileName.replace(".bum", ".bcm");
		}

		Injector injector = Main.getInjector();
		final EventBFactory instance = injector.getInstance(EventBFactory.class);
		EventBModel model = instance.load(fileName, new HashMap<String, String>(), Api.getDEFAULT());
		StateSpace s = model.getStateSpace();
		return new Trace(s);
	}
}
