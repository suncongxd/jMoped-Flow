package jmoped;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gjt.jclasslib.bytecode.AbstractInstruction;
import org.gjt.jclasslib.bytecode.BranchInstruction;
import org.gjt.jclasslib.bytecode.ImmediateByteInstruction;
import org.gjt.jclasslib.bytecode.ImmediateShortInstruction;
import org.gjt.jclasslib.bytecode.IncrementInstruction;
import org.gjt.jclasslib.bytecode.Opcodes;
import org.gjt.jclasslib.bytecode.SimpleInstruction;
import org.gjt.jclasslib.structures.CPInfo;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.constants.ConstantMethodrefInfo;
import org.gjt.jclasslib.structures.constants.ConstantNameAndTypeInfo;
import org.gjt.jclasslib.structures.AccessFlags;

import jmoped.automata.Automata;
import jmoped.automata.RecordStruct;
import jmoped.PDS;

public class MethodWrapper extends PDSMethod
{
	private String formattedName;
	private ClassFile classFile;
	private PDSInfo info;
	private MethodInfo method;//target method
	/**
	 * See {@link #getNewVarSlot()}.
	 */
	private int varSlot= 1;
	/**
	 * See {@link #getNewLabelOffset()}.
	 */
	private int labelOffset = 1;
	
	private int maxLocalStack;
	
	/*added by suncong*/
	private static int OPCODE_SLOT=-1;
	private PDS pDS;
/* constructor, modified by suncong to add a param of PDS in that the Automata need PDS to trace
 * the instructions */
	public MethodWrapper(MethodInfo method, PDSInfo pdsInfo, PDS pds)
		throws InvalidByteCodeException, IOException 
	{
		super(method.getClassFile(), method, pdsInfo);
		this.classFile = method.getClassFile();
		this.method = method;
		//formattedName = "initialWrapperFor_" + PDSMethod.formatMethodName//commented by suncong, for simpler
		formattedName = "IW_" + PDSMethod.formatMethodName
			(new String[] {classFile.getThisClassName(), method.getName(), method.getDescriptor()});
		this.info = pdsInfo;
		//by suncong:
		this.pDS=pds;
	}
/*********************** toRemopla*****************************/
	public String toRemoplaHead() throws InvalidByteCodeException 
	{
		StringBuilder out = new StringBuilder("module void ");
		return out.append(formattedName).append("()").toString();
	}
/* important to modify for self-composition, use the information get from the
 * getInstanceWrapperInstructions() and getStaticWrapperInstructions(). pass the PDSClass list
 *to this method, in order to */
	public String toRemopla(ArrayList<PDSClass> classList) throws InvalidByteCodeException 
	{
		 /*generate the original Wrapper instructions*/
		StringBuilder out = new StringBuilder(toRemoplaHead()).append("\n{\n");
		List<PDSInst> instructions = isStatic() ? getStaticWrapperInstructions() : getInstanceWrapperInstructions();
		
		/*approximate execution on the instructions before target method, without considering branches
		 *later need to find the new instructions in these methods.*/
		List<PDSMethod> mtdsBeforeTarget=new ArrayList<PDSMethod>();
		Automata.TraceMethodsBeforeTarget(instructions,this.method,mtdsBeforeTarget,pDS);
		printMethodList(mtdsBeforeTarget,"methods called before target method");
		//approximate execution to find the methods called by the target:
		List<PDSMethod> mtdsInTarget=new ArrayList<PDSMethod>();
		Automata.TraceMethodsCalledByTarget(instructions,this.method,mtdsInTarget,pDS);
		printMethodList(mtdsInTarget,"methods called by target method");
		
		//List<PDSMethod> intersection=FindInterSectionMethods(mtdsBeforeTarget,mtdsinTarget);
		for(PDSMethod md:mtdsBeforeTarget){
			md.generateNewPosList();
			md.printNewPosList();////
		}
		for(PDSMethod md:mtdsInTarget){
			md.generateNewPosList();
			md.printNewPosList();////
		}
		/***********************************************************************/
		int targetPos=Automata.FindTargetPos(instructions,this.method);
		//System.out.println("Pos of invoke target:"+targetPos);
		List<Integer> pushUndefPoses=new ArrayList<Integer>();
		Automata.FindPushUndefPositionsBeforeTarget(instructions,targetPos,pushUndefPoses);
		//for(int i: pushUndefPoses)//
		//	System.out.println(i);//
		List<RecordStruct> newPosForParam=new ArrayList<RecordStruct>();
		Automata.AutomataForNewSeq(instructions,newPosForParam,classFile);
		//for(RecordStruct rs: newPosForParam)//
		//	System.out.println("<"+rs.getStartPos()+","+rs.getEndPos()+">");//
		excludeUnrelatedPushUndef(newPosForParam, pushUndefPoses);
		//System.out.println("related pushUndefs:");//
		//for(int i: pushUndefPoses) System.out.println(i);//
		//System.out.println(getParamSigForTarget());//
		/*do we really need matching params?*/
		boolean paramMatch=Automata.matchSigForParams(getParamSigForTarget(),targetPos,pushUndefPoses,newPosForParam);
		if(!paramMatch){
			System.out.println("Instructions of initial state preparation incorrectly matched!!!");
		}
		/******************************************/
		int postOpPosition=findPostOperationPosition(targetPos,newPosForParam);
		System.out.println("post operation position:"+postOpPosition);////////
		PDSMethod mtd=new PDSMethod(formattedName, maxLocalStack, classFile);
		int pi=getNewVarSlot();
		int pj=getNewVarSlot();
		int af_instructions_length=0;
		if(postOpPosition!=-1){//need to add post operations, add at the position of (postOpPosition+1)
			List<PDSInst> af_instructions=new ArrayList<PDSInst>();
			int nextLabel=PostInitOp(af_instructions,mtd,pi,pj);
			PDSInst next_ins=instructions.get(postOpPosition+1);////////////
			instructions.addAll(postOpPosition+1,af_instructions);
			af_instructions_length=af_instructions.size();
			//PDSInst ins=instructions.get(postOpPosition+1+af_instructions_length);
			//ins.getInstruction().setOffset(nextLabel);
			next_ins.getInstruction().setOffset(nextLabel);
		}
		List<PDSInst> stub_instructions=new ArrayList<PDSInst>();
		int nextLabel2=StubOp(stub_instructions,mtd,pi,pj);
		PDSInst next_ins2=instructions.get(targetPos+af_instructions_length+1);
		instructions.addAll(targetPos+af_instructions_length+1,stub_instructions);
		if(nextLabel2!=-1){//next label in the stub
			next_ins2.getInstruction().setOffset(nextLabel2);
		}
/*		List<RecordStruct> records=new ArrayList<RecordStruct>();
		Automata.AutomataForNewSeq(instructions,records,classFile);
		if(records.size()>0){
			
			PDSMethod mtd=new PDSMethod(formattedName, maxLocalStack, classFile);*/
			/*set the offset of instruction following the slot instructions, that
			 *is the offset of the original instruction following the method <init>*/
			/*int abs_offset;
			for(RecordStruct rs: records){
				abs_offset=getNewLabelOffset();
				if(rs.getEndPos()>=instructions.size()-1){
					//rs.getEndPos() is the last instruction of instructions, add a 'return' instruction
					//at the end of instructions, this should be never happen
					AbstractInstruction absInst=new SimpleInstruction(Opcodes.OPCODE_RETURN);
					absInst.setOffset(abs_offset);
					PDSInst newInst=new PDSInst(mtd,absInst,info,false);
					newInst.translate();
					instructions.add(newInst);
				}
				else{
					instructions.get(rs.getEndPos()+1).setOffsetForAbstractInst(abs_offset);
				}
			}*/
/*			for(RecordStruct rs: records){
				//generate a series of PDSInst for post-init push operations 
			 	//and add to the position record(i).getEndPos()+1
				List<PDSInst> af_instructions=new ArrayList<PDSInst>();
				//int next_offset=instructions.get(rs.getEndPos()+1).getOffsetFromAbstractInst();
				PostInitializationOp(af_instructions,mtd,instructions.get(rs.getStartPos()).getNewedLowCount());
				instructions.addAll(rs.getEndPos()+1,af_instructions);
			}
		}*/
		/********************************************************************************/
		for (int i = 0; i < maxLocalStack; i++) {
			out.append("int s").append(i).append(";\n");
			out.append("int s").append(i).append(info.getTTail()).append(";\n");
		}
		
		for (int i = 0; i < varSlot; i++) {
			out.append("int v").append(i).append(";\n");//not composed yet.
		}

		int offset = labelOffset + 1;
		for (PDSInst i : instructions) {
			AbstractInstruction inst = i.getInstruction();
			if (inst.getOffset() == 0) {
				inst.setOffset(offset++);
			}
			out.append(i.toRemopla());
			/*added by suncong*/
			if(i.getToRemPair())
				out.append(i.toRemoplaPair());
		}
		
		return out.append("\n}\n").toString();
	}

/*********************************************************/
/* if we want to verify static method, this method is used for initialization*/
	private List<PDSInst> getStaticWrapperInstructions() throws InvalidByteCodeException
	{
		PDSInfo pdsInfo = getInfo();
		List<PDSInst> instructions = new ArrayList<PDSInst>();
		
		int methodIndex = indexOfMethodRef(classFile, method.getName(), method.getDescriptor());
		// + 1 for heap index?
		// 3 for array initialization and stubs, 2 for postinitoperations(<3, thus omitted)
		maxLocalStack = Math.max(3, getParamCount() + 1);

		PDSInst newPDSInst;
		PDSMethod mtd=new PDSMethod(formattedName, maxLocalStack, classFile);
		/*-----------------------------------------------------------*/
		addPushUndefinedParams(instructions, method.getDescriptor(), getParamCount(), mtd);
		/*-----------------------------------------------------------*/
		newPDSInst=new PDSInst(mtd,new ImmediateShortInstruction(Opcodes.OPCODE_INVOKESTATIC, methodIndex),info);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//addInstruction(instructions, new ImmediateShortInstruction(Opcodes.OPCODE_INVOKESTATIC, methodIndex), maxLocalStack);
		/*-----------------------------------------------------------*/
		newPDSInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_RETURN),info);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//addInstruction(instructions, new SimpleInstruction(Opcodes.OPCODE_RETURN), maxLocalStack);
		/*-----------------------------------------------------------*/
		return instructions;
	}
