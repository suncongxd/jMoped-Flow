/* Jif poker - an implementation of mental poker protocol in Jif
 * (C) 2005 Aslan Askarov
 *
 */
package test.slicing.mp;

public interface DataFieldAttribute{ // extends Object{ //JifObject[L]{
	public byte[] toByteArray();
	public int byteLength();
}

