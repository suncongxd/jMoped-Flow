package test.slicing.NetworkFlow;

//import java.util.*;
 
/**
 * Finds the maximum flow in a flow network.
 * @param E neighbour lists
 * @param C capacity matrix (must be n by n)
 * @param s source
 * @param t sink
 * @return maximum flow
 */
 class Element{
 	int e;
 	Element next;
 }
 
 class Queue{
 	Element head;
 	Element end;
 	Queue(){
 		end=head=new Element();
 		head.next=null;
 	}
 	public boolean isEmpty(){
 		if(head==end)
 			return true;
 		else return false;
 	}
 	public void offer(int s){
 		Element t=new Element();
 		t.e=s;
 		t.next=null;
 		end.next=t;
 		end=end.next;
 	}
 	public int poll(){
 		int tmp=head.next.e;
 		head.next=head.next.next;
 		return tmp;
 	}
 }
public class EdmondsKarp {
	int[][] E;
	int[][] C;
	private final static int MAX_VALUE=10000;
	private int min(int a,int b){
		return a<b?a:b;
	}
    public int EK(int s, int t) {
        int n = C.length;
        // Residual capacity from u to v is C[u][v] - F[u][v]
        int[][] F = new int[n][n];
        while (true) {
            int[] P = new int[n]; // Parent table
//            Arrays.fill(P, -1);
			for(int i=0;i<n;i++)
				P[i]=-1;
            P[s] = s;
            int[] M = new int[n]; // Capacity of path to node
            M[s] = MAX_VALUE;
            // BFS queue
//            Queue<Integer> Q = new LinkedList<Integer>();
			Queue Q=new Queue();
            Q.offer(s);
            LOOP:
            while (!Q.isEmpty()) {
                int u = Q.poll();
                for (int v : E[u]) {
                    // There is available capacity,
                    // and v is not seen before in search
                    if (C[u][v] - F[u][v] > 0 && P[v] == -1) {
                        P[v] = u;
                        M[v] = min(M[u], C[u][v] - F[u][v]);
                        if (v != t)
                            Q.offer(v);
                        else {
                            // Backtrack search, and write flow
                            while (P[v] != v) {
                                u = P[v];
                                F[u][v] += M[t];
                                F[v][u] -= M[t];
                                v = u;
                            }
                            break LOOP;
                        }
                    }
                }
            }
            if (P[t] == -1) { // We did not find a path to t
                int sum = 0;
                for (int x : F[s])
                    sum += x;
                return sum;
            }
        }
    }
}