/**********************important to us, called by MethodWrapper.toRemopla()*************************/

	private List<PDSInst> getInstanceWrapperInstructions() throws InvalidByteCodeException
	{
		PDSInfo pdsInfo = getInfo();
		List<PDSInst> instructions = new ArrayList<PDSInst>();
		
		int constructorIndex = indexOfMethodRef(classFile, "<init>", null);
		String constructorDesc = getDescriptor(classFile, constructorIndex);
		int paramCount = getParamCount(classFile, constructorIndex);
		/*********************maxLocalStack Initialization****************/
		//here we suppose each method has its array param ahead of all the non-array params.
		int arrayCount=0;
		int non_arrayCount=0;
		for (int i = 0; i < getParamCount(); i++) {
			if (PDSMethod.isArray(method.getDescriptor(),i))
				arrayCount++;
			else non_arrayCount++;
		}
		//(arrayCount+3-1) for array params
		if(arrayCount>0){
			maxLocalStack=(arrayCount+3-1)+1;//1 for 'this'
			if(non_arrayCount>2)//2 units free after array params are initialized.
				maxLocalStack=maxLocalStack+non_arrayCount-2;
		}
		else{//no array param
			maxLocalStack=getParamCount()+1;
		}
		//suppose the constructor do not have any array param:
		maxLocalStack=Math.max(maxLocalStack,paramCount+1);
		//3 for stub, and 2 for postinitoperations:
		maxLocalStack=Math.max(maxLocalStack,3);
		//maxLocalStack = Math.max(4, Math.max(getParamCount(), paramCount) + 1);
		/*******************************************************************/
		PDSInst newPDSInst;
		PDSMethod mtd=new PDSMethod(formattedName, maxLocalStack, classFile);
		/*new A: s0=ptr, heap[ptr]=1,ptr=ptr+1+1--------------------*/
		newPDSInst=new PDSInst(mtd, new ImmediateShortInstruction(Opcodes.OPCODE_NEW, classFile.getThisClass()),pdsInfo,false);
		newPDSInst.newInstForInitWrapper();
		instructions.add(newPDSInst);
		/*dup: s0=s0, s1=s0,...-------------------------------------*/
		newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_DUP), pdsInfo);
		//newPDSInst.translate(); call pushWithInstForInitWrapper() instead of translate()
		newPDSInst.pushWithInstForInitWrapper(info.stackIndex(0), info.stackIndex(0)+info.getTTail());
		instructions.add(newPDSInst);
		/*-----------------------------------------------------------*/
		addPushUndefinedParams(instructions, constructorDesc, paramCount, mtd);
		/*invokespecial A.<init>-------------------------------------*/
		newPDSInst=new PDSInst(mtd, new ImmediateShortInstruction(Opcodes.OPCODE_INVOKESPECIAL, constructorIndex),	pdsInfo);
		//newPDSInst.translate(); call invokeSpecialInstForInitWrapper() instead of translate()
		newPDSInst.invokespecialInstForInitWrapper();
		instructions.add(newPDSInst);
		/*-----------------------------------------------------------
		 *until now unsupport non-primitive param except 'this'*/
		List<Integer> lowPrimIndices=info.getPolicy().getLowPrimIndices(method.getName(),method.getDescriptor());
		//System.out.println("&&^^:"+method.getDescriptor()+";"+lowPrimIndices.size());//////////
		addPushUndefinedParamsForTarget(instructions, method.getDescriptor(), getParamCount(), mtd,
								lowPrimIndices);
		/*-----------------------------------------------------------*/
		newPDSInst=new PDSInst(mtd, new ImmediateShortInstruction(Opcodes.OPCODE_INVOKESPECIAL,
				indexOfMethodRef(classFile, method.getName(), method.getDescriptor())),pdsInfo);
		//newPDSInst.translate();
		newPDSInst.invokespecialInstForInitWrapper();
		instructions.add(newPDSInst);
		/*-----------------------------------------------------------*/
		newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_RETURN), pdsInfo,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		/*-----------------------------------------------------------*/
		return instructions;
	}

