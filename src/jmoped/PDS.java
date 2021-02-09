
package jmoped;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.MethodInfo;

public class PDS {
	
	static Logger logger = Logger.getLogger(PDS.class);
	
	private PDSInfo pdsInfo;
	private ArrayList<PDSClass> pdsClassList = new ArrayList<PDSClass>();

	/**
	 * Optional initial method which represents the entry point for the model
	 * checking analysis. This used to be the main method, but we want to
	 * allow other static and even non static methods as well.
	 */
	private PDSMethod initialMethod = null;
	
	private MethodWrapper initialMethodWrapper = null;
	
	/**
	 * Creates a PDS instance to generate Remopla code for the class file
	 * located at <code>classFileName</code>
	 * @param classFileName path name to the file can either be relative
	 * to the current working directory or absolute
	 * @throws Exception
	 */
	public PDS(String classFileName) throws Exception {
		this(new PDSInfo(new File(classFileName)));
	}
	//modified by suncong, add parameter policyFileName
	public PDS(String classFileName, String methodName, String methodDescriptor, String policyFileName)
		throws Exception
	{
		this(new PDSInfo(new File(classFileName), methodName, methodDescriptor, policyFileName));
	}
	
	/**
	 * Creates a PDS instance to generate Remopla code for the given
	 * class.
	 * @param clazz
	 * @throws Exception
	 */
	public PDS(Class clazz) throws Exception {
		this(new PDSInfo(clazz));
	}
	
	public PDS(Class clazz, String methodName, String methodDescriptor) 
		throws Exception
	{
		this(new PDSInfo(clazz, methodName, methodDescriptor));
	}

	public PDS(String className, String[] pathEntries) throws Exception 
	{
		this(new PDSInfo(className, pathEntries));
	}
	
	public PDS(String className, String[] pathEntries, String methodName, String methodDescriptor) throws Exception 
	{
		this(new PDSInfo(className, pathEntries, methodName, methodDescriptor));
	}
	
	public PDS(PDSInfo info) throws Exception {
		pdsInfo = info;
		logger.info("Entering PDS constructor.");
		initPDSClassList();
		pdsInfo.setClassIndexForPolicy();//added by suncong, must be called after buildClassTable()
		pdsInfo.setRelativeFieldOffsetForPolicy();//added by suncong, must be called after buildFieldTable()
		pdsInfo.PrintPolicy();//added by suncong,debugging info
		setupInitialMethod();
		logger.info("Leaving PDS constructor.");
	}
	
	/**
	 * Returns the configuration object responsible for this pds instance.
	 * @return
	 */
	public PDSInfo getInfo()
	{
		return pdsInfo;
	}
	
	/**
	 * Returns the method that has been set to be the entry point for the analysis or
	 * the main method of the class that is to be analysed.
	 * 
	 * @return <code>null</code> if no method has been set and there is no main 
	 * method in the first class
	 * @throws InvalidByteCodeException 
	 */
	public PDSMethod getInitialMethod()
	{
		return initialMethod;
	}
	
	/**
	 * Returns null if there is none.
	 */
	public MethodWrapper getInitialMethodWrapper()
	{
		return initialMethodWrapper;
	}
	
	private PDSMethod getInternalInitialMethod()
	{
		return initialMethodWrapper != null ? initialMethodWrapper : getInitialMethod();
	}
	
	// TODO move this to unit tests
/*commented by suncong
	public static void main(String[] args) {
		
		PDS pds;
		
		try {
			PDSInfo.simplified = true;
			pds = new PDS("ex/LinkedList.class");
			logger.info("Included Methods:\n" + pds.includedTableInfo());
			logger.info("Field Indices:\n" + pds.fieldIndexTableInfo());
			System.out.print(pds.toRemopla());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	/*******************************************************************/
	/**                       PDSInfo METHODS                         **/
	/*******************************************************************/
	
	public String fieldIndexTableInfo() {
		
		return pdsInfo.fieldIndexTableInfo();
	}
	
	public String getLabelAssertError() {
		
		return pdsInfo.getLabelAssertError();
	}
	
