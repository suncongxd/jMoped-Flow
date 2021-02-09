package test.Availability.jfp;

class Doctor{//L
	Patient pat;//L
	HIVSpecialist hiv;//L
	
	Doctor(){
		pat=new Patient();
		hiv=new HIVSpecialist();
		hiv.hivPat=pat;
	}
}

class Patient{//L
	int name;//name:L
	boolean hiv;//hiv:H
	int drug;//drug:L
}

class HIVSpecialist{//L
	Patient hivPat;//L
}

class Patient2{//L. this class for aliasing leak check.
	int bloodGroup;//L
}

class MainClass{//H
	public void CF_leak(){//unsafe
/*		Patient r=new Patient();
		Doctor dr=new Doctor();
		HIVSpecialist hivSp=new HIVSpecialist();
		dr.pat=r;
		dr.hiv=hivSp;
		dr.hiv.hivPat=dr.pat;*/
		Doctor dr=new Doctor();
		Patient lpatient=dr.pat;
		if(lpatient.hiv) lpatient.drug=1;
		else lpatient.drug=0;	
		//return lpatient;
	}
	
	public boolean Aliasing_leak(boolean g){//g:H, ret:L
		Patient2 lp1=new Patient2();
		Patient2 lp2=new Patient2();
		Patient2 hp;
		int bg=lp1.bloodGroup;
		if(g) hp=lp1;
		else hp=lp2;
		hp.bloodGroup=3;
		if(bg==lp1.bloodGroup) return false;
		else return true;
	}
}