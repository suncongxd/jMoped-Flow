package jmoped.automata;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.bytecode.Opcodes;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.bytecode.ImmediateShortInstruction;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.constants.ConstantClassInfo;
import org.gjt.jclasslib.structures.constants.ConstantMethodrefInfo;

import jmoped.PDSInst;
import jmoped.PDSInfo;
import jmoped.PDSMethod;
import jmoped.MethodWrapper;
import jmoped.PDS;

//import java.lang.String;

public class Automata{
	
	// the param className must contain its path information
	public static void AutomataForNewSeq(List<PDSInst> instList, List<RecordStruct> records,
		ClassFile cf) throws InvalidByteCodeException {
		records.clear();//init
		String clsName="";//the invoke <init> instruction should match the new instruction.
		int mode=0;
		RecordStruct rs=new RecordStruct();
		for(int i=0;i<instList.size();i++){
			PDSInst ins=instList.get(i);
			if(ins.getInstruction()==null)
				continue;
			if(mode==0 && ins.getInstruction().getOpcode()!=Opcodes.OPCODE_NEW){//<0> && !new
				continue;
			}
			if(mode==0 && ins.getInstruction().getOpcode()==Opcodes.OPCODE_NEW){
				clsName=PDSInst.getNewedClassName(cf,ins.getInstruction());
				System.out.println("reach instruction: new "+clsName);////////////
				if(!(ins.getPDSInfo().isIncludedClass(clsName)))
					continue;
				//if(!clsName.equals(className))//<0> && new !A
				//	continue;
				mode=1;
				rs.setStartPos(i);
				rs.setEndPos(i);
				continue;
			}
			if(mode==1 && ins.getInstruction().getOpcode()!=Opcodes.OPCODE_DUP){//<1> && !dup
				mode=0;
				rs.setBack();
				continue;
			}
			if(mode==1 && ins.getInstruction().getOpcode()==Opcodes.OPCODE_DUP){//<1> && dup
				mode=2;
				rs.setEndPos(i);
				continue;
			}
			if(mode==2 && ins.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESPECIAL){
				String[] calledName=PDSInst.getReferencedName(cf,ins.getInstruction());				
				if(calledName[0].equals(clsName) && calledName[1].equals("<init>")){//<2> && invokespecial A.<init>
					System.out.println("invoke "+clsName+".<init> met");////////////
					mode=0;
					rs.setEndPos(i);
					records.add(rs);
					rs=new RecordStruct();
					continue;
				}
				else{//<2> && invokespecial !A.<init>
					mode=0;
					rs.setBack();
					continue;
				}
			}
			if(mode==2 && ins.getInstruction().getOpcode()!=Opcodes.OPCODE_INVOKESPECIAL){
				if(bParamPrepOp(ins)){
					rs.setEndPos(i);
					continue;
				}
				else{
					mode=0;
					rs.setBack();
					continue;
				}
			}
		}
	}
	private static boolean bParamPrepOp(PDSInst inst){
		/* to fix me*/
		return true;
	}
	
