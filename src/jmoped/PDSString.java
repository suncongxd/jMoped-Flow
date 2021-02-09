package jmoped;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.gjt.jclasslib.structures.CPInfo;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantStringInfo;
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;

import jmoped.PDSInfo.HeapOption;

/**
 * Manipulates bytecode instruction concerning java String and StringBuilder.
 * 
 * @author suwimodh
 *
 */
public class PDSString {
	
	private Hashtable<Character, Integer> charTable;
	private int stringBuilderSize = PDSDefault.STRING_BUILDER_SIZE;
	
	static Logger logger = Logger.getLogger(PDSString.class);

	/**
	 * Constructor. 
	 * 
	 * This is supposed to be called from PDSInfo after analyzing 
	 * which classes are included.
	 * 
	 */
	public PDSString() {
		
	}
	
	/**
	 * Get the number of bits that is needed for representing a character.
	 * 
	 * @return the number of bits
	 */
	public int getCharBits() {
		
		return (int) Math.ceil(Math.log(charTable.size())/Math.log(2.0));
	}
	
	/**
	 * Get the binary representation of the character.
	 * 
	 * @param c the character
	 * @return the binary representation
	 */
	public int getCharRep(char c) {
		
		return charTable.get(c);
	}
	
	/**
	 * Get the size of a StringBuilder.
	 * 
	 * @return the size
	 */
	public int getStringBuilderSize() {
		
		return stringBuilderSize;
	}
	
	/**
	 * Set the size of a StringBuilder.
	 * 
	 * @param size the size
	 */
	public void setStringBuilderSize(int size) {
		
		stringBuilderSize = size;
	}
	
	/**
	 * Creates a PDSStmt that contains Remopla codes which handle:
	 * 1) invokespecial java/lang/StringBuilder.<init>()V
	 * 2) invokespecial java/lang/StringBuilder.<init>(Ljava/lang/String;)V
	 * 
	 * @param pdsInfo
	 * @param pdsMethod
	 * @param pdsInst
	 * @param calledName
	 * @return the PDSStmt that contains Remopla code
	 */
	public static PDSStmt invokespecialStringBuilder(PDSInfo pdsInfo, 
			PDSMethod pdsMethod, PDSInst pdsInst, String[] calledName) {
		
		PDSStmt pdsStmt = new PDSStmt();
		if (calledName[2].equals("()V")) {
			pdsStmt.addNext(pdsInst.popStmt(1, 1), pdsInst.popStmt(1,1));
		} else if (calledName[2].equals("(Ljava/lang/String;)V")) {
			pdsStmt = initWithStringStmt(pdsInfo, pdsMethod, pdsInst, 
					pdsInfo.stackIndex(1), pdsInfo.stackIndex(0), 2, 0, false);
		}
		
		return pdsStmt;
	}
	
	/**
	 * Creates a PDSStmt that contains Remopla codes which handle:
	 * 1) invokevirtual java/lang/StringBuilder.append(Ljava/lang/String;)Ljava/lang/StringBuilder
	 * 2) invokevirtual java/lang/String.length()I
	 * 3) invokevirtual java/lang/String.toString()Ljava/lang/String;
	 * 4) invokevirtual java/lang/String.equals(Ljava/lang/Object;)Z
	 * 
	 * @param pdsInfo
	 * @param pdsMethod
	 * @param pdsInst
	 * @param calledName
	 * @return the PDSStmt that contains Remopla code
	 */
	public static PDSStmt invokevirtualString(PDSInfo pdsInfo, PDSMethod pdsMethod, 
			PDSInst pdsInst, String[] calledName) {
		
		if (appendInvoked(calledName)) {
			
			return appendStmt(pdsInfo, pdsMethod, pdsInst);
		} else if (lengthInvoked(calledName)) {
			
			return lengthStmt(pdsInfo, pdsInst);
		} else if (toStringInvoked(calledName)) {
			
			return toStringStmt(pdsInfo, pdsMethod, pdsInst);
		} else if (equalsInvoked(calledName)) {
			
			pdsInfo.setIncludeStringEqualsMethod();
			return new PDSStmt(pdsInst.popPushStmt("stringEquals(s1, s0)", 2, 1),
								pdsInst.popPushStmt("stringEquals(s1, s0)",2,1));
		}
		
		return new PDSStmt();
	}
	
