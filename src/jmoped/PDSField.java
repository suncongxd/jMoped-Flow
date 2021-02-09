
package jmoped;


import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.structures.elementvalues.ConstElementValue;
import org.apache.log4j.Logger;

import jmoped.annotation.PDSAnnotationUtils;

/**
 * Field information for the pushdown system.
 */
public class PDSField {
	
	static Logger logger = Logger.getLogger(PDSField.class);

	//private String className;
	private FieldInfo fieldInfo;
	private String fieldName;
	private PDSInfo pdsInfo;
	
	private int bits;
	private int index;

	public PDSField(String className, FieldInfo fieldInfo, PDSInfo pdsInfo) 
			throws InvalidByteCodeException
	{
		
		//this.className = className;
		this.fieldInfo = fieldInfo;
		this.pdsInfo = pdsInfo;
		fieldName = className + "_" + formatFieldName(fieldInfo.getName());
		logger.info("\t\tfieldName=" + fieldName);
		
		if (isBoolean()) bits = 1;
		else bits = 0;
	}
	
	/**
	 * Formats the field name to the one legal to Moped.
	 *
	 * @param field the field name.
	 * @return the formatted field name.
	 */
/*&*/
	public static String formatFieldName(String name) {
		
		return name.replaceAll("[./$]", "_");
	}
/*&*/
	public static String formatFieldName(String[] name) {
		
		return formatFieldName(name[0], name[1]);
	}
/*&*/
	public static String formatFieldName(String name1, String name2) {
		
		return PDSClass.formatClassName(name1) + "_"  + formatFieldName(name2);
	}
/*&*/
	public boolean isBoolean() {
		
		String descriptor = null;
		
		try {
			descriptor = fieldInfo.getDescriptor();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		
		return descriptor.equals("Z");
	}

	/**
	 * Sets the number of bits which should be used for this field.
	 * 
	 * @param 0 resets the user choice and honors the annotation or default bits
	 * 
	 * @throws IllegalStateException if the field is not static, instance fields do
	 * not support variable bit numbers
	 */
/*& used only in PDSInfo.buildFieldTable*/
	public void setFieldBits(int bits) 
	{
		/*if (!isStatic()) {
			throw new IllegalStateException("Bits can only be set on static fields");
		}*/
		this.bits = bits;
	}
	
	public String getName() {
		
		try {
			return fieldInfo.getName();
		} catch (InvalidByteCodeException e) {
			e.printStackTrace();
		}
		
		logger.fatal("Error in jclasslib");
		return null;
	}
	
	public String getFormattedName() {
		
		return fieldName;
	}
		
	public boolean isName(String name) {
		
		return getName().equals(name);
	}
	
	/**
	 * Returns the underlying field info object.
	 * @return
	 */
	public FieldInfo getFieldInfo() {
		return fieldInfo;
	}
	
	/**
	 * Returns the remopla code for a static field honoring the 
	 * {@link de.uni_stuttgart.fmi.szs.jmoped.annotation.PDSFieldBits}.
	 * 
	 * @throws InvalidByteCodeException
	 * @throws IllegalStateException if this is called on a non-static field
	 */
/*&*/
	public String toRemopla() throws InvalidByteCodeException
	{
		if (!isStatic()) {
			throw new IllegalStateException("Should only be called on static methods");
		}
		
		StringBuilder builder = new StringBuilder("int ");
		builder.append(getFormattedName());
		
		if (hasExtraBits()) {
			builder.append("(");
			builder.append(getBits());
			builder.append(")");
		}
		
		builder.append(";\n");
		return builder.toString();
	}
	
	/*added by suncong*/
	public String toRemoplaPair() throws InvalidByteCodeException{
		if(!isStatic()){
			throw new IllegalStateException("Should only be called on static methods");
		}
		StringBuilder builder=new StringBuilder("int ");
		builder.append(pdsInfo.generatePairLabel(getFormattedName(),""));
		
		if (hasExtraBits()) {
			builder.append("(");
			builder.append(getBits());
			builder.append(")");
		}
		
		builder.append(";\n");
		return builder.toString();
	}
	/*****************/

/*&*/
	public int getBits() throws InvalidByteCodeException
	{
		
		// see if there are user set bits
		if (this.bits > 0) {
			return bits;
		}
		
		ConstElementValue bitsValue = PDSAnnotationUtils.getFieldBits(fieldInfo);
		if (bitsValue == null)
			return pdsInfo.getBits();
		int bits = PDSAnnotationUtils.getBitCount(bitsValue, fieldInfo.getClassFile());
		return bits == 0 ? pdsInfo.getBits() : bits;
	}
	
	private boolean hasExtraBits() throws InvalidByteCodeException
	{
		return isBoolean() || bits > 0 || PDSAnnotationUtils.getFieldBits(fieldInfo) != null;
	}
	
	public void setIndex(int index) {
		
		this.index = index;
	}
	
	public int getIndex() {
		
		return index;
	}
	
	public boolean isStatic()
	{
		return isStatic(fieldInfo.getAccessFlags());
	}
	
	/**
	 * Checks if the given access flag is static.
	 *
	 * @return <code>true</code> if static.
	 */
	public static boolean isStatic(int flag) {
		
		return (flag & AccessFlags.ACC_STATIC) != 0;
	}
	
	/**
	 * Returns true if the field descriptor denotes a reference type, i.e. all
	 * objects and all arrays.
	 */
	public static boolean isReference(String descriptor)
	{
		return descriptor.startsWith("L") || descriptor.startsWith("[");
	}
	
}