	public static void TraceMethodsBeforeTarget(List<PDSInst> instList,MethodInfo targetMethodInfo,
			List<PDSMethod> methods, PDS pDS) throws InvalidByteCodeException{
		methods.clear();
		for(PDSInst inst: instList){
			if(inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESPECIAL ||
				inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESTATIC ||
				inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKEVIRTUAL){
				int idx=((ImmediateShortInstruction)inst.getInstruction()).getImmediateShort();
				int targetMtdIdx=MethodWrapper.indexOfMethodRef(targetMethodInfo.getClassFile(),
										targetMethodInfo.getName(),targetMethodInfo.getDescriptor());
				//System.out.println("index from invoke:"+idx+";index from MethodInfo of target:"+targetMtdIdx);
				if(idx==targetMtdIdx){
					//System.out.println("target method reached in Automata.TraceMethodsBeforeTarget");
					break;
				}
				//invoke other method before target:
				ConstantMethodrefInfo info=(ConstantMethodrefInfo)(targetMethodInfo.getClassFile()
											.getConstantPoolEntry(idx,ConstantMethodrefInfo.class));///
				String[] name={info.getClassInfo().getName(),
						info.getNameAndTypeInfo().getName(),info.getNameAndTypeInfo().getDescriptor()};
				String formattedName=PDSMethod.formatMethodName(name);
				//System.out.println("formatted name of method before target:"+formattedName);
				PDSMethod mtd;
				if(PDSInfo.getSimplified()){
					mtd=pDS.getMethodByFormattedNameForSimplified(
						PDSMethod.formatMethodName(info.getClassInfo().getName()),formattedName,
						info.getNameAndTypeInfo().getDescriptor());
				}
				else{
					mtd=pDS.getMethodByFormattedNameForNonSimplified(formattedName);
				}
				if(mtd!=null){
					System.out.println("method called before target:"+mtd.getClassName()+";"
						+mtd.getFormattedName()+";"+mtd.getDescriptor());
					methods.add(mtd);
				}
			}
		}
		/*at this point the list methods contains all the methods called in initwrapper before target method*/
		
		for(int i=0;i<methods.size();i++){
			PDSMethod mtd=methods.get(i);
			List<PDSInst> list=mtd.getPDSInstList();
			for(PDSInst inst:list){
				if(inst.getInstruction()==null)
					continue;
				if(inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESPECIAL ||
					inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESTATIC ||
					inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKEVIRTUAL){
					int idx=((ImmediateShortInstruction)inst.getInstruction()).getImmediateShort();
					
					//ConstantMethodrefInfo info=(ConstantMethodrefInfo)(targetMethodInfo.getClassFile()
					//						.getConstantPoolEntry(idx,ConstantMethodrefInfo.class));
					ConstantMethodrefInfo info=(ConstantMethodrefInfo)(mtd.getClassFile()
											.getConstantPoolEntry(idx,ConstantMethodrefInfo.class));
					String[] name={info.getClassInfo().getName(),
							info.getNameAndTypeInfo().getName(),info.getNameAndTypeInfo().getDescriptor()};
					String formattedName=PDSMethod.formatMethodName(name);
					System.out.println("method called before target in "+mtd.getFormattedName()+":"+formattedName);
					PDSMethod tmpMtd;
					if(PDSInfo.getSimplified()){
						tmpMtd=pDS.getMethodByFormattedNameForSimplified(
							PDSMethod.formatMethodName(info.getClassInfo().getName()),formattedName,
							info.getNameAndTypeInfo().getDescriptor());
					}
					else{
						tmpMtd=pDS.getMethodByFormattedNameForNonSimplified(formattedName);
					}
					if(tmpMtd!=null){
						if(methods.contains(tmpMtd)){//to find if it has existed in methods.
							continue;
						}
						methods.add(tmpMtd);//append the tmpMtd to the end of methods.
					}
					else{
						//System.out.println("method "+formattedName+" not found in the valid method list");
					}
				}
			}
		}
	}
	public static void TraceMethodsCalledByTarget(List<PDSInst> instList,MethodInfo targetMethodInfo,
			List<PDSMethod> methods, PDS pDS) throws InvalidByteCodeException{
		methods.clear();
		PDSMethod targetMtd=null;
		for(PDSInst inst: instList){
			if(inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESPECIAL ||
				inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESTATIC ||
				inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKEVIRTUAL){
				int idx=((ImmediateShortInstruction)inst.getInstruction()).getImmediateShort();
				int targetMtdIdx=MethodWrapper.indexOfMethodRef(targetMethodInfo.getClassFile(),
										targetMethodInfo.getName(),targetMethodInfo.getDescriptor());
				if(idx==targetMtdIdx){
					ConstantMethodrefInfo info=(ConstantMethodrefInfo)(targetMethodInfo.getClassFile()
											.getConstantPoolEntry(idx,ConstantMethodrefInfo.class));
					String[] name={info.getClassInfo().getName(),
							info.getNameAndTypeInfo().getName(),info.getNameAndTypeInfo().getDescriptor()};
					String formattedName=PDSMethod.formatMethodName(name);
					//System.out.println(formattedName);
					if(PDSInfo.getSimplified()){
						targetMtd=pDS.getMethodByFormattedNameForSimplified(
							PDSMethod.formatMethodName(info.getClassInfo().getName()),formattedName,
							info.getNameAndTypeInfo().getDescriptor());
					}
					else{
						targetMtd=pDS.getMethodByFormattedNameForNonSimplified(formattedName);
					}
					break;
				}
			}
		}
		if(targetMtd!=null){
			methods.add(targetMtd);
			for(int i=0;i<methods.size();i++){
				List<PDSInst> list=methods.get(i).getPDSInstList();
				//if(list==null) System.out.println(111222333);///////////////
				ClassFile cf=methods.get(i).getClassFile();
				for(PDSInst inst:list){
					if(inst.getInstruction()==null)
						continue;
					if(inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESPECIAL ||
						inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESTATIC ||
						inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKEVIRTUAL){
						int idx=((ImmediateShortInstruction)inst.getInstruction()).getImmediateShort();
						
						ConstantMethodrefInfo info=(ConstantMethodrefInfo)(cf.getConstantPoolEntry(idx,
														ConstantMethodrefInfo.class));
						String[] name={info.getClassInfo().getName(),
								info.getNameAndTypeInfo().getName(),info.getNameAndTypeInfo().getDescriptor()};
						String formattedName=PDSMethod.formatMethodName(name);
						System.out.println("method called by target "+targetMtd.getFormattedName()+":"+formattedName);
						PDSMethod tmpMtd;
						if(PDSInfo.getSimplified()){
							tmpMtd=pDS.getMethodByFormattedNameForSimplified(
								PDSMethod.formatMethodName(info.getClassInfo().getName()),formattedName,
								info.getNameAndTypeInfo().getDescriptor());
						}
						else{
							tmpMtd=pDS.getMethodByFormattedNameForNonSimplified(formattedName);
						}
						if(tmpMtd!=null){
							if(methods.contains(tmpMtd)){//to find if it has existed in methods.
								//System.out.println(tmpMtd.getName()+"already found");
								continue;
							}
							methods.add(tmpMtd);//append the tmpMtd to the end of methods.
						}
						else{
							//System.out.println("method "+formattedName+" not found in the valid method list");
						}
					}
				}
			}
		}
	}
	
