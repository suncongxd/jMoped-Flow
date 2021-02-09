package test.Availability.dn;

class Node {//L
	public int val;//val:L
}
class D3 {//H
	Node[] m1(int x){ //x:L. ret:L. SAFE
		Node[] m_result;
		m_result=new Node[1];
		int i= 0;
		while (i<1) {
			m_result[i]= new Node();
			m_result[i].val= x; // *** insecure if change "= x" to "= secret"
			i++;
		}
		return m_result;
	}
	
	Node[] m2(int secret){ //secret:H. ret:L. UNSAFE
		Node[] m_result;
		m_result=new Node[1];
		int i= 0;
		while (i<1) {
			m_result[i]= new Node();
			m_result[i].val= secret;
			i++;
		}
		return m_result;
	}

/*	Node[] m_result, m_result$;
	void Pair_m(int x,int secret,int x$,int secret$){
		m_result= new Node[10];
		m_result$= new Node[10];
		int i= 0;
		while (i<10){
			m_result[i]= new Node();
			m_result$[i]= new Node();
			m_result[i].val= x;
			m_result$[i].val= m_result[i].val;
			i++;
		}
	}*/
}
/* If the assignment to val in the original method is changed to
m_result[i].val= secret;
then the transformed program will have
m_result[i].val= secret;
m_result$[i].val= secret$;
instead of
m_result[i].val= x;
m_result$[i].val= m_result[i].val;
and it will be rejected by ESC/Java2. */