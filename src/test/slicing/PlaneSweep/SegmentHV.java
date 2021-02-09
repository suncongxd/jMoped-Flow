/*************************************************************************
 *  Compilation:  javac SegmentHV.java
 *  Execution:    java SegmentHV
 *  
 *  Axis-aligned line segment (horizontal or vertical).
 *
 *************************************************************************/
package test.slicing.PlaneSweep;

public class SegmentHV{// implements Comparable<SegmentHV> {
    public final int x1, y1;  // lower left
    public final int x2, y2;  // upper right

    // precondition: x1 <= x2, y1 <= y2 and either x1 == x2 or y1 == y2
    public SegmentHV(int x1, int y1, int x2, int y2) {
//        if (x1 >  x2 || y1 >  y2) throw new RuntimeException("Illegal hv-segment");
//        if (x1 != x2 && y1 != y2) throw new RuntimeException("Illegal hv-segment");
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    // is this segment horizontal or vertical?
    public boolean isHorizontal() { return (y1 == y2); }
    public boolean isVertical()   { return (x1 == x2); }

    // compare on y1 coordinate; break ties with other coordinates
    public int compareTo(SegmentHV that) {
        if      (this.y1 < that.y1) return -1;
        else if (this.y1 > that.y1) return +1;
        else if (this.y2 < that.y2) return -1;
        else if (this.y2 > that.y2) return +1;
        else if (this.x1 < that.x1) return -1;
        else if (this.x1 > that.x1) return +1;
        else if (this.x2 < that.x2) return -1;
        else if (this.x2 > that.x2) return +1;
        return 0;
    }
/*        
    public String toString() {
        String s = "";
        if      (isHorizontal()) s = "horizontal: ";
        else if (isVertical())   s = "vertical:   ";
        return s + "(" + x1 + ", " + y1 + ") -> (" + x2 + ", " + y2 + ")";
    }
*/



    // test client
/*    public static void main(String[] args) {
        SegmentHV a = new SegmentHV(15, 20, 15, 60);
        SegmentHV b = new SegmentHV(10, 40, 35, 40);
        SegmentHV c = new SegmentHV(10, 40, 35, 40);
        System.out.println("a = " + a);
        System.out.println("b = " + b);


    }
*/
}