	public String getLabelHeapOverflow() {
		
		return pdsInfo.getLabelHeapOverflow();
	}

	public String getLabelNPE() {
	
	return pdsInfo.getLabelNPE();
	}
	
	public String getMopedPath() {
		
		return pdsInfo.getMopedPath();
	}
	
	public String includedTableInfo() throws InvalidByteCodeException {
		
		return pdsInfo.includedTableInfo();
	}
/*&*/
	public void putAllLineBitTable(Hashtable<Integer, Integer> lbTable) {
		
		pdsInfo.putAllLineBitTable(lbTable);
	}
/*&*/
	public void setBits(int bits) {
		
		pdsInfo.setBits(bits);
	}
	public int getBits(){
		return pdsInfo.getBits();
	}
/*&*/
	public void setHeapOption(PDSInfo.HeapOption heapOption) {
		
		pdsInfo.setHeapOption(heapOption);
	}
/*&*/
	public void setHeapSize(int heapSize) {
		
		pdsInfo.setHeapSize(heapSize);
	}
	
	/*******************************************************************/
	/**                       PRIVATE METHODS                         **/
	/*******************************************************************/
	
	private String selfLoopStmt(String label) {
		
		StringBuffer out = new StringBuffer();
		
		out.append(label);
		out.append(": goto ");
		out.append(label);
		out.append(";\n");
		
		return out.toString();
	}

	/**
	 * Returns an unmodifiable list of all PDSClass objects that have been
	 * created.
	 *
	 * @return 
	 */
	public List<PDSClass> getPDSClassList()
	{
		return Collections.unmodifiableList(pdsClassList);
	}
	