/***********************************************************/
	private void addPushUndefinedParams(List<PDSInst> instructions, String descriptor, int paramCount, 
			PDSMethod mtd) throws InvalidByteCodeException
	{
		PDSInst newPDSInst;
		for (int i = 0; i < paramCount; i++) {
			newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_ICONST_M1), info);
			//newPDSInst.translate();
			newPDSInst.pushWithInstForInitWrapper("undef","undef");
			instructions.add(newPDSInst);

			if (PDSMethod.isArray(descriptor, i)) {
				newPDSInst=new PDSInst(mtd, new ImmediateByteInstruction
					(Opcodes.OPCODE_ANEWARRAY, true, Opcodes.NEWARRAY_T_INT), info);
				newPDSInst.translate();
				instructions.add(newPDSInst);
 				initializeArrayToUndefined(instructions, mtd, false);
			}
		}
	}
	private void addPushUndefinedParamsForTarget(List<PDSInst> instructions, String descriptor,
		int paramCount, PDSMethod mtd, List<Integer> lowPrimIndices) throws InvalidByteCodeException{
		PDSInst newPDSInst;
		for (int i = 0; i < paramCount; i++) {
			//newPDSInst.translate();
			if(lowPrimIndices.contains(i)){
				//System.out.println("$$$descriptor:"+descriptor);
				if(!PDSMethod.isArray(descriptor,i)){//low prim non-array
					newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_ICONST_M1), info);
					newPDSInst.pushWithInstForInitWrapper("undef",info.getStackName()+"0");
					instructions.add(newPDSInst);
				}
				else{//low primitive array
					System.out.println("low indice for array:"+i);////////
					//at first, push the array length for heap and heapt respectively, same and indefinite length.
					newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_ICONST_M1), info);
					newPDSInst.pushWithInstForInitWrapper("undef",info.getStackName()+"0");//same length
					instructions.add(newPDSInst);
		//if
		//ptr+s0+1>15 || ptrt+s0t+1>15 ->	goto HeapOverFlow;
		//else ->s0=ptr, heap[ptr]=s0, ptr=ptr+s0+1, s0t=ptrt, heapt[ptrt]=s0t, ptrt=ptrt+s0t+1;
		//fi
					newPDSInst=new PDSInst(mtd, 
						new ImmediateShortInstruction (Opcodes.OPCODE_NEWARRAY, Opcodes.NEWARRAY_T_INT), info, false);
					newPDSInst.newarrayInstForParam();
					instructions.add(newPDSInst);
	 				initializeArrayToUndefined(instructions, mtd, true);//true for low array
				}
			}
			else{
				if(!PDSMethod.isArray(descriptor,i)){//high prim non-array
					newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_ICONST_M1), info);
					newPDSInst.pushWithInstForInitWrapper("undef","undef");
					instructions.add(newPDSInst);
				}
				else{//high prim array
					newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_ICONST_M1), info);
					newPDSInst.pushWithInstForInitWrapper("undef",info.getStackName()+"0");//same length
					instructions.add(newPDSInst);
		//if
		//ptr+s0+1>15 || ptrt+s0t+1>15 ->	goto HeapOverFlow;
		//else ->s0=ptr, heap[ptr]=s0, ptr=ptr+s0+1, s0t=ptrt, heapt[ptrt]=s0t, ptrt=ptrt+s0t+1;
		//fi
					newPDSInst=new PDSInst(mtd, 
						new ImmediateShortInstruction (Opcodes.OPCODE_NEWARRAY, Opcodes.NEWARRAY_T_INT), info, false);
					newPDSInst.newarrayInstForParam();
					instructions.add(newPDSInst);
	 				initializeArrayToUndefined(instructions, mtd, false);//false for high array
				}
			}

			/* (PDSMethod.isArray(descriptor, i)) {
				newPDSInst=new PDSInst(mtd, new ImmediateByteInstruction
					(Opcodes.OPCODE_ANEWARRAY, true, Opcodes.NEWARRAY_T_INT), info);
				newPDSInst.translate();
				instructions.add(newPDSInst);
 				initializeArrayToUndefined(instructions, mtd);
			}*/
		}
	}
	
	//need a maximum depth of stack of 3.
	private void initializeArrayToUndefined(List<PDSInst> instructions, PDSMethod mtd,
					boolean bLowArray) throws InvalidByteCodeException
	{
		int arraySlot = getNewVarSlot();//va
		int indexSlot = getNewVarSlot();//vi
		int arraySlot2=getNewVarSlot();//va'
		int loopGuardLabelOffset = getNewLabelOffset();//label1
		int loopStartLabelOffset = getNewLabelOffset();//label2
		
		PDSInst newPDSInst;
		
		//va=s0,s0=s1,... store start index of heap for array in its var slot-----------
		newPDSInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ASTORE, false, arraySlot),info,false);
		newPDSInst.popToInstForArrayParam(info.getLocalVarName()+String.valueOf(arraySlot),
										info.getLocalVarName()+String.valueOf(arraySlot2));
		instructions.add(newPDSInst);
		//s0=0,s1=s0,... load 0 constant-----------------------------------
		newPDSInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_ICONST_0),info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//vi=s0,s0=s1,... store 0 in index slot-------------------------------
		newPDSInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE, false, indexSlot),info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		// goto loop guard
		//the relative offset is loopGuardLabelOffset (==1), the branchOffset of BranchInstruction is 0, thus "goto label_1(0+1)"
		newPDSInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO, loopGuardLabelOffset),info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//loop_start: s0=va, s1=s0,... load array-------------------------------
		AbstractInstruction instruction = new ImmediateByteInstruction(Opcodes.OPCODE_ALOAD, false, arraySlot);
		instruction.setOffset(loopStartLabelOffset);
		newPDSInst=new PDSInst(mtd,instruction,info,false);
		newPDSInst.pushWithInstForInitWrapper2(info.getLocalVarName()+String.valueOf(arraySlot),
											info.getLocalVarName()+String.valueOf(arraySlot2));
		instructions.add(newPDSInst);
		//s0=vi,s1=s0,... load index-----------------------------------------
		newPDSInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD, false, indexSlot),info,false);
		newPDSInst.pushWithInstForInitWrapper2(info.getLocalVarName()+String.valueOf(indexSlot),
											info.getLocalVarName()+String.valueOf(indexSlot));
		instructions.add(newPDSInst);
		//s0=undef,s1=s0,... push undefined parameter------------------------
		newPDSInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_ICONST_M1), info);
		if(bLowArray){
			newPDSInst.pushWithInstForInitWrapper("undef",info.stackIndex(0));
		}
		else{
			newPDSInst.pushWithInstForInitWrapper("undef","undef");
		}
		instructions.add(newPDSInst);
		//if
		//else -> heap[s2+s1+1]=s0,s0=s3,...
		//fi
		// store in array at index
		newPDSInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IASTORE),info,false);
		newPDSInst.iastoreInstForArrayParam();
		instructions.add(newPDSInst);
		//vi=vi+1, increase index by 1----------------------------------------
		newPDSInst=new PDSInst(mtd,new IncrementInstruction(Opcodes.OPCODE_IINC, false, indexSlot, 1),info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//loop_guard: s0=vi,s1=s0,... load index------------------------------------------
		instruction=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD, false, indexSlot);
		instruction.setOffset(loopGuardLabelOffset);
		newPDSInst=new PDSInst(mtd,instruction,info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//s0=va,s1=s0,... load array-------------------------------------------
		newPDSInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ALOAD, false, arraySlot),info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);		
		//s0=heap[s0]. load array length, OPCODE_ARRAYLENGTH is 190-------------
		newPDSInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_ARRAYLENGTH),info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//if
		//s1<s0 -> goto loop_start,s0=s2,...
		//else -> s0=s2,...
		//fi
		newPDSInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPLT, loopStartLabelOffset),info,false);
		newPDSInst.translate();
		instructions.add(newPDSInst);
		//s0=va,s1=s0,... load array on stack, so it can be used-------------------
		newPDSInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ALOAD, false, arraySlot),info,false);
		newPDSInst.pushWithInstForInitWrapper2(info.getLocalVarName()+String.valueOf(arraySlot),
											info.getLocalVarName()+String.valueOf(arraySlot2));
		instructions.add(newPDSInst);

	}
	
	private void appendLabel(StringBuilder out, int i)
	{
		out.append("\n").append(formattedName);
		if (i >= 0) {
			out.append(i);
		}
		out.append(": ");
	}
	
	/*Returns a new unique variable slot*/
	private int getNewVarSlot()
	{
		return varSlot++;
	}
	
	private int getNewLabelOffset()
	{
		return labelOffset++;
	}
	
	public String getFormattedName() 
	{
		return formattedName;
	}
	/*by suncong, assistant method for print info*/
	private void printMethodList(List<PDSMethod> methods, String title){
		System.out.println("*********"+title+"************");
		for(PDSMethod mtd: methods){
			System.out.println("<"+mtd.getClassName()+";"+mtd.getFormattedName()+";"+mtd.getDescriptor()+">");
		}
		System.out.println("******************************");
	}
	private List<PDSMethod> FindInterSectionMethods(List<PDSMethod> list1,List<PDSMethod> list2){
		List<PDSMethod> rlist=new ArrayList<PDSMethod>();
		for(PDSMethod tmpMtd: list1){
			for(PDSMethod tmpMtd2: list2){
				if(tmpMtd2==tmpMtd)
					rlist.add(tmpMtd);
			}
		}
		return rlist;
	}
	private void excludeUnrelatedPushUndef(List<RecordStruct> newPosForParam, List<Integer> pushUndefPoses){
		for(RecordStruct rs: newPosForParam){
			for(int i=0;i<pushUndefPoses.size();){
				int tmpPos=pushUndefPoses.get(i);
				if(tmpPos>=rs.getStartPos() && tmpPos<=rs.getEndPos()){
					pushUndefPoses.remove(i);
				}
				else i++;
			}
		}
	}
	private int findPostOperationPosition(int targetPos,List<RecordStruct> newPosForParam){
		int rPos=-1;
		for(RecordStruct rs: newPosForParam){
			if(rs.getEndPos()>=targetPos)
				continue;
			if(rs.getEndPos()>rPos)
				rPos=rs.getEndPos();
		}
		return rPos;
	}
	private String getParamSigForTarget() throws InvalidByteCodeException{
		//System.out.println(111);
		String s=method.getDescriptor();
		//System.out.println(s);
		if(s.indexOf('(')>=0){
			s=s.substring(s.indexOf('(')+1);
			//System.out.println(s);
		}
		if(s.lastIndexOf(')')>0){
			s=s.substring(0,s.lastIndexOf(')'));
		}
		else if(s.lastIndexOf(')')==0)
			s="";
		//System.out.println("++Original Param Sig:"+s);/////////////////////
		if((method.getAccessFlags()& AccessFlags.ACC_STATIC)!=0)//static
			return s;
		String typeOfThis="L"+method.getClassFile().getThisClassName()+";";
		//System.out.println("++ParamSigForTarget:"+typeOfThis+s);/////////////
		return (typeOfThis+s);
	}
	
	/* for(int i=0;i<ptridx;i++){
	 * 		for(int j=0;j<ptrref,j++){
	 *			if(lowidx2[i]==heap[lowref[j]])
	 *				heapt[lowidx1[i]]=heap[lowidx1[i]];
	 *		}
	 * }
	 *the stack depth required is 2.
	 */	
	private int PostInitOp(List<PDSInst> instructions, PDSMethod mtd, int pi, int pj) throws InvalidByteCodeException{
		//int pi=getNewVarSlot();
		//int pj=getNewVarSlot();
		int loopGuardNeg1=getNewLabelOffset();
		int loop1_start=getNewLabelOffset();
		int loopGuardNeg2=getNewLabelOffset();
		int loop2_start=getNewLabelOffset();
		int branchTar=getNewLabelOffset();
		PDSInst newInst;
		AbstractInstruction absInst;
		//s0=0,...	push 0--------------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info,false);
		newInst.pushWithInstForInitWrapper("0","");
		instructions.add(newInst);
		//pi=s0,... istore pi--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...--------------------------------------------------
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi);
		absInst.setOffset(loop1_start);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=ptridx,...----------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info, false);
		newInst.pushWithInstForInitWrapper(info.getPtridxName(),"");
		instructions.add(newInst);
		//if
		//s1>=s0 -> goto guard_neg1, s2=s0,...
		//else -> s0=s2,...
		//fi
		//--------------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPGE,loopGuardNeg1),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=0,...	push 0--------------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info,false);
		newInst.pushWithInstForInitWrapper("0","");
		instructions.add(newInst);
		//pj=s0,... istore pj--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,pj),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pj,...--------------------------------------------------
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj);
		absInst.setOffset(loop2_start);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=ptrref,...----------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info, false);
		newInst.pushWithInstForInitWrapper(info.getPtrrefName(),"");
		instructions.add(newInst);
		//if
		//s1>=s0 -> goto guard_neg2, s2=s0,...
		//else -> s0=s2,...
		//fi
		//--------------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPGE,loopGuardNeg2),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info, false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx2[s0]-------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx2Name());
		instructions.add(newInst);
		//s0=pj,...--------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj),info, false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowref[s0],...------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowrefName());
		instructions.add(newInst);
		//s0=heap[s0],...---------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//s0=heap[s0],...---------------------------------removed
		/*newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);*/
		//if
		//s1!=s0 -> goto branch_tar, s2=s0,...
		//else -> s2=s0,...
		//fi
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPNE,branchTar),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...----------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info, false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx1[s0],...-------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx1Name());
		instructions.add(newInst);
		//s0=s0,s1=s0,s2=s1,... dup------------------------
		newInst=new PDSInst(mtd, new SimpleInstruction(Opcodes.OPCODE_DUP), info,false);
		newInst.pushWithInstForInitWrapper(info.stackIndex(0),"");
		instructions.add(newInst);
		//s0=heap[s0],...-----------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//heapt[s1]=s0, s0=s2,.... (iastore)----------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IASTORE),info,false);
		newInst.iastoreInstForPostInitOp(info.generateTLabel(info.getHeapName()));
		instructions.add(newInst);
		//pj=pj+1---------------------------------------
		absInst=new IncrementInstruction(Opcodes.OPCODE_IINC,false,pj,1);
		absInst.setOffset(branchTar);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//goto loop2_start--------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO,loop2_start),info,false);
		newInst.translate();
		instructions.add(newInst);
		//pi=pi+1----------------------------------------
		absInst=new IncrementInstruction(Opcodes.OPCODE_IINC,false,pi,1);
		absInst.setOffset(loopGuardNeg2);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//goto loop1_start-------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO,loop1_start),info,false);
		newInst.translate();
		instructions.add(newInst);
		//next instruction set label loopGuardNeg1
		return loopGuardNeg1;
	}
	
	/*for(int i=0;i<ptridx;i++){
	*	for(int j=0;j<ptrref;j++){
	*		if(lowidx2[i]==heap[lowref[j]]){
	*			if(heapt[lowidx1[i]]!=heap[lowidx1[i]])
	*				goto IllegalFlow;
	*		}
	*	}
	*}
	*depth of stack is 3.
	*/
