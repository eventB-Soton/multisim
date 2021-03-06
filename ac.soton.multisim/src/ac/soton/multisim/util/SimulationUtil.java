/**
 * Copyright (c) 2014 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package ac.soton.multisim.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eventb.core.IEventBRoot;
import org.eventb.emf.core.machine.Machine;
import org.ptolemy.fmi.FMIScalarVariable;
import org.ptolemy.fmi.type.FMIBooleanType;
import org.ptolemy.fmi.type.FMIIntegerType;
import org.ptolemy.fmi.type.FMIRealType;
import org.ptolemy.fmi.type.FMIStringType;
import org.ptolemy.fmi.type.FMIType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

import ac.soton.multisim.AbstractVariable;
import ac.soton.multisim.Component;
import ac.soton.multisim.ComponentDiagram;
import ac.soton.multisim.DisplayComponent;
import ac.soton.multisim.Port;
import ac.soton.multisim.VariableCausality;
import ac.soton.multisim.VariableType;
import de.prob.cosimulation.FMU;

/**
 * @author vitaly
 * @custom
 */
public class SimulationUtil {
	
	public static final String OUTPUT_SEPARATOR = ",";
	
	/**
	 * Returns Event-B Root element of a machine.
	 * @param machine
	 * @return
	 */
	public static IEventBRoot getMachineRoot(Machine machine) {
		Resource resource = machine.eResource();
		if (resource != null && resource.isLoaded()) {
			IFile file = WorkspaceSynchronizer.getFile(resource);
			IRodinFile rodinFile = RodinCore.valueOf(file);
			if (rodinFile != null)
				return (IEventBRoot) rodinFile.getRoot();
		}
		return null;
	}

	/**
	 * Returns FMI value from provided Event-B (string) value.
	 * NOTE: As doubles are not yet supported by Event-B,
	 * they are modelled as integers, converted from a double
	 * with a specified precision: double = int / (10 ^ precision)
	 * 
	 * @param value 
	 * @param type
	 * @param intToReal integer to real conversion precision
	 * @return
	 */
	public static Object getFMIValue(String value, VariableType type, int intToReal) {
		switch (type) {
		case BOOLEAN:
			return Boolean.valueOf(value);
		case INTEGER:
			return Integer.valueOf(value);
		case REAL:
			Integer integer = Integer.valueOf(value);
			return integer.doubleValue() / Math.pow(10d, intToReal);
		case STRING:
		default:
			return value;
		}
	}

	/**
	 * Returns Event-B value from the provided FMI value.
	 * intToReal precision is required for the Real type.
	 * 
	 * @param value
	 * @param type
	 * @param intToReal
	 * @return
	 */
	public static String getEventBValue(Object value, VariableType type,
			int intToReal) {
		switch (type) {
		case BOOLEAN:
			assert value instanceof Boolean;
			return value.toString().toUpperCase();
		case INTEGER:
			assert value instanceof Integer;
			return value.toString();
		case REAL:
			assert value instanceof Double;
			double toB = ((Double) value).doubleValue() * Math.pow(10d, intToReal);
			return Integer.toString((int) toB);
		case STRING:
			assert value instanceof String;
			return (String) value;
		default:
			return null;
		}
	}

	/**
	 * @param fmu
	 * @param variable
	 * @param value
	 */
	public static void fmiSet(FMU fmu, AbstractVariable variable, Object value) {
		String name = variable.getName();
		switch (variable.getType()) {
		case BOOLEAN:
			fmu.set(name, (Boolean) value);
			break;
		case INTEGER:
			fmu.set(name, (Integer) value);
			break;
		case REAL:
			fmu.set(name, (Double) value);
			break;
		case STRING:
			fmu.set(name, (String) value);
			break;
		}
	}

