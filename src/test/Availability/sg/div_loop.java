package test.Availability.sg;

class div_loop{//H. bit=1, heapSize=1
	//example 3
	public int divide1(int low, int high){ //ret:L,unsafe
		return low/high;
	}
	
	//example 4: expose whether 'high' is zero
	public int divide2(int low, int high){//ret:L, unsafe
		int res;
		try{
			low=low/high;
			res=0;
		}
		catch(ArithmeticException ae){
			res=1;
		}
		return res;
	}
	
	//example 5:
	public int divide3(int low,int high){//ret:L. SAFE
		int res;
		try{
			low=low/high;
			res=0;
		}
		catch(ArithmeticException ae){
			res=1;
		}
		finally{
			res=2;
		}
		return res;
	}
	
	//example 1:
	public int loop1(int x){//x:H, ret:L. unsafe
		int res;
		for (res=0;res<x;res++);
		return res;
	}
	
	//example 2:
	public int loop2(int x){//x:H, ret:L. SAFE
		int res;
		for(res=0;res<x;res++);
		res=10;
		return res;
	}
	
	//Fig. 3
	public int loop_rec(int l,int v,int p){//l:H, v:L, p:L, ret:L. UNSAFE
		if(v>0){
			return loop_rec(l-1,l-1,p+1);
		}
		else {
			return p;
		}
	}
}