package test.slicing.PlaneSweep;
 
public class StdRandom {


    /**
     * Return real number uniformly in [0, 1).
     */
    public static double uniform() {
        return Math.random();
    }

    /**
     * Return real number uniformly in [a, b).
     */
    public static double uniform(double a, double b) {
        return a + Math.random() * (b-a);
    }

    /**
     * Return an integer uniformly between 0 and N-1.
     */
    public static int uniform(int N) {
        return (int) (Math.random() * N);
    }

    /**
     * Return a boolean, which is true with probability p, and false otherwise.
     */
    public static boolean bernoulli(double p) {
        return Math.random() < p;
    }

    /**
     * Return a boolean, which is true with probability .5, and false otherwise.
     */
    public static boolean bernoulli() {
        return bernoulli(0.5);
    }

    /**
     * Return a real number with a standard Gaussian distribution.
     */
    public static double gaussian() {
        // use the polar form of the Box-Muller transform
        double r, x, y;
        do {
            x = uniform(-1.0, 1.0);
            y = uniform(-1.0, 1.0);
            r = x*x + y*y;
        } while (r >= 1 || r == 0);
        return x * Math.sqrt(-2 * Math.log(r) / r);

        // Remark:  y * Math.sqrt(-2 * Math.log(r) / r)
        // is an independent random gaussian
    }

    /**
     * Return a real number from a gaussian distribution with given mean and stddev
     */
    public static double gaussian(double mean, double stddev) {
        return mean + stddev * gaussian();
    }

    /**
     * Return an integer with a geometric distribution with mean 1/p.
     */
    public static int geometric(double p) {
        // using algorithm given by Knuth
        return (int) Math.ceil(Math.log(uniform()) / Math.log(1.0 - p));
    }

    /**
     * Return an integer with a Poisson distribution with mean lambda.
     */
    public static int poisson(double lambda) {
        // using algorithm given by Knuth
        // see http://en.wikipedia.org.sixxs.org/wiki/Poisson_distribution
        int k = 0;
        double p = 1.0;
        double L = Math.exp(-lambda);
        do {
            k++;
            p *= uniform();
        } while (p >= L);
        return k-1;
    }

    /**
     * Return a real number with a Pareto distribution with parameter alpha.
     */
    public static double pareto(double alpha) {
        return Math.pow(1 - uniform(), -1.0/alpha) - 1.0;
    }

    /**
     * Return a real number with a Cauchy distribution.
     */
    public static double cauchy() {
        return Math.tan(Math.PI * (uniform() - 0.5));
    }

    /**
     * Return a number from a discrete distribution: i with probability a[i].
     */
    public static int discrete(double[] a) {
        // precondition: sum of array entries equals 1
        double r = Math.random();
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
            if (sum >= r) return i;
        }
        assert (false);
        return -1;
    }

    /**
     * Return a real number from an exponential distribution with rate lambda.
     */
    public static double exp(double lambda) {
        return -Math.log(1 - Math.random()) / lambda;
    }


    // swaps array elements i and j
    private static void exch(String[] a, int i, int j) {
        String swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }


    /**
     * Rearrange the elements of an array in random order.
     */
    public static void shuffle(Object[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of a double array in random order.
     */
    public static void shuffle(double[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of an int array in random order.
     */
    public static void shuffle(int[] a) {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int r = i + uniform(N-i);     // between i and N-1
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }


    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public static void shuffle(Object[] a, int lo, int hi) {
//        if (lo < 0 || lo > hi || hi >= a.length) throw new RuntimeException("Illegal subarray range");
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            Object temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public static void shuffle(double[] a, int lo, int hi) {
//        if (lo < 0 || lo > hi || hi >= a.length) throw new RuntimeException("Illegal subarray range");
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            double temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }

    /**
     * Rearrange the elements of the subarray a[lo..hi] in random order.
     */
    public static void shuffle(int[] a, int lo, int hi) {
//        if (lo < 0 || lo > hi || hi >= a.length) throw new RuntimeException("Illegal subarray range");
        for (int i = lo; i <= hi; i++) {
            int r = i + uniform(hi-i+1);     // between i and hi
            int temp = a[i];
            a[i] = a[r];
            a[r] = temp;
        }
    }


    /**
     * Unit test.
     */
/*    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);

        double[] t = { .5, .3, .1, .1 };

        for (int i = 0; i < N; i++) {
            StdOut.printf("%2d "  , uniform(100));
            StdOut.printf("%8.5f ", uniform(10.0, 99.0));
            StdOut.printf("%5b "  , bernoulli(.5));
            StdOut.printf("%7.5f ", gaussian(9.0, .2));
            StdOut.printf("%2d "  , discrete(t));
            StdOut.println();
        }
    }
*/
}