	/**
	 * Creates a PDSStmt that contains Remopla codes which handle the bytecode
	 * (ldc index), where index points to the entry of type string in the 
	 * constant pool.
	 * 
	 * The Remopla codes allocate the string in the heap.
	 * 
	 * @param cpInfo the array of constant pool entries
	 * @param immByte the index
	 * @param pdsInfo the global info
	 * @param pdsInst the object of this bytecode instruction
	 * @return the PDSStmt that contains Remopla code
	 */
	public static PDSStmt ldcString(CPInfo[] cpInfo, int immByte, 
			PDSInfo pdsInfo, PDSInst pdsInst) {
		
		ConstantStringInfo csInfo = (ConstantStringInfo) cpInfo[immByte];
		int constIndex = csInfo.getStringIndex();
		String string = ((ConstantUtf8Info) cpInfo[constIndex]).getString();
		int stringLength = string.length();
		char[] charArray = string.toCharArray();
		int charBits = pdsInfo.getCharBits();
		int intBits = pdsInfo.getBits();
		String heapName = pdsInfo.getHeapName();
		String heapName_pair=pdsInfo.generateTLabel(heapName);
		String heapPtr = pdsInfo.getHeapPtrName();
		String heapPtr_pair=pdsInfo.generateTLabel(heapPtr);
		String newHeapPtr, newHeapPtr_pair;
		PDSStmt pdsStmt = new PDSStmt();
		
		List<String> content = pdsInst.popPushStmt(heapPtr, 0, 1);
		List<String> content_pair=pdsInst.popPushStmt(heapPtr_pair,0,1);
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
			newHeapPtr = heapPtr + "+" + stringLength + "+1";
			newHeapPtr_pair = heapPtr_pair + "+" + stringLength + "+1";//
			content.add(pdsInfo.heapIndex(heapPtr) + "=" + stringLength);
			content_pair.add(pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),heapPtr_pair)
							+ "=" + stringLength);
			for (int j = 0; j < charArray.length; j++) {
				content.add(pdsInfo.heapIndex(heapPtr + "+" + (j + 1)) + "=" 
						+ pdsInfo.getCharRep(charArray[j]));
				content_pair.add(pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),
								heapPtr_pair + "+" + (j+1))
								+ "=" + pdsInfo.getCharRep(charArray[j]));
			}
		} else { // if (pdsInfo.getHeapOption() == HeapOption.TWODIMS)
			int blocks = PDSInst.countBlocks(charBits * stringLength, intBits);
			newHeapPtr = heapPtr + "+" + blocks + "+1";
			newHeapPtr_pair=heapPtr_pair+"+"+blocks+"+1";
			String beginPtr = intBits + "*" + heapPtr;
			String beginPtr_pair=intBits+"*"+heapPtr_pair;
			content.addAll(PDSInst.storeHeap(heapName, stringLength, beginPtr, intBits));
			content_pair.addAll(PDSInst.storeHeap(heapName_pair,stringLength,beginPtr_pair,intBits));
			for (int j = 0; j < charArray.length; j++) {
				content.addAll(PDSInst.storeHeap(heapName, pdsInfo.getCharRep(charArray[j]),
						beginPtr + "+" + (intBits + j * charBits), charBits));
				content_pair.addAll(PDSInst.storeHeap(heapName_pair,pdsInfo.getCharRep(charArray[j]),
									beginPtr_pair + "+" + (intBits + j * charBits), charBits));
			}
		}
		String heapUpdate = heapPtr + "=" + newHeapPtr;
		String heapUpdate_pair=heapPtr_pair+"="+newHeapPtr_pair;
		content.add(heapUpdate);
		content_pair.add(heapUpdate_pair);
		
		if (pdsInfo.checkForHeapOverflow()) {
			pdsStmt.addIf(PDSInst.ifStmt(
					newHeapPtr + ">" + pdsInfo.getStoredHeapSize(), 
					pdsInst.getOverflowCode(false),
					newHeapPtr_pair+">"+pdsInfo.getStoredHeapSize(),
					pdsInst.getOverflowCode(true)));
			pdsStmt.addIf(PDSInst.elseStmt(content, content_pair));
		} else {
			pdsStmt.addNext(content, content_pair);
		}
		return pdsStmt;
	}
	
	/**
	 * Returns true if a constructor of StringBuilder is called.
	 * 
	 * @param calledName the array of [class, method, descriptor]
	 * @return true if class is java/lang/StringBuilder, method is <init>,
	 * 		descriptor is ()V or (Ljava/lang/String;)V. False, otherwise.
	 */
	public static boolean isInvokespecialStringBuilder(String[] calledName) {
		
		return calledName[0].equals("java/lang/StringBuilder")
			&& calledName[1].equals("<init>")
			&& (calledName[2].equals("()V") 
					|| calledName[2].equals("(Ljava/lang/String;)V"));
	}
	
	/**
	 * Returns ture if a method of String or StringBuilder is called.
	 * 
	 * @param calledName the array of [class, method, descriptor]
	 * @return true if class is java/lang/String or java/lang/StringBuilder
	 */
	public static boolean isInvokevirtualString(String[] calledName) {
		
		return calledName[0].equals("java/lang/String")
				|| calledName[0].equals("java/lang/StringBuilder");
	}
	
	/**
	 * Retruns a string that represents header of the method, which simulates
	 * java/lang/Strig(Ljava/lang/Object;)Z.
	 * 
	 * @return the string
	 */
	public static String remoplaHeadForEquals() {
		
		return "module int stringEquals(int s1, int s0)";
	}
	
	/**
	 * Returns a string that represents the method, which simulates
	 * java/lang/Strig(Ljava/lang/Object;)Z.
	 * 
	 * @param pdsInfo the global info
	 * @return the string
	 */
	public static String remoplaForEquals(PDSInfo pdsInfo) {
		
		HeapOption heapOption = pdsInfo.getHeapOption();
		String heapName = pdsInfo.getHeapName();
		int intBits = pdsInfo.getBits();
		int charBits = pdsInfo.getCharBits();
		
		StringBuilder cont = new StringBuilder();
		
		cont.append(remoplaHeadForEquals());
		cont.append("\n");
		cont.append("{\n");
		cont.append("int counter;\n");
		cont.append("int length1;\n");
		cont.append("int length0;\n");
		cont.append("int equals(1);\n");
		cont.append("\n");
		cont.append("if\n");
		cont.append("\t:: s1 == s0 -> return 1;\n");
		cont.append("\t:: else -> skip;\n");
		cont.append("fi;\n");
		if (heapOption == HeapOption.SIMPLE) {
			cont.append("length1 = " + heapName + "[s1], length0 = " + heapName 
					+ "[s0], equals = 1;\n");
		} else {
			String l1 = PDSInst.loadHeap(heapName, intBits + "*s1", intBits);
			String l0 = PDSInst.loadHeap(heapName, intBits + "*s0", intBits);
			cont.append("length1 = " + l1 + ", length0 = " + l0 
					+ ", equals = 1;\n");
		}
		cont.append("if\n");
		cont.append("\t:: length1 == length0 -> counter = 0;\n");
		cont.append("\t\tdo\n");
		cont.append("\t\t\t:: (equals == 1) && (counter < length1) -> \n");
		cont.append("\t\t\t\tif\n");
		if (heapOption == HeapOption.SIMPLE) {
			cont.append("\t\t\t\t\t:: " 
					+ PDSInfo.stringIndex(heapName, "s1+counter+1") + " == " 
					+ PDSInfo.stringIndex(heapName, "s0+counter+1"));
		} else {
			String index1 = intBits + "*(s1+1)+" + charBits + "*counter";
			String index0 = intBits + "*(s0+1)+" + charBits + "*counter";
			cont.append("\t\t\t\t\t:: " 
					+ PDSInfo.stringIndex(heapName, index1) + " == " 
					+ PDSInfo.stringIndex(heapName, index0));
			for (int i = 1; i < charBits; i++) {
				
				index1 = intBits + "*(s1+1)+" + charBits + "*counter+" + i;
				index0 = intBits + "*(s0+1)+" + charBits + "*counter+" + i;
				cont.append(" && ");
				cont.append(PDSInfo.stringIndex(heapName, index1) + " == "
						+ PDSInfo.stringIndex(heapName, index0));
			}
		}
		cont.append(" -> counter = counter + 1;\n");
		cont.append("\t\t\t\t\t:: else -> equals = 0, counter = counter + 1;\n");
		cont.append("\t\t\t\tfi;\n");
		cont.append("\t\t\t:: else -> break;\n");
		cont.append("\t\tod;\n");
		cont.append("\t:: else -> equals = 0;\n");
		cont.append("fi;\n");
		cont.append("\n");
		cont.append("return equals;\n");
		cont.append("}\n");
		
		return cont.toString();
	}

	/*******************************************************************/
	/**                INTERNAL UTILITIY FUNCTIONS                    **/
	/*******************************************************************/
	
	private static boolean appendInvoked(String[] calledName) {
		
		return calledName[0].equals("java/lang/StringBuilder") 
		&& calledName[1].equals("append")
		&& calledName[2].equals("(Ljava/lang/String;)Ljava/lang/StringBuilder;");
	}
	
	private static boolean equalsInvoked(String[] calledName) {
		
		return calledName[0].equals("java/lang/String") 
		&& calledName[1].equals("equals")
		&& calledName[2].equals("(Ljava/lang/Object;)Z");
	}
	
	private static boolean lengthInvoked(String[] calledName) {
		
		return calledName[0].equals("java/lang/String") 
		&& calledName[1].equals("length")
		&& calledName[2].equals("()I");
	}
	
	private static boolean toStringInvoked(String[] calledName) {
		
		return calledName[0].equals("java/lang/StringBuilder") 
		&& calledName[1].equals("toString")
		&& calledName[2].equals("()Ljava/lang/String;");
	}
	
	/**
	 * Creates Remopla code for StringBuilder.append(LString;)LStringBuilder;.
	 * 
	 * If pdsInfo.getHeapOption() == HeapOption.SIMPLE,
	 * <pre>
	 * counter = 0, addedLength = heap[s0], adderLength = heap[s1];
	 * if
	 *   :: addedLenght + adderLength > pdsInfo.getStringBuilderSize() ->
	 *       pdsInst.getStringBuilderOverflowCode();
	 *   :: else -> skip;
	 * fi;
	 * do
	 *   :: counter < addedLength -> 
	 *       heap[s1+adderLength+counter+1] = heap[s0+counter+1],
	 *       counter = counter + 1;
	 *   :: else ->break;
	 * od;
	 * heap[s1] = heap[s1]+heap[s0], s0 = s1, s1 = s2, ...;
	 * </pre>
	 * 
	 * If pdsInfo.getHeapOption() == HeapOption.TWODIMS and assume that
	 * integer bits = 3 and character bits = 2 by default,
	 * <pre>
	 * counter = 0, addedLength = PDSInst.loadHeap(heap, 3*s0, 3),
	 *     adderLength = PDSInst.loadHeap(heap, 3*s1, 3);
	 * if
	 *   :: addedLenght + adderLength > pdsInfo.getStringBuilderSize() ->
	 *       pdsInst.getStringBuilderOverflowCode();
	 *   :: else -> skip;
	 * fi;
	 * do
	 *   :: counter < addedLength -> 
	 *       heap[3*(s1+1)+2*(adderLength+counter)] = heap[3*(s0+1)+2*counter],
	 *       heap[3*(s1+1)+2*(adderLength+counter)+1] = heap[3*(s0+1)+2*counter+1], 
	 *       counter = counter + 1;
	 *   :: else ->break;
	 * od;
	 * heap[s1] = PDSInst.storeHeap(heap, (addedLength+adderLength), 3*s1, 3), 
	 *   s0 = s1, s1 = s2, ...;
	 * </pre>
	 * 
	 * @param pdsInfo the global info
	 * @param pdsMethod the method that contains the instruction
	 * @param pdsInst the object of this bytecode instruction
	 * @return the PDSStmt that contains Remopla code
	 */
	private static PDSStmt appendStmt(PDSInfo pdsInfo, PDSMethod pdsMethod, PDSInst pdsInst) {
		String adder = pdsInfo.stackIndex(1);
		String addee = pdsInfo.stackIndex(0);
		int intBits = pdsInfo.getBits();
		int charBits = pdsInfo.getCharBits();
		PDSStmt pdsStmt = new PDSStmt();
		
		String counterVar = "counter";
		pdsMethod.addLocalVar(counterVar);
		String addedLengthVar = "addedLength";
		pdsMethod.addLocalVar(addedLengthVar);
		String adderLengthVar = "adderLength";
		pdsMethod.addLocalVar(adderLengthVar);
		
		pdsStmt.addNext(counterVar + "=0", counterVar+"=0");
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
			pdsStmt.addPar(addedLengthVar + "=" + pdsInfo.heapIndex(addee),
					addedLengthVar + "=" + pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),addee));
			pdsStmt.addPar(adderLengthVar + "=" + pdsInfo.heapIndex(adder),
					adderLengthVar + "=" + pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),adder));
		} else { // if (pdsInfo.getHeapOption() == HeapOption.TWODIMS)
			pdsStmt.addPar(addedLengthVar + "=" + PDSInst.loadHeap(pdsInfo.getHeapName(), 
							intBits + "*" + addee, intBits),
						addedLengthVar+"="+PDSInst.loadHeap(pdsInfo.generateTLabel(pdsInfo.getHeapName()),
							intBits+"*"+addee, intBits));
			pdsStmt.addPar(adderLengthVar + "=" + PDSInst.loadHeap(pdsInfo.getHeapName(), 
							intBits + "*" + adder, intBits),
							adderLengthVar+"="+PDSInst.loadHeap(pdsInfo.generateTLabel(pdsInfo.getHeapName()),
							intBits + "*" + adder, intBits));
		}
		
		if (pdsInfo.checkForStringBuilderOverflow()) {
			String cond = addedLengthVar + "+" + adderLengthVar + ">" + pdsInfo.getStringBuilderSize();
			pdsStmt.addIf(PDSInst.ifStmt(cond, pdsInst.getStringBuilderOverflowCode(false),
										cond, pdsInst.getStringBuilderOverflowCode(true)));
			pdsStmt.addIf(PDSInst.elseStmt("skip", "skip"));
		}
		
		StringBuilder cont = new StringBuilder();
		cont.append("do\n");
		cont.append("\t:: " + counterVar + "<" + addedLengthVar);
		cont.append(" -> ");
		StringBuilder cont_pair=new StringBuilder();
		cont_pair.append(cont);
		
		String lhs, rhs;
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
			
			lhs = adder + "+" + adderLengthVar + "+" + counterVar + "+1";
			rhs = addee + "+" + counterVar + "+1";
			cont.append(pdsInfo.heapIndex(lhs) + "=" + pdsInfo.heapIndex(rhs));
			cont.append(", ");
			cont_pair.append(pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), lhs) + "=" +
				pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), rhs));
			cont_pair.append(", ");
		} else { // if (pdsInfo.getHeapOption() == HeapOption.TWODIMS)
			lhs = intBits + "*(" + adder + "+1) + " + charBits + "*(" + adderLengthVar + "+" + counterVar + ")";
			rhs = intBits + "*(" + addee + "+1) + " + charBits + "*" + counterVar;
			for (int i = 0; i < charBits; i++) {
				cont.append(pdsInfo.heapIndex(lhs + " + " + i) + "=" + pdsInfo.heapIndex(rhs + " + " + i));
				cont.append(", ");
				cont_pair.append(pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),lhs + "+" + i) + "=" +
					pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), rhs + "+" + i));
				cont_pair.append(", ");
			}
		}
		cont.append(counterVar + "=" + counterVar + "+1");
		cont.append(";\n");
		cont.append("\t:: else -> break;\n");
		cont.append("od");
		cont_pair.append(counterVar+"="+counterVar+"+1");
		cont_pair.append(";\n");
		cont_pair.append("\t:: else -> break;\n");
		cont_pair.append("od");
		pdsStmt.addNext(cont.toString(), cont_pair.toString());
		pdsStmt.addNext(pdsInst.popPushStmt(adder, 2, 1), pdsInst.popPushStmt(adder,2,1));
		
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
			pdsStmt.addPar(pdsInfo.heapIndex(adder) + "=" + pdsInfo.heapIndex(adder) + "+" + pdsInfo.heapIndex(addee),
						pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), adder) + "=" +
						pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), adder) + "+" +
						pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), addee));
		} else {
			String newLength = "(" + addedLengthVar + "+" + adderLengthVar + ")";
			pdsStmt.addPar(PDSInst.storeHeap(pdsInfo.getHeapName(), newLength, intBits + "*" + adder, intBits),
							PDSInst.storeHeap(pdsInfo.generateTLabel(pdsInfo.getHeapName()), newLength, intBits +
							"*" + adder, intBits));
		}
		
		return pdsStmt;
	}
	
	/**
	 * Creates Remopla code for initializing the heap at adder with the string 
	 * pointed by addee. The stack is manipulated concerning pop and push.
	 * If alloc is true, the heap pointer is also incremented by the size of
	 * addee.
	 * 
	 * If pdsInfo.getHeapOption() == HeapOption.SIMPLE,
	 * <pre>
	 * counter = 0, addedLength = heap[addee];
	 * do
	 *   :: counter < addedLength -> 
	 *       heap[adder+counter+1] = heap[addee+counter+1],
	 *       counter = counter + 1;
	 *   :: else ->break;
	 * od;
	 * heap[s1] = heap[s0], 
	 * <if(pop==2 && push==0)> s0 = s2, s1 = s3, ...
	 * <if(pop==1 && push==1)> s0 = s1
	 * <if(alloc)> , ptr = ptr + addedLenght + 1;
	 * </pre>
	 * 
	 * If pdsInfo.getHeapOption() == HeapOption.TWODIMS and assume that
	 * integer bits = 3 and character bits = 2 by default,
	 * <pre>
	 * counter = 0, addedLength = PDSInst.loadHeap(heap, 3*adee, 3);
	 * do
	 *   :: counter < addedLength -> 
	 *       heap[3*(adder+1)+2*counter] = heap[3*(addee+1)+2*counter],
	 *       heap[3*(adder+1)+2*counter+1] = heap[3*(addee+1)+2*counter+1], 
	 *       counter = counter + 1;
	 *   :: else ->break;
	 * od;
	 * heap[s1] = PDSInst.storeHeap(heap, addedLength, 3*s1, 3),
	 * <if(pop==2 && push==0)> s0 = s2, s1 = s3, ...
	 * <if(pop==1 && push==1)> s0 = s1
	 * <if(alloc)> , ptr = ptr + ceil((2*addedLength)/3) + 1;
	 * </pre>
	 * 
	 * @param pdsInfo the global info
	 * @param pdsMethod the method that contains this instruction
	 * @param pdsInst the object of this bytecode instruction
	 * @param adder the pointer (to the heap) of adder
	 * @param addee the pointer (to the heap) of the string to be added.
	 * @param pop the number of pop of this bytecode instruction
	 * @param push the number of push of this bytecode instruction
	 * @param alloc if true, the heap is allocated
	 * @return the PDSStmt that contains Remopla code
	 */
	private static PDSStmt initWithStringStmt(PDSInfo pdsInfo, PDSMethod pdsMethod, 
			PDSInst pdsInst, String adder, String addee, int pop, int push,
			boolean alloc) {
		
		int intBits = pdsInfo.getBits();
		int charBits = pdsInfo.getCharBits();
		PDSStmt pdsStmt = new PDSStmt();
		
		String counterVar = "counter";
		pdsMethod.addLocalVar(counterVar);
		String addedLengthVar = "addedLength";
		pdsMethod.addLocalVar(addedLengthVar);
		
		pdsStmt.addNext(counterVar + "=0", counterVar+"=0");
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
			pdsStmt.addPar(addedLengthVar + "=" + pdsInfo.heapIndex(addee),
							addedLengthVar+"="+pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), addee));
		} else { // if (pdsInfo.getHeapOption() == HeapOption.TWODIMS)
			pdsStmt.addPar(addedLengthVar + "=" + PDSInst.loadHeap(pdsInfo.getHeapName(), intBits + "*" + addee, intBits),
				addedLengthVar+"="+PDSInst.loadHeap(pdsInfo.generateTLabel(pdsInfo.getHeapName()), intBits+"*"+addee, intBits));
		}
		
		if (pdsInfo.checkForStringBuilderOverflow() && !alloc) {
			
			String cond = addedLengthVar + ">" + pdsInfo.getStringBuilderSize();
			pdsStmt.addIf(PDSInst.ifStmt(cond, pdsInst.getStringBuilderOverflowCode(false),
										cond, pdsInst.getStringBuilderOverflowCode(true)));
			pdsStmt.addIf(PDSInst.elseStmt("skip","skip"));
		}
		
		StringBuilder cont = new StringBuilder();
		cont.append("do\n");
		cont.append("\t:: " + counterVar + "<" + addedLengthVar);
		cont.append(" -> ");
		
		String lhs, rhs;
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
			
			lhs = adder + "+" + counterVar + "+1";
			rhs = addee + "+" + counterVar + "+1";
			cont.append(pdsInfo.heapIndex(lhs) + "=" + pdsInfo.heapIndex(rhs));
			cont.append(", ");
		} else { // if (pdsInfo.getHeapOption() == HeapOption.TWODIMS)
			
			lhs = intBits + "*(" + adder + "+1) + " + charBits + "*" + counterVar;
			rhs = intBits + "*(" + addee + "+1) + " + charBits + "*" + counterVar;
			for (int i = 0; i < charBits; i++) {
				cont.append(pdsInfo.heapIndex(lhs + " + " + i) + "=" 
						+ pdsInfo.heapIndex(rhs + " + " + i));
				cont.append(", ");
			}
		}
		cont.append(counterVar + "=" + counterVar + "+1");
		cont.append(";\n");
		cont.append("\t:: else -> break;\n");
		cont.append("od");
		pdsStmt.addNext(cont.toString(), cont.toString());
		
		if (push == 0)
			pdsStmt.addNext(pdsInst.popStmt(pop, pop), pdsInst.popStmt(pop, pop));
		else
			pdsStmt.addNext(pdsInst.popPushStmt(adder, pop, push), pdsInst.popPushStmt(adder,pop,push));
		
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
			pdsStmt.addPar(pdsInfo.heapIndex(adder) + "=" + pdsInfo.heapIndex(addee),
						pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),adder)+"="+
						pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),addee));
		} else {
			pdsStmt.addPar(PDSInst.storeHeap(pdsInfo.getHeapName(), addedLengthVar, intBits + "*" + adder, intBits),
				PDSInst.storeHeap(pdsInfo.generateTLabel(pdsInfo.getHeapName()), addedLengthVar, intBits+"*"+adder, intBits));
		}
		
		if (alloc) {
			String heapPtr = pdsInfo.getHeapPtrName();
			String heapPtr_pair=pdsInfo.generateTLabel(pdsInfo.getHeapPtrName());
			if (pdsInfo.getHeapOption() == HeapOption.SIMPLE) {
				String newHeapPtr = heapPtr + "+" + addedLengthVar + "+1";
				String newHeapPtr_pair=heapPtr_pair+"+"+addedLengthVar+"+1";
				String stmt = heapPtr + "=" + newHeapPtr;
				String stmt_pair=heapPtr_pair+"="+newHeapPtr_pair;
				if (pdsInfo.checkForHeapOverflow()) {
					pdsStmt.addIf(PDSInst.ifStmt(newHeapPtr + ">" + pdsInfo.getHeapSize(), pdsInst.getOverflowCode(false),
											newHeapPtr_pair+">"+pdsInfo.getHeapSize(),pdsInst.getOverflowCode(true)));
					pdsStmt.addIf(PDSInst.elseStmt(stmt, stmt_pair));
				} else {					
					pdsStmt.addPar(stmt, stmt_pair);
				}
			} else {
				//String s0 = pdsInfo.stackIndex(0);
				String lenTimesArrBits = "(" + addedLengthVar + "*" + charBits + ")";
				String newHeapPtr1 = heapPtr + "+" + lenTimesArrBits + "/" + intBits + "+1";
				String newHeapPtr2 = newHeapPtr1 + "+1";
				String newHeapPtr1_pair=heapPtr_pair+"+"+lenTimesArrBits+"/"+intBits+"+1";
				String newHeapPtr2_pair=newHeapPtr1_pair+"+1";
				String lenModIntBits = PDSInst.iremStmt(lenTimesArrBits, String.valueOf(intBits));
				String cond1 = lenModIntBits + "==0";
				String cond2 = lenModIntBits + "!=0";
				String stmt1 = heapPtr + "=" + newHeapPtr1;
				String stmt2 = heapPtr + "=" + newHeapPtr2;
				String stmt1_pair=heapPtr_pair+"="+newHeapPtr1_pair;
				String stmt2_pair=heapPtr_pair+"="+newHeapPtr2_pair;
				
				if (pdsInfo.checkForHeapOverflow()) {
					int heapSize = pdsInfo.getStoredHeapSize();
					String ofCode = pdsInst.getOverflowCode(false);
					String ofCode_pair=pdsInst.getOverflowCode(true);
					pdsStmt.addIf(PDSInst.addNewarrayStmt(cond1, newHeapPtr1 + ">" + heapSize, ofCode,
														cond1, newHeapPtr1_pair+">"+heapSize, ofCode_pair));
					pdsStmt.addIf(PDSInst.addNewarrayStmt(cond1, newHeapPtr1 + "<=" + heapSize, stmt1,
														cond1, newHeapPtr1_pair+"<="+heapSize, stmt1_pair));
					pdsStmt.addIf(PDSInst.addNewarrayStmt(cond2, newHeapPtr2 + ">" + heapSize, ofCode,
														cond2, newHeapPtr2_pair+">"+heapSize, ofCode_pair));
					pdsStmt.addIf(PDSInst.addNewarrayStmt(cond2, newHeapPtr2 + "<=" + heapSize, stmt2,
														cond2, newHeapPtr2_pair+">"+heapSize, stmt2_pair));
				} else {
					pdsStmt.addIf(PDSInst.ifStmt(cond1, stmt1, cond1, stmt1_pair));
					pdsStmt.addIf(PDSInst.ifStmt(cond2, stmt2, cond2, stmt2_pair));
				}
			}
		}
		
		return pdsStmt;
	}
	
	/**
	 * Creates Remopla code for StringBuilder.append(LString;)LStringBuilder;.
	 * 
	 * If pdsInfo.getHeapOption() == HeapOption.SIMPLE,
	 * <pre>
	 * s0 = heap[s0];
	 * </pre>
	 * 
	 *  If pdsInfo.getHeapOption() == HeapOption.TWODIMS and assume that
	 * integer bits = 3 by default,
	 * <pre>
	 * s0 = PDSInst.loadHeap(heap, 3*s0, 3);
	 * </pre>
	 * 
	 * @param pdsInfo the global info
	 * @param pdsInst the object of this bytecode instruction
	 * @return the PDSStmt that contains Remopla code
	 */
	private static PDSStmt lengthStmt(PDSInfo pdsInfo, PDSInst pdsInst) {
		
		String s0 = pdsInfo.stackIndex(0);
		String stringLength, stringLength_pair;
		PDSStmt pdsStmt = new PDSStmt();
		
		if (pdsInfo.getHeapOption() == HeapOption.SIMPLE){
			stringLength = pdsInfo.heapIndex(s0);
			stringLength_pair=pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()), s0);
		} else {// if (pdsInfo.getHeapOption() == HeapOption.TWODIMS)
			stringLength = PDSInst.loadHeap(pdsInfo.getHeapName(), pdsInfo.getBits() + "*" + s0, pdsInfo.getBits());
			stringLength_pair=PDSInst.loadHeap(pdsInfo.generateTLabel(pdsInfo.getHeapName()), pdsInfo.getBits()+"*"+s0, pdsInfo.getBits());
		}
		if (pdsInfo.checkForNullPointerExceptions()) {
			pdsStmt.addIf(PDSInst.ifStmt(s0 + "==0", pdsInst.getNPECode(false),
										s0+"==0", pdsInst.getNPECode(true)));
			pdsStmt.addIf(PDSInst.elseStmt(pdsInst.popPushStmt(stringLength, 1, 1),
										pdsInst.popPushStmt(stringLength_pair,1,1)));
		} else {			
			pdsStmt.addNext(pdsInst.popPushStmt(stringLength, 1, 1),
							pdsInst.popPushStmt(stringLength_pair,1,1));
		}
		
		return pdsStmt;
	}
	
	/**
	 * Creates Remopla code for java/lang/StringBuilder.toString()Ljava/lang/String;
	 * 
	 * @param pdsInfo the global info
	 * @param pdsMethod the method that contains this instruction
	 * @param pdsInst the object of this bytecode instruction
	 * @return the Remopla code
	 */
	private static PDSStmt toStringStmt(PDSInfo pdsInfo, PDSMethod pdsMethod,
			PDSInst pdsInst) {
		
		String ptr = pdsInfo.getHeapPtrName();
		String s0 = pdsInfo.stackIndex(0);
		return initWithStringStmt(pdsInfo, pdsMethod, pdsInst, ptr, s0, 1, 1, true);
	}
	
	/**
	 * Builds the table of every character that will appear in the Remopla code.
	 * 
	 * @param cf the ClassFile
	 */
	private void buildCharTable(ClassFile cf) {
		
		CPInfo[] cpInfo = cf.getConstantPool();
		ConstantStringInfo csInfo;
		int constIndex;
		String string;
		
		logger.debug(cpInfo.length);
		for (int i = 1; i < cpInfo.length; i++) {
			
			if (cpInfo[i] == null) continue;
			if (cpInfo[i].getTag() == CPInfo.CONSTANT_STRING) {
				
				csInfo = (ConstantStringInfo) cpInfo[i];
				constIndex = csInfo.getStringIndex();
				string = ((ConstantUtf8Info) cpInfo[constIndex]).getString();
				char[] charArray = string.toCharArray();
				for (int j = 0; j < charArray.length; j++) {
					
					if (!charTable.containsKey(charArray[j]))
						charTable.put(charArray[j], charTable.size());
				}
			}
		}
		
		logger.debug(charTable.toString());
	}

	/**
	 * Builds the table of every character that will appear in the Remopla code.
	 * 
	 * @param classFileList the liast of included ClassFile
	 */
	public void buildCharTable(List<ClassFile> classFileList) {
		
		charTable = new Hashtable<Character, Integer>();
		for (ClassFile cf : classFileList) {
			
			try {
				logger.debug("buildCharTable for " + cf.getThisClassName());
			} catch (InvalidByteCodeException e) {
			}
			buildCharTable(cf);
		}
	}
	
}
