/*
    Copyright (C) 2005 Dejvuth Suwimonteerabuth

    This file is part of jMoped.

    jMoped is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    jMoped is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with jMoped; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package jmoped;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.gjt.jclasslib.bytecode.AbstractInstruction;
import org.gjt.jclasslib.bytecode.Opcodes;
import org.gjt.jclasslib.io.ByteCodeReader;
import org.gjt.jclasslib.structures.AccessFlags;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;
import org.gjt.jclasslib.structures.attributes.LineNumberTableAttribute;
import org.gjt.jclasslib.structures.attributes.LineNumberTableEntry;
import org.gjt.jclasslib.structures.attributes.LocalVariableTableAttribute;
import org.gjt.jclasslib.structures.attributes.LocalVariableTableEntry;
import org.gjt.jclasslib.structures.elementvalues.ElementValue;

import jmoped.annotation.PDSAnnotationUtils;

import jmoped.automata.RecordStruct;
import jmoped.automata.Automata;

/**
 * Method information for the pushdown system.
 */
public class PDSMethod {
	
	static Logger logger = Logger.getLogger(PDSMethod.class);
	
	private ClassFile classFile;
	private MethodInfo methodInfo;
	private PDSInfo pdsInfo;

	private String methodName;

	private ElementValue[] annotatedBits;
	
	private CodeAttribute codeAttr;
	private List<AbstractInstruction> instList = Collections.emptyList();
	//private List<String> paramList;
	private List<PDSLocalVar> pdsLocalVarList;
	private List<PDSInst> pdsInstList;

	private int paramCount = -1;
	
	private String humanMethodName = null;
	
	private String className = null;

