package jmoped.policygen;

import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.JDOMException;

import jmoped.PDSInfo;
import jmoped.policygen.ParamStruct;

public class PolicyInfo{
	TargetStruct targetStruct;
	ArrayList<TargetStruct> nonTargets;
	String policyPath;//record the path in Read() since PDSInfo.getClassIndex() require the
					//class name with path to find the index of class.

	PDSInfo pdsInfo;
	
	public PolicyInfo(){
		targetStruct=null;
		nonTargets=new ArrayList<TargetStruct>();
		pdsInfo=null;
	}
	
	public void Read(String policy_file) throws JDOMException, IOException {
		policyPath=policy_file.substring(0,policy_file.lastIndexOf("/")+1);
		//System.out.println("policy path: "+policyPath);///////////////
		File f=new File(policy_file);
		FileInputStream fs=new FileInputStream(f);
		SAXBuilder sb=new SAXBuilder();
		Document doc=sb.build(fs);
		Element root=doc.getRootElement();
		Element tar=root.getChild("target");
		if(tar!=null){//parse the target class
			String class_name=tar.getAttributeValue("class-name");
			int level=tar.getAttribute("sec").getIntValue();
			targetStruct=new TargetStruct(class_name,level);
			//System.out.println(targetStruct.getClassName()+"#"+String.valueOf(targetStruct.getSecurityLevel()));
			/*********************************************/
			List<Element> flds=tar.getChildren("field");
			ArrayList<FieldStruct> tmpFlds=new ArrayList();
			for(Element fld : flds){
				tmpFlds.add(new FieldStruct(fld.getAttributeValue("name"),
											fld.getAttributeValue("type"),
											fld.getAttribute("sec").getIntValue(),pdsInfo));
			}
			//for(int i=0;i<tmpFlds.size();i++)//
			//	tmpFlds.get(i).Print();//
			targetStruct.setFields(tmpFlds);
			/********************************************/
			List<Element> mtds=tar.getChildren("method");
			ArrayList<MethodStruct> tmpMtds=new ArrayList();
			for(Element mtd: mtds){
				MethodStruct ms=new MethodStruct(mtd.getAttributeValue("name"));
				
				ParamStruct ps;
				List<Element> prms=mtd.getChildren("param");
				for(Element prm: prms){
					ps=new ParamStruct(prm.getAttributeValue("name"),
									prm.getAttributeValue("type"),
									prm.getAttribute("sec").getIntValue(),pdsInfo);
					ms.addToParamList(ps);
				}
				
				Element rt=mtd.getChild("retvar");
				if(rt!=null){
					//System.out.println("kaokaokao");
					ps=new ParamStruct("RETURN_VAR",
									rt.getAttributeValue("type"),
									rt.getAttribute("sec").getIntValue(),pdsInfo);
					ms.setRetVar(ps);
				}
				//ms.Print();
				tmpMtds.add(ms);
			}
			targetStruct.setMethods(tmpMtds);
			//targetStruct.Print();
		}
		List<Element> non_tar=root.getChildren("non-target");
		if(!non_tar.isEmpty()){//parse the non-target classes, only the fields related. methods are omitted.
			List<TargetStruct> tmpTSList=new ArrayList<TargetStruct>();
			for(Element ntar: non_tar){
				//System.out.println("non-target class:"+ntar.getAttributeValue("class-name")+";"+ntar.getAttributeValue("sec"));
				TargetStruct tmpTS=new TargetStruct(ntar.getAttributeValue("class-name"),ntar.getAttribute("sec").getIntValue());
				List<Element> flds=ntar.getChildren("field");
				ArrayList<FieldStruct> tmpFlds=new ArrayList<FieldStruct>();
				for(Element fld: flds){
					tmpFlds.add(new FieldStruct(fld.getAttributeValue("name"),
												fld.getAttributeValue("type"),
												fld.getAttribute("sec").getIntValue(),pdsInfo));
				}
				tmpTS.setFields(tmpFlds);
				tmpTS.setMethods(new ArrayList<MethodStruct>());//set method list empty, since we don't care.
				tmpTSList.add(tmpTS);
			}
			nonTargets.addAll(tmpTSList);
			//for(TargetStruct ts: tmpTSList)
			//	ts.Print();
		}
	}
	
	public String getTargetClassName(){
		return targetStruct.getClassName();
	}
	public TargetStruct getTargetClass(){
		return targetStruct;
	}
	public int getTargetLevel(){
		return targetStruct.getSecurityLevel();
	}
	
	public void setClassIndex(String className, int idx){
		if(className.equals(targetStruct.getClassName())){
			targetStruct.setClassIndex(idx);
			return;
		}
		for(TargetStruct ts: nonTargets){
			if(className.equals(ts.getClassName())){
				ts.setClassIndex(idx);
				return;
			}
		}
	}
	public TargetStruct getTargetByClassIndex(int classidx){
		if(targetStruct.getClassIndex()==classidx){
			return targetStruct;
		}
		for(TargetStruct ts: nonTargets){
			if(ts.getClassIndex()==classidx)
				return ts;
		}
		return null;
	}
	public int getClassIndex(String className){
		if(className.equals(targetStruct.getClassName())){
			return targetStruct.getClassIndex();
		}
		for(TargetStruct ts: nonTargets){
			if(className.equals(ts.getClassName()))
				return ts.getClassIndex();
		}
		return -1;//not find TargetStruct corresponding to className
	}
	
	public ArrayList<TargetStruct> getNonTargetClasses(){
		return nonTargets;
	}
	
	public String getPolicyPath(){
		return policyPath;
	}

	public void setPDSInfo(PDSInfo info){
		pdsInfo=info;
	}
	
	public void Print(){
		targetStruct.Print();
		for(TargetStruct ts: nonTargets){
			ts.Print();
		}
	}
	
	public List<Integer> getLowPrimIndices(String mtdName, String mtdDesc){
		List<Integer> rList=new ArrayList<Integer>();
		List<MethodStruct> mtds=getTargetClass().getMethods();
		System.out.println("--++--to find<"+mtdName+","+mtdDesc+">");/////////////
		for(MethodStruct mtd: mtds){
			System.out.println("in target class method list:<"+mtd.getName()+","+mtd.getDescriptor()+">");//////////
			if(mtd.getName().equals(mtdName) && mtd.getDescriptor().equals(mtdDesc)){
				rList=mtd.getLowPrimParamIndices();
			}
		}
		return rList;
	}
	public boolean lowPrimRetVar(String mtdName, String mtdDesc){
		List<MethodStruct> mtds=getTargetClass().getMethods();
		for(MethodStruct ms: mtds){
			if(ms.getName().equals(mtdName) && ms.getDescriptor().equals(mtdDesc)){
				if(ms.getRetVar()!=null){
					return ms.getRetVar().low() && ms.getRetVar().bPrim;
				}
			}
		}
		return false;
	}
	
	public int getClassLevelByName(String className){
		//System.out.println("&&&&&&:"+this.getPolicyPath()+targetStruct.getClassName());
		String tmpName=getPolicyPath()+targetStruct.getClassName();
		if(tmpName.equals(className)){
			return targetStruct.getSecurityLevel();
		}
		for(TargetStruct ts: nonTargets){
			tmpName=getPolicyPath()+ts.getClassName();
			if(tmpName.equals(className))
				return ts.getSecurityLevel();
		}
		return High.value;
	}
}