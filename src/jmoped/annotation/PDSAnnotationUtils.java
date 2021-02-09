package jmoped.annotation;

import org.gjt.jclasslib.structures.AbstractStructureWithAttributes;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.FieldInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.RuntimeInvisibleAnnotationsAttribute;
import org.gjt.jclasslib.structures.constants.ConstantIntegerInfo;
import org.gjt.jclasslib.structures.elementvalues.AnnotationElementValue;
import org.gjt.jclasslib.structures.elementvalues.ArrayElementValue;
import org.gjt.jclasslib.structures.elementvalues.ConstElementValue;
import org.gjt.jclasslib.structures.elementvalues.ElementValue;

/**
 * Static annotations utils class that provides methods for looking up jmoped
 * specific annotations and for retrieving their values.
*
* @see {@link de.uni_stuttgart.fmi.szs.jmoped.annotation.PDSParameterBits}
* @see {@link de.uni_stuttgart.fmi.szs.jmoped.annotation.PDSFieldBits}
 */
public class PDSAnnotationUtils 
{
	
	/**
	 * Returns the array of parameter bits, the array elements are of type
	 * {@link ConstElementValue}.
	 * 
	 * @param method the method whose parameter bits should be extracted
	 * @return null if there is no {@link PDSParameterBits} annotation.
	 */
/*&*/
	public static ElementValue[] getParameterBitsArray(MethodInfo method)
		throws InvalidByteCodeException
	{
		AnnotationElementValue[] annotations = getAnnotations(method);
		if (annotations == null) {
			return null;
		}
		
		AnnotationElementValue annotation = 
			getParameterBitsAnnotation(annotations, method.getClassFile());
		if (annotation == null) {
			return null;
		}
		
		return getBitsArray(annotation);
	}
	
	/**
	 * Returns the annotation bit of a field.
	 * 
	 * @param field the field whose bits should be extracted
	 * @return null if there is no {@link PDSFieldBits} annotation for this field
	 * @throws InvalidByteCodeException
	 */
/*&*/
	public static ConstElementValue getFieldBits(FieldInfo field) 
		throws InvalidByteCodeException
	{
		AnnotationElementValue[] annotations = getAnnotations(field);
		if (annotations == null) {
			return null;
		}

		AnnotationElementValue annotation = 
			getFieldBitsAnnotation(annotations, field.getClassFile());
		if (annotation == null) {
			return null;
		}
		
		ConstElementValue bitsValue = (ConstElementValue) annotation.getElementValuePairEntries()[0].getElementValue();
		return bitsValue;
	}
	
	/**
	 * Private helper to retrieve the Annotaions attribute. 
	 * @param structure
	 * @return null if there is none
	 */
/*&*/
	private static AnnotationElementValue[] getAnnotations(AbstractStructureWithAttributes structure)
	{
		RuntimeInvisibleAnnotationsAttribute attr = 
			(RuntimeInvisibleAnnotationsAttribute)
			structure.findAttribute(RuntimeInvisibleAnnotationsAttribute.class);
	
		if (attr == null) {
			return null;
		}

		return attr.getRuntimeAnnotations();
	}
	
	/**
	 * Returns the integer value of <code>value</code> which has to be
	 * of type {@link ConstElementValue}.
	 *  
	 * @param value
	 * @param classFile neede for looking up the constant value
	 * 
	 * @throws InvalidByteCodeException
	 */
	public static int getBitCount(ElementValue value, ClassFile classFile)
		throws InvalidByteCodeException
	{
		int index = ((ConstElementValue)value).getConstValueIndex();
		ConstantIntegerInfo info = (ConstantIntegerInfo)
			classFile.getConstantPoolEntry(index, ConstantIntegerInfo.class);
		return info.getInt();
	}

	/**
	 * Private helper method that returns ElementValue array.
	 * @param annotation
	 * @return
	 */
/*&*/
	private static ElementValue[] getBitsArray(AnnotationElementValue annotation)
	{
		ArrayElementValue bitsArray = (ArrayElementValue) annotation.getElementValuePairEntries()[0].getElementValue();
		return bitsArray.getElementValueEntries();
	}
	
	/**
	 * Private helper method which looks for an annotation of type 
	 * <code>annotationType</code>.
	 * 
	 * @param annotations
	 * @param classFile
	 * @param annotationType
	 * @return
	 * @throws InvalidByteCodeException
	 */
/*&*/
	private static AnnotationElementValue getAnnotation
		(AnnotationElementValue[] annotations, ClassFile classFile, 
		 String annotationType)
		throws InvalidByteCodeException
	{
		for (AnnotationElementValue annotation : annotations) {
			int type = annotation.getTypeIndex();
			String typeName = classFile.getConstantPoolEntryName(type);
			if (typeName.equals(annotationType)) {
				return annotation;
			}
		}
		return null;	
	}
/*&*/
	private static AnnotationElementValue getFieldBitsAnnotation
		(AnnotationElementValue[] annotations, ClassFile classFile)
		throws InvalidByteCodeException
	{
		return getAnnotation(annotations, classFile,
							 "Lde/uni_stuttgart/fmi/szs/jmoped/annotation/PDSFieldBits;");
	}
/*&*/
	private static AnnotationElementValue getParameterBitsAnnotation
		(AnnotationElementValue[] annotations, ClassFile classFile)
		throws InvalidByteCodeException
	{
		return getAnnotation(annotations, classFile, 
							 "Lde/uni_stuttgart/fmi/szs/jmoped/annotation/PDSParameterBits;");
	}
}
