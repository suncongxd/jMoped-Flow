
package test.slicing.mp;
//import java.math.BigInteger;
//import jif.util.ArrayList;

public class BigIntVector extends java.util.ArrayList{ // implements DataFieldAttribute {
	/** inherited from Vector */
	private static final long serialVersionUID = -2767605614048989439L;

	public BigIntVector() {
//		super();
	}

     public BigIntVector(int initialCapacity) {
//		super();
        ensureCapacity(initialCapacity);
	}
/*
     public byte[] toByteArray() {       
		int size = this.byteLength();
		byte[] retval = new byte[size];
		int i = 0;
//        try {
            for (int j = 0 ; j < size; j++) {
//                try  {
                    byte [] x_i =  this.getBI(i).toByteArray();
                    System.arraycopy(x_i, 0, retval, i, x_i.length);
//                } catch (NullPointerException e) {
//                } catch (ArrayStoreException e) {}
            }
            return retval;
//        }catch(IndexOutOfBoundsException ignored) {}
//        return null;
	}
*/
/*
     public int byteLength() {
		int n = this.size();
		int size = 0;
//        try {
            for (int j = 0; j < size; j++) {
                    DABigInteger x =  this.getBI(j);
                    if (x != null)
                        size += Math.ceil((x.bitLength() + 1)/8.0);
            }
            return size;
//        } catch (IndexOutOfBoundsException ignored) {
//        } catch (ArithmeticException ignored) {}
        // unreachable
//        return 0;
	}
*/

     public int getBI(int i){ // throws (IndexOutOfBoundsException){
//		 try {
         	return (Integer) this.get(i);
//        } catch (ClassCastException e) { return null; } 
	 }

/*
     public String toString() {
//        try {
            int n = this.size();
            String s = "{";
            for (int i = 0; i < n; i++ ){
                DABigInteger bi = this.getBI(i);
                s+=bi.toString();
                if (i < n- 1) 
                    s+= ",";
            }
            s += "}";
            return s;
//        } catch (NullPointerException ignored) {
//        } catch (IndexOutOfBoundsException ignored ) { }
//        return "";
	}
*/
}


