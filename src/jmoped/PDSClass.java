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

import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.structures.constants.ConstantClassInfo;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PDSClass {
	
	static Logger logger = Logger.getLogger(PDSClass.class);
	
	private ClassFile classFile;
	PDSInfo pdsInfo;

	private String thisClassName;
	private String superClassName;
	private String[] interfaceName;
	private List<PDSField> pdsStaticFieldList = new ArrayList<PDSField>();
	private List<PDSField> pdsNonStaticFieldList = new ArrayList<PDSField>();
	private List<PDSMethod> pdsMethodList;
	
	private int index;

/**************************CONSTRUCTOR RELATED*************************/
/*&*/
	public PDSClass (ClassFile classFile, PDSInfo pdsInfo) throws InvalidByteCodeException 
	{
		
		logger.info("Entering PDSClass constructor");
		
		this.classFile = classFile;
		this.pdsInfo = pdsInfo;
		
		String className = classFile.getThisClassName();
		
		thisClassName = formatClassName(className);
		logger.info("\tthisClassName=" + thisClassName);
		
		superClassName = formatClassName(classFile.getSuperClassName());
		logger.info("\tsuperClassName=" + superClassName);
		
		initInterfaceName(classFile);
		
		initPDSFieldList(classFile, pdsInfo);
		
		//pdsInfo.buildFieldIndexTable(this);
		//pdsInfo.buildClassSizeTable(this);
		//initPDSMethodList(pdsInfo);
		
		logger.info("Leaving PDSClass constructor");
	}
/*&*/
	public static String formatClassName(String name) {
		// TODO review
//		return (name == null) ? null : name.replace('/', '_');
		return (name == null) ? null : PDSMethod.formatMethodName(name);
		// TODO refactor, it basically 
	}
/*&*/
	private void initInterfaceName(ClassFile cf) 
			throws InvalidByteCodeException {
		
		int[] name = cf.getInterfaces();
		ConstantClassInfo ccInfo;
		
		interfaceName = new String[name.length];
		for (int i = 0; i < name.length; i++) {
			
			logger.debug("\tinterface index = " + name[i]);
			ccInfo = (ConstantClassInfo) cf.getConstantPoolEntry(name[i], 
					ConstantClassInfo.class);
			interfaceName[i] = ccInfo.getName();
			logger.info("\timplements " + interfaceName[i]);
		}
	}
/*&*/
	private void initPDSFieldList(ClassFile cf, PDSInfo pdsInfo) 
		throws InvalidByteCodeException
	{	
		
		PDSField pdsField;
		FieldInfo[] fieldInfo;
		String fieldName;
		
		while (cf != null) {
			fieldInfo = cf.getFields();
			for (int i = 0; i < fieldInfo.length; i++) {
				
				fieldName = fieldInfo[i].getName();
				if (pdsInfo.isIgnoredField(fieldName)) {
					continue;
				}
				
				if (!hasField(fieldName)) {
					pdsField = new PDSField(thisClassName, fieldInfo[i], pdsInfo);
					
					if (pdsField.isStatic()) {
						pdsStaticFieldList.add(pdsField);
					} else {
						pdsNonStaticFieldList.add(pdsField);
					}
				}
			}
			
			cf = pdsInfo.getStoredClassFile(cf.getSuperClassName());
		}
	}
/*&*/
	public boolean hasField(String fieldName) {
	
		PDSField pdsField = getField(fieldName);
		return (pdsField != null) ? true : false;
	}
/*&*/
	PDSField getField(String fieldName) {
		
		for (PDSField pdsField : pdsStaticFieldList)
			if (pdsField.getName().equals(fieldName))
				return pdsField;
		
		for (PDSField pdsField : pdsNonStaticFieldList)
			if (pdsField.getName().equals(fieldName))
				return pdsField;
		
		return null;
	}
/***********************************************************************/
/**************************initPDSMethodList*********************************/
/*&*/
	void initPDSMethodList(PDSInfo pdsInfo) throws InvalidByteCodeException,
		IOException
	{
		
		logger.debug("Entering initPDSMethodList of class=" + thisClassName);
		
		List<String> methodList = pdsInfo.getIncludedMethods(classFile.getThisClassName());
		MethodInfo[] methodInfo = classFile.getMethods();
				
		pdsMethodList = new ArrayList<PDSMethod>();
		for (int i = 0; i < methodInfo.length; i++) {
			String nameAndType = methodInfo[i].getName() + methodInfo[i].getDescriptor();
			if (methodList.contains(nameAndType)) {
				pdsMethodList.add(new PDSMethod(classFile, methodInfo[i], pdsInfo));
			}
		}
	}
/********************************************************************/
	
	public void setIndex(int index) {
		
		this.index = index;
	}
	
	public int getIndex() {
		
		return index;
	}
/*&*/
	public void translate() throws InvalidByteCodeException {
		
		for (PDSMethod pdsMethod : pdsMethodList) {
			pdsMethod.translate();
		}
	}
	
	public String getFormattedClassName() {
		
		return thisClassName;
	}
	
	public String getFormattedSuperClassName() {
		
		return superClassName;
	}
	
	public String[] getInterfaceName() {
		
		return interfaceName;
	}
	
	/**
	 * Expects the full method signature including parameters of the method.
	 * <p>
	 * Examples:
	 * <pre>
	 * main([java/lang/String;)V for main method
	 * </pre>
	 * <p>
	 * <code>name</code> can for example be constructed using 
	 * {@link ClassMember#getName()} + {@link ClassMember#getDescriptor()}.
	 * @return null if the method was not found or there was a bytecode
	 * exception
	 */	
/*&*/
	public PDSMethod getMethod(String name, String des) {

		
		PDSMethod pdsMethod;
		
		for (int i = 0; i < pdsMethodList.size(); i++) {
			
			pdsMethod = pdsMethodList.get(i);
			if (pdsMethod.isNameAndDescriptor(name, des)) {
				return pdsMethod;
			}
		}
		
		return null;
	}

	/**
	 * Added for testing purposes.
	 * @param index
	 * @return
	 */
/*&*/
	PDSMethod getMethod(int index)
	{
		return pdsMethodList.get(index);
	}
	
	/**
	 * Added for testing purposes.
	 * @return an unmodifiable list with elements of type {@link PDSMethod}.
	 */
	public List<PDSMethod> getMethodList() {
		return Collections.unmodifiableList(pdsMethodList);
	}
	
	int getFieldBit(String fieldName) throws InvalidByteCodeException {
		
		PDSField pdsField = getField(fieldName);
		return (pdsField != null) ? pdsField.getBits() : 0;
	}
	
	/**
	 * 
	 * @return an unmodifiable list with elements of type {@link PDSField}.
	 */
	public List<PDSField> getStaticFieldList() 	{
		return Collections.unmodifiableList(pdsStaticFieldList);
	}
	
	/**
	 * Returns the static initializer method of this class if this class 
	 * has one, otherwise <code>null</code>.
	 */
	public PDSMethod getMethodClinit() {

		return getMethod("<clinit>", "()V");

	}
	
	/**
	 * Returns the static main method if this class has one otherwise 
	 * <code>null</code>.
	 */
	public PDSMethod getMethodMain() {
		
		return getMethod("main", "([Ljava/lang/String;)V");
	}
	
	public String getName() {
		
		try {
			return classFile.getThisClassName();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		
		logger.error("Error in jclasslib");
		return null;
	}

	/**
	 * Returns the list of instance fields of this class.
	 * <p>
	 * The elements are of type {@link PDSField} and the list is unmodifiable. 
	 */
/*&*/
	public List<PDSField> getNonStaticFieldList() {
		return Collections.unmodifiableList(pdsNonStaticFieldList);
	}
	
	public String getSuperName() {
		
		try {
			return classFile.getSuperClassName();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		
		logger.error("Error in jclasslib");
		return null;
	}

	public boolean isName(String name) {
		
		String className = null;
		
		try {
			className = classFile.getThisClassName();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		
		return className.equals(name);
	}
	
	/**
	 * Returns the number of instance fields in this class.
	 */
	public int size() throws InvalidByteCodeException {
		
		int count = 0;
		for (PDSField pdsField : pdsNonStaticFieldList)
			count += pdsField.getBits();
		
		return (int) Math.ceil(((float) count)/((float) pdsInfo.getBits()));
		//return pdsNonStaticFieldList.size();
	}
	
	/**
	 * Returns a string containing the declaration of all static fields and 
	 * all methods of this class in Remopla syntax.
	 * @return
	 * @throws InvalidByteCodeException
	 */
	public String toRemoplaGlobal() throws InvalidByteCodeException {
		
		StringBuffer out = new StringBuffer();
		
		for (PDSField pdsField : pdsStaticFieldList) {
			out.append(pdsField.toRemopla());
			/*added by suncong*/
			out.append(pdsField.toRemoplaPair());
			/*******************/
		}
		
		for (PDSMethod pdsMethod : pdsMethodList) {
			out.append(pdsMethod.toRemoplaHead());
			out.append(";\n");
			/*added by suncong*/
			out.append(pdsMethod.toRemoplaHeadPair());
			out.append(";\n");
			/*******************/
		}
		return out.toString();
	}
	/**
	 * Returns a string that contains the definitions of all the methods in
	 * this class.
	 * @return
	 * @throws InvalidByteCodeException
	 */
	public String toRemoplaModule() throws InvalidByteCodeException {
		
		PDSMethod pdsMethod;
		StringBuffer out = new StringBuffer();
		
		for (int i = 0; i < pdsMethodList.size(); i++) {
			
			pdsMethod = pdsMethodList.get(i);
			out.append(pdsMethod.toRemopla());
			out.append("\n");
			/* added by suncong*/
			out.append(pdsMethod.toRemoplaPair()).append("\n");
		}
		
		return out.toString();
	}
	

	/**
	 * Added for testing purposes, returns {@link #getName()};
	 */
//	@Override
	public String toString()
	{
		return getName();
	}
	
	/**
	 * Two classes are considered equal when their classnames are the 
	 * same.
	 */
	//@Override
	public boolean equals(Object o) {
		if (o instanceof PDSClass) {
			return thisClassName.equals(((PDSClass)o).thisClassName);
		}
		return false;
	}
	
//	@Override
	public int hashCode() {
		return thisClassName.hashCode();
	}
	
	/*******************************************************************/
	/**                      PRIVATE METHODS                          **/
	/*******************************************************************/
	public ClassFile getClassFile()
	{
		return classFile;
	}

}