/*  old stub, modified when writing the chinese journal.
 *	private int StubOp(List<PDSInst> instructions,PDSMethod mtd,int pi,int pj) throws InvalidByteCodeException{
		int loopGuardNeg1=getNewLabelOffset();
		int loop1_start=getNewLabelOffset();
		int loopGuardNeg2=getNewLabelOffset();
		int loop2_start=getNewLabelOffset();
		int branchTar=getNewLabelOffset();
		int gotoIllegal=getNewLabelOffset();
		//int branchTar2=getNewLabelOffset();
		PDSInst newInst;
		AbstractInstruction absInst;
		//s0=0,...	push 0--------------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info,false);
		newInst.pushWithInstForInitWrapper("0","");
		instructions.add(newInst);
		//pi=s0,... istore pi--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...--------------------------------------------------
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi);
		absInst.setOffset(loop1_start);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=ptridx,...----------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info, false);
		newInst.pushWithInstForInitWrapper(info.getPtridxName(),"");
		instructions.add(newInst);
		//if
		//s1>=s0 -> goto guard_neg1, s2=s0,...
		//else -> s0=s2,...
		//fi
		//--------------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPGE,loopGuardNeg1),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=0,...
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info,false);
		newInst.pushWithInstForInitWrapper("0","");
		instructions.add(newInst);
		//pj=s0,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,pj),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pj,...
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj);
		absInst.setOffset(loop2_start);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=ptrref,...
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info, false);
		newInst.pushWithInstForInitWrapper(info.getPtrrefName(),"");
		instructions.add(newInst);
		//if
		//s1>=s0 -> goto guard_neg2, s2=s0,...
		//else -> s0=s2,...
		//fi
		//--------------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPGE,loopGuardNeg2),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx2[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx2Name());
		instructions.add(newInst);
		//s0=pj,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowref[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowrefName());
		instructions.add(newInst);
		//s0=heap[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		////s0=heap[s0]
		//newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		//newInst.ialoadInstForPostInitOp(info.getHeapName());
		//instructions.add(newInst);
		//if
		//s1!=s0 -> goto branch_tar, s2=s0,...
		//else -> s2=s0,...
		//fi
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPNE,branchTar),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx1[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx1Name());
		instructions.add(newInst);
		//s0=heapt[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.generateTLabel(info.getHeapName()));
		instructions.add(newInst);
		//s0=pi
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx1[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx1Name());
		instructions.add(newInst);
		//s0=heap[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//if
		//s1==s0 -> goto branch_tar2, s0=s2,...
		//else -> s0=s2,...
		//fi
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPEQ,branchTar),info,false);
		newInst.translate();
		instructions.add(newInst);
		//goto IllegalFlow
		absInst=new BranchInstruction(Opcodes.OPCODE_GOTO, -1);
		absInst.setOffset(gotoIllegal);//for the last stub instruction standing for equiv of return variable
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.gotoStmtForStubGen(info.getIllegalFlowLabel(),"");
		instructions.add(newInst);
		//pj=pj+1
		absInst=new IncrementInstruction(Opcodes.OPCODE_IINC,false,pj,1);
		absInst.setOffset(branchTar);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//goto loop2_start
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO,loop2_start),info,false);
		newInst.translate();
		instructions.add(newInst);
		//pi=pi+1
		absInst=new IncrementInstruction(Opcodes.OPCODE_IINC,false,pi,1);
		absInst.setOffset(loopGuardNeg2);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//goto loop1_start
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO,loop1_start),info,false);
		newInst.translate();
		instructions.add(newInst);
		//next instruction set label loopGuardNeg1
		if(info.getPolicy().lowPrimRetVar(method.getName(),method.getDescriptor())){//primitive low
			//generate stub for retvar, need extra length 1 of operand stack
			//s0=s0t,...
			absInst=new SimpleInstruction(OPCODE_SLOT);
			absInst.setOffset(loopGuardNeg1);
			newInst=new PDSInst(mtd,absInst,info,false);
			newInst.pushWithInstForInitWrapper(info.stackIndex(0)+info.getTTail(),"");
			instructions.add(newInst);
			//if
			// s0!=s1 -> goto IllegalFlow, s0=s2,...
			// else -> s0=s2,...
			//fi
			newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPNE,gotoIllegal),info,false);
			//newInst.iflcmpInstForLabel("!=",info.getIllegalFlowLabel());
			newInst.translate();
			instructions.add(newInst);
			
			return -1;
		}
		else{
			return loopGuardNeg1;
		}
	}*/
	
	/*new stub,2009.11.5
	*
	*for(int j=0;j<ptrref;j++){
	*	for(int i=0;i<ptridx;i++){
	*		if(lowidx2[i]==heap[lowref[j]]){
	*			if(heapt[lowidx1[i]]!=heap[lowidx1[i]])
	*				goto IllegalFlow;
	*		}
	*	}
	*	if(heapt[heapt[lowref[j]]]!=heap[heap[lowref[j]]])
	*	goto IllegalFlow;
	*}
	*depth of stack is 3.
	*/
	private int StubOp(List<PDSInst> instructions,PDSMethod mtd,int pi,int pj) throws InvalidByteCodeException{
		int loopGuardNeg1=getNewLabelOffset();
		int loop1_start=getNewLabelOffset();
		int loopGuardNeg2=getNewLabelOffset();
		int loop2_start=getNewLabelOffset();
		int loopGuardNeg3=getNewLabelOffset();//branchTar
		int gotoIllegal=getNewLabelOffset();
		//int branchTar2=getNewLabelOffset();
		PDSInst newInst;
		AbstractInstruction absInst;
		//s0=0,...	push 0--------------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info,false);
		newInst.pushWithInstForInitWrapper("0","");
		instructions.add(newInst);
		//pj=s0,... istore pj--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,pj),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pj,...--------------------------------------------------
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj);
		absInst.setOffset(loop1_start);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=ptrref,...----------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info, false);
		newInst.pushWithInstForInitWrapper(info.getPtrrefName(),"");
		instructions.add(newInst);
		//if
		//s1>=s0 -> goto guard_neg1, s2=s0,...
		//else -> s0=s2,...
		//fi
		//--------------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPGE,loopGuardNeg1),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=0,...
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info,false);
		newInst.pushWithInstForInitWrapper("0","");
		instructions.add(newInst);
		//pj=s0,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pj,...
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi);
		absInst.setOffset(loop2_start);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=ptridx,...
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info, false);
		newInst.pushWithInstForInitWrapper(info.getPtridxName(),"");
		instructions.add(newInst);
		//if
		//s1>=s0 -> goto guard_neg2, s2=s0,...
		//else -> s0=s2,...
		//fi
		//--------------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPGE,loopGuardNeg2),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx2[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx2Name());
		instructions.add(newInst);
		//s0=pj,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowref[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowrefName());
		instructions.add(newInst);
		//s0=heap[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//if
		//s1!=s0 -> goto loopGuardNeg3, s2=s0,...
		//else -> s2=s0,...
		//fi
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPNE,loopGuardNeg3),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=pi,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx1[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx1Name());
		instructions.add(newInst);
		//s0=heapt[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.generateTLabel(info.getHeapName()));
		instructions.add(newInst);
		//s0=pi
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pi),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx1[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidx1Name());
		instructions.add(newInst);
		//s0=heap[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//if
		//s1==s0 -> goto loopGuardNeg3, s0=s2,...
		//else -> s0=s2,...
		//fi
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPEQ,loopGuardNeg3),info,false);
		newInst.translate();
		instructions.add(newInst);
		//gotoIllegal: goto IllegalFlow
		absInst=new BranchInstruction(Opcodes.OPCODE_GOTO, -1);
		absInst.setOffset(gotoIllegal);//for the last stub instruction standing for equiv of return variable
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.gotoStmtForStubGen(info.getIllegalFlowLabel(),"");
		instructions.add(newInst);
		
		//loopGuardNeg3: pi=pi+1
		absInst=new IncrementInstruction(Opcodes.OPCODE_IINC,false,pi,1);
		absInst.setOffset(loopGuardNeg3);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//goto loop2_start
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO,loop2_start),info,false);
		newInst.translate();
		instructions.add(newInst);
		
		//loopGuardNeg2: s0=pj,...
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj);
		absInst.setOffset(loopGuardNeg2);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowref[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowrefName());
		instructions.add(newInst);
		//s0=heap[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//s0=heap[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//s0=vj,...
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,pj),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowref[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowrefName());
		instructions.add(newInst);
		//s0=heapt[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.generateTLabel(info.getHeapName()));
		instructions.add(newInst);
		//s0=heapt[s0]
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.generateTLabel(info.getHeapName()));
		instructions.add(newInst);
		//if
		//s1!=s0 -> goto gotoIllegal, s0=s2,...
		//s0=s2,...
		//fi
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPNE,gotoIllegal),info,false);
		newInst.translate();
		instructions.add(newInst);
		//pj=pj+1...
		newInst=new PDSInst(mtd,new IncrementInstruction(Opcodes.OPCODE_IINC,false,pj,1),info,false);
		newInst.translate();
		instructions.add(newInst);
		//goto loop1_start
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO,loop1_start),info,false);
		newInst.translate();
		instructions.add(newInst);
		
		//next instruction set label loopGuardNeg1
		if(info.getPolicy().lowPrimRetVar(method.getName(),method.getDescriptor())){//primitive low
			//generate stub for retvar, need extra length 1 of operand stack
			//s0=s0t,...
			absInst=new SimpleInstruction(OPCODE_SLOT);
			absInst.setOffset(loopGuardNeg1);
			newInst=new PDSInst(mtd,absInst,info,false);
			newInst.pushWithInstForInitWrapper(info.stackIndex(0)+info.getTTail(),"");
			instructions.add(newInst);
			//if
			// s0!=s1 -> goto IllegalFlow, s0=s2,...
			// else -> s0=s2,...
			//fi
			newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPNE,gotoIllegal),info,false);
			//newInst.iflcmpInstForLabel("!=",info.getIllegalFlowLabel());
			newInst.translate();
			instructions.add(newInst);
			
			return -1;
		}
		else{
			return loopGuardNeg1;
		}
	}
