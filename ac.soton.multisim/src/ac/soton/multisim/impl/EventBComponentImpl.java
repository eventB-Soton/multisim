/**
 * Copyright (c) 2014 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * $Id$
 */
package ac.soton.multisim.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eventb.core.IEventBRoot;
import org.eventb.emf.core.CorePackage;
import org.eventb.emf.core.EventBCommented;
import org.eventb.emf.core.EventBCommentedElement;
import org.eventb.emf.core.EventBNamed;
import org.eventb.emf.core.EventBNamedCommentedElement;
import org.eventb.emf.core.impl.AbstractExtensionImpl;
import org.eventb.emf.core.machine.Event;
import org.eventb.emf.core.machine.Machine;

import com.google.inject.Injector;

import ac.soton.multisim.Component;
import ac.soton.multisim.ComponentDiagram;
import ac.soton.multisim.EventBComponent;
import ac.soton.multisim.EventBPort;
import ac.soton.multisim.MultisimPackage;
import ac.soton.multisim.Port;
import ac.soton.multisim.exception.ModelException;
import ac.soton.multisim.exception.SimulationException;
import ac.soton.multisim.util.SimulationUtil;
import de.be4.ltl.core.parser.LtlParseException;
import de.prob.Main;
import de.prob.animator.command.ExecuteUntilCommand;
import de.prob.animator.domainobjects.EvalResult;
import de.prob.animator.domainobjects.LTL;
import de.prob.model.eventb.EventBModel;
import de.prob.scripting.Api;
import de.prob.scripting.EventBFactory;
import de.prob.statespace.AnimationSelector;
import de.prob.statespace.State;
import de.prob.statespace.StateSpace;
import de.prob.statespace.Trace;
import de.prob.statespace.Transition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Event BComponent</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getName <em>Name</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getInputs <em>Inputs</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getOutputs <em>Outputs</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getMachine <em>Machine</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#isComposed <em>Composed</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getStartStepEvents <em>Start Step Events</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getEndStepEvents <em>End Step Events</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getTrace <em>Trace</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#isRecordTrace <em>Record Trace</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getTraceFileName <em>Trace File Name</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.EventBComponentImpl#getStepSize <em>Step Size</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EventBComponentImpl extends AbstractExtensionImpl implements EventBComponent {
	
	/**
	 * Event-B expression constants.
	 * @custom
	 */
	private static final String INIT = "$initialise_machine";
	private static final String TT = "TRUE=TRUE";
	private static final String EQ = "=";
	private static final String AND = "&";
	private static final String LTL_END = ")";
	private static final String LTL_OR = "or[";
	private static final String LTL_RBRACKET = "]";
	private static final String LTL_START = "F Y ([";

	/**
	 * @custom
	 */
	private static DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
	private static Random random = new Random(System.nanoTime());
	private Set<String> endStepSet = new HashSet<String>();
	private LTL ltl;
	private StringBuilder stringBuilder = new StringBuilder();
	private BufferedWriter traceWriter;
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2015 University of Southampton.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html";

	/**
	 * The default value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMENT_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getComment() <em>Comment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComment()
	 * @generated
	 * @ordered
	 */
	protected String comment = COMMENT_EDEFAULT;
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInputs() <em>Inputs</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInputs()
	 * @generated
	 * @ordered
	 */
	protected EList<Port> inputs;

	/**
	 * The cached value of the '{@link #getOutputs() <em>Outputs</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputs()
	 * @generated
	 * @ordered
	 */
	protected EList<Port> outputs;

	/**
	 * The cached value of the '{@link #getMachine() <em>Machine</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMachine()
	 * @generated
	 * @ordered
	 */
	protected Machine machine;

	/**
	 * The default value of the '{@link #isComposed() <em>Composed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isComposed()
	 * @generated
	 * @ordered
	 */
	protected static final boolean COMPOSED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isComposed() <em>Composed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isComposed()
	 * @generated
	 * @ordered
	 */
	protected boolean composed = COMPOSED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getStartStepEvents() <em>Start Step Events</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStartStepEvents()
	 * @generated
	 * @ordered
	 */
	protected EList<Event> startStepEvents;
	/**
	 * The cached value of the '{@link #getEndStepEvents() <em>End Step Events</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEndStepEvents()
	 * @generated
	 * @ordered
	 */
	protected EList<Event> endStepEvents;
	/**
	 * The default value of the '{@link #getTrace() <em>Trace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTrace()
	 * @generated
	 * @ordered
	 */
	protected static final Trace TRACE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTrace() <em>Trace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTrace()
	 * @generated
	 * @ordered
	 */
	protected Trace trace = TRACE_EDEFAULT;

	/**
	 * The default value of the '{@link #isRecordTrace() <em>Record Trace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecordTrace()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RECORD_TRACE_EDEFAULT = false;
	/**
	 * The cached value of the '{@link #isRecordTrace() <em>Record Trace</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecordTrace()
	 * @generated
	 * @ordered
	 */
	protected boolean recordTrace = RECORD_TRACE_EDEFAULT;

	/**
	 * The default value of the '{@link #getTraceFileName() <em>Trace File Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTraceFileName()
	 * @generated
	 * @ordered
	 */
	protected static final String TRACE_FILE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTraceFileName() <em>Trace File Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTraceFileName()
	 * @generated
	 * @ordered
	 */
	protected String traceFileName = TRACE_FILE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getStepSize() <em>Step Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStepSize()
	 * @generated
	 * @ordered
	 */
	protected static final int STEP_SIZE_EDEFAULT = 0;
	/**
	 * The cached value of the '{@link #getStepSize() <em>Step Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStepSize()
	 * @generated
	 * @ordered
	 */
	protected int stepSize = STEP_SIZE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EventBComponentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MultisimPackage.Literals.EVENT_BCOMPONENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComment(String newComment) {
		String oldComment = comment;
		comment = newComment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__COMMENT, oldComment, comment));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getLabel() {
		return getMachine() == null ? "" : getMachine().getName();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Trace getTrace() {
		return trace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTrace(Trace newTrace) {
		Trace oldTrace = trace;
		trace = newTrace;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__TRACE, oldTrace, trace));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRecordTrace() {
		return recordTrace;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecordTrace(boolean newRecordTrace) {
		boolean oldRecordTrace = recordTrace;
		recordTrace = newRecordTrace;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__RECORD_TRACE, oldRecordTrace, recordTrace));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTraceFileName() {
		return traceFileName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTraceFileName(String newTraceFileName) {
		String oldTraceFileName = traceFileName;
		traceFileName = newTraceFileName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__TRACE_FILE_NAME, oldTraceFileName, traceFileName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getStepSize() {
		return stepSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStepSize(int newStepSize) {
		int oldStepSize = stepSize;
		stepSize = newStepSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__STEP_SIZE, oldStepSize, stepSize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Port> getInputs() {
		if (inputs == null) {
			inputs = new EObjectContainmentEList.Resolving<Port>(Port.class, this, MultisimPackage.EVENT_BCOMPONENT__INPUTS);
		}
		return inputs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Port> getOutputs() {
		if (outputs == null) {
			outputs = new EObjectContainmentEList.Resolving<Port>(Port.class, this, MultisimPackage.EVENT_BCOMPONENT__OUTPUTS);
		}
		return outputs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Machine getMachine() {
		if (machine != null && machine.eIsProxy()) {
			InternalEObject oldMachine = (InternalEObject)machine;
			machine = (Machine)eResolveProxy(oldMachine);
			if (machine != oldMachine) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MultisimPackage.EVENT_BCOMPONENT__MACHINE, oldMachine, machine));
			}
		}
		return machine;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Machine basicGetMachine() {
		return machine;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMachine(Machine newMachine) {
		Machine oldMachine = machine;
		machine = newMachine;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__MACHINE, oldMachine, machine));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isComposed() {
		return composed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setComposed(boolean newComposed) {
		boolean oldComposed = composed;
		composed = newComposed;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.EVENT_BCOMPONENT__COMPOSED, oldComposed, composed));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Event> getStartStepEvents() {
		if (startStepEvents == null) {
			startStepEvents = new EObjectResolvingEList<Event>(Event.class, this, MultisimPackage.EVENT_BCOMPONENT__START_STEP_EVENTS);
		}
		return startStepEvents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Event> getEndStepEvents() {
		if (endStepEvents == null) {
			endStepEvents = new EObjectResolvingEList<Event>(Event.class, this, MultisimPackage.EVENT_BCOMPONENT__END_STEP_EVENTS);
		}
		return endStepEvents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * Customised ProB animator startup code from {@link de.prob.ui.eventb.StartAnimationHandler#execute(ExecutionEvent)}
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void instantiate() throws SimulationException {
		
		// load event-b machine
		final IEventBRoot machineRoot = SimulationUtil.getMachineRoot(getMachine());
		if (machineRoot == null) {
			throw new SimulationException("Cannot load machine '" + getMachine().getName() + "' of component '" + getName()
					+ "':\nMachine root cannot be determined.");
		}
		
		String fileName = machineRoot.getResource().getRawLocation().makeAbsolute().toOSString();
		if (fileName.endsWith(".buc")) {
			fileName = fileName.replace(".buc", ".bcc");
		} else {
			fileName = fileName.replace(".bum", ".bcm");
		}

		// parse ProB arguments
		Map<String, String> params = new HashMap<String, String>();
		String args = ((ComponentDiagram) eContainer()).getArguments();
		if (args != null && !args.trim().isEmpty()) {
			String[] argArray = args.split(",");
			try {
				for (int i=0; i< argArray.length; i++) {
					String[] arg = argArray[i].split(EQ);
					params.put(arg[0], arg[1]);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new SimulationException("ProB parameter format is invalid");
			}
		}

		// load a machine
		Injector injector = Main.getInjector();
		final EventBFactory instance = injector.getInstance(EventBFactory.class);
		EventBModel model = instance.load(fileName, params, Api.getDEFAULT());
		if (model == null)
			throw new SimulationException("ProB could not load machine file '" + fileName + "' with parameters=" + params.toString());

		// get a trace
		StateSpace s = model.getStateSpace();
		s.startTransaction();	// presumably putting everything into a transaction should make it perform faster
		trace = new Trace(s);	//NOTE: don't use setTrace() method to avoid notification
		System.gc();
		
		// recall events for doStep matching
		EList<Event> endStep = getEndStepEvents();
		if (!endStepSet.isEmpty())
			endStepSet.clear();
		for (Event e : endStep)
			endStepSet.add(e.getName());
		
		// 'EndStep' event enabledness LTL formula: F Y ([event 1] or [event 2] or ...)
		// F - execute until an event is enabled; Y - execute the enabled event as well
		stringBuilder.setLength(0);
		stringBuilder.append(LTL_START).append(endStep.get(0).getName()).append(LTL_RBRACKET);
		for (int i=1; i<endStep.size(); i++)
			stringBuilder.append(LTL_OR).append(endStep.get(i).getName()).append(LTL_RBRACKET);
		stringBuilder.append(LTL_END);

		try {
			ltl = new LTL(stringBuilder.toString());
		} catch (LtlParseException e) {
			throw new SimulationException("LTL parse failure '" + ltl.toString() + "'", e);
		}
		
		// trace output file
		if (isRecordTrace()) {
			traceFileName = WorkspaceSynchronizer
					.getFile(machine.eResource()).getLocation()
					.removeLastSegments(1).addTrailingSeparator().toOSString()
					+ getName() + "_" + dateFormat.format(new java.util.Date()) + ".xml";
			recordStart(fileName, traceFileName);
		}

		// disable notification for modifying output ports
		// so that using EMF transactions is not required
		// while setting port value
		for (Port p : getOutputs())
			p.eSetDeliver(false);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws SimulationException 
	 * @generated NOT
	 */
	public void initialise(int tStart, int tStop) throws SimulationException {
		// execute first two events: 'setup_constants' and 'initialise'
		//NOTE: setup_constants can be absent if there are no constants
		trace = trace.anyEvent(null);
		if (isRecordTrace())
			recordOp(trace.getCurrent().getTransition());
		if (!INIT.equals(trace.getCurrentTransition().getName())) {
			trace = trace.anyEvent(null);
			if (isRecordTrace())
				recordOp(trace.getCurrent().getTransition());
		}
		if (!INIT.equals(trace.getCurrentTransition().getName()))
			throw new SimulationException("Cannot initialise component '" + getName()
					+ "':\nOperation '$initialise_machine' not found.");
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws SimulationException 
	 * @throws ModelException 
	 * @generated NOT
	 */
	public void readInputs() throws SimulationException, ModelException {
		// build parameter predicate for 'StartStep' event execution
		stringBuilder.setLength(0);
		stringBuilder.append(TT);
		for (Port p : getInputs()) {
			// if port not connected, let ProB pick the value non-deterministically
			if (p.getIn() == null)
				continue;
			
			// add parameter to event predicate string
			stringBuilder.append(AND)
				.append(((EventBPort) p).getParameter().getName())
				.append(EQ)
				.append(SimulationUtil.getEventBValue(p.getIn().getValue(), p.getType(), ((EventBPort) p).getIntToReal()));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void writeOutputs() {
		State state = trace.getCurrentState();
		for (Port p : getOutputs()) {
			p.setValue(SimulationUtil.getFMIValue(
					((EvalResult) state.eval(((EventBPort) p).getVariable().getName())).getValue(), 
					p.getType(), 
					((EventBPort) p).getIntToReal()));
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws ModelException 
	 * @throws SimulationException 
	 * @generated NOT
	 */
	public void doStep(int time, int step) throws ModelException, SimulationException {
		// find enabled 'StartStep' event
		List<String> enabled = new ArrayList<String>();
		String predicate = stringBuilder.toString();
		EList<Event> startStep = getStartStepEvents();
		for (Event event : startStep) {
			try {
				if (trace.canExecuteEvent(event.getName(), predicate))
					enabled.add(event.getName());
			} catch (IllegalArgumentException e) {
				// no operation found -> proceed
			}
		}
		
		// no 'StartStep' events are enabled
		if (enabled.isEmpty())
			throw new ModelException("No enabled 'StartStep' events found in '" + getName() + "' for a predicate: " + predicate);
		
		// execute one of the enabled 'StartStep' events
		trace = trace.execute(enabled.get(random.nextInt(enabled.size())), predicate);
		
		// recording
		if (isRecordTrace()) {
			recordOp(trace.getCurrentTransition());
		}
				
		// skip if current event is already an 'EndStep' event
		if (endStepSet.contains(trace.getCurrentTransition().getName()))
			return;
		
		StateSpace stateSpace = trace.getStateSpace();
		ExecuteUntilCommand command = new ExecuteUntilCommand(trace.getStateSpace(), trace.getCurrentState(), ltl);
		stateSpace.execute(command);
		trace = trace.addTransitions(command.getNewTransitions());
		
		if (!command.isSuccess()) {
			if (isRecordTrace()) {
				recordOps(command.getNewTransitions());
			}
			
			if (command.conditionNotReached())
				throw new SimulationException("ExecuteUntilCommand cannot be completed (possible infinite loop).\nSee recorded trace or animation for details.");
			if (command.isDeadlocked())
				throw new ModelException("Deadlock in component '" + getName() + "'\nSee recorded trace or animation for details.");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws SimulationException 
	 * @generated NOT
	 */
	public void terminate() {
		
		// re-enable notifications
		for (Port p : getOutputs())
			p.eSetDeliver(true);
		
		if (trace != null) {
			trace.getStateSpace().endTransaction();
			
			// save trace
			if (isRecordTrace())
				recordEnd();
	
			// show in ProB
			Injector injector = Main.getInjector();
			AnimationSelector selector = injector.getInstance(AnimationSelector.class);
			selector.addNewAnimation(trace);
			
			trace = null;
		}
		
		System.gc();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String doGetName() {
		if (this.eIsProxy()){
			String fragment = ((InternalEObject)this).eProxyURI().fragment();
			int ind = fragment.lastIndexOf("::");
			if (ind>-1) fragment = fragment.substring(ind+2);
			fragment = fragment.substring(fragment.lastIndexOf('.')+1);
			return fragment;
		}else{
			return name;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MultisimPackage.EVENT_BCOMPONENT__INPUTS:
				return ((InternalEList<?>)getInputs()).basicRemove(otherEnd, msgs);
			case MultisimPackage.EVENT_BCOMPONENT__OUTPUTS:
				return ((InternalEList<?>)getOutputs()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MultisimPackage.EVENT_BCOMPONENT__COMMENT:
				return getComment();
			case MultisimPackage.EVENT_BCOMPONENT__NAME:
				return getName();
			case MultisimPackage.EVENT_BCOMPONENT__INPUTS:
				return getInputs();
			case MultisimPackage.EVENT_BCOMPONENT__OUTPUTS:
				return getOutputs();
			case MultisimPackage.EVENT_BCOMPONENT__MACHINE:
				if (resolve) return getMachine();
				return basicGetMachine();
			case MultisimPackage.EVENT_BCOMPONENT__COMPOSED:
				return isComposed();
			case MultisimPackage.EVENT_BCOMPONENT__START_STEP_EVENTS:
				return getStartStepEvents();
			case MultisimPackage.EVENT_BCOMPONENT__END_STEP_EVENTS:
				return getEndStepEvents();
			case MultisimPackage.EVENT_BCOMPONENT__TRACE:
				return getTrace();
			case MultisimPackage.EVENT_BCOMPONENT__RECORD_TRACE:
				return isRecordTrace();
			case MultisimPackage.EVENT_BCOMPONENT__TRACE_FILE_NAME:
				return getTraceFileName();
			case MultisimPackage.EVENT_BCOMPONENT__STEP_SIZE:
				return getStepSize();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MultisimPackage.EVENT_BCOMPONENT__COMMENT:
				setComment((String)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__NAME:
				setName((String)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__INPUTS:
				getInputs().clear();
				getInputs().addAll((Collection<? extends Port>)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__OUTPUTS:
				getOutputs().clear();
				getOutputs().addAll((Collection<? extends Port>)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__MACHINE:
				setMachine((Machine)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__COMPOSED:
				setComposed((Boolean)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__START_STEP_EVENTS:
				getStartStepEvents().clear();
				getStartStepEvents().addAll((Collection<? extends Event>)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__END_STEP_EVENTS:
				getEndStepEvents().clear();
				getEndStepEvents().addAll((Collection<? extends Event>)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__TRACE:
				setTrace((Trace)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__RECORD_TRACE:
				setRecordTrace((Boolean)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__TRACE_FILE_NAME:
				setTraceFileName((String)newValue);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__STEP_SIZE:
				setStepSize((Integer)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MultisimPackage.EVENT_BCOMPONENT__COMMENT:
				setComment(COMMENT_EDEFAULT);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__INPUTS:
				getInputs().clear();
				return;
			case MultisimPackage.EVENT_BCOMPONENT__OUTPUTS:
				getOutputs().clear();
				return;
			case MultisimPackage.EVENT_BCOMPONENT__MACHINE:
				setMachine((Machine)null);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__COMPOSED:
				setComposed(COMPOSED_EDEFAULT);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__START_STEP_EVENTS:
				getStartStepEvents().clear();
				return;
			case MultisimPackage.EVENT_BCOMPONENT__END_STEP_EVENTS:
				getEndStepEvents().clear();
				return;
			case MultisimPackage.EVENT_BCOMPONENT__TRACE:
				setTrace(TRACE_EDEFAULT);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__RECORD_TRACE:
				setRecordTrace(RECORD_TRACE_EDEFAULT);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__TRACE_FILE_NAME:
				setTraceFileName(TRACE_FILE_NAME_EDEFAULT);
				return;
			case MultisimPackage.EVENT_BCOMPONENT__STEP_SIZE:
				setStepSize(STEP_SIZE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MultisimPackage.EVENT_BCOMPONENT__COMMENT:
				return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
			case MultisimPackage.EVENT_BCOMPONENT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case MultisimPackage.EVENT_BCOMPONENT__INPUTS:
				return inputs != null && !inputs.isEmpty();
			case MultisimPackage.EVENT_BCOMPONENT__OUTPUTS:
				return outputs != null && !outputs.isEmpty();
			case MultisimPackage.EVENT_BCOMPONENT__MACHINE:
				return machine != null;
			case MultisimPackage.EVENT_BCOMPONENT__COMPOSED:
				return composed != COMPOSED_EDEFAULT;
			case MultisimPackage.EVENT_BCOMPONENT__START_STEP_EVENTS:
				return startStepEvents != null && !startStepEvents.isEmpty();
			case MultisimPackage.EVENT_BCOMPONENT__END_STEP_EVENTS:
				return endStepEvents != null && !endStepEvents.isEmpty();
			case MultisimPackage.EVENT_BCOMPONENT__TRACE:
				return TRACE_EDEFAULT == null ? trace != null : !TRACE_EDEFAULT.equals(trace);
			case MultisimPackage.EVENT_BCOMPONENT__RECORD_TRACE:
				return recordTrace != RECORD_TRACE_EDEFAULT;
			case MultisimPackage.EVENT_BCOMPONENT__TRACE_FILE_NAME:
				return TRACE_FILE_NAME_EDEFAULT == null ? traceFileName != null : !TRACE_FILE_NAME_EDEFAULT.equals(traceFileName);
			case MultisimPackage.EVENT_BCOMPONENT__STEP_SIZE:
				return stepSize != STEP_SIZE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == EventBCommented.class) {
			switch (derivedFeatureID) {
				case MultisimPackage.EVENT_BCOMPONENT__COMMENT: return CorePackage.EVENT_BCOMMENTED__COMMENT;
				default: return -1;
			}
		}
		if (baseClass == EventBCommentedElement.class) {
			switch (derivedFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == EventBNamed.class) {
			switch (derivedFeatureID) {
				case MultisimPackage.EVENT_BCOMPONENT__NAME: return CorePackage.EVENT_BNAMED__NAME;
				default: return -1;
			}
		}
		if (baseClass == EventBNamedCommentedElement.class) {
			switch (derivedFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == Component.class) {
			switch (derivedFeatureID) {
				case MultisimPackage.EVENT_BCOMPONENT__INPUTS: return MultisimPackage.COMPONENT__INPUTS;
				case MultisimPackage.EVENT_BCOMPONENT__OUTPUTS: return MultisimPackage.COMPONENT__OUTPUTS;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == EventBCommented.class) {
			switch (baseFeatureID) {
				case CorePackage.EVENT_BCOMMENTED__COMMENT: return MultisimPackage.EVENT_BCOMPONENT__COMMENT;
				default: return -1;
			}
		}
		if (baseClass == EventBCommentedElement.class) {
			switch (baseFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == EventBNamed.class) {
			switch (baseFeatureID) {
				case CorePackage.EVENT_BNAMED__NAME: return MultisimPackage.EVENT_BCOMPONENT__NAME;
				default: return -1;
			}
		}
		if (baseClass == EventBNamedCommentedElement.class) {
			switch (baseFeatureID) {
				default: return -1;
			}
		}
		if (baseClass == Component.class) {
			switch (baseFeatureID) {
				case MultisimPackage.COMPONENT__INPUTS: return MultisimPackage.EVENT_BCOMPONENT__INPUTS;
				case MultisimPackage.COMPONENT__OUTPUTS: return MultisimPackage.EVENT_BCOMPONENT__OUTPUTS;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (comment: ");
		result.append(comment);
		result.append(", name: ");
		result.append(name);
		result.append(", composed: ");
		result.append(composed);
		result.append(", trace: ");
		result.append(trace);
		result.append(", recordTrace: ");
		result.append(recordTrace);
		result.append(", traceFileName: ");
		result.append(traceFileName);
		result.append(", stepSize: ");
		result.append(stepSize);
		result.append(')');
		return result.toString();
	}

	/* @custom
	 */
	@Override
	public String getExtensionId() {
		return MultisimPackage.EXTENSION_ID;
	}
	
	/**
	 * Records the header of the trace (instantiates buffer).
	 * @param modelName
	 * @param traceFileName 
	 * @return recorded file name
	 * @throws SimulationException
	 */
	private String recordStart(String modelName, String traceFileName) throws SimulationException {
		try {
			traceWriter = new BufferedWriter(new FileWriter(traceFileName));
			traceWriter.append("<trace><model>").append(modelName).append("</model>\n");
		} catch (IOException e) {
			throw new SimulationException("Cannot create a trace file", e);
		}
		return traceFileName;
	}
	
	/**
	 * Records an operation.
	 * @param op
	 * @custom
	 */
	private void recordOp(Transition op) {
		op.evaluate();
		try {
			traceWriter.append("<Operation name=\"").append(op.getName()).append("\">");
			for (String p : op.getParams()) {
				traceWriter.append("<Parameter name=\"").append(p).append("\"/>");
			}
			traceWriter.append("</Operation>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Records a list of operations.
	 * @param ops
	 * @custom
	 */
	private void recordOps(List<Transition> ops) {
		trace.getStateSpace().evaluateTransitions(ops);
		for (Transition op : ops) {
			recordOp(op);
		}
	}

	/**
	 * Records the end of the trace file and writes the buffer.
	 * @custom
	 */
	private void recordEnd() {
		if (traceWriter != null) {
			try {
				traceWriter.write("</trace>");
			} catch (IOException e) {
				traceFileName = null;
//				throw new SimulationException("Cannot write to a trace file", e);
			} finally {
				try {
					traceWriter.close();
				} catch (IOException e) {}
			}
		}
	}

} //EventBComponentImpl
