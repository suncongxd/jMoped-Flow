
package test.Julia.mp;

import java.math.BigInteger;

public class CRT {
    public static BigInteger crt2(BigInteger v1, BigInteger v2, BigInteger m1, BigInteger m2)
        throws ArithmeticException{
        if (v1==null || v2==null || m1== null|| m2 == null)
            return null;
        BigInteger x = null;
        try {
            BigInteger C2 = BigInteger.ONE;
            BigInteger u = m1.modInverse(m2);
            C2 = u.multiply(C2).mod(m2);
            u = v1;
            x = u;
            u = v2.subtract(x).multiply(C2).mod(m2);
            x = x.add(u.multiply(m1));
        } catch (NullPointerException impossible) {}
        return x;
    }
}