
package test.Julia.mp;
 
public class Digest implements DataFieldAttribute {
	private final byte[] digest;
	public Digest(byte[] dg) {
		this.digest = dg;
	}

     public byte[] toByteArray() {
		return this.digest;
	}

     public int byteLength() {
        byte [] d = this.digest;
        if (d != null) return d.length;
        else return 0;
    }

    public int hashCode() {
        int hashCode = 1;
        int i = 0;
        byte [] digest = this.digest;
        if (digest == null) return 0;
        while (i < digest.length) {
            try {
                hashCode = 31*hashCode + digest[i];
            }
            catch (IndexOutOfBoundsException e) {}
        }
        return hashCode;
    }
    
    public String toString() {
       String s = "[";
       int i = 0;
       byte [] digest = this.digest;
       if (digest == null) return "";
       while (i < digest.length) {
           s += Integer.toString(i);
           if ( i< digest.length - 1)
               s+= ",";
       }
       s += "]";
       return s;
    }

    public boolean equals (IDComparable o) {
        if (o == null || !(o instanceof Digest)) 
            return false;
        try {
            Digest that = (Digest) o;
            byte[] thisdigest = this.digest;
            byte[] thatdigest = that.digest;
            if (thisdigest == null || thatdigest == null)
                return false;
            if (thisdigest.length != thatdigest.length)
                return false;
            int t = thisdigest.length;
            for (int i = 0; i < t; i++)
                if (thisdigest[i] != thatdigest[i])
                    return false;
        } catch (ClassCastException impossible) {
        } catch (ArrayIndexOutOfBoundsException impossible){
        }
        return true;
    }
}























