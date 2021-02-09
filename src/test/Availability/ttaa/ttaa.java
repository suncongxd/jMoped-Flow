package test.Availability.ttaa;

//Terauchi and Aiken 05
class ttaa{
	//Figure 1:
	public int ttaa1(boolean high,int y){//high:H, y:L, ret:L
		int z=1;
		int x;
		if(high) x=1;
		else x=z;
		return (x+y);
	}
	
	//Figure 4:
	public int ttaa2(int high,int k){ //high:H, k:L, ret:L
		int low;
		int f1=1,f2=0;
		while(high>0){
			f1=f1+f2;f2=f1-f2;high=high-1;
		}
		if(f1>k) low=1;
		else low=0;
		return low;
	}
	
	//Figure 9:
	public int ttaa3(boolean high,int n,int i){//high:H, i:L, n:L
		int f1=1,f2=0;
		int x;
		int low=0;
		while (n>0){
			f1=f1+f2;f2=f1-f2;n=n-1;
		}
		if(high) x=1;
		else x=1;
		while(i<f1){
			low=low+x;i=i+1;
		}
		return low;
	}

}