/*********************************************************************/
	/*if [<init>,()V] exists in the constant pool, its index is returned, if it doesn't exist, two entries 
	(ConstantNameAndTypeInfo, ConstantMethodrefInfo) are inserted to constant pool, 
	representing method [<init>,desc] which originally appear in constant pool.
	make it public by suncong, to call it in Automata.java*/
	public static int indexOfMethodRef(ClassFile classFile, String name, String desc) throws InvalidByteCodeException
	{
		int index = findIndexOfMethodRef(classFile, name, desc);
		if (index != -1) {
			return index;
		}
		return createMethodRef(classFile, name, desc);
	}

	static int findIndexOfMethodRef(ClassFile classFile, String name, String desc) throws InvalidByteCodeException
	{
		CPInfo[] pool = classFile.getConstantPool();
		int candidate = -1;
		desc = desc != null ? desc : "()V";
		
		for (int i = 0; i < pool.length; i++) {
			CPInfo entry = pool[i];
			if (entry != null) {
				if (entry.getTag() == CPInfo.CONSTANT_METHODREF) {
					ConstantMethodrefInfo methodInfo = (ConstantMethodrefInfo)entry;
					if (methodInfo.getClassIndex() != classFile.getThisClass()) {
						// not a method ref of this class
						continue;
					}
					ConstantNameAndTypeInfo info = methodInfo.getNameAndTypeInfo();
					if (!info.getName().equals(name)) {
						continue;
					}
					candidate = i;
					if (info.getDescriptor().equals(desc)) {
						// the exact candidate
						return candidate;
					}
				}
			}
		}
		return candidate;
	}

	/*when createMethodRef is called, findIndexOfMethodRef(classFile,name,desc) has just return -1,
	thus in createMethodRef, when indexOfTypeAndName is called, the createTypeAndName (in
	indexOfTypeAndName) must be called, a ConstantNameAndTypeInfo struct is inserted into ConstantPool
	and the index is returned to the 'index'. Then a ConstantMethodrefInfo structure is added to the end of
	ConstantPool, its index is returned by createMethodRef*/
	static int createMethodRef(ClassFile classFile, String name, String desc) throws InvalidByteCodeException
	{
		int index = indexOfTypeAndName(classFile, name, desc);
		ConstantMethodrefInfo info = new ConstantMethodrefInfo();
		info.setClassFile(classFile);
		info.setNameAndTypeIndex(index);
		info.setClassIndex(classFile.getThisClass());
		return addConstantPoolEntry(classFile, info);
	}

	static int indexOfTypeAndName(ClassFile classFile, String name, String desc) throws InvalidByteCodeException
	{
		int index = findIndexOfMethodRef(classFile, name, desc);
		if (index != -1) {
			return index;
		}
		return createTypeAndName(classFile, name, desc);
	}

	static int addConstantPoolEntry(ClassFile classFile, CPInfo info)
	{
		CPInfo[] pool = classFile.getConstantPool();
		CPInfo[] newPool = new CPInfo[pool.length + 1];
		System.arraycopy(pool, 0, newPool, 0, pool.length);
		newPool[newPool.length - 1] = info;
		classFile.setConstantPool(newPool);
		return newPool.length - 1;
	}

	static int findIndexOfTypeAndName(ClassFile classFile, String name, String desc)
		throws InvalidByteCodeException
	{
		CPInfo[] pool = classFile.getConstantPool();
		int candidate = -1;
		desc = desc != null ? desc : "()V";
		
		for (int i = 0; i < pool.length; i++) {
			CPInfo entry = pool[i];
			if (entry == null) {
				continue;
			}
			if (entry.getTag() == CPInfo.CONSTANT_NAME_AND_TYPE) {
				ConstantNameAndTypeInfo info = (ConstantNameAndTypeInfo)entry;
				if (!info.getName().equals(name)) {
					continue;
				}
				candidate = i;
				if (info.getDescriptor().equals(desc)) {
					// optimal constructor candidate;
					return candidate;
				}
			}
		}
		return candidate;
	}
	
	static int createTypeAndName(ClassFile classFile, String name, String desc)
		throws InvalidByteCodeException
	{
		MethodInfo candidate = null;
		desc = desc != null ? desc : "()V";
		
		for (MethodInfo info : classFile.getMethods()) {
			if (!info.getName().equals(name)) {
				continue;
			}
			candidate = info;
			if (info.getDescriptor().equals(desc) ) {
				break;
			}
		}
		
		ConstantNameAndTypeInfo info = new ConstantNameAndTypeInfo();
		info.setClassFile(classFile);
		info.setNameIndex(candidate.getNameIndex());
		info.setDescriptorIndex(candidate.getDescriptorIndex());
		return addConstantPoolEntry(classFile, info);
	}
