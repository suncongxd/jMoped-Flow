package jmoped.policygen;

import java.util.ArrayList;

public class TargetStruct{
	private String className;
	private int sec;
	private ArrayList<FieldStruct> flds;
	private ArrayList<MethodStruct> mtds;
	
	private int classIndex;
	
	TargetStruct(String name, int level){
		className=name;
		sec=level;
		flds=null;
		mtds=null;
		
		classIndex=-1;
	}
	public String getClassName(){
		return className;
	}
	public int getSecurityLevel(){
		return sec;
	}
	public void setMethods(ArrayList<MethodStruct> mtdslist){
		mtds=mtdslist;
	}
	public ArrayList<MethodStruct> getMethods(){
		return mtds;
	}
	public void setFields(ArrayList<FieldStruct> fldslist){
		flds=fldslist;
	}
	public ArrayList<FieldStruct> getFields(){
		return flds;
	}
	public void setClassIndex(int idx){
		classIndex=idx;
	}
	public int getClassIndex(){
		return classIndex;
	}
	
	public void setRelativeOffsetForField(String fldName, int offset){
		for(FieldStruct fs: flds){
			if(fldName.equals(fs.getName()))
				fs.setRelativeOffset(offset);
		}
	}
	void Print(){
		System.out.println("**********************************************************************");
		System.out.println("Target Class:"+className+"#\tSecurity Level:"+String.valueOf(sec));
		System.out.println("Field information:");
		for(int i=0;i<flds.size();i++)
			flds.get(i).Print();
		System.out.println("Method Information:");
		for(int i=0;i<mtds.size();i++)
			mtds.get(i).Print(className);
		System.out.println("**********************************************************************");
	}
}