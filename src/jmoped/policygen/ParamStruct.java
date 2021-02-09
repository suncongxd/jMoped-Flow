package jmoped.policygen;

import jmoped.PDSInfo;

class ParamStruct extends FieldStruct{
	public ParamStruct(String nm, String type, int level, PDSInfo info){
		super(nm,type,level,info);
	}
}
/*class ParamStruct{
	String name;
	String type;
	boolean bArray;
	int length;
	int sec;
	
	ParamStruct(String nameStr,String typeStr, int level){
		name=nameStr;
		if(typeStr.endsWith("[]")){
			type=typeStr.replaceAll("[]","");
			bArray=true;
		} else {
			this.type=typeStr;
			bArray=false;
		}
		length=-1;
		sec=level;
	}
	public void setSecurityLevel(int level){
		sec=level;
	}
	public void setArrayLength(int len){
		length=len;
	}
	public void Print(String pre){
		System.out.println(pre+":<"+name+","+type+(bArray?"["+String.valueOf(length)+"]":"")+","+String.valueOf(sec)+">");
	}
}*/