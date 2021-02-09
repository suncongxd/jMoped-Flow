package test.Julia.mp;

import java.security.*;
import java.util.ArrayList;

public class PHCrypto {
	private final DABigInteger p;
	private final DABigInteger q;
	private int nlength ;
	private final DABigInteger m;
 	public final int BITLENGTH = 128;
	public DABigInteger rp;
	public DABigInteger rq;
	public PHCrypto(int n) throws NoSuchAlgorithmException, MPException {
		DABigInteger a = DABigInteger.ZERO(), b = DABigInteger.ZERO();		
		try {
			do { 
			a = DABigInteger.probablePrime(BITLENGTH, SecureRandom.getInstance("SHA1PRNG"));
			} while (a.compareTo(DABigInteger.ZERO()) == 0);
			do {
				b = DABigInteger.probablePrime(BITLENGTH, SecureRandom.getInstance("SHA1PRNG"));
			} while ((b.compareTo(DABigInteger.ZERO()) == 0) || (b.compareTo(a) == 0));
            this.nlength = n;		
            this.p = a;
            this.q = b;
            this.m = p.multiply(q);
            generateR();
        } catch (NullPointerException e) {
            throw new MPException("NPE");
		} catch (ArithmeticException e) {
            throw new MPException("AE");
        } catch (IllegalArgumentException e) {
            throw new MPException("IAE");
        }
	}
	
	
	public BigIntVector getSecretKey(){ 
            BigIntVector v = new BigIntVector();
            v.ensureCapacity(4);
            v.add(p);
            v.add(q);
            v.add(rp);
            v.add(rq);
            return v;
	}
	
	public DABigInteger getPublicParam() {
		return this.m;
	}
	
	public PHCrypto(int n, BigIntVector keyVector) throws IllegalArgumentException {
		if (keyVector == null) 
			throw new IllegalArgumentException("PHCrypto: keyVector is null");
		if (keyVector.size() != 4) {
			throw new IllegalArgumentException("PHCrypto: keyVector invalid size");
		}
		DABigInteger p = null;
		DABigInteger q = null;
		try {
			// to avoid NPE
			p = (DABigInteger) keyVector.get(0); 
			q = (DABigInteger) keyVector.get(1);
			this.rp = (DABigInteger) keyVector.get(2);
			this.rq = (DABigInteger) keyVector.get(3);
			this.nlength = n;		
		} catch (IndexOutOfBoundsException ignored) {
		} catch (ClassCastException ex) {
			throw new IllegalArgumentException("PHCrypto: CCE");
		}
		if (p == null || q == null || rp == null || rq == null)
			throw new IllegalArgumentException("PHCrypto: null value");
		this.m = p.multiply(q);
		this.p = p;
		this.q = q;
	}
	
    public PHCrypto(int n, DABigInteger p, DABigInteger q, DABigInteger rp, DABigInteger rq)
        throws IllegalArgumentException{
        if (p == null || q == null || rp == null || rq == null)
            throw new IllegalArgumentException("PHCrypto: null value");
        this.p = p;
        this.q = q;
        this.nlength = n;
        this.rp = rp;
        this.rq = rq;
        this.m = p.multiply(q);
    }
	
    public void generateR() throws NoSuchAlgorithmException {
	try {
            do {
                    this.rp = new DABigInteger(p.bitLength(), SecureRandom.getInstance("SHA1PRNG"));
            } while ((rp.compareTo(p)>= 0 ) || (rp.compareTo(DABigInteger.ZERO()) <= 0));
            do {
                    this.rq = new DABigInteger(q.bitLength(), SecureRandom.getInstance("SHA1PRNG"));
            } while ((rq.compareTo(q) >= 0 ) || (rq.compareTo(DABigInteger.ZERO()) <= 0));
        } catch (NullPointerException ignored ) {
        } catch (IllegalArgumentException ignored) {
        }
    }

