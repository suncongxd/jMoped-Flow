
package test.Julia.mp;

import java.math.BigInteger;
 
public class BigIntPair{// implements JifObject{
    public boolean equals(IDComparable o) {
        return false;
    }

    private final DABigInteger x;
    private final DABigInteger y;
    public BigIntPair(DABigInteger a, DABigInteger b) {
            x = a;
            y = b;
    }

    public DABigInteger getX() {
            return this.x;
    }

    public DABigInteger getY() {
            return this.y;
    }

    public int byteLength() {
        DABigInteger x = this.x;
        DABigInteger y = this.y;
        if (x == null || y == null) return -1;
        //int x_bitLength = x.bitLength();
        //int y_bitLength = y.bitLength();
        int x_byteLength = x.byteLength();
        int y_byteLength = y.byteLength();
        int retval = x_byteLength + y_byteLength;
        return retval;
    }
    
    public String toString() {
        DABigInteger x= this.x;
        DABigInteger y = this.y;
        if (x == null || y == null) return "";
        return "(" + x.toString() + ", " + y.toString() + ")";
    }

    public int hashCode() {
        DABigInteger x= this.x;
        DABigInteger y = this.y;
        if (x == null || y == null) return 1;
        return x.hashCode()+31*y.hashCode();
    }    
  
}

