package test.Julia.JLex;

import java.util.Enumeration;

final class SparseBitSet{// implements Cloneable {
    int  offs[];
    long bits[];
    int size;
    
    static final private int LG_BITS = 6;
    static final private int BITS = 1<<LG_BITS;
    static final private int BITS_M1 = BITS-1;

    public SparseBitSet() {
		bits = new long[4];
		offs = new int [4];
		size = 0;
    }

    public SparseBitSet(int nbits) {
		this();
    }

    public SparseBitSet(SparseBitSet set) {
		bits = new long[set.size];
		offs = new int [set.size];
		size = 0;
    }

    private void new_block(int bnum) {
		new_block(bsearch(bnum), bnum);
    }
    private void new_block(int idx, int bnum) {
		if (size==bits.length) { // resize
		    long[] nbits = new long[size*3];
		    int [] noffs = new int [size*3];
		    System.arraycopy(bits, 0, nbits, 0, size);
		    System.arraycopy(offs, 0, noffs, 0, size);
		    bits = nbits;
		    offs = noffs;
		}
//		CUtility.ASSERT(size<bits.length);
		insert_block(idx, bnum);
    }
    private void insert_block(int idx, int bnum) {
//		CUtility.ASSERT(idx<=size);
//		CUtility.ASSERT(idx==size || offs[idx]!=bnum);
		System.arraycopy(bits, idx, bits, idx+1, size-idx);
		System.arraycopy(offs, idx, offs, idx+1, size-idx);
		offs[idx]=bnum;
		bits[idx]=0; //clear them bits.
		size++;
    }
    private int bsearch(int bnum) {
		int l=0, r=size; // search interval is [l, r)
		while (l<r) {
		    int p = (l+r)/2;
		    if (bnum<offs[p]) r=p;
		    else if (bnum>offs[p]) l=p+1;
		    else return p;
		}
//		CUtility.ASSERT(l==r);
		return l; // index at which the bnum *should* be, if it's not.
    }

    public void set(int bit) {
		int bnum = bit >> LG_BITS;
		int idx  = bsearch(bnum);
		if (idx >= size || offs[idx]!=bnum)
		    new_block(idx, bnum);
		bits[idx] |= (1L << (bit & BITS_M1) );
    }

    public void clear(int bit) {
		int bnum = bit >> LG_BITS;
		int idx  = bsearch(bnum);
		if (idx >= size || offs[idx]!=bnum)
		    new_block(idx, bnum);
		bits[idx] &= ~(1L << (bit & BITS_M1) );
    }

    public void clearAll() {
		size = 0;
    }

    public boolean get(int bit) {
		int bnum = bit >> LG_BITS;
		int idx  = bsearch(bnum);
		if (idx >= size || offs[idx]!=bnum)
		    return false;
		return 0 != ( bits[idx] & (1L << (bit & BITS_M1) ) );
    }

    public void and(SparseBitSet set) {
		binop(this, set, AND);
    }

    public void or(SparseBitSet set) {
		binop(this, set, OR);
    }

    public void xor(SparseBitSet set) {
		binop(this, set, XOR);
    }

    private static interface BinOp {
		public long op(long a, long b);
    }
    private static final BinOp AND = new BinOp() {
		public final long op(long a, long b) { return a & b; }
    };
    private static final BinOp OR = new BinOp() {
		public final long op(long a, long b) { return a | b; }
    };
    private static final BinOp XOR = new BinOp() {
		public final long op(long a, long b) { return a ^ b; }
    };
    private static final void binop(SparseBitSet a, SparseBitSet b, BinOp op) {
		int  nsize = a.size + b.size;
		long[] nbits; 
		int [] noffs;
		int a_zero, a_size;
		// be very clever and avoid allocating more memory if we can.
		if (a.bits.length < nsize) { // oh well, have to make working space.
		    nbits = new long[nsize];
		    noffs = new int [nsize];
		    a_zero  = 0; a_size = a.size;
		} else { // reduce, reuse, recycle!
		    nbits = a.bits;
		    noffs = a.offs;
		    a_zero = a.bits.length - a.size; a_size = a.bits.length;
		    System.arraycopy(a.bits, 0, a.bits, a_zero, a.size);
		    System.arraycopy(a.offs, 0, a.offs, a_zero, a.size);
		}
		// ok, crunch through and binop those sets!
		nsize = 0;
		for (int i=a_zero, j=0; i<a_size || j<b.size; ) {
		    long nb; int no;
		    if (i<a_size && (j>=b.size || a.offs[i] < b.offs[j])) {
				nb = op.op(a.bits[i], 0);
				no = a.offs[i];
				i++;
		    } else if (j<b.size && (i>=a_size || a.offs[i] > b.offs[j])) {
				nb = op.op(0, b.bits[j]);
				no = b.offs[j];
				j++;
		    } else { // equal keys; merge.
				nb = op.op(a.bits[i], b.bits[j]);
				no = a.offs[i];
				i++; j++;
		    }
		    if (nb!=0) {
				nbits[nsize] = nb;
				noffs[nsize] = no;
				nsize++;
		    }
		}
		a.bits = nbits;
		a.offs = noffs;
		a.size = nsize;
    }