    public PHInteger encrypt(DABigInteger a, int n) throws IllegalArgumentException, MPException {
        if (a == null)
            throw new IllegalArgumentException();
        try {
            ArrayList v = new ArrayList();
            v.ensureCapacity(n);
            if (a.compareTo(this.m) >= 0) {
                throw new IllegalArgumentException
                    ("argument out of clear text space: " + a.toString() 
                    + ">=" + this.m.toString());
            }
            DABigInteger ss  = DABigInteger.ZERO();
            DABigInteger ai  = DABigInteger.ZERO();
            DABigInteger rpi = this.rp;
            DABigInteger rqi = this.rq;
            for (int i = 0; i<n; i++) {
                if ( i < n - 1) {
                        do {
                         ai = new DABigInteger(m.bitLength()-1, SecureRandom.getInstance("SHA1PRNG"));
                        } while ((ai.compareTo(m) >= 0 ) || (ai.compareTo(DABigInteger.ZERO()) <= 0));
                        ss = ss.add(ai).mod(m);
                }
                else
                        ai = a.subtract(ss).mod(m);
                BigIntPair pair = new BigIntPair(ai.multiply(rpi).mod(p), ai.multiply(rqi).mod(q));
                rpi = rpi.multiply(rp).mod(p);
                rqi = rqi.multiply(rq).mod(q);
                v.add(pair);
            }
            return new PHInteger(v);	
        } catch (NullPointerException e) {
        } catch (ArithmeticException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        throw new MPException();
    }
	
    public PHInteger encrypt(DABigInteger a) throws IllegalArgumentException, MPException {
        if (a == null)
            throw new IllegalArgumentException();
        PHInteger retval = encrypt(a, nlength);
        return retval;
    }
	
    public EncryptedCardVector encryptCardVector(CardVector w, int n)
    throws IllegalArgumentException, MPException {
        try {
            PHIntVector ew = new PHIntVector();
            ew.ensureCapacity(w.getValueVector().size());
            for (int i = 0; i < w.getValueVector().size(); i++) {
                DABigInteger x = (DABigInteger)w.getValueVector().get(i);
                PHInteger ex = encrypt(x, n);
                ew.add(i, ex);			
            }
            return new EncryptedCardVector(ew);
        } catch (NullPointerException e) {
        } catch (IndexOutOfBoundsException e) {
        } catch (ClassCastException e) {
        }
        return null;
    }
	
    public EncryptedCardVector encryptCardVector(CardVector w)
    throws IllegalArgumentException, MPException  {
		return encryptCardVector(w, this.nlength);
    }
	
    public DABigInteger decrypt(PHInteger encr) throws IllegalArgumentException, MPException{
        try {
            ArrayList encrypted = encr.getValueVector();
            DABigInteger x = DABigInteger.ZERO();
            DABigInteger y = DABigInteger.ZERO();
            int n = encrypted.size();
            DABigInteger rpi = this.rp;
            DABigInteger rqi = this.rq;
            for (int i = 0; i < n ; i++) {
                BigIntPair pair = (BigIntPair) encrypted.get(i);
                DABigInteger a = pair.getX();
                DABigInteger b = pair.getY();
                DABigInteger rpii = rpi.modInverse(p);
                DABigInteger rqii = rqi.modInverse(q);
                rpi = rpi.multiply(rp).mod(p);
                rqi = rqi.multiply(rq).mod(q);
                x = x.add(a.multiply(rpii));
                y = y.add(b.multiply(rqii));						
            }
            DABigInteger d = new DABigInteger(CRT.crt2(x.getValue(), y.getValue(),p.getValue(),q.getValue())).mod(m);
            return d;
        } catch (NullPointerException e) {
        } catch (IndexOutOfBoundsException e) {
        } catch (ClassCastException e) {
        } catch (ArithmeticException e) {
        }
        throw new MPException("decryption of PHInteger failed...");
    }
	
    public CardVector decryptCardVector(EncryptedCardVector ew)
                        throws IllegalArgumentException, MPException {
        try {
            BigIntVector w = new BigIntVector();
            w.ensureCapacity(ew.getValueVector().size());
            for (int i = 0; i < ew.getValueVector().size(); i++) {
                PHInteger ew_i = (PHInteger)ew.getValueVector().get(i);
                DABigInteger wi = this.decrypt(ew_i);
                w.add(wi);
            }
            return new CardVector(w);
        } catch (NullPointerException e) {
        } catch (IndexOutOfBoundsException e) {
        } catch (ClassCastException e) {
        }
        throw new MPException("decryption of card vector failed...");
    }
	
    public DABigInteger getM() {
		return this.m;
    }
}

