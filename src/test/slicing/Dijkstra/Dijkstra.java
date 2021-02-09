//Data Structures and Algorithms in Java, Peter Drake
package test.slicing.Dijkstra;

public class Dijkstra{
	private double[][] edges;//H
/*	private double getCost(int i, int j) {
		if (i == j) {
			return 0.0;
		}
		if (edges[i][j] == 0.0) {
			return Double.POSITIVE_INFINITY;
		}
		return edges[i][j];
	}*/
/*	protected int cheapest(double[] distances, boolean[] visited) {
		int best = -1;
		for (int i = 0; i < edges.length; i++) {
			if (!visited[i] && ((best < 0) || (distances[i] < distances[best]))) {
				best = i;
			}
		}
		return best;
	}*/
/*	private double min(double a,double b){
		if(a<b) return a;
		else return b;
	}*/
	public double[] distancesFrom(int source) {//source:L; ret:L
		double[] result = new double[edges.length];
		double tmp;
		//java.util.Arrays.fill(result, Double.POSITIVE_INFINITY);
		for(int i=0;i<edges.length;i++)
			result[i]=18;//Double.POSITIVE_INFINITY;
		result[source] = 0;
		boolean[] visited = new boolean[edges.length];
		for (int i = 0; i < edges.length; i++) {
//			int vertex = cheapest(result, visited);
			int vertex=-1;
			for (int k = 0; k < edges.length; k++) {
				if (!visited[k] && ((vertex < 0) || (result[k] < result[vertex]))) {
					vertex = k;
				}
			}
			visited[vertex] = true;
			for (int j = 0; j < edges.length; j++) {
				if (vertex == j) {
					tmp=0.0;
				}
				else if (edges[vertex][j] == 0.0) {
					tmp=18;//Double.POSITIVE_INFINITY;
				}
				else tmp=edges[vertex][j];
				//result[j] = min(result[j], result[vertex] + tmp);
				if(result[j]> result[vertex]+tmp)
					result[j]=result[vertex]+tmp;
			}
		}
		return result;
	}
/*	public int size() {
		return edges.length;
	}*/

}