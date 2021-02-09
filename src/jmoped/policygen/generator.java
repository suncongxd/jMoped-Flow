package jmoped.policygen;

/*import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.JDOMException;*/
import java.io.*;
import org.jdom.*;
import org.jdom.output.*;

public class generator{

	public static void GenerateXMLPolicy(String xmlFileName) throws JDOMException, IOException{
		Element root=new Element("policy");
		Document Doc=new Document(root);
		//add content of xml
		Element tgt=new Element("target");
		tgt.setAttribute("class-name","A");
		tgt.setAttribute("sec","0");
		//tgt.addContent(new Element("class-name").setText(target.getClassName()));
		//tgt.addContent(new Element("sec").setText(Integer.toString(target.getSecurityLevel())));
		for(int i=0;i<3;i++){
			Element tmpMtd=new Element("method");
			//MethodStruct ms=target.getMethods().get(i);
			tmpMtd.setAttribute("name","methodname");
			for(int j=0;j<5;j++){
				//ParamStruct ps=ms.getParamList().get(j);
				Element par=new Element("param");
				par.setAttribute("name","paramname");
				par.setAttribute("type","paramtype");
				par.setAttribute("sec","paramsec");
				//par.addContent(new Element("name").setText(ps.getParamName()));
				//par.addContent(new Element("type").setText(ps.getParamType()));
				//par.addContent(new Element("sec").setText(Integer.toString(ps.getSecurityLevel())));
				tmpMtd.addContent(par);
			}
			Element rtv=new Element("retvar");
			rtv.setAttribute("type","retvartype");
			rtv.setAttribute("sec","retvarsec");
			tmpMtd.addContent(rtv);
			Element bePassed=new Element("pass");
			Element verifyingTime=new Element("time");
			tmpMtd.addContent(bePassed);
			tmpMtd.addContent(verifyingTime);
			
			tgt.addContent(tmpMtd);
		}
		
/*		Element lows=new Element("low-non-targets");
		for(int i=0;i<lowNonTargets.size();i++){
			lows.addContent(new Element("name").setText(lowNonTargets.get(i)));
		}
		Element highs=new Element("high-non-targets");
		for(int i=0;i<highNonTargets.size();i++){
			highs.addContent(new Element("name").setText(highNonTargets.get(i)));
		}*/
		
		root.addContent(tgt);
		//root.addContent(lows);
		//root.addContent(highs);
		
		XMLOutputter XMLOut=new XMLOutputter();
		XMLOut.output(Doc, new FileOutputStream(xmlFileName));   
	}
}