	/**
	 *The original getMethodByFormattedName can not be used in simplified mode.
	 */
	public PDSMethod getMethodByFormattedNameForNonSimplified(String methodName)
	{
		
		if (initialMethodWrapper != null 
				&& initialMethodWrapper.getFormattedName().equals(methodName)) {
			return initialMethodWrapper;
		}
		//System.out.println("methodName:"+methodName);///
		for (PDSClass clazz : pdsClassList) {
			//System.out.println("clazz.formattedClassName:"+clazz.getFormattedClassName());///
			if (methodName.startsWith(clazz.getFormattedClassName())) {
				for (PDSMethod method : clazz.getMethodList()) {
					if (method.getFormattedName().equals(methodName)) {
						return method;
					}
				}
			}
		}
		return null;
	}
	public PDSMethod getMethodByFormattedNameForSimplified(String className,
		String methodName, String methodDescriptor){
		for(PDSClass clazz: pdsClassList){
			if(className.equals(clazz.getFormattedClassName())){
				for(PDSMethod mtd:clazz.getMethodList()){
					if(mtd.getFormattedName().equals(methodName) &&	mtd.getDescriptor().equals(methodDescriptor)){
						System.out.println("reach method<"+clazz.getFormattedClassName()+
								";"+mtd.getFormattedName()+";"+mtd.getDescriptor()+">");
						return mtd;
					}
				}
			}
		}
		return null;
	}
/*&*//*****************************initPDSClassList******************************/
	private void initPDSClassList() throws InvalidByteCodeException, IOException
	{
		logger.debug("Entering initPDSClassList");
		
		List<ClassFile> classFileList = pdsInfo.getClassFileList();
		for (ClassFile classFile : classFileList) {
			pdsClassList.add(new PDSClass(classFile, pdsInfo));
		}
		
		pdsInfo.buildFieldTable(pdsClassList);
		pdsInfo.buildClassTable(pdsClassList);
		
		for (PDSClass pdsClass : pdsClassList) {
			pdsClass.initPDSMethodList(pdsInfo);
		}
			
		logger.debug("Leaving initPDSClassList");
	}
/**********************************************************************/
/*************************setupInitialMethod*********************************/
	private void setupInitialMethod() throws InvalidByteCodeException, IOException
	{
		MethodInfo method = pdsInfo.getInitialMethod();
		if (method == null) {
			return;
		}
		
		initialMethod = pdsClassList.get(0).getMethod(method.getName(), method.getDescriptor());
		
		if (!initialMethod.isMainMethod()) {
			if (!initialMethod.isStatic()|| initialMethod.getParamCount() > 0) {
				initialMethodWrapper = new MethodWrapper(initialMethod.getMethodInfo(), pdsInfo, this);//by suncong,add the last param
			}
		}
	}
/**************************************************************************/
/************************translate*****************************************/
	public void translate() throws InvalidByteCodeException {
		
		for (PDSClass pdsClass : pdsClassList) {
			pdsClass.translate();
		}
	}
/*********************************************************************/
/************************toRemopla****************************************/
	public String toRemopla() throws InvalidByteCodeException, IOException {
		
		PDSClass pdsClass;
		StringBuffer out = new StringBuffer();
		PDSMultiArrayInitializerMethod multiArrayMethod = 
			pdsInfo.includeMultiArrayInitializerMethod() ? 
					new PDSMultiArrayInitializerMethod(pdsInfo) : null;
		
		int heapSize = pdsInfo.getStoredHeapSize();
		int heapBit = PDSDefault.getMinimumBitsForHeapSize(heapSize);
		int intBit = pdsInfo.getBits();
		
		out.append("define DEFAULT_INT_BITS ");
		out.append(String.valueOf(intBit));
		out.append("\n\n");
		
		out.append("int ");
		out.append(pdsInfo.heapIndex(String.valueOf(pdsInfo.getHeapSize())));
		if (pdsInfo.getHeapOption() == PDSInfo.HeapOption.TWODIMS) {
			out.append("(1)");
		}
		out.append(";\n");
		
		out.append("int ").append(pdsInfo.getHeapPtrName()).append("(").append(String.valueOf(heapBit)).append(");\n");
		
		/*added by suncong*/
		out.append("int ");
		out.append(pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),String.valueOf(pdsInfo.getHeapSize())));
		if (pdsInfo.getHeapOption() == PDSInfo.HeapOption.TWODIMS) {
			out.append("(1)");
		}
		out.append(";\n");
		out.append("int ").append(pdsInfo.generateTLabel(pdsInfo.getHeapPtrName())).append("(").append(String.valueOf(heapBit)).append(");\n");
		
		out.append("int ");
		out.append(pdsInfo.stringIndex(pdsInfo.getLowidx1Name(), String.valueOf(pdsInfo.getHeapSize())));
		out.append(";\n");
		out.append("int ");
		out.append(pdsInfo.stringIndex(pdsInfo.getLowidx2Name(), String.valueOf(pdsInfo.getHeapSize())));
		out.append(";\n");
		out.append("int ").append(pdsInfo.getPtridxName()).append("(").append(String.valueOf(heapBit)).append(");\n");
		out.append("int ");
		out.append(pdsInfo.stringIndex(pdsInfo.getLowrefName(), String.valueOf(pdsInfo.getHeapSize())));
		out.append(";\n");
		out.append("int ").append(pdsInfo.getPtrrefName()).append("(").append(String.valueOf(heapBit)).append(");\n\n");
		/**********************************/
		
		// collect declarations of static fields and declarations of
		// class methods
		
		if (initialMethodWrapper != null) {
			out.append(initialMethodWrapper.toRemoplaHead()).append(";\n");
		}
		if (multiArrayMethod != null 
				&& pdsInfo.getHeapOption() == PDSInfo.HeapOption.SIMPLE) {
			out.append(multiArrayMethod.toRemoplaHead()).append(";\n");
		}
		if (pdsInfo.isIncludeStringEqualsMethod()) {
			out.append(PDSString.remoplaHeadForEquals()).append(";\n");
		}
		
		for (int i = 0; i < pdsClassList.size(); i++) {
			
			pdsClass = (PDSClass) pdsClassList.get(i);
			out.append(pdsClass.toRemoplaGlobal());
			out.append("\n");
		}
		
		out.append("init");
		
		if (PDSInfo.Stefan) {
			addStefansOutput(out);
		} 
		// We'll always use Stefan's option.
		/*else {

			for (int i = pdsClassList.size() - 1; i >= 0; i--) {
				
				pdsClass = (PDSClass) pdsClassList.get(i);
				pdsMethod = pdsClass.getMethodClinit();
				if (pdsMethod != null) {
					pdsMethod.addInitPtr();	//Dirty pointer initialization
					out.append(" ");
					out.append(pdsMethod.getFormattedName());
				}
			}
			pdsMethod = getInternalInitialMethod();
			if (pdsMethod != null) {
				out.append(" ");
				out.append(pdsMethod.getFormattedName());
			}
			out.append(";\n\n");
		}*/
		
		// collect all method definitions
		
		if (initialMethodWrapper != null) {
			/*toRemopla of initialMethodWrapper need to be modified. We pass the pdsClassList
			 *into it, because during generating the initialWrapper instructions we need to
			 *record the method called by A.<init> and generate a series of PDSMethod which has
			 *post initialization operations.*/
			out.append(initialMethodWrapper.toRemopla(pdsClassList)).append("\n");
		}
		if (multiArrayMethod != null 
				&& pdsInfo.getHeapOption() == PDSInfo.HeapOption.SIMPLE) {
			out.append(multiArrayMethod.toRemopla()).append("\n");
		}
		if (pdsInfo.isIncludeStringEqualsMethod()) {
			out.append(PDSString.remoplaForEquals(pdsInfo)).append("\n");
		}
		
		for (int i = 0; i < pdsClassList.size(); i++) {
			pdsClass = (PDSClass) pdsClassList.get(i);
			out.append(pdsClass.toRemoplaModule());
			out.append("\n");
		}

		// add assert error state
		// TODO maybe only add this if it might be reachable, i.e. if there are 
		// assert statements in the code
		/*added by suncong*/
		out.append(selfLoopStmt(pdsInfo.getIllegalFlowLabel()));
		/******************/		
		out.append(selfLoopStmt(pdsInfo.getLabelAssertError()));
		
		out.append(selfLoopStmt(pdsInfo.getLabelGeneralException()));
		
		if (pdsInfo.checkForHeapOverflow()) {
			out.append(selfLoopStmt(pdsInfo.getLabelHeapOverflow()));
		}
		
		if (pdsInfo.checkForIndexOutOfBounds()) {
			out.append(selfLoopStmt(pdsInfo.getLabelIndexOutOfBounds()));
		}
		
		if (pdsInfo.checkForNullPointerExceptions()) {
			out.append(selfLoopStmt(pdsInfo.getLabelNPE()));
		}
		
		if (pdsInfo.checkForStringBuilderOverflow()) {
			out.append(selfLoopStmt(pdsInfo.getLabelStringBuilderOverflow()));
		}
		
		return out.toString();
	}
