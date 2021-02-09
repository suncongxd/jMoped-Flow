package test.slicing.PlaneSweep;

public class HVIntersection {

    // the sweep-line algorithm
//    public static void main(String[] args) {
	public void F(int N){//N:H
//        int N = Integer.parseInt(args[0]);
        int INFINITY = Integer.MAX_VALUE;   // -INFINITY is second smallest integer

        // generate N random h-v segments
        SegmentHV[] segments = new SegmentHV[N];
        for (int i = 0; i < N; i++) {
            int x1  = (int) (Math.random() * 10);
            int x2  = x1 + (int) (Math.random() * 3);
            int y1  = (int) (Math.random() * 10);
            int y2  = y1 + (int) (Math.random() * 5);
            if (Math.random() < 0.5) segments[i] = new SegmentHV(x1, y1, x1, y2);
            else                     segments[i] = new SegmentHV(x1, y1, x2, y1);
//            System.out.println(segments[i]);
        }
//        System.out.println();

        // create events
        MinPQ pq = new MinPQ();
        for (int i = 0; i < N; i++) {
            if (segments[i].isVertical()) {
                Event e = new Event(segments[i].x1, segments[i]);
                pq.insert(e);
            }
            else if (segments[i].isHorizontal()) {
                Event e1 = new Event(segments[i].x1, segments[i]);
                Event e2 = new Event(segments[i].x2, segments[i]);
                pq.insert(e1);
                pq.insert(e2);
            }
        }

        // run sweep-line algorithm
        RangeSearch st = new RangeSearch();

        while (!pq.isEmpty()) {
            Event e = pq.delMin();
            int sweep = e.time;
            SegmentHV segment = e.segment;
           
            // vertical segment
            if (segment.isVertical()) {
                // a bit of a hack here - use infinity to handle degenerate intersections
                SegmentHV seg1 = new SegmentHV(-INFINITY, segment.y1, -INFINITY, segment.y1);
                SegmentHV seg2 = new SegmentHV(+INFINITY, segment.y2, +INFINITY, segment.y2);
/*                Iterable<SegmentHV> list = st.range(seg1, seg2);
                for (SegmentHV seg : list) {
                    System.out.println("Intersection:  " + segment);
                    System.out.println("               " + seg);
                }*/
            }

            // next event is left endpoint of horizontal h-v segment
            else if (sweep == segment.x1) {
                st.add(segment);
            }

            // next event is right endpoint of horizontal h-v segment
            else if (sweep == segment.x2) {
                st.remove(segment);
            }
        }


    }

}

