
package test.Julia.mp;

import java.util.ArrayList;

public class PHInteger implements DataFieldAttribute{ //, JifObject[L]{
	private ArrayList value;

	public PHInteger(ArrayList v) {
		this.value = v;
	}
	
	public String toString() {
            ArrayList value = this.value;
            if (value == null)
                return "";
            int n = value.size();
            String s = "(";
            for (int i = 0 ; i < n ;i++) {
                try  {
                    BigIntPair pair = (BigIntPair) value.get(i);
                    s += "[" + pair.getX().toString() +	", " + pair.getY() +"]";
                } catch(IndexOutOfBoundsException ignored) {
                } catch (NullPointerException ignored) {
                } catch (ClassCastException ignored) {
                }
                if (i < n-1)
                        s+=",";
            }
            s+=")";
            return s;
	}

    public boolean equals(IDComparable o)   {
        try {
            PHInteger that = (PHInteger)o;
            return this.value.equals(that.value);
        } catch (NullPointerException ignored) {
        } catch (ClassCastException ignored) {
        }
        return false;
    }
    
    public int hashCode() {
        ArrayList value = this.value;
        return value == null ? 0: value.hashCode();
    }
    
    public byte[] toByteArray() {
        ArrayList value = this.value;
        if (value == null) return null;
        int n = value.size();
        int size = this.byteLength();
        byte[] ret = new byte[size];
        int r = 0;
        for (int j = 0; j < n; j++) {
            byte [] v_X = null;
            byte [] v_Y = null;
            try {
                BigIntPair pair = (BigIntPair)value.get(j);
                v_X = pair.getX().toByteArray();
                v_Y = pair.getY().toByteArray();   
                System.arraycopy(v_X, 0, ret, r, v_X.length);
                r += v_X.length;
                System.arraycopy(v_Y, 0, ret, r, v_Y.length);
                r += v_Y.length;
            } catch (IndexOutOfBoundsException ignored) { return null;
            } catch (NullPointerException ignored) { return null;
            } catch (ClassCastException ignored) { return null;
            } catch (ArrayStoreException ignored) { return null;
            }            		
	}
	return ret;
    }
	
    public int byteLength() {
        ArrayList value = this.value;
        if (value == null)
            return 0;
        int n = value.size();
        int bitlength = 0;
        for (int i = 0; i < n; i++) {
            try {
                BigIntPair pair = (BigIntPair) value.get(i);
                int inc = (int) Math.ceil((pair.getX().bitLength() + 1)/8.0);
                bitlength += inc;
                inc = (int) Math.ceil((pair.getY().bitLength() + 1)/8.0);
                bitlength += inc;
            } catch (NullPointerException ignored) { return 0;
            } catch (IndexOutOfBoundsException ignored) {  return 0;
            } catch (ClassCastException ignored) {  return 0;
            } catch (ArithmeticException ignored) {  return 0;
            }
	}
	return bitlength;
    }
	
    public ArrayList getValueVector() {
            return this.value;
    }
    
    public BigIntPair getBigIntPair(int index) throws IndexOutOfBoundsException {
        ArrayList value = this.value;
        try {
            return value == null? null : (BigIntPair) value.get(index);
        } catch (ClassCastException e) {}
        return null;
    }
    
    public int size() {
        ArrayList value = this.value;
        return value == null? 0 :value.size();
    }

    public PHInteger add(PHInteger phi) throws IllegalArgumentException{
	ArrayList a = this.value;
        ArrayList b = null;
        if (a == null || phi == null) 
            throw new IllegalArgumentException("argument is null");
        b = phi.getValueVector();
        if ( b == null) return null;
        int min = (a.size() < b.size()) ? a.size() : b.size() ;
        int max = (a.size() > b.size()) ? a.size() : b.size() ;
        ArrayList c = new ArrayList();
        int i = 0;
        for (; i < min; i++) {
            try {
                BigIntPair pai = (BigIntPair) a.get(i);
                BigIntPair pbi = (BigIntPair) b.get(i);
                BigIntPair cbi = new BigIntPair( pai.getX().add(pbi.getX()), pai.getY().add(pbi.getY()));
                c.add(cbi);
            } catch (NullPointerException ignored) {  return null;
            } catch (IndexOutOfBoundsException ignored) {  return null;
            } catch (ClassCastException ignored) { return null;
            }
        }
	ArrayList t = (max == a.size()) ? a : b;
        
        for (; i < max; i++) {
            try {
                c.add(t.get(i));
            } catch (NullPointerException ignored) {
                return null;
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
        return new PHInteger(c);
    }
	
    public PHInteger mult(DABigInteger b) throws IllegalArgumentException{
	ArrayList a = this.value;
        if (a == null) return null;
        int n = a.size();
		ArrayList c = new ArrayList();
		for (int i = 0; i < n; i++) {
            try {
                BigIntPair pai = (BigIntPair) a.get(i);
                BigIntPair pci = new BigIntPair(
                        pai.getX().multiply(b),
                        pai.getY().multiply(b));
                c.add(pci);
            } catch (NullPointerException ignored) {
            } catch (IndexOutOfBoundsException ignored) {
            } catch (ClassCastException ignored) {
            }
		}
		return new PHInteger(c);
	}
	
    public PHInteger mult(PHInteger phi) throws IllegalArgumentException{
	ArrayList a = this.value;
        if (phi == null) return null;
		ArrayList b = phi.getValueVector();
        if (a == null || b == null) 
            return null;
        int n = a.size();
        int m = b.size();
	ArrayList c = new ArrayList();
        BigIntPair zeroPair = new BigIntPair(DABigInteger.ZERO(), DABigInteger.ZERO());
        for (int k = 0; k < n+m; k++)
                c.add(zeroPair);
	for (int i = 0; i < n; i++) {
            BigIntPair ai = null;
            try {
                ai = (BigIntPair)a.get(i);
            } catch (IndexOutOfBoundsException ignored) {
            } catch (ClassCastException ignored) {
            }
            if (ai == null) return null;
            DABigInteger aiq = ai.getY();
            for (int j = 0; j < m ; j++) {
                try {
                    BigIntPair mp = (BigIntPair)b.get(j);
                    BigIntPair pair = (BigIntPair)c.get(i+j+1);
                    DABigInteger x = pair.getX().add(ai.getX().multiply(mp.getX()));
                    DABigInteger y = pair.getY().add(ai.getY().multiply(mp.getY()));
                    pair = new BigIntPair (x,y);
                    c.set(i+j+1, pair);
                } catch (NullPointerException ignored) {
                    return null;
                } catch (IndexOutOfBoundsException ignored) {
                    return null;
                } catch (ClassCastException ignored) {
                    return null;
                }
            }
	}
	return new PHInteger(c);	
    }
}