/********************************************************************/
	private String getDescriptor(ClassFile classFile, int constructorIndex)
		throws InvalidByteCodeException
	{
		ConstantMethodrefInfo info = (ConstantMethodrefInfo)classFile.getConstantPoolEntry(constructorIndex, 
					ConstantMethodrefInfo.class);
		return info.getNameAndTypeInfo().getDescriptor();
	}
	
	static int getParamCount(ClassFile classFile, int methodRefIndex)
		throws InvalidByteCodeException
	{
		ConstantMethodrefInfo info = (ConstantMethodrefInfo)classFile.getConstantPoolEntry(methodRefIndex,
					ConstantMethodrefInfo.class);
		String desc = info.getNameAndTypeInfo().getDescriptor();
		return PDSMethod.countParam(desc);
	}
	/* old PostInitializationOp
	 * for(int i=ptridx-newedLowCount;i<ptridx;i++)
	 *	heapt[lowidx[i]]=heap[lowidx[i]]
	 *ptridx=ptridx-newedLowCount
	 * two stack locations are enough, need not to reset the maxLocalStack
	 */
/*	private void PostInitializationOp(List<PDSInst> instructions, PDSMethod mtd, int newedLowCount)
									throws InvalidByteCodeException{
		int curridx=getNewVarSlot();
		int upperbound=getNewVarSlot();
		int heapidx=getNewVarSlot();
		int heap_cont=getNewVarSlot();
		int loopStartLabelOffset=getNewLabelOffset();
		int loopGuardJmpNext=getNewLabelOffset();
		System.out.println("PostInitOp--curridx:"+curridx+";upperbound:"+upperbound+
							";heapidx:"+heapidx+";heap_cont:"+heap_cont+
							";loop start offset:"+loopStartLabelOffset+
							";guard jump offset:"+loopGuardJmpNext);
							
		if(newedLowCount<=0)
			return;
		
		PDSInst newInst;
		AbstractInstruction absInst;
		//s0=ptr-newedLowCount,...--------------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info,false);
		newInst.pushWithInstForInitWrapper(info.getPtridxName()+"-"+String.valueOf(newedLowCount),"");
		instructions.add(newInst);
		//v_curridx=s0,...--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,curridx),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=ptr,...--------------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(OPCODE_SLOT),info, false);
		newInst.pushWithInstForInitWrapper(info.getPtridxName(),"");
		instructions.add(newInst);
		//v_upperbound=s0,...--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,upperbound),info,false);
		newInst.translate();
		instructions.add(newInst);
		//loop guard: s0=v_curridx,...--------------------------------------------------
		absInst=new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,curridx);
		absInst.setOffset(loopStartLabelOffset);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=v_upperbound--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,upperbound),info,false);
		newInst.translate();
		instructions.add(newInst);
		//if
		//s1>=s0 -> goto guard_jmp_next, s2=s0,...
		//else -> s0=s2,...
		//fi
		//--------------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_IF_ICMPGE,loopGuardJmpNext),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=v_curridx,...--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,curridx),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=lowidx[s0]--------------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getLowidxName());
		instructions.add(newInst);
		//v_heapidx=s0,...--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,heapidx),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=v_heapidx,...--------------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,heapidx),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=heap[s0]-------------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IALOAD),info,false);
		newInst.ialoadInstForPostInitOp(info.getHeapName());
		instructions.add(newInst);
		//v_heap_cont=s0,...---------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE,false,heap_cont),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=v_heapidx,...------------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,heapidx),info,false);
		newInst.translate();
		instructions.add(newInst);
		//s0=v_heap_cont,...-----------------------------------------
		newInst=new PDSInst(mtd,new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD,false,heap_cont),info,false);
		newInst.translate();
		instructions.add(newInst);
		//heapt[s1]=s0,s0=s2,...--------------------------------------
		newInst=new PDSInst(mtd,new SimpleInstruction(Opcodes.OPCODE_IASTORE),info,false);
		newInst.iastoreInstForPostInitOp(info.generateTLabel(info.getHeapName()));
		instructions.add(newInst);
		//v_curridx=v_curridx+1---------------------------------------
		newInst=new PDSInst(mtd,new IncrementInstruction(Opcodes.OPCODE_IINC,false,curridx,1),info,false);
		newInst.translate();
		instructions.add(newInst);
		// goto loop_start--------------------------------------------
		newInst=new PDSInst(mtd,new BranchInstruction(Opcodes.OPCODE_GOTO,loopStartLabelOffset),info,false);
		newInst.translate();
		instructions.add(newInst);
		//guard_jmp_next: ptridx=ptridx-newedLowCount---------------
		absInst=new SimpleInstruction(OPCODE_SLOT);
		absInst.setOffset(loopGuardJmpNext);
		newInst=new PDSInst(mtd,absInst,info,false);
		newInst.oneInstForInitWrapper(info.getPtridxName()+"="+
									info.getPtridxName()+"-"+String.valueOf(newedLowCount), "");
		instructions.add(newInst);
	}*/
	
