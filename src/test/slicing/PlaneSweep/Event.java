package test.slicing.PlaneSweep;

    public class Event{// implements Comparable<Event> {
        int time;
        SegmentHV segment;

        public Event(int time, SegmentHV segment) {
            this.time    = time;
            this.segment = segment;
        }

        public int compareTo(Event that) {
            if      (this.time < that.time) return -1;
            else if (this.time > that.time) return +1;
            else                            return  0; 
        }
    }