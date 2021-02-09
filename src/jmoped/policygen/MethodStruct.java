package jmoped.policygen;

import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

class MethodStruct{
	private ArrayList<ParamStruct> params;
	private ParamStruct retvar;
	private String methodName;
	
	MethodStruct(String name){
		methodName=name;
		params=new ArrayList<ParamStruct>();
		retvar=null;
	}

	public void addToParamList(ParamStruct ps){
		params.add(ps);
	}
	public void setRetVar(ParamStruct ps){
		retvar=ps;
	}
/*	public void setParamSecurityLevel(int idx,int level){
		if(idx>=params.size() || idx<0) return;
		params.get(idx).setSecurityLevel(level);
	}
	public void setRetVarSecurityLevel(int level){
		if(retvar==null) return;
		retvar.setSecurityLevel(level);
	}*/
	public ParamStruct getRetVar(){
		return retvar;
	}
	/*********for addPushUndefinedParams of MethodWrapper**************/
	public String getName(){
		return methodName;
	}
	public String getDescriptor(){
		StringBuilder prm=new StringBuilder();
		prm.append("(");
		for(ParamStruct ps: params){
			prm.append(ps.getDescType());
		}
		prm.append(")");
		if(retvar==null)
			prm.append("V");
		else
			prm.append(retvar.getDescType());
		return prm.toString();
	}
	public List<Integer> getLowPrimParamIndices(){
		List<Integer> rList=new ArrayList<Integer>();
		for(int i=0;i<params.size();i++){
			ParamStruct ps=params.get(i);
			if(ps.bPrim && ps.low())
				rList.add(i);
		}
		return rList;
	}
	/*******************************************************************/
	public void Print(String pre){
		System.out.println("========="+pre+"."+methodName+"===========");
		//System.out.println(methodName+"-->");
		for(int i=0;i<params.size();i++){
			params.get(i).Print("param");
		}
		if(retvar!=null)
			retvar.Print("retvar");
		System.out.println("===============================");
	}
}