	public PDSMethod(ClassFile classFile, MethodInfo methodInfo, PDSInfo pdsInfo)
		throws InvalidByteCodeException, IOException 
	{
		this.classFile = classFile;
		this.methodInfo = methodInfo;
		this.pdsInfo = pdsInfo;
		
		String[] name = new String[3];
		name[0] = classFile.getThisClassName();
		name[1] = methodInfo.getName();
		name[2] = methodInfo.getDescriptor();
		methodName = PDSMethod.formatMethodName(name);
		
		logger.debug("Initializing PDSMethod methodName=" + methodName);
		
		//initParamList();
		annotatedBits = PDSAnnotationUtils.getParameterBitsArray(methodInfo);

		codeAttr = (CodeAttribute)methodInfo.findAttribute(CodeAttribute.class);

		if (codeAttr == null) {
			initPDSInstListForNative();
		} else {
			instList = (List<AbstractInstruction>) 
						ByteCodeReader.readByteCode(codeAttr.getCode());
			initPDSInstList();
		}
		
		initLocalVarList();
		
		newPosList=new ArrayList<RecordStruct>();//added by suncong
	}
	/**************** field and constructor added by suncong, used to discard PDSInstOld*******************/
	int maxStackSize;
	PDSMethod(String formattedName, int max_stack, ClassFile class_file){
		methodName=formattedName;
		maxStackSize=max_stack;
		classFile=class_file;
		newPosList=new ArrayList<RecordStruct>();
	}
	/*added by suncong, the PDSInstList returned is used in Automata.TraceMethodsBeforeTarget to trace the
	 *instructions*/
	 public List<PDSInst> getPDSInstList(){
	 	return this.pdsInstList;
	 }
	 /********fields by suncong to record the position of 'new' structs of each method*/
	 List<RecordStruct> newPosList;
	 public void addNewPosRecord(RecordStruct rs){
	 	newPosList.add(rs);
	 }
	 public List<RecordStruct> getNewPosList(){
	 	return newPosList;
	 }
	 public void generateNewPosList() throws InvalidByteCodeException {
		List<RecordStruct> rslist=new ArrayList<RecordStruct>();
	 	Automata.AutomataForNewSeq(pdsInstList,rslist,classFile);
	 	newPosList.addAll(rslist);
	 }
	 public void printNewPosList(){
	 	System.out.println("***** new structure position list for method "+this.getFormattedName()+"******");
	 	for(RecordStruct rs: newPosList){
	 		System.out.println("<"+rs.getStartPos()+","+rs.getEndPos()+">");
	 	}
	 	System.out.println("***************************************");
	 }
	/*********************************************************/
/*&*/
	private void initPDSInstListForNative() throws 
			InvalidByteCodeException {
		
		String descriptor = methodInfo.getDescriptor();
		PDSInst pdsInst;
		
		pdsInstList = new ArrayList<PDSInst>();
		if (isVoid(descriptor)) {
			pdsInst = new PDSInst(this, pdsInfo);
			pdsInst.addInst("return","return");
			pdsInstList.add(pdsInst);
		} else {
			String retVar = pdsInfo.varIndex(countParam(descriptor));
			pdsInst = new PDSInst(this, pdsInfo);
			pdsInst.addInst(retVar + "=undef", retVar+"=undef");
			pdsInstList.add(pdsInst);
			
			pdsInst = new PDSInst(this, pdsInfo);
			pdsInst.addInst("return " + retVar, "return "+retVar);
			pdsInstList.add(pdsInst);
		}
	}
/*&*/
	private void initPDSInstList() throws InvalidByteCodeException {
		
		logger.debug("Entering initPDSInstList() of method: " + methodName);
		
		pdsInstList = new ArrayList<PDSInst>();
		
		for (Iterator<AbstractInstruction> i = instList.iterator(); i.hasNext();) {
			AbstractInstruction inst = i.next();
			
			pdsInstList.add(new PDSInst(this, inst, pdsInfo));
			
			if (isAssertionErrorConstructor(inst)) {
				// ignore the throw instruction after an assertion error constructo
				// since it is never reached
				if (i.hasNext()) {
					inst = i.next();
					if (inst.getOpcode() != Opcodes.OPCODE_ATHROW) {
						pdsInstList.add(new PDSInst(this, inst, pdsInfo));
					}
				}
			}
		}
		
	}
/*&*/
	boolean isAssertionErrorConstructor(AbstractInstruction inst)
	throws InvalidByteCodeException
	{
		if (inst.getOpcode() == Opcodes.OPCODE_INVOKESPECIAL) {
			//String[] calledName = PDSInstOld.getReferencedName(classFile, inst); discard PDSInstOld
			String[] calledName = PDSInst.getReferencedName(classFile, inst);
			if (calledName[0].equals("java/lang/AssertionError")
					&& calledName[1].equals("<init>")) {
				return true;
			}
		}
		return false;
	}
/*&*/
	private void initLocalVarList() throws InvalidByteCodeException {
		
		pdsLocalVarList = new ArrayList<PDSLocalVar>();
		
		if (codeAttr == null) {
			
			addLocalVarListFromDescriptor();
			return;
		}
		
		PDSLocalVar pdsLocalVar;
		int bits = pdsInfo.getBits();
		int varBits;
		LocalVariableTableAttribute lvtAttr = (LocalVariableTableAttribute)
				codeAttr.findAttribute(LocalVariableTableAttribute.class);
		if (lvtAttr != null) {
			
			LocalVariableTableEntry[] lvtEntry = lvtAttr.getLocalVariableTable();
			for (int i = 0; i < lvtEntry.length; i++) {
				
				int varIndex = lvtEntry[i].getIndex();
				if (annotatedBits != null && varIndex < annotatedBits.length) {
					varBits = PDSAnnotationUtils.getBitCount(
							annotatedBits[varIndex], classFile);
				} else {
					varBits = 0;
				}
				String varName = pdsInfo.varIndex(varIndex);
				pdsLocalVar = new PDSLocalVar(lvtEntry[i], varName, varBits);
				pdsLocalVarList.add(pdsLocalVar);
				logger.debug("Local Var: " + pdsLocalVar.getName() 
						+ "(" + pdsLocalVar.getBits() + ")");
			}
		} else {
			
			logger.debug("LocalVariableTableAttribute is null");
			for (int i = 0; i < codeAttr.getMaxLocals(); i++) {
				
				if (annotatedBits != null && i < annotatedBits.length) {
					varBits = PDSAnnotationUtils.getBitCount(annotatedBits[i], 
							classFile);
				} else {
					varBits = 0;
				}
				pdsLocalVar = new PDSLocalVar(pdsInfo.varIndex(i), varBits);
				pdsLocalVarList.add(pdsLocalVar);
			}
				
		}
		
		logger.debug("initLocalVarTable completed");
	}
/*&*/
	private void addLocalVarListFromDescriptor() 
			throws InvalidByteCodeException {
		
		int paramCount = getParamCount();
		if (!isStatic()) {
			paramCount++;
		}
		PDSLocalVar pdsLocalVar;
		
		for (int i = 0; i < paramCount; i++) {
			
			pdsLocalVar = new PDSLocalVar(pdsInfo.varIndex(i));
			pdsLocalVarList.add(pdsLocalVar);
		}
	}
/*&*/
	public int getParamCount()
	{
		if (paramCount == -1) {
			String desc = getDescriptor();
			if (desc != null) {
				paramCount = PDSMethod.countParam(desc);
			}
		}
		return paramCount;
	}
/*&*/
	public String getDescriptor() {
		
		String methodDes = null;
		
		try {
			methodDes = methodInfo.getDescriptor();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		
		return methodDes;
	}
/*&*/
	public boolean isStatic() {
		
		return (methodInfo.getAccessFlags() & AccessFlags.ACC_STATIC) != 0;
	}
/*&*/
	public void translate() throws InvalidByteCodeException {
		
		for (PDSInst pdsInst : pdsInstList) {
			pdsInst.translate();
		}
	}
	
	/** Dirty hack to init heap pointer. To be fixed. 
	 * TODO 
	 */
	/*public void addInitPtr() {
		
		PDSInst pdsInst = (PDSInst) pdsInstList.get(0);
		pdsInst.addInitPtr();
	}*/
	
	public void replaceLastInst(String st,String st_pair) {
		
		PDSInst pdsInst = pdsInstList.get(pdsInstList.size() - 1);
		pdsInst.replaceInst(st,st_pair);
	}
	
	public ClassFile getClassFile() {
		
		return classFile;
	}
	
	public String getClassName()
	{
		if (className == null) {
			try {
				className = getMethodInfo().getClassFile().getThisClassName();
			}
			catch (InvalidByteCodeException ive) {
				// TODO not correct
				className = getFormattedName();
			}
		}
		return className;
	}
	
	public String getHumanReadableName()
	{
		if (humanMethodName == null) {
			ClassFile classFile = getMethodInfo().getClassFile();
			try {
				String className = classFile.getThisClassName();
				int lastIndex = className.lastIndexOf('/');
				if (lastIndex > 0) {
					className = className.substring(lastIndex + 1, className.length());
				}
				humanMethodName = className.replace('$', '.');
				String name = getName();
				if (name.equals("<init>")) {
					humanMethodName += "." 
						+ humanMethodName.substring(humanMethodName.lastIndexOf('.') + 1);
				}
				else {
					humanMethodName += "." + name;
				}
			}
			catch (InvalidByteCodeException ive) {
				humanMethodName = getFormattedName();
			}
		}
		return humanMethodName;
	}
	
	/**
	 * Returns the number of parameters given the method's descriptor in 
	 * the JVM standard.
	 *
	 * @param descriptor the method descriptor.
	 * @return the number of parameters.
	 */
/*&*/
	public static int countParam(String descriptor) {
		
		int count = 0;
		String param = descriptor.substring(1, descriptor.indexOf(")"));
		
		for (int i = 0; i < param.length(); i++) {
			if (param.charAt(i) == '[') {
				continue;
			}
			if (param.charAt(i) == 'L') {
				while (param.charAt(i) != ';') {
					i++;
				}
			}
			count++;
		}
		
		return count;
	}
	
	/**
	 * Returns true if the parameter at <code>index</code> is an array.
	 * <p>
	 * Indexes begin at zero and are smaller than {@link #getParamCount()}.
	 * @param descriptor
	 * @param index
	 * @return
	 */
	public static boolean isArray(String descriptor, int index)
	{
		int count = 0;
		char[] array = descriptor.substring(1, descriptor.indexOf(")")).toCharArray();
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '[') {
				if (count == index) {
					return true;
				}
				else {
					while (i + 1 < array.length && array[i+1] == '[') {
						i++;
					}
					continue;
				}
			}
			if (array[i] == 'L') {
				while (array[i] != ';') {
					i++;
				}
			}
			count++;
			if (count > index) {
				break;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the descriptor returns a reference
	 * @param descriptor
	 * @return
	 */
	public static boolean returnsReference(String descriptor)
	{
		return descriptor.indexOf(")L") != -1 || descriptor.indexOf(")[") != -1;
	}
	
	/**
	 * Returns true if the parameter at <code>index</code> is an array.
	 * <p>
	 * Indexes begin at zero and are smaller than {@link #getParamCount()}.
	 */
	public boolean isParamArray(int index)
	{
		String desc = getDescriptor();
		if (desc != null) {
			return PDSMethod.isArray(desc, index);
		}
		return false;
	}
		
	/**
	 * Returns the info object responsible for this method.
	 */
	public PDSInfo getInfo()
	{
		return pdsInfo;
	}
	
	public static String formatMethodName(String name) {
		
		String out = name;
		
		out = out.replaceAll("[()/;$]", "_");
		out = out.replace('[', 'A');
		out = out.replace('.', '_');
		out = out.replaceAll("_{2,}", "_");
		out = out.replaceAll("[<>]", "_");
		if (out.endsWith("_"))
			out = out.substring(0, out.length()-1);
		if (out.startsWith("_"))
			out = out.substring(1, out.length());
		
		return out;
	}
	
	public static String formatMethodName(String[] name) {
		
		StringBuffer out = new StringBuffer();
		
		if (PDSInfo.simplified) {
			if (name[1].equals("<init>")) {
				out.append(PDSClass.formatClassName(name[0]));
			} else {
				out.append(formatMethodName(name[1]));
			}
		} else {
			out.append(formatMethodName(PDSClass.formatClassName(name[0]) 
					+ "_" + name[1] + "_" + name[2]));
		}
		
		return out.toString();
	}
	
	public String getFormattedName() {
		
		return methodName;
	}
		

/****************modified by suncong, in order to discard PDSInstOld*****************
	public int getMaxStack() {
		
		return codeAttr.getMaxStack();
	}*/
	public int getMaxStack() {
		if(codeAttr!=null)
			return codeAttr.getMaxStack();
		else
			return maxStackSize;
	}
	/************************************************/	
	
	/**
	 * Returns the method info object that represents this method.
	 * @return
	 */
	public MethodInfo getMethodInfo() {
		return methodInfo;
	}
	
	/**
	 * Returns the unmodifiable list of instructions of this method. 
	 * @return
	 */
	public List<AbstractInstruction> getInstructionList() {
		return Collections.unmodifiableList(instList);
	}
	
	public void addLocalVar(String name) {
		
		addLocalVar(name, 0);
	}
	
	/**
	 * Adds new variable name with size bits into pdsLocalVarList from external.
	 * The method does nothing if name is already exists in pdsLocalVarList.
	 * The variable is flagged as external.
	 * 
	 * @param name the name of the variable.
	 * @param bits the size of the variable
	 */
	public void addLocalVar(String name, int bits) {
		
		for (PDSLocalVar pdsLocalVar : pdsLocalVarList) {
			if (pdsLocalVar.getName().equals(name))
				return;
		}
		pdsLocalVarList.add(new PDSLocalVar(name, bits, true));
	}
	
	/**
	 * Returns a string which represents information inside pdsLocalVarList.
	 * 
	 * @return the info string.
	 */
	public String localVarInfo() {
		
		StringBuilder out = new StringBuilder();
		
		for (PDSLocalVar pdsLocalVar : pdsLocalVarList) {
			out.append("int ");
			out.append(pdsLocalVar.getName());
			out.append("(");
			out.append(pdsLocalVar.getBits());
			out.append(")");
			out.append(";\n");
		}
		
		return out.toString();
	}
	
	public int findLocalVarIndex(int var, int pc) {
		
		PDSLocalVar pdsLocalVar;
		
		for (int i = 0; i < pdsLocalVarList.size(); i++) {
			pdsLocalVar = pdsLocalVarList.get(i);
			if ((pdsLocalVar.getIndex() == var) 
					&& (pc > pdsLocalVar.getStartPc())
					&& (pc < pdsLocalVar.getLength())) {
				return i;
			}
		}
		
		return -1;
	}
	
	public boolean isLocalVarArray(int index) {
		
		int descIndex = pdsLocalVarList.get(index).getDescriptorIndex();
		String desc = null;
		try {
			desc = classFile.getConstantPoolUtf8Entry(descIndex).getString();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		if (isArray(desc, 0))
			return true;
		else
			return false;
	}
	
	public int getLocalVarBits(int index) {
		
		return pdsLocalVarList.get(index).getBits();
	}
	
	public int getMaxLocalVarBits(String name) {
		
		int max = 0;
		
		for (PDSLocalVar pdsLocalVar : pdsLocalVarList) {
			
			if (pdsLocalVar.getName().equals(name)) {
				if (pdsLocalVar.getBits() > max) {
					max = pdsLocalVar.getBits();
				}
			}
		}
		
		return (max == 0) ? pdsInfo.getBits() : max;
	}
	
	/*public int getParamVarBits(int index) throws InvalidByteCodeException {
		
		if (paramBits == null)
			return getMaxLocalVarBits(pdsInfo.varIndex(index));
		
		if (index >= paramBits.length) {
			return pdsInfo.getBits();
		}
		return PDSAnnotationUtils.getBitCount(paramBits[index], classFile);
	}*/
	
	/**
	 * @param name
	 * @return
	 */
/*&*/
	public boolean isNameAndDescriptor(String name, String des) {
		
		return getName().equals(name) && getDescriptor().equals(des);
	}
	/**
	 * Returns the name part of the method, this is not enough to uniquely
	 * identify it.
	 * 
	 * @return null if the name could not be retrieved from the underlying
	 * MethodInfo object
	 */
/*&*/
	public String getName() {
		
		String methodName = null;
		
		try {
			methodName = methodInfo.getName();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		
		return methodName;
	}
/*&*/
	public boolean isMainMethod() {
		return isNameAndDescriptor("main", "([Ljava/lang/String;)V");
	}

	public boolean isName(String name) {
		
		return getName().equals(name);
	}
		

	
	public int getBitsAtPc(int pc) {
		
		logger.debug("getBitsAtPc(" + pc + ")");
		LineNumberTableAttribute lntAttr = (LineNumberTableAttribute)
				codeAttr.findAttribute(LineNumberTableAttribute.class);
		
		// If for some reason LineNumberTable doesn't exist,
		// return the default value.
		if (lntAttr == null) return pdsInfo.getBits();
		
		LineNumberTableEntry[] lineNumberTable = lntAttr.getLineNumberTable();
		
		if (lineNumberTable == null) return pdsInfo.getBits();
		
		int i = 0;
		while (i < lineNumberTable.length 
				&& lineNumberTable[i].getStartPc() < pc) {
			i++;
		}
		
		return pdsInfo.getBitsAtLine(lineNumberTable[i-1].getLineNumber());
	}
	
	/**
	 * Overriden for testing purposes, returns {@link #getName()}.
	 */
	@Override
	public String toString()
	{
		return getName();
	}
/*&*/
	public boolean isVoid() {
		
		return isVoid(getDescriptor());
	}
	
	public static boolean isVoid(String descriptor) {
		
		return descriptor.endsWith("V");
	}
	
	/* original toRemopla() */
	public String toRemopla() throws InvalidByteCodeException {
	
		int paramCount = getParamCount();
		
		if (!isStatic()) {
			paramCount++;
		}
			
		PDSInst pdsInst;
		StringBuffer out = new StringBuffer();
		
		out.append(toRemoplaHead());
		out.append("\n{\n");
		if (codeAttr != null) {
			
			for (int i = paramCount; i < codeAttr.getMaxLocals(); i++) {
				
				String var = pdsInfo.varIndex(i);
				out.append("int ");
				out.append(var);
				out.append("(");
				out.append(getMaxLocalVarBits(var));
				out.append(")");
				out.append(";\n");
			}
			
			for (PDSLocalVar pdsLocalVar : pdsLocalVarList) {
				
				if (pdsLocalVar.isExternal()) {
					
					out.append("int ");
					out.append(pdsLocalVar.getName());
					out.append("(");
					if (pdsLocalVar.getBits() != 0)
						out.append(pdsLocalVar.getBits());
					else
						out.append(pdsInfo.getBits());
					out.append(")");
					out.append(";\n");
				}
			}
			for (int i = 0; i < codeAttr.getMaxStack(); i++) {
				
				out.append("int ");
				out.append(pdsInfo.stackIndex(i));
				out.append(";\n");
			}
		}
		// The case where the method is native but return something.
		// A local variable is needed to return undef.
		else if (!isVoid()) {
			out.append("int ");
			out.append(pdsInfo.varIndex(paramCount));
			out.append(";\n");
		}
		
		for (int i = 0; i < pdsInstList.size(); i++) {
			
			pdsInst = pdsInstList.get(i);
			out.append(pdsInst.toRemopla());
		}
		out.append("}\n");
		
		return out.toString();
	}
	/*added by suncong*/
	public String toRemoplaPair() throws InvalidByteCodeException {
	
		int paramCount = getParamCount();
		
		if (!isStatic()) {
			paramCount++;
		}
			
		PDSInst pdsInst;
		StringBuffer out = new StringBuffer();
		
		out.append(toRemoplaHeadPair()).append("\n{\n");
		if (codeAttr != null) {
			for (int i = paramCount; i < codeAttr.getMaxLocals(); i++) {
				String var = pdsInfo.varIndex(i);
				out.append("int ").append(var).append("(").append(getMaxLocalVarBits(var)).append(");\n");
			}
			
			for (PDSLocalVar pdsLocalVar : pdsLocalVarList) {
				
				if (pdsLocalVar.isExternal()) {
					out.append("int ").append(pdsLocalVar.getName()).append("(");
					if (pdsLocalVar.getBits() != 0)
						out.append(pdsLocalVar.getBits());
					else
						out.append(pdsInfo.getBits());
					out.append(");\n");
				}
			}
			for (int i = 0; i < codeAttr.getMaxStack(); i++) {
				out.append("int ").append(pdsInfo.stackIndex(i)).append(";\n");
			}
		}
		// The case where the method is native but return something.
		// A local variable is needed to return undef.
		else if (!isVoid()) {
			out.append("int ").append(pdsInfo.varIndex(paramCount)).append(";\n");
		}
		
		for (int i = 0; i < pdsInstList.size(); i++) {
			
			pdsInst = pdsInstList.get(i);
			out.append(pdsInst.toRemoplaPair());
		}
		out.append("}\n");
		
		return out.toString();
	}
	
	/* original toRemoplaHead()*/
	public String toRemoplaHead() throws InvalidByteCodeException {
		
		int paramCount = getParamCount();
		String var;
		
		if (!isStatic()) {
			paramCount++;
		}
			
		StringBuffer out = new StringBuffer();
		
		out.append("module ");
		if (isVoid()) {
			out.append("void ");
		} else {
			out.append("int ");
		}
		out.append(methodName);
		logger.debug(methodName + ", paramCount=" + paramCount);
		out.append("(");
		if (paramCount > 0) {
			
			var = pdsInfo.varIndex(0);
			out.append("int ");
			out.append(var);
			out.append("(");
			out.append(getMaxLocalVarBits(var));
			out.append(")");
			for (int i = 1; i < paramCount; i++) {
				
				var = pdsInfo.varIndex(i);
				out.append(", int ");
				out.append(var);
				out.append("(");
				out.append(getMaxLocalVarBits(var));
				out.append(")");
			}
		}
		out.append(")");
		
		return out.toString();
	}
	/*added by suncong*/
	public String toRemoplaHeadPair() throws InvalidByteCodeException {
		
		int paramCount = getParamCount();
		String var;
		
		if (!isStatic()) {
			paramCount++;
		}

		StringBuffer out = new StringBuffer();
		
		out.append("module ");
		if (isVoid()) {
			out.append("void ");
		} else {
			out.append("int ");
		}
		out.append(pdsInfo.generatePairLabel(methodName,""));//
		out.append("(");
		if (paramCount > 0) {
			var = pdsInfo.varIndex(0);
			String tmp="int "+ var+"("+String.valueOf(getMaxLocalVarBits(var))+")";
			out.append(tmp);
			for (int i = 1; i < paramCount; i++) {
				var = pdsInfo.varIndex(i);
				tmp=", int "+var+"("+String.valueOf(getMaxLocalVarBits(var))+")";
				out.append(tmp);
			}
		}
		out.append(")");
		
		return out.toString();
	}
	/*************/
}
