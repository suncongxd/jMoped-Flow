package test.slicing.mp;

//import java.security.*;
import java.util.Random;

public class CardVector{ // implements DataFieldAttribute {
	private BigIntVector value; 
    
	public CardVector(BigIntVector v){ // throws IllegalArgumentException {
//        if (v == null) 
//            throw new IllegalArgumentException();
		this.value = v;
	}
/*
    public CardVector(int  x, int t, int z, int m) { // throws (IllegalArgumentException, NoSuchAlgorithmException, MPException){
//        if (z == null || m == null) 
//            throw new IllegalArgumentException();
		value = new BigIntVector();
//		try {
			for (int j = 0; j < t; j++) {
				int P_ij;
				if (j != x - 1) {
					int k;
					do {					       
					    k = new DABigInteger(4,new Random());// SecureRandom.getInstance("SHA1PRNG"));
					} while ((k.compareTo(DABigInteger.ZERO()) == 0) ||
							 (k.multiply(z).compareTo(m) >= 0));
					P_ij = k.multiply(z);
				} else {
					do {
						P_ij = new DABigInteger(z.bitLength(),new Random());// SecureRandom.getInstance("SHA1PRNG"));
					} while ((P_ij.mod(z).compareTo(DABigInteger.ZERO()) == 0) 
							|| (P_ij.compareTo(m) >= 0 ));
				}
				value.add(P_ij);
			}
//		} catch (NullPointerException e) {	throw new MPException("NPE");	
//		} catch (ArithmeticException e) { throw new MPException("AE");
        //} catch (IndexOutOfBoundsException e) { throw new MPException("IOOBE");
//        }
	}*/
/*
	public BigIntVector getValueVector() {
		return this.value;
	}
*/
/*	
	public DABigInteger getBI (int index){ //throws IndexOutOfBoundsException {
        BigIntVector value = this.value;
        return value == null ? null: value.getBI(index); 
	}
*/
/*
     public byte[] toByteArray() {
        BigIntVector value = this.value;
        return value == null ? null: value.toByteArray();
	}
*/
/*
    public int byteLength() {
        BigIntVector value = this.value;
        return value == null ? 0: value.byteLength(); 
	}
*/
/*
    public CardVector multMatrix(PermutationMatrix pi){ // throws (IllegalArgumentException, MPException){
//        if (pi == null)
//            throw new IllegalArgumentException();
        BigIntVector value = this.value;
//        if (value == null) 
//            throw new MPException("value is null");
		int t = value.size();
		BigIntVector w = new BigIntVector();
//		try {
            for (int i = 0; i < t; i++) {
                BigIntVector rowi = (BigIntVector)pi.getMatrix().get(i);
                DABigInteger coli = (DABigInteger)value.get(i);
                
                if (i == 0)
                    for (int j = 0; j < t; j++) {
                        DABigInteger p_ij = (DABigInteger)rowi.get(j);
                        w.add(p_ij.multiply(coli));
                    }
                else 
                    for (int j = 0; j < t; j++) {
                        DABigInteger p_ij = (DABigInteger)rowi.get(j);
                        DABigInteger w_j = (DABigInteger) w.get(j);
                        w.set(j, w_j.add(p_ij.multiply(coli)));
                    }			
            }
            return new CardVector(w);
//        } catch (NullPointerException e) { 
//            throw new MPException("multMatrix: NPE");        
//        } catch (ClassCastException e)  {
//            throw new MPException("multMatrix: CCE");
//        } catch (IndexOutOfBoundsException e) {
//            throw new MPException("multMatrix: IOB"); 
//        }
	}
*/

	public int getCardValue(int z){ // throws IllegalArgumentException, MPException{
//        try {
            int found = -1;
            int t = value.size();
            for (int i = 0; i < t; i++) {
                int v_i = (Integer) value.get(i);
                if (v_i%z!=0)//v_i.mod(z).compareTo(DABigInteger.ZERO()) != 0)
                    {
                     if (found < 0)
                        found = i+1;
//                     else 
//                        throw new IllegalArgumentException("ambigous card or incorrect modulus");
                    }
            }
            return found;
//        } catch (NullPointerException e) {
//        } catch (IndexOutOfBoundsException e) {
//        } catch (ClassCastException e) {
//        } catch (ArithmeticException e) { }
//        throw new MPException();
	}

/*
	public String toString() {
       BigIntVector value = this.value;
       return value == null ? "" : value.toString();
	}*/
/*
    public boolean{L;o} equals (IDComparable[L] o) {
        if (o == null)
            return false;
        if (!(o instanceof CardVector[L]))
            return false;
        
        try {
            CardVector [L] that = (CardVector[L]) o;
            BigIntVector[L] thisvalue = this.value;
            BigIntVector[L] thatvalue = that.value;
            if (thisvalue == null || thatvalue == null)
                return false;
            return thisvalue.equals(thatvalue);
        } catch (ClassCastException e) {
        }
        return false;
    }
*/
/*
    public int hashCode() {
        BigIntVector value = this.value;        
        return value == null ? 0: value.hashCode();
    }*/
}


