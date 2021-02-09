
package test.Julia.mp;

import java.util.ArrayList;
import java.security.*;

public class PermutationMatrix {
    private ArrayList matrix;
    private int bitLength = 0;
    public PermutationMatrix(ArrayList v) throws IllegalArgumentException{
        if (v == null)
            throw new IllegalArgumentException();
	this.matrix = v;
    }
    public PermutationMatrix(byte[] p, DABigInteger z)
        throws IllegalArgumentException, NoSuchAlgorithmException,MPException {
        if (z == null)
            throw new IllegalArgumentException("z is null");
        if (p == null)
            throw new IllegalArgumentException("p is null");
        matrix = new BigIntVector();
        if (p == null || z == null) return;
	int t = p.length; // obtain the size of the matrix we want
        try {
            for (int i = 0; i < t; i++) {
                BigIntVector rowi = new BigIntVector();
                for (int j = 0; j < t; j++) {
                    DABigInteger P_ij;
                    if (j != p[i] - 1) {
                        DABigInteger k;
                        do {
                            k = new DABigInteger(z.bitLength(), SecureRandom.getInstance("SHA1PRNG"));
                        } while (k.compareTo(DABigInteger.ZERO()) == 0);
                        P_ij = k.multiply(z);
                    } else {
                        boolean loop = true;
                        do {
                            P_ij = new DABigInteger(z.bitLength(), SecureRandom.getInstance("SHA1PRNG"));
                        } while (P_ij.mod(z).compareTo(DABigInteger.ZERO()) == 0);
                    }
                    rowi.add(P_ij);
                    bitLength += P_ij.byteLength();
                }
                matrix.add(rowi);
            }
        } catch (ArithmeticException e) {
            throw new MPException();
        } catch (NullPointerException e) {
            throw new MPException();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MPException();
        } catch (IllegalArgumentException e) {
            throw new MPException();
        }
    }
    
    
    public boolean validate(byte[] p, DABigInteger z)
        throws IllegalArgumentException, MPException {
        if (z == null)
            throw new IllegalArgumentException("z is null");
        if (p == null)
            throw new IllegalArgumentException("p is null");
        ArrayList matrix = this.matrix;
        if (matrix == null)
            throw new MPException("validate: matrix is null");
        int t = matrix.size();
        try {
            for (int i = 0; i<t; i++) {
                BigIntVector rowi = (BigIntVector) matrix.get(i);
                for (int j = 0; j<t; j++) {
                    DABigInteger P_ij = (DABigInteger) rowi.get(j);
                    if (j != p[i]-1) {
                        if (P_ij.mod(z).compareTo(DABigInteger.ZERO()) != 0)  {
                           throw new MPException("validate: " + Integer.toString(j) + "," + Integer.toString(i) + "  " + Integer.toString(p[i] -1) + " :" +
                           "P_ij mod z for j/=p[i]-1 is ZERO " + 
                           "P_ij = " + P_ij.toString() +", z=" + z.toString() + this.toString());                                    
                        }
                    } else { // j== p[i] - 1 , that is P_ij mod z /= 0
                        if (P_ij.mod(z).compareTo(DABigInteger.ZERO()) == 0)
                           return false;
                    }
                }
            }
        } catch (IndexOutOfBoundsException ex) {
            throw new MPException("validate: IOB");
        } catch (ClassCastException ex) {
            throw new MPException("validate: CCE");
        } catch (ArithmeticException ex) {
            throw new MPException("validate: AE");
        } catch (NullPointerException ex) {
            throw new MPException("validate: NPE");
        }
        return true;
    }

    public String toString() {
        ArrayList matrix = this.matrix;
        if (matrix == null) return "(null)";
	String rs = "(";
        int t = matrix.size();
        for (int i = 0; i < t; i++) {
            rs += "[";
            BigIntVector rowi = null;
            try {
                rowi = (BigIntVector) matrix.get(i);
            } catch (IndexOutOfBoundsException ignored) { return "";
            } catch (ClassCastException ignored) { return "";
            }
            for (int j = 0; j < t; j++) {
                try {
                    DABigInteger P_ij = (DABigInteger) rowi.get(j);
                    rs += P_ij.toString();
                    if (j != t - 1)
                        rs += ",";
                } catch (NullPointerException ignored) { return "";
                } catch (ClassCastException ignored) { return "";
                } catch (IndexOutOfBoundsException ignored) { return "";
                }
            }
            rs += "]";
            if (i != t - 1)
                rs += ",";
	}
        rs += ")";
        return rs;
    }

    public PermutationMatrix cloneMatrix() {
        ArrayList m = this.matrix;
        if (m == null) return null;
        int t = m.size();        
        ArrayList y = new ArrayList();
        y.ensureCapacity(t);
        for (int i = 0; i < t; i++){
            try {
                BigIntVector rowi = (BigIntVector)m.get(i);
                y.add(rowi);
            } catch (IndexOutOfBoundsException ignored) {
            } catch (ClassCastException ignored) {
            }
        }
        try {
            return new PermutationMatrix(y);
        } catch (IllegalArgumentException ignored){
            return null;
        }
    }

	public byte[] toByteArray(){
            ArrayList matrix = this.matrix;
            if (matrix == null) return null;
            byte[] rv = new byte[this.bitLength];
            int u = 0;
            int t = matrix.size();
            for (int i = 0; i< t; i++) {
                BigIntVector rowi = null;
                try {
                    rowi = (BigIntVector) matrix.get(i);
                } catch (IndexOutOfBoundsException ignored) { return null;
                } catch (ClassCastException ignored) { return null;
                }
                for (int j = 0; j < t; j++) {
                    try {
                        byte[] p_ij = ((DABigInteger) rowi.get(i)).toByteArray();
                        for (int k = 0; k < p_ij.length; k++, u++ )
                            rv[u] = p_ij[u];
                    } catch (NullPointerException ignored) { return null;
                    } catch (ClassCastException ignored) { return null;
                    } catch (IndexOutOfBoundsException ignored) { return null;
                    }
                }
            }
            return rv;
	}
	
    public Digest getDigest() throws MPException{
        ArrayList matrix = this.matrix;
        if (matrix == null) throw new MPException();
        int t = matrix.size();
        try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                for (int i = 0; i < t; i++) {
                    try {
                        BigIntVector rowi = (BigIntVector) matrix.get(i);
                        for (int j = 0; j <t; j++)
                            md.update(((DABigInteger) rowi.get(j)).toByteArray());
                    } catch (IndexOutOfBoundsException ignored) {
                        throw new MPException("IOB");
                    }
		}
                return new Digest (md.digest());
	} catch (NoSuchAlgorithmException e) {
            throw new MPException("NSA");
	} catch (NullPointerException e) {
            throw new MPException("NPE");
        } catch (ClassCastException e) {
            throw new MPException("CCE");
        } 
    }

    public void setNonZero(int i, DABigInteger z)
        throws MPException, IllegalArgumentException, IndexOutOfBoundsException {
        ArrayList matrix = this.matrix;
        if (matrix == null)
            throw new MPException("matrix is null");
        if (z == null)
            throw new IllegalArgumentException("z is null");
	DABigInteger zMinusOne = z.subtract(DABigInteger.ONE());
	int t = matrix.size();
	if (t <= i )
	    throw new IllegalArgumentException("setNonZero" + Integer.toString(t) + " " + Integer.toString(i));
	BigIntVector rowi = new BigIntVector();
	for (int j = 0; j < t; j ++) {
	    rowi.add(zMinusOne);
	}
        matrix.set(i, rowi);
    }

    public ArrayList getMatrix() {
	return matrix;
    }
}
	
