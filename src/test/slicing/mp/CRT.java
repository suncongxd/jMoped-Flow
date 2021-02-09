
package test.slicing.mp;

//import java.math.BigInteger;

public class CRT {
	/**
	 * Chinese remainder theorem for the case of 2 equations
	 * Garner's algorithm
	 * Handbook of Applied Cryptography, Algorithm 14.71, page 612
	 * @param v1
	 * @param v2
	 * @param m1
	 * @param m2
	 * @return
	 */
    public static int crt2(int v1,int v2, int m1, int m2)
        /*throws ArithmeticException*/{
//        if (v1==null || v2==null || m1== null|| m2 == null)
//            return null;
        int x;// = null;
//        try {
            int C2 =1;// BigInteger.ONE;
            int u = (1/m1)%m2;//m1.modInverse(m2);
            C2 = (u*C2)%m2;//u.multiply(C2).mod(m2);
            u = v1;
            x = u;
            u = ((v2-x)*C2)%m2;//v2.subtract(x).multiply(C2).mod(m2);
            x =x+(u*m1);// x.add(u.multiply(m1));
  //      } catch (NullPointerException impossible) {}
        
        return x;
	}
}

