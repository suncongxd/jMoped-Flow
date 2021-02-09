package test.Availability.tax;

class Tax{
	private int tot;//tot:H
	public Tax(int t){
		tot=t;
	}
	public void add(int amount){
		tot+=amount;
	}
	public int get(){
		return tot;
	}
}

class Net{
	private int low;//L
	public int get(int code){//code:L; ret:L
		low=code;
		if(code==0) return 5;//threshod is 5
		else return code;//low rate: 1; high rate:2
	}
}

class Income{
	private int amount;//amount:H
	public Income(int a){
		amount=a;
	}
	public int get(){
		return amount;
	}
}

class CalcTax{//L
	private Income income;//income:H
	private Net net;//net:L
	private Tax tax;//tax:H
	public CalcTax(int i,int t){ //i:H, t:H
		income=new Income(i);
		net=new Net();
		tax=new Tax(t);
	}
	public void calc(){//unsafe
		int amount=income.get();
		int code=(amount<net.get(0))?1:2;
		tax.add(amount*net.get(code));
	}
}

/*class CalcTax{
	private Income income;
	private Net net;
	public CalcTax(Income i,Net n){
		income=i; net=n;
	}
	public void calc(Tax tax){
		int amount=income.get();
		int code=(amount<net.get(0))?1:2;
		tax.add(amount*net.get(code));
	}
}*/