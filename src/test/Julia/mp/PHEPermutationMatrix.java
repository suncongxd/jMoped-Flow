
package test.Julia.mp;

import java.security.*;

public class PHEPermutationMatrix {
    public PHIntVector matrix;
    public PHEPermutationMatrix(PermutationMatrix mx, PHIntVector d,
        PHIntVector e, DABigInteger z_i, DABigInteger z_j)
        throws NoSuchAlgorithmException, MPException, IllegalArgumentException {
        if (mx == null || d == null|| e == null || z_i == null || z_j == null) {
            throw new IllegalArgumentException("One of the arguments is null") ;
        }
	try {
            int s = d.size();
            int t = mx.getMatrix().size();
            this.matrix = new PHIntVector();
            this.matrix.ensureCapacity(t);
            for (int k = 0; k < t; k++) {
                BigIntVector mxRowK = (BigIntVector) mx.getMatrix().get(k);
                PHIntVector rowk = new PHIntVector();
                rowk.ensureCapacity(t);
                for (int l = 0; l < t; l++) {
                    DABigInteger p_kl = (DABigInteger) mxRowK.get(l);
                    int g = 1 + (int) Math.round((s - 2) * Math.random());
                    PHInteger h = null;
                    for (int ii = 0; ii < g; ii++) {
                        int jj = (int) Math.round((s - 2) * Math.random());
                        PHInteger temp = d.getPHI(jj);
                        if (ii == 0)
                            h = temp;
                        else
                            h = h.add(temp);
                    }
                    DABigInteger c = DABigInteger.ZERO();
                    try {
                        do {
                                c = new DABigInteger(2, SecureRandom.getInstance("SHA1PRNG"));
                        } while ((c.mod(z_i).compareTo(DABigInteger.ZERO()) == 0) || (c.compareTo(z_i) >= 0));
                    } catch (NullPointerException exc) {
                    }
                    PHInteger h_ = h.mult(c);
                    PHInteger pc_kl = null;
                    if (p_kl.mod(z_j).compareTo(DABigInteger.ZERO()) == 0) {
                            pc_kl = h_;
                    } else {
                            int g_ = (int) Math.round((s - 1) * Math.random());
                            PHInteger e_g_ = e.getPHI(g_);
                            pc_kl = h_.add(e_g_);
                    }
                    rowk.add(pc_kl);
		}
		matrix.add(rowk);
            }
	} catch (NullPointerException ex) { throw new MPException("NPE");
        } catch (IndexOutOfBoundsException ex) { throw new MPException("IOB");
        } catch (ClassCastException ex) { throw new MPException("CCE");
        } catch (ArithmeticException ex) { throw new MPException("AE");
        }
    }

	public PHIntVector getMatrix() {
		return this.matrix;
	}
}

