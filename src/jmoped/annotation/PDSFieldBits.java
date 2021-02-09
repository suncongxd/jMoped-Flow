package jmoped.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Specifies the number of bits that should be used for a static class field.
 * <p>
 * A value of 0 means the default number of bits should be used.
 * <p>
 * Due to the implementation only smaller values than the default number of bits
 * make sense.
 */
@Target(ElementType.FIELD)
public @interface PDSFieldBits 
{
	int value();
}