	/**
	 * @param fmu
	 * @param variable
	 * @return
	 */
	public static Object fmiGet(FMU fmu, AbstractVariable variable) {
		String name = variable.getName();
		Object value = null;
		switch (variable.getType()) {
		case BOOLEAN:
			value  = fmu.getBoolean(name) == false; //XXX: hack to fix the bug in JFMI (returns a negated value of the actual boolean value)
			break;
		case INTEGER:
			value = fmu.getInt(name);
			break;
		case REAL:
			value = fmu.getDouble(name);
			break;
		case STRING:
			value = fmu.getString(name);
			break;
		}
		return value;
	}

	/**
	 * Returns component metamodel's variable type
	 * that corresponds to FMI scalar variable.
	 * Returns String type as default.
	 * 
	 * @param scalarVariable
	 * @return
	 */
	public static VariableType fmiGetType(FMIScalarVariable scalarVariable) {
		FMIType type = scalarVariable.type;
		if (type instanceof FMIRealType) {
			return VariableType.REAL;
		} else if (type instanceof FMIIntegerType) {
			return VariableType.INTEGER;
		} else if (type instanceof FMIBooleanType) {
			return VariableType.BOOLEAN;
		} else {
			return VariableType.STRING;
		}
	}
	
	/**
	 * Returns default value of the FMI scalar variable,
	 * or null if not set.
	 * 
	 * @param scalarVariable
	 * @return
	 */
	public static Object fmiGetDefaultValue(FMIScalarVariable scalarVariable) {
		FMIType type = scalarVariable.type;
		Object value = null;
		if (type instanceof FMIRealType) {
			value = ((FMIRealType) type).start;
		} else if (type instanceof FMIIntegerType) {
			value = ((FMIIntegerType) type).start;
		} else if (type instanceof FMIBooleanType) {
			value = ((FMIBooleanType) type).start;
		} else if (type instanceof FMIStringType) {
			value = ((FMIStringType) type).start;
		}
		return value;
	}

	/**
	 * Returns components metamodel's variable causality that
	 * corresponds to provided FMI scalar variable's causality.
	 * 
	 * @param scalarVariable
	 * @return causality
	 */
	public static VariableCausality fmiGetCausality(FMIScalarVariable scalarVariable) {
		switch (scalarVariable.causality) {
		case internal: return VariableCausality.INTERNAL;
		case input: return VariableCausality.INPUT;
		case output: return VariableCausality.OUTPUT;
		default: return VariableCausality.NONE;
		}
	}
	
	/**
	 * Creates an output file writer.
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static BufferedWriter createOutputWriter(File file) throws IOException {
		if (!file.exists())
			file.createNewFile();
		return new BufferedWriter(new FileWriter((File) file));
	}

	/**
	 * Writes diabgram variable columns to the output file writer.
	 * 
	 * @param diagram
	 * @param writer
	 * @throws IOException 
	 */
	public static void writeColumns(ComponentDiagram diagram, BufferedWriter writer) throws IOException {
		writer.write("time");
		for (Component c : diagram.getComponents()) {
			//XXX: current hack to ignore display component for outputs
			if (c instanceof DisplayComponent)
				continue;
			
			String name = c.getName();
			for (Port p : c.getOutputs())
				writer.write(OUTPUT_SEPARATOR + name + "." + p.getName());
		}
		writer.write('\n');
	}

	/**
	 * Writes diagram variable values at specific simulation time to the output file writer.
	 * 
	 * @param diagram
	 * @param time
	 * @param writer
	 * @throws IOException 
	 */
	public static void writeOutput(ComponentDiagram diagram, long time, BufferedWriter writer) throws IOException {
		writer.write(Double.toString(time/1000.0));
		for (Component c : diagram.getComponents()) {
			//XXX: current hack to ignore display component for outputs
			if (c instanceof DisplayComponent)
				continue;
			
			for (Port p : c.getOutputs()) {
				writer.write(OUTPUT_SEPARATOR + toPlotValue(p.getValue().toString()));
			}
		}
		writer.write('\n');
	}

	/**
	 * Converts boolean string to integer string for plotting.
	 * @param value
	 * @return
	 */
	private static String toPlotValue(String value) {
		assert value != null;
		if ("false".equals(value.toLowerCase()))
			return "0";
		if ("true".equals(value.toLowerCase()))
			return "1";
		return value;
	}
}
