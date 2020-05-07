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

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eventb.emf.core.impl.EventBNamedImpl;

import ac.soton.multisim.Component;
import ac.soton.multisim.ComponentDiagram;
import ac.soton.multisim.EventBComponent;
import ac.soton.multisim.MultisimPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component Diagram</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link ac.soton.multisim.impl.ComponentDiagramImpl#getComponents <em>Components</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.ComponentDiagramImpl#getStopTime <em>Stop Time</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.ComponentDiagramImpl#getStartTime <em>Start Time</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.ComponentDiagramImpl#getArguments <em>Arguments</em>}</li>
 *   <li>{@link ac.soton.multisim.impl.ComponentDiagramImpl#isRecordOutputs <em>Record Outputs</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ComponentDiagramImpl extends EventBNamedImpl implements ComponentDiagram {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2015 University of Southampton.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html";

	/**
	 * The cached value of the '{@link #getComponents() <em>Components</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponents()
	 * @generated
	 * @ordered
	 */
	protected EList<Component> components;

	/**
	 * The default value of the '{@link #getStopTime() <em>Stop Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStopTime()
	 * @generated
	 * @ordered
	 */
	protected static final int STOP_TIME_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getStopTime() <em>Stop Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStopTime()
	 * @generated
	 * @ordered
	 */
	protected int stopTime = STOP_TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getStartTime() <em>Start Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStartTime()
	 * @generated
	 * @ordered
	 */
	protected static final int START_TIME_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getStartTime() <em>Start Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStartTime()
	 * @generated
	 * @ordered
	 */
	protected int startTime = START_TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getArguments() <em>Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArguments()
	 * @generated
	 * @ordered
	 */
	protected static final String ARGUMENTS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getArguments() <em>Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getArguments()
	 * @generated
	 * @ordered
	 */
	protected String arguments = ARGUMENTS_EDEFAULT;

	/**
	 * The default value of the '{@link #isRecordOutputs() <em>Record Outputs</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecordOutputs()
	 * @generated
	 * @ordered
	 */
	protected static final boolean RECORD_OUTPUTS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRecordOutputs() <em>Record Outputs</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRecordOutputs()
	 * @generated
	 * @ordered
	 */
	protected boolean recordOutputs = RECORD_OUTPUTS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentDiagramImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MultisimPackage.Literals.COMPONENT_DIAGRAM;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Component> getComponents() {
		if (components == null) {
			components = new EObjectContainmentEList.Resolving<Component>(Component.class, this, MultisimPackage.COMPONENT_DIAGRAM__COMPONENTS);
		}
		return components;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getStopTime() {
		return stopTime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStopTime(int newStopTime) {
		int oldStopTime = stopTime;
		stopTime = newStopTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.COMPONENT_DIAGRAM__STOP_TIME, oldStopTime, stopTime));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStartTime(int newStartTime) {
		int oldStartTime = startTime;
		startTime = newStartTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.COMPONENT_DIAGRAM__START_TIME, oldStartTime, startTime));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getArguments() {
		return arguments;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setArguments(String newArguments) {
		String oldArguments = arguments;
		arguments = newArguments;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.COMPONENT_DIAGRAM__ARGUMENTS, oldArguments, arguments));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isRecordOutputs() {
		return recordOutputs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRecordOutputs(boolean newRecordOutputs) {
		boolean oldRecordOutputs = recordOutputs;
		recordOutputs = newRecordOutputs;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MultisimPackage.COMPONENT_DIAGRAM__RECORD_OUTPUTS, oldRecordOutputs, recordOutputs));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MultisimPackage.COMPONENT_DIAGRAM__COMPONENTS:
				return ((InternalEList<?>)getComponents()).basicRemove(otherEnd, msgs);
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
			case MultisimPackage.COMPONENT_DIAGRAM__COMPONENTS:
				return getComponents();
			case MultisimPackage.COMPONENT_DIAGRAM__STOP_TIME:
				return getStopTime();
			case MultisimPackage.COMPONENT_DIAGRAM__START_TIME:
				return getStartTime();
			case MultisimPackage.COMPONENT_DIAGRAM__ARGUMENTS:
				return getArguments();
			case MultisimPackage.COMPONENT_DIAGRAM__RECORD_OUTPUTS:
				return isRecordOutputs();
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
			case MultisimPackage.COMPONENT_DIAGRAM__COMPONENTS:
				getComponents().clear();
				getComponents().addAll((Collection<? extends Component>)newValue);
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__STOP_TIME:
				setStopTime((Integer)newValue);
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__START_TIME:
				setStartTime((Integer)newValue);
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__ARGUMENTS:
				setArguments((String)newValue);
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__RECORD_OUTPUTS:
				setRecordOutputs((Boolean)newValue);
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
			case MultisimPackage.COMPONENT_DIAGRAM__COMPONENTS:
				getComponents().clear();
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__STOP_TIME:
				setStopTime(STOP_TIME_EDEFAULT);
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__START_TIME:
				setStartTime(START_TIME_EDEFAULT);
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__ARGUMENTS:
				setArguments(ARGUMENTS_EDEFAULT);
				return;
			case MultisimPackage.COMPONENT_DIAGRAM__RECORD_OUTPUTS:
				setRecordOutputs(RECORD_OUTPUTS_EDEFAULT);
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
			case MultisimPackage.COMPONENT_DIAGRAM__COMPONENTS:
				return components != null && !components.isEmpty();
			case MultisimPackage.COMPONENT_DIAGRAM__STOP_TIME:
				return stopTime != STOP_TIME_EDEFAULT;
			case MultisimPackage.COMPONENT_DIAGRAM__START_TIME:
				return startTime != START_TIME_EDEFAULT;
			case MultisimPackage.COMPONENT_DIAGRAM__ARGUMENTS:
				return ARGUMENTS_EDEFAULT == null ? arguments != null : !ARGUMENTS_EDEFAULT.equals(arguments);
			case MultisimPackage.COMPONENT_DIAGRAM__RECORD_OUTPUTS:
				return recordOutputs != RECORD_OUTPUTS_EDEFAULT;
		}
		return super.eIsSet(featureID);
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
		result.append(" (stopTime: ");
		result.append(stopTime);
		result.append(", startTime: ");
		result.append(startTime);
		result.append(", arguments: ");
		result.append(arguments);
		result.append(", recordOutputs: ");
		result.append(recordOutputs);
		result.append(')');
		return result.toString();
	}

	/**
	 * Temporary logic: generates status report. 
	 * (non-Javadoc)
	 * @see ac.soton.multisim.ComponentDiagram#createStatusReport()
	 * @custom
	 */
	@Override
	public String createStatusReport() {
		StringBuffer sb = new StringBuffer("");
		for (Component c : components)
			if (c instanceof EventBComponent) {
				EventBComponent ebc = (EventBComponent) c;
				if (ebc.isRecordTrace() && ebc.getTraceFileName() != null)
					sb .append("Trace recorded to:\n\t"+ebc.getTraceFileName()+"\n");
			}
		return sb.toString();
	}

} //ComponentDiagramImpl