/*commented by suncong, to discard PDSInstOld
	private List<PDSInstOld> getStaticWrapperInstructions() throws InvalidByteCodeException
	{
		PDSInfo pdsInfo = getInfo();
		List<PDSInstOld> instructions = new ArrayList<PDSInstOld>();
		int methodIndex = indexOfMethodRef(classFile, method.getName(), method.getDescriptor());
		// + 1 for heap index?
		// 3 for array initialization
		maxLocalStack = Math.max(3, getParamCount() + 1);
		
		addPushUndefinedParams(instructions, method.getDescriptor(), 
				getParamCount(), maxLocalStack);

		addInstruction(instructions, new ImmediateShortInstruction(Opcodes.OPCODE_INVOKESTATIC,
				methodIndex), maxLocalStack);
		
		addInstruction(instructions, new SimpleInstruction(Opcodes.OPCODE_RETURN), 
				maxLocalStack);
		
		return instructions;
	}
*/

/*commented by suncong, to discard PDSInstOld
private List<PDSInstOld> getInstanceWrapperInstructions() throws InvalidByteCodeException
	{
		PDSInfo pdsInfo = getInfo();
		List<PDSInstOld> instructions = new ArrayList<PDSInstOld>();
		int constructorIndex = indexOfMethodRef(classFile, "<init>", null);
		String constructorDesc = getDescriptor(classFile, constructorIndex);
		int paramCount = getParamCount(classFile, constructorIndex);
		// + 1 for heap index?
		// 3 for array initialization
		maxLocalStack = Math.max(3, Math.max(getParamCount(), paramCount) + 1);
		
		addInstruction(instructions, new ImmediateShortInstruction(Opcodes.OPCODE_NEW, 
				classFile.getThisClass()), maxLocalStack);
		
		addInstruction(instructions, new SimpleInstruction(Opcodes.OPCODE_DUP),
				maxLocalStack);
		
		addPushUndefinedParams(instructions, constructorDesc, paramCount, 
				maxLocalStack);

		addInstruction(instructions, new ImmediateShortInstruction(Opcodes.OPCODE_INVOKESPECIAL,
				constructorIndex), maxLocalStack);
		
		addPushUndefinedParams(instructions, method.getDescriptor(), 
				getParamCount(), maxLocalStack);
		
		addInstruction(instructions, new ImmediateShortInstruction(Opcodes.OPCODE_INVOKESPECIAL,
				indexOfMethodRef(classFile, method.getName(), method.getDescriptor())), maxLocalStack);

		addInstruction(instructions, new SimpleInstruction(Opcodes.OPCODE_RETURN),
				maxLocalStack);
		
		return instructions;
	}*/
	
