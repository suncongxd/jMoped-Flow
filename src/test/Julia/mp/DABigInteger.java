
package test.Julia.mp;

import java.math.BigInteger;
import java.util.Random;

public class DABigInteger implements DataFieldAttribute{ //, JifObject[L] {
    private final BigInteger value;
    public BigInteger getValue() {
        return this.value;
    }
     public int byteLength() { 
        BigInteger v = this.value;
        if (v != null)        
            try {
                return (int) (Math.ceil((v.bitLength() + 1)/8.0));
            } catch (ArithmeticException ignored) {       
            }
        return -1;
     }

    public byte[] toByteArray() {
        BigInteger value  = this.value;
        if (value == null) return null;
        byte [] ba = value.toByteArray(); 
        return ba;
    }

    public DABigInteger (java.math.BigInteger val) throws IllegalArgumentException {
        if (val == null)
            throw new IllegalArgumentException();
    	this.value = val;
    }

    public DABigInteger(byte[] val) throws NumberFormatException {
        if (val != null)
            this.value  = new BigInteger (val);
        else 
            this.value = null;
    }

    public DABigInteger(int signum, byte[] magnitude) throws NumberFormatException{
        value = new BigInteger(signum, magnitude);
    }

    public DABigInteger(int bitLength,  int certainty, Random rnd) throws ArithmeticException {
        this.value = new BigInteger (bitLength, certainty, rnd);
	}

    public DABigInteger(int numBits, Random rnd) throws IllegalArgumentException {
        this.value = new BigInteger (numBits, rnd);
    }

    public DABigInteger(String val) throws NumberFormatException{
		this.value = new BigInteger(val);
	}

    public DABigInteger(String val, int radix) throws NumberFormatException {
		this.value = new BigInteger(val, radix);
	}

    public static DABigInteger ZERO() {
        try {
            return new DABigInteger(BigInteger.ZERO);
        } catch (IllegalArgumentException impossible) {}
        return null;
    }
    
    public static DABigInteger ONE()  {
        try {
            return new DABigInteger(BigInteger.ONE);
        } catch (IllegalArgumentException impossible) {}
        return null;
    }
    
    public static DABigInteger valueOf(long val) {
        BigInteger v = BigInteger.valueOf(val);
        try {
            return new DABigInteger(v);
        } catch (IllegalArgumentException impossible) {}
        return null;
    }
    
    public int compareTo (DABigInteger that) throws IllegalArgumentException{     
        if (that == null)
            throw new IllegalArgumentException("that is null");
        BigInteger thisvalue = this.value;
        if (thisvalue != null)
            return thisvalue.compareTo(that.value);
        return 0;  // impossible
    }

    public DABigInteger add(DABigInteger that) throws IllegalArgumentException {
        if (that == null)
            throw new IllegalArgumentException();
        BigInteger y = that.value;
        BigInteger x = this.value;
        BigInteger sum = null;
        if (x != null && y != null) 
             sum = x.add(y);
        return new DABigInteger(sum);
    }
    
    public DABigInteger multiply(DABigInteger that) throws IllegalArgumentException{
        if (that == null) throw new IllegalArgumentException();
        BigInteger thisvalue = this.value;
        BigInteger thatvalue = that.value;
        BigInteger res = null;
        if (thisvalue != null && thatvalue != null)
            res  = thisvalue.multiply(thatvalue);
        return new DABigInteger(res);
    }
    
    public DABigInteger subtract(DABigInteger that) throws IllegalArgumentException{
        if (that == null) throw new IllegalArgumentException();
        BigInteger thisvalue = this.value;
        BigInteger thatvalue = that.value;
        BigInteger res = null;
        if (thisvalue != null && thatvalue != null)
            res  = thisvalue.subtract(thatvalue);
        return new DABigInteger(res);
    }
    
    public DABigInteger divide(DABigInteger that)
    throws IllegalArgumentException, ArithmeticException {
        if (that == null) throw new IllegalArgumentException();
        BigInteger thisvalue = this.value;
        BigInteger thatvalue = that.value;
        BigInteger res = null;
        if (thisvalue != null && thatvalue != null)
            res  = thisvalue.divide(thatvalue);
        return new DABigInteger(res);        
    }
    
    public DABigInteger mod(DABigInteger that)
    throws IllegalArgumentException, ArithmeticException{
        if (that == null) throw new IllegalArgumentException();
        BigInteger thisvalue = this.value;
        BigInteger thatvalue = that.value;
        BigInteger res = null;
        if (thisvalue != null && thatvalue != null)
            res  = thisvalue.mod(thatvalue);
        return new DABigInteger(res);
    }
    
    public DABigInteger modInverse(DABigInteger that)
    throws IllegalArgumentException, ArithmeticException{
        if (that == null) throw new IllegalArgumentException();
        BigInteger thisvalue = this.value;
        BigInteger thatvalue = that.value;
        BigInteger res = null;
        if (thisvalue != null && thatvalue != null)
            res  = thisvalue.modInverse(thatvalue);
        return new DABigInteger(res);
    }
    
    public static DABigInteger probablePrime(int bitLength, Random rnd) throws ArithmeticException {
        try {
         return new DABigInteger(BigInteger.probablePrime(bitLength, rnd));
        } catch (IllegalArgumentException notprobable) { // :)
        }
        return null;
    }

    public String toString() {
        BigInteger v = this.value;
        if (v == null) return "";
        return v.toString();
    }
    
    public int hashCode() {
        BigInteger v = this.value;
        if (v == null) return -1;
        return v.hashCode();
    }
    
    public boolean equals(IDComparable obj) {
        return true;
    }
    
    public int bitLength() {
        BigInteger v = this.value;
        if (v == null) return -1;
        return v.bitLength();
    }
    
}