/*&*/
	public void addStefansOutput(StringBuffer out) {

		PDSClass tmpClass;
		PDSMethod tmpMethod;
		String initMethodName = null;
		PDSClass pdsClass;
		PDSMethod pdsMethod;
		
		out.append(" ");
		out.append(PDSDefault.JMOPED_INIT);
		for (int i = pdsClassList.size() - 1; i >= 0; i--) {
			pdsClass = (PDSClass) pdsClassList.get(i);
			pdsMethod = pdsClass.getMethodClinit();
			if (pdsMethod != null) {
				if (initMethodName == null) {
					initMethodName = pdsMethod.getFormattedName();
				}
				tmpMethod = null;
				for (int j = i - 1; j >= 0; j--) {
					tmpClass = (PDSClass) pdsClassList.get(j);
					tmpMethod = tmpClass.getMethodClinit();
					if (tmpMethod != null) {
						break;
					}
				}
				
				if (tmpMethod != null) {
					pdsMethod.replaceLastInst("goto " + tmpMethod.getFormattedName(),
									"goto "+pdsInfo.generatePairLabel(tmpMethod.getFormattedName(),""));
				} else {
					tmpMethod = getInternalInitialMethod();
					if (tmpMethod != null) {
						pdsMethod.replaceLastInst("goto " + tmpMethod.getFormattedName(),
									"goto "+pdsInfo.generatePairLabel(tmpMethod.getFormattedName(),""));
					} else {
						logger.error("Main method not found.");
					}
				}
			}
		}
		if (initMethodName == null) {
			pdsMethod = getInternalInitialMethod();
			if (pdsMethod != null) {
				initMethodName = pdsMethod.getFormattedName();
			} else {
				logger.error("Main method not found.");
			}
		}
		out.append(";\n\n");
		out.append(initModule(initMethodName));
	}

	/**
	 * Create a special module, used to initialize heap and its pointer.
	 * The module name is defined in PDSDefault.JMOPED_INIT.
	 * The method is called when Stefan option is on.
	 * After the initialization, the instruction "goto gotoName" is added.
	 * 
	 * @param gotoName
	 * @return
	 */
