package jmoped.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Specifies the number of bits that should be used for the unit test of a method.
 * <p>
 * For arrays it specifies the maximum length of the array.
 * <p>
 * A value of 0 means the default number of bits should be used.
 * <p>
 * Due to the implementation only smaller values than the default number of bits
 * make sense.
 */
@Target(ElementType.METHOD)
public @interface PDSParameterBits 
{
	int[] value();
}
