
package test.Julia.mp;

import java.security.*;

public class CardVector implements DataFieldAttribute {
	private BigIntVector value; 
	public CardVector(BigIntVector v) throws IllegalArgumentException {
        if (v == null) 
            throw new IllegalArgumentException();
		this.value = v;
	}

    public CardVector(int  x, int t, DABigInteger z, DABigInteger m)
    throws IllegalArgumentException, NoSuchAlgorithmException, MPException {
        if (z == null || m == null) 
            throw new IllegalArgumentException();
		value = new BigIntVector();
		try {
			for (int j = 0; j < t; j++) {
                            DABigInteger P_ij;
                            if (j != x - 1) {
                                DABigInteger k;
                                do {
                                    k = new DABigInteger(4, SecureRandom.getInstance("SHA1PRNG"));
                                } while ((k.compareTo(DABigInteger.ZERO()) == 0) ||
                                        (k.multiply(z).compareTo(m) >= 0));
					P_ij = k.multiply(z);
				} else {
                                    do {
                                        P_ij = new DABigInteger(z.bitLength(), SecureRandom.getInstance("SHA1PRNG"));
                                    } while ((P_ij.mod(z).compareTo(DABigInteger.ZERO()) == 0)
					|| (P_ij.compareTo(m) >= 0 ));
				}
				value.add(P_ij);
			}
		} catch (NullPointerException e) { throw new MPException("NPE");
		} catch (ArithmeticException e) { throw new MPException("AE");
        }
    }

    public BigIntVector getValueVector() {
            return this.value;
    }

    public DABigInteger getBI (int index) throws IndexOutOfBoundsException {
        BigIntVector value = this.value;
        return value == null ? null: value.getBI(index);
    }

     public byte[] toByteArray() {
        BigIntVector value = this.value;
        return value == null ? null: value.toByteArray(); 
    }

    public int byteLength() {
        BigIntVector value = this.value;
        return value == null ? 0: value.byteLength(); 
    }

    public CardVector multMatrix(PermutationMatrix pi) throws IllegalArgumentException, MPException{
        if (pi == null)
            throw new IllegalArgumentException();
        BigIntVector value = this.value;
        if (value == null) 
            throw new MPException("value is null");
	int t = value.size();
	BigIntVector w = new BigIntVector();
	try {
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
        } catch (NullPointerException e) { 
            throw new MPException("multMatrix: NPE");        
        } catch (ClassCastException e)  {
            throw new MPException("multMatrix: CCE");
        } catch (IndexOutOfBoundsException e) {
            throw new MPException("multMatrix: IOB"); 
        }
	}

    public int getCardValue(DABigInteger z) throws IllegalArgumentException, MPException{
        try {
            int found = -1;
            int t = value.size();
            for (int i = 0; i < t; i++) {
                DABigInteger v_i = (DABigInteger) value.get(i);
                if (v_i.mod(z).compareTo(DABigInteger.ZERO()) != 0)
                    {
                     if (found < 0)
                        found = i+1;
                     else 
                        throw new IllegalArgumentException
                            ("ambigous card or incorrect modulus");
                    }
            }
            return found;
        } catch (NullPointerException e) {
        } catch (IndexOutOfBoundsException e) {
        } catch (ClassCastException e) {
        } catch (ArithmeticException e) {
        }
        // something wrong happened
        throw new MPException();
	}
	
    public String toString() {
       BigIntVector value = this.value;
       return value == null ? "" : value.toString();
    }
    
    public boolean equals (IDComparable o) {
        if (o == null)
            return false;
        if (!(o instanceof CardVector))
            return false;
        try {
            CardVector that = (CardVector) o;
            BigIntVector thisvalue = this.value;
            BigIntVector thatvalue = that.value;
            if (thisvalue == null || thatvalue == null)
                return false;
            return thisvalue.equals(thatvalue);
        } catch (ClassCastException e) {
        }
        return false;
    }

    public int hashCode() {
        BigIntVector value = this.value;        
        return value == null ? 0: value.hashCode();
    }
}