/* original initModule
	private String initModule(String gotoName) {
		
		StringBuffer out = new StringBuffer();
		
		out.append("module void ");
		out.append(PDSDefault.JMOPED_INIT);
		out.append("()\n");
		out.append("{\n");
		out.append(pdsInfo.getHeapPtrName());
//		out.append("=1;\n");
		out.append("=1, ");
		out.append("A i (0,");
		out.append(String.valueOf(pdsInfo.getHeapSize() - 1));
		out.append(") ");
		out.append(pdsInfo.heapIndex("i"));
		out.append("=0;\n");
		out.append("goto ");
		out.append(gotoName);
		out.append(";\n");
		out.append("}\n\n");
		
		return out.toString();
	}
*/
	/*modified by suncong*/
	private String initModule(String gotoName) {
		
		StringBuffer out = new StringBuffer();
		
		out.append("module void ").append(PDSDefault.JMOPED_INIT).append("() {\n");
		
		out.append(pdsInfo.getHeapPtrName()).append("=1, ");
		out.append(pdsInfo.generateTLabel(pdsInfo.getHeapPtrName())).append("=1, ");
		
		out.append(pdsInfo.heapIndex("0")).append("=1, ");
		out.append("A i (1,").append(String.valueOf(pdsInfo.getHeapSize()-1)).append(") ");
		out.append(pdsInfo.heapIndex("i")).append("=undef, ");
		out.append(pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),"0")).append("=1, ");
		out.append("A i (1,").append(String.valueOf(pdsInfo.getHeapSize()-1)).append(") ");
		out.append(pdsInfo.stringIndex(pdsInfo.generateTLabel(pdsInfo.getHeapName()),"i")).append("=undef, ");
		
		out.append(pdsInfo.getPtridxName()).append("=0, ");
		out.append("A i (0,").append(String.valueOf(pdsInfo.getHeapSize()-1)).append(") ");
		out.append(pdsInfo.stringIndex(pdsInfo.getLowidx1Name(),"i")).append("=undef, ");
		out.append("A i (0,").append(String.valueOf(pdsInfo.getHeapSize()-1)).append(") ");
		out.append(pdsInfo.stringIndex(pdsInfo.getLowidx2Name(),"i")).append("=undef, ");
		/*if the target method is non-static and the target class is low level, we need to
		 *add the index of 'this' to the lowref list here*/
		PDSMethod mtd=getInitialMethod();
		if(mtd.isStatic() || pdsInfo.getSecurityLevelOfTargetClass()!=0){
			out.append(pdsInfo.getPtrrefName()).append("=0, ");
			out.append("A i (0,").append(String.valueOf(pdsInfo.getHeapSize()-1)).append(") ");
			out.append(pdsInfo.stringIndex(pdsInfo.getLowrefName(),"i")).append("=undef;\n");
		}
		else{
			out.append(pdsInfo.stringIndex(pdsInfo.getLowrefName(),"0")).append("=0, ");
			out.append(pdsInfo.getPtrrefName()).append("=1, ");
			out.append("A i (1,").append(String.valueOf(pdsInfo.getHeapSize()-1)).append(") ");
			out.append(pdsInfo.stringIndex(pdsInfo.getLowrefName(),"i")).append("=undef;\n");
		}
		
		out.append("goto ").append(gotoName).append(";\n");
		out.append("}\n\n");
		
		return out.toString();
	}

}