/* commented for discarding PDSInstOld
 *	private void addPushUndefinedParams(List<PDSInstOld> instructions,  
			String descriptor,	int paramCount, int maxLocalStack) throws InvalidByteCodeException
	{
		for (int i = 0; i < paramCount; i++) {
			addPushUndefInstruction(instructions, maxLocalStack);
			if (PDSMethod.isArray(descriptor, i)) {
				addInstruction(instructions, new ImmediateByteInstruction
					(Opcodes.OPCODE_ANEWARRAY, true, Opcodes.NEWARRAY_T_INT), maxLocalStack);
 				initializeArrayToUndefined(instructions, maxLocalStack);
			}
		}
	}*/
/*	useless by discard PDSInstOld, commented by suncong
	private void addInstruction(List<PDSInstOld> instructions, AbstractInstruction inst, 
			int maxLocalStack) throws InvalidByteCodeException
	{
		instructions.add(new PDSInstOld(classFile, formattedName, inst, maxLocalStack, getInfo()));
	}*/
	
	/**
	 * Assumes the array is the current  top element on the operand stack.
	 * @param instructions
	 * @throws InvalidByteCodeException
	 */
/*commented to discard PDSInstOld
	private void initializeArrayToUndefined(List<PDSInstOld> instructions, int maxLocalStack) 
		throws InvalidByteCodeException
	{
		int arraySlot = getNewVarSlot();
		int indexSlot = getNewVarSlot();
		int loopGuardLabelOffset = getNewLabelOffset();
		int loopStartLabelOffset = getNewLabelOffset();
		
		// store array in its var slot
		addInstruction(instructions,
				new ImmediateByteInstruction(Opcodes.OPCODE_ASTORE, false, arraySlot),
				maxLocalStack);

		// load 0 constant
		addInstruction(instructions, new SimpleInstruction(Opcodes.OPCODE_ILOAD_0),
				maxLocalStack);
		
		// store it in index slot
		addInstruction(instructions,
				new ImmediateByteInstruction(Opcodes.OPCODE_ISTORE, false, indexSlot),
				maxLocalStack);
		
		// goto loop guard
		addInstruction(instructions,
				new BranchInstruction(Opcodes.OPCODE_GOTO, loopGuardLabelOffset),
				maxLocalStack);
		
		// load array
		AbstractInstruction instruction =
			new ImmediateByteInstruction(Opcodes.OPCODE_ALOAD, false, arraySlot);
		instruction.setOffset(loopStartLabelOffset);
		addInstruction(instructions, instruction, maxLocalStack);
		
		// load index
		addInstruction(instructions, 
				new ImmediateByteInstruction(Opcodes.OPCODE_ILOAD, false, indexSlot),
				maxLocalStack);

		// push undefined parameter
		addPushUndefInstruction(instructions, maxLocalStack);
		
		// store in array at index
		addInstruction(instructions, new SimpleInstruction(Opcodes.OPCODE_IASTORE),
				maxLocalStack);
		
		// increase index by 1
		addInstruction(instructions, 
				new IncrementInstruction(Opcodes.OPCODE_IINC, false,	indexSlot, 1),
				maxLocalStack);
		
		// load array
		addInstruction(instructions,
				new ImmediateByteInstruction(Opcodes.OPCODE_ALOAD, false, arraySlot),
				maxLocalStack);
		
		// load array length
		addInstruction(instructions, 
				new SimpleInstruction(Opcodes.OPCODE_ARRAYLENGTH),
				maxLocalStack);
		
		instruction = new BranchInstruction(Opcodes.OPCODE_IF_ICMPLT, 
				loopStartLabelOffset - loopGuardLabelOffset);
		instruction.setOffset(loopGuardLabelOffset);
		addInstruction(instructions, instruction, maxLocalStack);
		
		// load array on stack, so it can be used
		addInstruction(instructions,
				new ImmediateByteInstruction(Opcodes.OPCODE_ALOAD, false, arraySlot),
				maxLocalStack);
	}
*/
		
/*commented for discarding PDSInstOld
 *	private void addPushUndefInstruction(List<PDSInstOld> instructions, int maxLocalStack)
		throws InvalidByteCodeException
	{
		addInstruction(instructions, new SimpleInstruction(Opcodes.OPCODE_ICONST_M1), 
				maxLocalStack);;
	}*/
		
}
