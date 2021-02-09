package test.slicing.PlaneSweep;

public class RangeSearch{//<Key extends Comparable<Key>>  {

    private Node root;   // root of the BST

    // BST helper node data type
    private class Node {
        SegmentHV key;            // key
        Node left, right;   // left and right subtrees
        int N;              // node count of descendents

        public Node(SegmentHV key) {
            this.key = key;
            this.N   = 1;
        }
    }

   /*************************************************************************
    *  BST search
    *************************************************************************/

    public boolean contains(SegmentHV key) {
        return contains(root, key);
    }

    private boolean contains(Node x, SegmentHV key) {
        if (x == null) return false;
        int cmp = key.compareTo(x.key);
        if      (cmp == 0) return true;
        else if (cmp  < 0) return contains(x.left,  key);
        else               return contains(x.right, key);
    }

   /*************************************************************************
    *  randomized insertion
    *************************************************************************/
    public void add(SegmentHV key) {
        root = add(root, key);
    }

    // make new node the root with uniform probability
    private Node add(Node x, SegmentHV key) {
        if (x == null) return new Node(key);
        int cmp = key.compareTo(x.key);
        if (cmp == 0) { return x; }
        if (StdRandom.bernoulli(1.0 / (size(x) + 1.0))) return addRoot(x, key);
        if (cmp < 0) x.left  = add(x.left,  key); 
        else         x.right = add(x.right, key); 
        // (x.N)++;
        fix(x);
        return x;
    }


    private Node addRoot(Node x, SegmentHV key) {
        if (x == null) return new Node(key);
        int cmp = key.compareTo(x.key);
        if      (cmp == 0) { return x; }
        else if (cmp  < 0) { x.left  = addRoot(x.left,  key); x = rotR(x); }
        else               { x.right = addRoot(x.right, key); x = rotL(x); }
        return x;
    }




   /*************************************************************************
    *  deletion
    *************************************************************************/
    private Node joinLR(Node a, Node b) { 
        if (a == null) return b;
        if (b == null) return a;

        if (StdRandom.bernoulli((double) size(a) / (size(a) + size(b))))  {
            a.right = joinLR(a.right, b);
            fix(a);
            return a;
        }
        else {
            b.left = joinLR(a, b.left);
            fix(b);
            return b;
        }
    }

    private Node remove(Node x, SegmentHV key) {
        if (x == null) return null; 
        int cmp = key.compareTo(x.key);
        if      (cmp == 0) x = joinLR(x.left, x.right);
        else if (cmp  < 0) x.left  = remove(x.left,  key);
        else               x.right = remove(x.right, key);
        fix(x);
        return x;
    }

    // remove given key if it exists
    public void remove(SegmentHV key) {
        root = remove(root, key);
    }




   /*************************************************************************
    *  Range searching
    *************************************************************************/

    // return all keys between k1 and k2
/*    public Iterable<Key> range(Key k1, Key k2) {
        Queue<Key> list = new Queue<Key>();
        if (less(k2, k1)) return list;
        range(root, k1, k2, list);
        return list;
    }
    private void range(Node x, Key k1, Key k2, Queue<Key> list) {
        if (x == null) return;
        if (lte(k1, x.key))  range(x.left, k1, k2, list);
        if (lte(k1, x.key) && lte(x.key, k2)) list.enqueue(x.key);
        if (lte(x.key, k2)) range(x.right, k1, k2, list);
    }
*/


   /*************************************************************************
    *  Utility functions
    *************************************************************************/

    // return the smallest key
    public SegmentHV min() {
        SegmentHV key = null;
        for (Node x = root; x != null; x = x.left)
            key = x.key;
        return key;
    }
    
    // return the largest key
    public SegmentHV max() {
        SegmentHV key = null;
        for (Node x = root; x != null; x = x.right)
            key = x.key;
        return key;
    }


   /*************************************************************************
    *  useful binary tree functions
    *************************************************************************/

    // return number of nodes in subtree rooted at x
    public int size() { return size(root); }
    private int size(Node x) { 
        if (x == null) return 0;
        else           return x.N;
    }

    // height of tree (empty tree height = 0)
    public int height() { return height(root); }
    private int height(Node x) {
        if (x == null) return 0;
        return 1 + Math.max(height(x.left), height(x.right));
    }


   /*************************************************************************
    *  helper BST functions
    *************************************************************************/

    // fix subtree count field
    private void fix(Node x) {
        if (x == null) return;                 // check needed for remove
        x.N = 1 + size(x.left) + size(x.right);
    }

    // right rotate
    private Node rotR(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        fix(h);
        fix(x);
        return x;
    }

    // left rotate
    private Node rotL(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        fix(h);
        fix(x);
        return x;
    }


   /*************************************************************************
    *  Debugging functions that test the integrity of the tree
    *************************************************************************/

    // check integrity of subtree count fields
    public boolean check() { return checkCount() && isBST(); }

    // check integrity of count fields
    private boolean checkCount() { return checkCount(root); }
    private boolean checkCount(Node x) {
        if (x == null) return true;
        return checkCount(x.left) && checkCount(x.right) && (x.N == 1 + size(x.left) + size(x.right));
    }


    // does this tree satisfy the BST property?
    private boolean isBST() { return isBST(root, min(), max()); }

    // are all the values in the BST rooted at x between min and max, and recursively?
    private boolean isBST(Node x, SegmentHV min, SegmentHV max) {
        if (x == null) return true;
        if (less(x.key, min) || less(max, x.key)) return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    } 



   /*************************************************************************
    *  helper comparison functions
    *************************************************************************/

    private boolean less(SegmentHV k1, SegmentHV k2) {
        return k1.compareTo(k2) < 0;
    }

    private boolean lte(SegmentHV k1, SegmentHV k2) {
        return k1.compareTo(k2) <= 0;
    }


   /*************************************************************************
    *  test client
    *************************************************************************/
/*    public static void main(String[] args) {
        int N = 0;
        RangeSearch<String> st = new RangeSearch<String>();
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            st.add(s);
        }

        System.out.println("height:          " + st.height());
        System.out.println("size:            " + st.size());
        System.out.println("min key:         " + st.min());
        System.out.println("max key:         " + st.max());
        System.out.println("integrity check: " + st.check());
        System.out.println();

        System.out.println("kevin to kfg");
        Iterable<String> list = st.range("kevin", "kfg");
        for (String s : list)
            System.out.println(s);
        System.out.println();

        System.out.println("paste to pasty");
        list = st.range("paste", "pasty");
        for (String s : list)
            System.out.println(s);
        System.out.println();

    }
*/
}

