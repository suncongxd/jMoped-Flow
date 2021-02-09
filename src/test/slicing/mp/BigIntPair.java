/* Jif poker - an implementation of mental poker protocol in Jif
 * (C) 2005 Aslan Askarov
 *
 */
package test.slicing.mp;

import java.math.BigInteger;
 
public class BigIntPair{ // implements JifObject[L]{
/*    public boolean equals(IDComparable[L] o) {
        return false;
    }
*/
	private final int x;
    private final int y;
	public BigIntPair(int a, int b) {
		x = a;
		y = b;		
	}
	
	public int getX() {
		return this.x;		
	}
	
	public int getY() {
		return this.y;
	}
/*
	public int byteLength() {
        int x = this.x;
        int y = this.y;
        if (x == null || y == null) return -1;
        int x_byteLength = x.byteLength();
        int y_byteLength = y.byteLength();
        int retval = x_byteLength + y_byteLength;
        return retval;
	}
    */
/*    public String toString() {
        int x= this.x;
        int y = this.y;
        if (x == null || y == null) return "";
        return "(" + x.toString() + ", " + y.toString() + ")";
    }

    public int hashCode() {
        DABigInteger x= this.x;
        DABigInteger y = this.y;
        if (x == null || y == null) return 1;
        return x.hashCode()+31*y.hashCode();
    }*/
}

