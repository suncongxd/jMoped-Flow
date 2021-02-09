package test.Availability.patient;

class Patient{
	int name;//L
	int hiv;//H
	int getName(){
		return this.name;
	}
	void setName(int n){
		this.name=n;
	}
	void setHIV(int s){
		this.hiv=s;
	}
	int getHIV(){
		return this.hiv;
	}
}
/*
class XPatient extends LPatient{
	int hiv;
	void setHIV(int s){
		this.hiv=s;
	}
	int getHIV(){
		return this.hiv;
	}
}*/

class MainClass{//H
	Patient lp;//L
	MainClass(){
		lp=new Patient();
	}
/*	void Proc1(LPatient lp){
		int lbuf=lp.getName();
		int hbuf=lp.getName();
		XPatient xp=new XPatient();
		xp.setName(lbuf);
	}*/
	void Proc2(int high){//safe
		int lbuf=lp.getName();
		int hbuf=lp.getName();
		Patient xp=new Patient();
		xp.setName(lbuf);
		hbuf=high;
		xp.setHIV(hbuf);
	}
	int Proc3(int high){//ret:L,unsafe
		int lbuf=lp.getName();
		int hbuf=lp.getName();
		Patient xp=new Patient();
		xp.setName(lbuf);
		hbuf=high;
		xp.setHIV(hbuf);
		lbuf=hbuf;
		return lbuf;
	}
	void Proc4(int high){//unsafe
		int lbuf=lp.getName();
		int hbuf=lp.getName();
		Patient xp=new Patient();
		xp.setName(lbuf);
		hbuf=high;
		xp.setHIV(hbuf);
		lp.setName(xp.getHIV());
	}
}