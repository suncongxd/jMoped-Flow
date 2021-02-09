package jmoped.policygen;

import java.util.ArrayList;
import jmoped.policygen.Low;
import jmoped.policygen.High;
import jmoped.policygen.UndefLevel;
import jmoped.PDSInfo;

public class FieldStruct{
	String name;
	String descType;
	int sec;
	
	PDSInfo pdsInfo;
	
	int rel_offset;//the relative offset from the start pos of heap object.
	boolean bPrim;//whether the field is primitive(array) or not
	boolean bArray;//whether the field is array type or not
	
	/*****************constructor related******************************/
	public FieldStruct(String nm, String type, int level, PDSInfo info){
		name=nm;
		pdsInfo=info;
		descType=MapType2BytecodeType(type);
		sec=(level!=Low.value && level!=High.value)? UndefLevel.value : level;
	}
	
	private String MapType2BytecodeType(String type){
		if(!type.contains("[")){//not array
			bArray=false;
			bPrim=isPrimitiveNonArray(type);
			return MapNonArrayType2BytecodeType(type);
		}
		else{//array
			bArray=true;
			String tmp=type;
			String tmp2="";
			while(tmp.contains("[")){
				tmp=tmp.substring(0,tmp.lastIndexOf('['));
				tmp2=tmp2+"[";
			}
			bPrim=isPrimitiveNonArray(tmp);
			tmp2=tmp2+MapNonArrayType2BytecodeType(tmp);
			return tmp2;
		}
	}
	private boolean isPrimitiveNonArray(String natype){
		if(natype.equals("byte") || natype.equals("char") || natype.equals("double") ||
			natype.equals("float") || natype.equals("int") || natype.equals("long") ||
			natype.equals("short") || natype.equals("boolean"))
			return true;
		else return false;
	}
	private String MapNonArrayType2BytecodeType(String type){
		if(type.equals("byte"))
			return "B";
		if(type.equals("char"))
			return "C";
		if(type.equals("double"))
			return "D";
		if(type.equals("float"))
			return "F";
		if(type.equals("int"))
			return "I";
		if(type.equals("long"))
			return "J";
		if(type.equals("short"))
			return "S";
		if(type.equals("boolean"))
			return "Z";

		/*here need to be modified, if the type is Object, we can not get java/lang/Object for use,
		 *instead we derive $policyPath/Object... fix me*/
		String s=pdsInfo.getPolicy().getPolicyPath()+type;
		if(!pdsInfo.isIncludedClass(s))
			return "";
		return "L"+s+";";
	}
	/***************************************************/
	public String getName(){
		return name;
	}
	public void setRelativeOffset(int offset){
		rel_offset=offset;
	}
	public boolean primitive(){
		return bPrim;
	}
	public boolean low(){
		if(sec==Low.value) return true;
		else return false;
	}
	public boolean bArray(){
		return bArray;
	}
	public int getOffset(){
		return rel_offset;
	}
	public String getDescType(){
		return descType;
	}
	public void Print(String pre){
		System.out.println(pre+":<name:"+name+";type:"+descType+";sec:"+String.valueOf(sec)+
			";offset:"+rel_offset+";bPrim:"+bPrim+";bArray:"+bArray+">");
	}
	public void Print(){
		System.out.println("Field:<name:"+name+";type:"+descType+";sec:"+String.valueOf(sec)+
			";offset:"+rel_offset+";bPrim:"+bPrim+";bArray:"+bArray+">");
	}
}