	public static int FindTargetPos(List<PDSInst> instList,MethodInfo mtdInfo) throws InvalidByteCodeException{
		int rpos=-1;
		for(int i=0;i<instList.size();i++){
			PDSInst inst=instList.get(i);
			if(inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESPECIAL ||
				inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKESTATIC ||
				inst.getInstruction().getOpcode()==Opcodes.OPCODE_INVOKEVIRTUAL){
				int idx=((ImmediateShortInstruction)inst.getInstruction()).getImmediateShort();
				int targetMtdIdx=MethodWrapper.indexOfMethodRef(mtdInfo.getClassFile(),
										mtdInfo.getName(),mtdInfo.getDescriptor());
				if(idx==targetMtdIdx){//target found
					rpos=i;
					return rpos;
				}
			}
		}
		return rpos;
	}
	
	public static void FindPushUndefPositionsBeforeTarget(List<PDSInst> instList,int targetPos, 
		List<Integer> pushUndefPoses) throws InvalidByteCodeException{
		pushUndefPoses.clear();
		for(int i=0;i<targetPos;i++){
			PDSInst inst=instList.get(i);
			if(inst.getInstruction().getOpcode()==Opcodes.OPCODE_ICONST_M1){
				pushUndefPoses.add(i);
			}
		}
	}