    public int hashCode() {
		long h = 1234;
		for (int i=0; i<size; i++)
		    h ^= bits[i] * offs[i];
		return (int)((h >> 32) ^ h);
    }

    public int size() {
		return (size==0)?0:((1+offs[size-1]) << LG_BITS);
    }

    public boolean equals(Object obj) {
		if ((obj != null) && (obj instanceof SparseBitSet))
		    return equals(this, (SparseBitSet)obj); 
		return false;
    }

    public static boolean equals(SparseBitSet a, SparseBitSet b) {
		for (int i=0, j=0; i<a.size || j<b.size; ) {
		    if (i<a.size && (j>=b.size || a.offs[i] < b.offs[j])) {
				if (a.bits[i++]!=0) return false;
		    } else if (j<b.size && (i>=a.size || a.offs[i] > b.offs[j])) {
				if (b.bits[j++]!=0) return false;
		    } else { // equal keys
				if (a.bits[i++]!=b.bits[j++]) return false;
		    }
		}
		return true;
    }

    public Object clone() {
		try { 
		    SparseBitSet set = (SparseBitSet)super.clone();
		    set.bits = (long[]) bits.clone();
		    set.offs = (int []) offs.clone();
		    return set;
		} catch (CloneNotSupportedException e) {
		    // this shouldn't happen, since we are Cloneable
		    throw new InternalError();
		}
    }

    public Enumeration elements() {
		return new Enumeration() {
		    int idx=-1, bit=BITS;
		    { advance(); }
		    public boolean hasMoreElements() {
				return (idx<size);
		    }
		    public Object nextElement() {
				int r = bit + (offs[idx] << LG_BITS);
				advance();
				return new Integer(r);
		    }
		    private void advance() {
				while (idx<size) {
				    while (++bit<BITS)
					if (0!=(bits[idx] & (1L<<bit)))
					    return;
				    idx++; bit=-1;
				}
		    }
		};
    }

    public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('{');
		for (Enumeration e=elements(); e.hasMoreElements(); ) {
		    if (sb.length() > 1) sb.append(", ");
		    sb.append(e.nextElement());
		}
		sb.append('}');
		return sb.toString();
    }

    private boolean isValid() {
		if (bits.length!=offs.length) return false;
		if (size>bits.length) return false;
		if (size!=0 && 0<=offs[0]) return false;
		for (int i=1; i<size; i++)
		    if (offs[i] < offs[i-1])
			    return false;
		return true;
    }

    public static void main(String[] args) {
		final int ITER = 500;
		final int RANGE= 65536;
		SparseBitSet a = new SparseBitSet();
//		CUtility.ASSERT(!a.get(0) && !a.get(1));
//		CUtility.ASSERT(!a.get(123329));
		a.set(0); //CUtility.ASSERT(a.get(0) && !a.get(1));
		a.set(1);// CUtility.ASSERT(a.get(0) && a.get(1));
		a.clearAll();
//		CUtility.ASSERT(!a.get(0) && !a.get(1));
		java.util.Random r = new java.util.Random();
		java.util.Vector v = new java.util.Vector();
		for (int n=0; n<ITER; n++) {
		    int rr = ((r.nextInt()>>>1) % RANGE) << 1;
		    a.set(rr); v.addElement(new Integer(rr));
		    // check that all the numbers are there.
//		    CUtility.ASSERT(a.get(rr) && !a.get(rr+1) && !a.get(rr-1));
//		    for (int i=0; i<v.size(); i++)
//			CUtility.ASSERT(a.get(((Integer)v.elementAt(i)).intValue()));
		}
		SparseBitSet b = (SparseBitSet) a.clone();
//		CUtility.ASSERT(a.equals(b) && b.equals(a));
		for (int n=0; n<ITER/2; n++) {
		    int rr = (r.nextInt()>>>1) % v.size();
		    int m = ((Integer)v.elementAt(rr)).intValue();
		    b.clear(m); v.removeElementAt(rr);
		    // check that numbers are removed properly.
//		    CUtility.ASSERT(!b.get(m));
		}
//		CUtility.ASSERT(!a.equals(b));
		SparseBitSet c = (SparseBitSet) a.clone();
		SparseBitSet d = (SparseBitSet) a.clone();
		c.and(a);
//		CUtility.ASSERT(c.equals(a) && a.equals(c));
		c.xor(a);
//		CUtility.ASSERT(!c.equals(a) && c.size()==0);
		d.or(b);
//		CUtility.ASSERT(d.equals(a) && !b.equals(d));
		d.and(b);
//		CUtility.ASSERT(!d.equals(a) && b.equals(d));
		d.xor(a);
//		CUtility.ASSERT(!d.equals(a) && !b.equals(d));
		c.or(d); c.or(b);
//		CUtility.ASSERT(c.equals(a) && a.equals(c));
		c = (SparseBitSet) d.clone();
		c.and(b);
//		CUtility.ASSERT(c.size()==0);
//		System.out.println("Success.");
    }
}