	public static boolean matchSigForParams(String paramDesc, int targetPos, List<Integer> pushUndefPoses, 
		List<RecordStruct> newPosForParam) throws InvalidByteCodeException{

		int mode=0;
		StringBuffer tmp=new StringBuffer();
		List<String> list=new ArrayList<String>();
		for(int i=0;i<paramDesc.length();i++){
			char c=paramDesc.charAt(i);
			if((c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z') && mode==0){
				StringBuilder sb=new StringBuilder();
				sb.append(c);
				list.add(sb.toString());
				continue;
			}
			if(mode==0 && c=='L'){
				mode=1;
				tmp.append(c);
				continue;
			}
			if(mode==1){
				tmp.append(c);
				if(c==';'){
					mode=0;
					list.add(tmp.toString());
					tmp=new StringBuffer();
				}
				continue;
			}
			if(mode==0 && c=='['){
				mode=2;
				tmp.append(c);
				continue;
			}
			if(mode==2 && c=='['){
				mode=3;
				tmp.append(c);
				continue;
			}
			if(mode==2 && (c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z')){
				tmp.append(c);
				list.add(tmp.toString());
				tmp=new StringBuffer();
				mode=0;
				continue;
			}
			if(mode==2 && c=='L'){
				tmp.append(c);
				mode=1;
				continue;
			}
			//only support array with two dimensions
			if(mode==3 && (c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z')){
				tmp.append(c);
				mode=0;
				list.add(tmp.toString());
				tmp=new StringBuffer();
				continue;
			}
			if(mode==3 && c=='L'){
				tmp.append(c);
				mode=1;
				continue;
			}
		}
		int pivot=targetPos-1;
		int newRange;
		for(int i=list.size()-1;i>=0;i--){
			String curType=list.get(i);
			if(bPrimitive(curType)){
				if(pushUndefPoses.contains(pivot)){
					pivot--;
					continue;
				}
				else return false;
			}
			else{
				if((newRange=findEndPosInRecordStructList(newPosForParam,pivot))>0){
					pivot=pivot-newRange;
					continue;
				}
				else return false;
			}
		}
		if(pivot==-1){
			System.out.println("Param Signature match exactly");
			return true;
		}
		return false;
	}
	private static boolean bPrimitive(String type){
		while(type.startsWith("[")){
			type=type.substring(1,type.length()-1);
		}
		if(type.equals("B") || type.equals("C") || type.equals("D") || type.equals("F") || type.equals("I") ||
			type.equals("J") || type.equals("S") || type.equals("Z"))
			return true;
		if(type.startsWith("L")){
			return false;
		}
		return false;
	}
	private static int findEndPosInRecordStructList(List<RecordStruct> rsList, int endPos){
		int range=-1;
		for(RecordStruct rs: rsList){
			if(rs.getEndPos()==endPos){
				range=rs.getEndPos()-rs.getStartPos()+1;
			}
		}
		return range;
	}

/*	public static ArrayList<String> deriveParamList(String sp){
		int mode=0;
		StringBuffer tmp=new StringBuffer();
		ArrayList<String> list=new ArrayList<String>();
		
		for(int i=0;i<sp.length();i++){
			char c=sp.charAt(i);
			if((c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z') && mode==0){
				StringBuilder sb=new StringBuilder();
				sb.append(c);
				//System.out.println("string buffer:"+sb.toString());///////////
				list.add(sb.toString());
				continue;
			}
			if(mode==0 && c=='L'){
				mode=1;
				tmp.append(c);
				continue;
			}
			if(mode==1){
				tmp.append(c);
				if(c==';'){
					mode=0;
					list.add(tmp.toString());
					tmp=new StringBuffer();
				}
				continue;
			}
			if(mode==0 && c=='['){
				mode=2;
				tmp.append(c);
				continue;
			}
			if(mode==2 && c=='['){
				mode=3;
				tmp.append(c);
				continue;
			}
			if(mode==2 && (c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z')){
				tmp.append(c);
				list.add(tmp.toString());
				tmp=new StringBuffer();
				mode=0;
				continue;
			}
			if(mode==2 && c=='L'){
				tmp.append(c);
				mode=1;
				continue;
			}
			//only support array with two dimensions
			if(mode==3 && (c=='B' || c=='C' || c=='D' || c=='F' || c=='I' || c=='J' || c=='S' || c=='Z')){
				tmp.append(c);
				mode=0;
				list.add(tmp.toString());
				tmp=new StringBuffer();
				continue;
			}
			if(mode==3 && c=='L'){
				tmp.append(c);
				mode=1;
				continue;
			}
		}
		return list;
	}*/
}
