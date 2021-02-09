
package jmoped;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.gjt.jclasslib.bytecode.AbstractInstruction;
import org.gjt.jclasslib.bytecode.Opcodes;
import org.gjt.jclasslib.io.ByteCodeReader;
import org.gjt.jclasslib.io.ClassFileReader;
import org.gjt.jclasslib.structures.AccessFlags;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.CPInfo;
import org.gjt.jclasslib.structures.constants.ConstantStringInfo;
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;
import org.gjt.jclasslib.structures.constants.ConstantClassInfo;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//added by suncong:
import jmoped.policygen.PolicyInfo;
import jmoped.policygen.TargetStruct;
import jmoped.policygen.FieldStruct;

/**
 * This class manages all the configuration options that are currently used.
 * <p>
 * Its constructors are either given a class file or a file on the file system which is read
 * as a {@link org.gjt.jclasslib.structures.ClassFile}. After PDSInfo's constructor is
 * finished, PDSInfo contains all the {@link #getClassFileList() class files} that are 
 * needed when calling any method from the given class file.
 * <p>
 * Included are files which are either instantiated or otherwise invoked from from the
 * original class file. 
 */
public class PDSInfo {
	
	static Logger logger = Logger.getLogger(PDSInfo.class);
	
	private File rootFile;

	private ArrayList<ClassFile> classFileList = new ArrayList<ClassFile>();
	private HashMap<String, ClassFile> classFileByName = new HashMap<String, ClassFile>();

	/*build by buildFieldTable and buildClassTable*/
	private Hashtable<String, PDSField> fieldTable = new Hashtable<String, PDSField>();
	private Hashtable<String, PDSClass> classTable = new Hashtable<String, PDSClass>();

	/**
	 * Map of instantiated classes.
	 */
	private Map<String, ClassNode> classNodeByName = new HashMap<String, ClassNode>();
	private Map<String, InterfaceNode> interfaceByName = new HashMap<String, InterfaceNode>();
	
	// use classTable instead
	//private Hashtable<String, Integer> classIndexTable 
	//		= new Hashtable<String, Integer>();
	
	private Set<MethodInvocation> virtualInvocations = new HashSet<MethodInvocation>();
	private Set<MethodInvocation> interfaceInvocations = new HashSet<MethodInvocation>();
	
	/* use infor from PDSDefault*/
	private String labelHeapOverflow = PDSDefault.LABEL_HEAP_OVERFLOW;
	private String labelNPE = PDSDefault.LABEL_NULL_POINTER_EXCEPTION;
	private String labelGeneralException = PDSDefault.LABEL_GENERAL_EXCEPTION;
	private String labelIndexOutOfBounds = PDSDefault.LABEL_INDEX_OUT_OF_BOUNDS;
	private String labelStringBuilderOverflow = PDSDefault.LABEL_STRINGBUILDER_OVERFLOW;
	private boolean checkForHeapOverflow = PDSDefault.CHECK_FOR_HEAP_OVERFLOW;
	private boolean checkForIndexOutOfBounds = PDSDefault.CHECK_FOR_INDEX_OUT_OF_BOUNDS;
	private boolean checkForNullPointerExceptions = PDSDefault.CHECK_FOR_NULL_POINTER_EXCEPTIONS;
	private boolean checkForStringBuilderOverflow = PDSDefault.CHECK_FOR_STRINGBUILDER_OVERFLOW;

	/* use infor of jmoped.conf*/
	private String labelAssertError = PDSDefault.LABEL_ASSERT_ERROR;
	private int bits = PDSDefault.BITS;
	private String heapName = PDSDefault.HEAP_NAME;
	private String heapPtrName = PDSDefault.HEAP_PTR_NAME;
	private int heapSize = PDSDefault.HEAP_SIZE;
	private String mopedPath = PDSDefault.MOPED_PATH;
	private String varName = PDSDefault.LOCAL_VAR_NAME;
	private String stackName = PDSDefault.STACK_NAME;

	private Hashtable<String, List<String>> includedTable = new Hashtable<String, List<String>>();
	private Hashtable<String, List<String>> ignoredTable = new Hashtable<String, List<String>>(PDSDefault.IGNORED_TABLE);
	private HashSet<String> ignoredFieldSet = new HashSet<String>();
	private Hashtable<Integer, Integer> lineBitTable = new Hashtable<Integer, Integer>();
	
	/*added by suncong*/
	private String lowidx1Name="lowidx1";
	private String lowidx2Name="lowidx2";
	private String ptridxName="ptridx";
	private String lowrefName="lowref";
	private String ptrrefName="ptrref";
	private String pairTail="_pair";
	private String tTail="t";
	private String illegalFlowLabel="IllegalFlow";
	
	public String getLowidx1Name(){
		return lowidx1Name;
	}
	public String getLowidx2Name(){
		return lowidx2Name;
	}
	public String getPtridxName(){
		return ptridxName;
	}
	public String getLowrefName(){
		return lowrefName;
	}
	public String getPtrrefName(){
		return ptrrefName;
	}
	public String getPairTail(){
		return pairTail;
	}
	public String generatePairLabel(String pre,String post){
		return pre+pairTail+post;
	}
	public String generateTLabel(String pre){
		return pre+tTail;
	}
	public String getTTail(){
		return tTail;
	}
	public String getIllegalFlowLabel(){
		return illegalFlowLabel;
	}

	PolicyInfo policy;
	
	public PolicyInfo getPolicy(){
		return policy;
	}
	
	public void setClassIndexForPolicy(){
		if(policy==null)
			return;
			
		String tarName=policy.getTargetClassName();
		int idx;
		//System.out.println("target class name: "+tarName);/////////////////////
		if(isIgnoredClass(tarName)){
			System.out.println("target class "+tarName+" ignored in setClassIndexForPolicy.");
		}
		else{
			idx=getClassIndex(policy.getPolicyPath()+tarName);
			policy.setClassIndex(tarName,idx);
			//System.out.println("target class index: "+String.valueOf(policy.getClassIndex(tarName)));/////
		}
		ArrayList<TargetStruct> ts_list=policy.getNonTargetClasses();
		for(TargetStruct ts: ts_list){
			tarName=ts.getClassName();
			if(isIgnoredClass(tarName)){
				System.out.println("non-target class "+tarName+" ignored in setClassIndexForPolicy.");
			}
			else{
				idx=getClassIndex(policy.getPolicyPath()+tarName);
				policy.setClassIndex(tarName,idx);
				//System.out.println("non-target class index: "+String.valueOf(policy.getClassIndex(tarName)));/////
			}
		}
	}
	
	public void setRelativeFieldOffsetForPolicy(){
		if(policy==null)
			return;
		TargetStruct tar=policy.getTargetClass();
		PDSClass cls=classTable.get(policy.getPolicyPath()+tar.getClassName());
		if(cls==null){
			System.out.println("not find PDSClass for targetClass");
			return;
		}
		ArrayList<FieldStruct> flds=tar.getFields();
		for(FieldStruct fs: flds){
			//System.out.println("currently processed field: "+ fs.getName());/////////////
			String policyFldName=fs.getName();
			if(isIgnoredField(policyFldName))
				continue;
			for(PDSField f : cls.getNonStaticFieldList()){//until now we do not support static field in policy file
				if(policyFldName.equals(f.getName())){
					//System.out.println("relative offset of field "+fs.getName()+ " is set to "+String.valueOf(f.getIndex()));///
					fs.setRelativeOffset(f.getIndex());
				}
			}
		}
		ArrayList<TargetStruct> nonTargets=policy.getNonTargetClasses();
		for(TargetStruct ts: nonTargets){
			cls=classTable.get(policy.getPolicyPath()+ts.getClassName());
			if(cls==null){
				System.out.println("not find PDSClass for non-target class");
				continue;
			}
			flds=ts.getFields();
			for(FieldStruct fs: flds){
				String policyFldName=fs.getName();
				if(isIgnoredField(policyFldName))
					continue;
				for(PDSField f: cls.getNonStaticFieldList()){
					if(policyFldName.equals(f.getName())){
						//System.out.println("relative offset of field "+fs.getName() + " is set to "+String.valueOf(f.getIndex()));////////
						fs.setRelativeOffset(f.getIndex());
					}
				}
			}
		}
	}
	public void PrintPolicy(){
		policy.Print();
	}
	public ArrayList<FieldStruct> getPolicyFields(int classidx){
		TargetStruct ts=policy.getTargetByClassIndex(classidx);
		if(ts==null)
			return new ArrayList<FieldStruct>();
		return ts.getFields();
	}
	//by suncong
	public static boolean getSimplified(){
		return simplified;
	}
	public int getSecurityLevelOfTargetClass(){
		return policy.getTargetLevel();
	}
	/*************************************/
	
	/**
	 * Two type of heap manipulation strategies:
	 * 
	 * SIMPLE is the same as before.
	 * 
	 * TWODIMS is similar to SIMPLE but represents a heap as a bit string. 
	 * For example if bits=4 and heapSize=15, the resulting Remopla will 
	 * declare: int heap[60](1); However, the heap poiter will have 4 bits 
	 * as usual. The trick is that we always access the heap as multiples 
	 * of 4, i.e. we always multiply heap pointer by 4 before accessing 
	 * the heap. This way we don't have to declare pointer with lots of 
	 * bits and are able to be flexible with elements in the heap, e.g.
	 * we can declare a field of an object having different bit size
	 * or declare an array of 1 bits. Nevertheless, we waste some space 
	 * in the heap anyway, since we always have to start with the offset
	 * of 4. 
	 */
	public static enum HeapOption { SIMPLE, TWODIMS }
	private HeapOption heapOption = HeapOption.SIMPLE;
	
	static boolean simplified = false;
	static boolean Stefan = true;

	private ClassFile classFile;
	
	private MethodInfo initialMethod;
	
	private String[] searchPaths = new String[0];
	
	private PDSString pdsString = new PDSString();
	
	/**
	 * Whether or not to include multiarray initializer method. It's final value
	 * is set when the included methods are determined.
	 */
	private boolean includeMultiNewArrayInitializerMethod = false;
	
	private boolean includeStringEqualsMethod = false;
	
	public PDSInfo(ClassFile classFile, String confName, String methodName,
			String methodDescriptor, String[] searchPaths) throws Exception
	{
		if (new File(confName).exists()) {
			parseConfig(confName);
		}
		this.classFile = classFile;
		this.searchPaths = searchPaths;
		initialize(methodName, methodDescriptor);
		pdsString.buildCharTable(classFileList);
	}
	
	/**
	 * Creates a pds info object for the class file at <code>rootFile</code>.
	 * @param rootFile
	 * @throws Exception
	 */
	public PDSInfo(File rootFile) throws Exception {
		this(rootFile, PDSDefault.CONFIG_NAME);
	}
	
	public PDSInfo(File rootFile, String confName, String methodName, 
			String methodDescriptor, String policyFileName) throws Exception
	{
            //System.out.println("^^^^^^"+confName);///////////
		this.rootFile = rootFile;
		this.classFile = ClassFileReader.readFromFile(rootFile);
		//System.out.println("super class:"+this.classFile.getSuperClassName());
		if (new File(confName).exists()) {
			parseConfig(confName);
		}
		initialize(methodName, methodDescriptor);
		pdsString.buildCharTable(classFileList);
		policy=new PolicyInfo();
		policy.setPDSInfo(this);//must be set to point back
		policy.Read(policyFileName);
	}
	
	public PDSInfo(File rootFile, String confName) throws Exception
	{
		this(rootFile, confName, null, null);
	}
	
	public PDSInfo(File rootFile, String methodName, String methodDescriptor, String policyFileName)
		throws Exception
	{
		this(rootFile, PDSDefault.CONFIG_NAME, methodName, methodDescriptor, policyFileName);
	}
	
	public PDSInfo(ClassFile classFile) throws Exception {
		this(classFile, new String[0]);
	}
	
	public PDSInfo(ClassFile classFile, String[] searchPaths) throws Exception {
		this(classFile, PDSDefault.CONFIG_NAME, null, null, searchPaths);
	}
	
	public PDSInfo(ClassFile classFile, String methodName, 
				   String methodDescriptor, String[] searchPaths)
		throws Exception
	{
            this(classFile, PDSDefault.CONFIG_NAME, methodName, methodDescriptor, searchPaths);
            System.out.println("Default Configuration File Name"+ PDSDefault.CONFIG_NAME);
	}
	
	public PDSInfo(Class clazz) throws Exception {
		this(PDSInfo.findClassFile(clazz.getName()));
	}
	
	public PDSInfo(Class clazz,  String methodName, String methodDescriptor) 
		throws Exception 
	{
		this(PDSInfo.findClassFile(clazz.getName()), methodName, 
			 methodDescriptor, new String[0]);
	}
	
	public PDSInfo(String className, String[] pathEntries) throws Exception {
		this(PDSInfo.findClassFile(className, pathEntries), pathEntries);
	}
	
	public PDSInfo(String className, String[] pathEntries, String methodName, 
			String methodDescriptor) throws Exception 
	{
		this(PDSInfo.findClassFile(className, pathEntries), methodName, 
			 methodDescriptor, pathEntries);
	}

	/**
	 * Computes all methods and class files which should be included on the
	 * static code path from the initial method given to it.
	 *
	 * If <code>methodName</code> is null, the main method is used.
	 */
/*&*/
	private void initialize(String methodName, String methodDescriptor) 
		throws IOException, InvalidByteCodeException
	{
            System.out.println("####:"+methodName+","+methodDescriptor);/////////
		if (methodName != null) {
			initialMethod = classFile.getMethod(methodName, methodDescriptor);
			//System.out.println("foo method info:"+initialMethod.getName()+";"+initialMethod.getDescriptor());
			if (initialMethod == null) {
				throw new IllegalArgumentException
					("Method " + methodName + methodDescriptor 
					 + "  not found in input class file");
			}
                        System.out.println("1");
			if ((initialMethod.getAccessFlags() & AccessFlags.ACC_STATIC) == 0) {
				//	System.out.println("aaa");
				// include constructor, preferring <init>()V
				MethodInfo candidate = null;
				for (MethodInfo method : classFile.getMethods()) {
					if (method.getName().equals("<init>")) {
						candidate = method;
						if (method.getDescriptor().equals("()V")) {
							break;
						}
					}
				}
                                System.out.println("2");
				addMethod(candidate);
                                System.out.println("3");
			}
			addMethod(initialMethod);
                        System.out.println("4");
		}
		else {
			initialMethod = classFile.getMethod("main", 	"([Ljava/lang/String;)V");
			if (initialMethod != null) {
				addMethod(initialMethod);
			}
			// remove this branch eventually, still here for backwards
			// compatibility
			else {
				// add all methods of input class
				for (MethodInfo method : classFile.getMethods()) {
					addMethod(method);
				}	
			}
		}
	}
/*&*/
	MethodInfo getInitialMethod()
	{
		return initialMethod;
	}
/*&*/
	public int getBits() {
		
		return bits;
	}
/*&*/
	public void putAllLineBitTable(Hashtable<Integer, Integer> lbTable) {
		
		lineBitTable.putAll(lbTable);
	}
/*&*/
	public void setBitsAtLine(int l, int b) {
		
		lineBitTable.put(l, b);
	}
	
	public int getBitsAtLine(int l) {
		
		logger.debug("getBitsAtLine(" + l + ")");
		Integer b = lineBitTable.get(l);
		return (b != null) ? b.intValue() : bits;
	}
	
	/**
	 * Sets the number of bits used for emulating integers, might also change
	 * the size of the heap.
	 * <p>
	 * The heap size can only be as large as the largest number that can be
	 * represented using <code>numOfBits</code> bits.
	 * @param numOfBits
	 */
	public void setBits(int numOfBits) {
		bits = numOfBits;
		int maximumHeapSize = PDSDefault.getMaximumHeapSizeForBits(bits);
		if (maximumHeapSize < heapSize) {
			heapSize = maximumHeapSize; 
		}
	}
	
	/**
	 * Returns an unmodifiable instance of the list of class files.
	 * @return
	 */
/*&*/
	public List<ClassFile> getClassFileList() {
		return Collections.unmodifiableList(classFileList);
	}
	
	public int getClassIndex(String className) {
		
		PDSClass clazz = classTable.get(className);
		if (clazz != null) {
			return clazz.getIndex();
		} else {
			logger.error("Cannot find className=" + className);
			return -1;
		}
	}
	
	/**
	 * Returns the name of the pointer into the Remopla heap.
	 * <p>
	 * Java's heap is simulated in Remopla using a static array and
	 * a pointer pointing to the last used array element/ next free element.
	 * <p>
	 * Defaults to {@link PDSDefault#HEAP_PTR_NAME}.
	 * @return
	 */
/*&*/
	public int getClassSize(String className) throws InvalidByteCodeException {
		
		PDSClass clazz = classTable.get(className);
		if (clazz != null) {
			return clazz.size();
		} else {
			logger.error("Cannot find className=" + className);
			return -1;
		}
	}
/*&*/
	public HeapOption getHeapOption() {
		
		return heapOption;
	}
/*&*/
	public void setHeapOption(HeapOption heapOption) {
		
		this.heapOption = heapOption;
	}
/*&*/
	public String getHeapPtrName() {
		
		return heapPtrName;
	}
	
	/**
	 * Returns the size of the Remopla heap.
	 * <p>
	 * Defaults to {@link PDSDefault#HEAP_SIZE}.
	 * @return
	 */
/*&*/
	public int getHeapSize() {
		
		if (heapOption == HeapOption.SIMPLE)
			return heapSize;
		else // if (heapOption == HeapOption.TWODIMS)
			return bits * heapSize;
	}
/*&*/
	public int getStoredHeapSize() {
		
		return heapSize;
	}
	
	/**
	 * Sets the size of the Remopla heap and may change the number of bits.
	 * <p>
	 * The number of bits must be large enough to index all elements in the
	 * heap array, so it is adapted if necessary.
	 * 
	 * @throws IllegalArgumentException if <code>size</code> smaller than 1
	 */
	public void setHeapSize(int size) {
		if (size < 1) {
			throw new IllegalArgumentException("heap size must be >= 1");
		}
		heapSize = size;
		bits = Math.max(bits, PDSDefault.getMinimumBitsForHeapSize(heapSize));
	}
	
	/**
	 * Returns the name of the static array used in Remopla to simulate
	 * the heap.
	 * <p>
	 * Defaults to {@link PDSDefault#HEAP_NAME}.
	 * @return
	 */
/*&*/
	public String getHeapName() {
		return heapName;
	}
	
	public int getStringBuilderSize() {
		
		return pdsString.getStringBuilderSize();
	}
	
	public int getCharBits() {
		
		return pdsString.getCharBits();
	}
	
	public int getCharRep(char c) {
		
		return pdsString.getCharRep(c);
	}
/*&*/
	public void setCheckForIndexOutOfBounds(boolean newValue)
	{
		checkForIndexOutOfBounds = newValue;
	}
/*&*/
	public boolean checkForIndexOutOfBounds()
	{
		return checkForIndexOutOfBounds;
	}
/*&*/
	public void setCheckForHeapOverflow(boolean newValue)
	{
		checkForHeapOverflow = newValue;
	}
/*&*/
	public boolean checkForHeapOverflow()
	{
		return checkForHeapOverflow;
	}
/*&*/
	public boolean checkForNullPointerExceptions()
	{
		return checkForNullPointerExceptions;
	}
/*&*/
	public boolean checkForStringBuilderOverflow() {
		
		return checkForStringBuilderOverflow;
	}
/*&*/
	public void setCheckForNullPointerExceptions(boolean newValue)
	{
		checkForNullPointerExceptions = newValue;
	}
	
	/**
	 * Returns the name of the variables used as stack in Remopla.
	 * <p>
	 * Defaults to {@link PDSDefault#STACK_NAME}.
	 * @return
	 */
	public String getStackName() {
		return stackName;
	}
	
	/**
	 * Returns the name of local variables created in jmoped's output.
	 * <p>
	 * Defaults to {@link PDSDefault#LOCAL_VAR_NAME}.
	 * @return
	 */
	public String getLocalVarName() {
		return varName;
	}
	
	/**
	 * Returns the methods of class <code>className</code> which
	 * are actually called on a path from the inital method, so they
	 * have to be translated to Remopla.
	 * @param className
	 * @return
	 */
	public List<String> getIncludedMethods(String className) {
		return Collections.unmodifiableList(includedTable.get(className));
	}
	
	public Collection<String> getImplementor(String interfaceName) 
	{
		List<String> directImplementers = getDirectImplementers(interfaceName);
		HashSet<String> implementers = new HashSet<String>();
		
		for (String className : directImplementers) {
			ClassNode classNode = getClassNode(className);
			Enumeration enumeration = classNode.breadthFirstEnumeration();
			while (enumeration.hasMoreElements()) {
				ClassNode node = (ClassNode) enumeration.nextElement();
				implementers.add(node.getClassName());
			}
		}
		return implementers;
	}
/*&*/
	public boolean includeMultiArrayInitializerMethod()
	{
		return includeMultiNewArrayInitializerMethod;
	}
/*&*/
	public boolean isIncludeStringEqualsMethod() {
		
		return includeStringEqualsMethod;
	}
/*&*/
	public void setIncludeStringEqualsMethod() {
		
		includeStringEqualsMethod = true;
	}
/*&*/
	public String getLabelAssertError() {
		
		return labelAssertError;
	}
/*&*/
	public String getLabelHeapOverflow() {
		
		return labelHeapOverflow;
	}
/*&*/
	public String getLabelNPE() {
		
		return labelNPE;
	}
/*&*/
	public String getLabelIndexOutOfBounds() {
		
		return labelIndexOutOfBounds;
	}
/*&*/
	public String getLabelGeneralException() {
		
		return labelGeneralException;
	}
/*&*/
	public String getLabelStringBuilderOverflow() {
		
		return labelStringBuilderOverflow;
	}

	/**
	 * Returns the name of the moped executable.
	 * <p>
	 * May be its absolute path name or just the filename if the executable
	 * is in PATH.
	 * @return
	 */	
/*&*/
	public String getMopedPath() {
		
		return mopedPath;
	}
	
	/**
	 * Returns the corresponding jclasslib's ClassFile instance 
	 * for a given className.
	 * 
	 * @param className
	 * @return
	 * @throws InvalidByteCodeException
	 */
/*&*/
	public ClassFile getStoredClassFile(String className) 
			throws InvalidByteCodeException {
		
		return getClassFile(className);
	}
	
	/**
	 * Returns the value of {@link #getHeapName()} subscripted
	 * by the given expression.
	 * @param expression
	 * @return
	 */
	public String heapIndex(String expression) {
		
		return stringIndex(heapName, expression);
	}
	/**
	 * Returns a subscript string of the form
	 * <code>array[expression]</code>.
	 * @param array
	 * @param expression
	 * @return
	 */
	public static String stringIndex(String array, String expression) {
		
		StringBuffer out = new StringBuffer();
		
		out.append(array);
		out.append("[");
		out.append(expression);
		out.append("]");
		
		return out.toString();
	}
	
	/**
	 * Returns the offset at which the given field is stored within its class.
	 * @param formattedFieldName
	 * @return -1 if field was not found
	 * TODO throw illegal argument exception?
	 */
	public int indexOfField(String formattedFieldName) {
		
		PDSField pdsField = fieldTable.get(formattedFieldName);
		if (pdsField != null) {
			return pdsField.getIndex();
		} else {
			logger.error("Cannot find formattedFieldName=" + formattedFieldName);
			logger.debug(fieldIndexTableInfo());
			return -1;
		}
	}
	
	public int getFieldBits(String formattedFieldName) 
			throws InvalidByteCodeException {
		
		PDSField pdsField = fieldTable.get(formattedFieldName);
		if (pdsField != null) {
			return pdsField.getBits();
		} else {
			logger.error("Cannot find formattedFieldName=" + formattedFieldName);
			logger.debug(fieldIndexTableInfo());
			return -1;
		}
	}

	
/*&*/	
	private boolean isIncluded(MethodInfo method)
		throws InvalidByteCodeException
	{
		return isIncludedMethod(method.getClassFile().getThisClassName(),
				method.getName(), method.getDescriptor());
	}
/*&*/	
	private boolean isIgnored(MethodInfo method) throws InvalidByteCodeException
	{
		return isIgnoredMethod(method.getClassFile().getThisClassName(),
				method.getName() + method.getDescriptor());
	}

	
	/**
	 * Returns true if the field should not be included in jmoped's output.
	 * @param fieldName
	 * @return
	 */
	public boolean isIgnoredField(String fieldName) {
		
		for (int i = 0; i < PDSDefault.IGNORED_FIELD.length; i++) {
			
			if (fieldName.startsWith(PDSDefault.IGNORED_FIELD[i])) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Takes an array of class name and field name and checks if either
	 * the class or the field in the class should be ignored.
	 * @param fieldName
	 * @return
	 */
	public boolean isIgnoredField(String[] fieldName) {
		
		return isIgnoredClass(fieldName[0]) || isIgnoredField(fieldName[1]) 
						|| isIgnoredFormattedField(fieldName);
	}
	
	/**
	 * Formats the array consisting of a class name and a field to a fully
	 * qualified field name and checks if it should be ignored.
	 * @param fieldName
	 * @return
	 */
	private boolean isIgnoredFormattedField(String[] fieldName) {
		
		String name = PDSField.formatFieldName(fieldName);
		
		if (ignoredFieldSet.contains(name)) {
			return true;
		}
		return isIgnoredField(name);
	}

	/**
	 * Returns true if the class is already due for inclusion.
	 * @param className
	 * @return
	 */
/*&*/
	public boolean isIncludedClass(String className) {
		
		return includedTable.get(className) != null;
	}

	/**
	 * Returns true if the class should be ignored while generating
	 * Remopla output.
	 * @param className
	 * @return
	 */
/*&*/
	public boolean isIgnoredClass(String className) {
		

		logger.debug("Entering isIgnoredClass(" + className + ")");
		List<String> list = ignoredTable.get(className);
		if (list == null) {
			return false;
		}
		return list.isEmpty();
	}
	
	
	/**
	 * Takes an array consisting of a class name, a method name and its 
	 * signature and checks if the method should be ignored in jmoped's
	 * output.
	 * @param names
	 * @return
	 */
/*&*/
	public boolean isIgnoredMethod(String[] names) {
		
		return isIgnoredMethod(names[0], names[1] + names[2]);
	}
/*&*/	
	public boolean isIgnoredMethod(String className, String nameAndType) {
		
		List<String> methodList = ignoredTable.get(className);
		if (methodList == null) {
			return false;
		}
		return methodList.isEmpty() || methodList.contains(nameAndType);
	}
	
	/**
	 * Takes an array of class name, method name and signature
	 * and returns true if the method is already due to be included.
	 * @param names
	 * @return
	 */
/*&*/
	public boolean isIncludedMethod(String[] names) {
		
		List<String> methodList = includedTable.get(names[0]);
		if (methodList == null) {
			return false;
		}
		return methodList.contains(names[1] + names[2]);
	}
/*&*/
	public boolean isIncludedMethod(String className, String methodName,
			String methodDescriptor)
	{
		return isIncludedMethod(new String[] { className, methodName, 
				methodDescriptor });
	}

	/**
	 * Parses the xml configuration file.
	 * @param confName
	 * @throws Exception
	 */
/*&*/
	private void parseConfig(String confName) throws Exception {/*&&*/
		parseConfig(new BufferedInputStream(new FileInputStream(confName)));
	}
	
/*&*/
	private void parseConfig(InputStream in) throws Exception {/*&&*/
		
		DocumentBuilderFactory factory;
		DocumentBuilder builder;
		
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		
		Document doc = builder.parse(in);
		
		NodeList nl1;
		Node n1, n2;
		NamedNodeMap nnm;
		String nodeName;
		
		nl1 = doc.getFirstChild().getChildNodes();
		for (int i = 0; i < nl1.getLength(); i++) {
			
			n1 = nl1.item(i);
			nodeName = n1.getNodeName();
			if (nodeName.equals("bits")) {//
				
				nnm = n1.getAttributes();
				if (nnm == null || nnm.getLength() == 0) {
					bits = Integer.parseInt(n1.getFirstChild().getNodeValue());
					logger.debug("From jmoped.conf bits=" + bits);
				} else {
					n2 = nnm.item(0);
					if (n2.getNodeName().equals("line")) {
						int l = Integer.parseInt(n2.getNodeValue());
						int b = Integer.parseInt(n1.getFirstChild().getNodeValue());
						lineBitTable.put(l, b);
						logger.debug("From jmoped.conf bits=" + b + " at line=" +l);
					}
				}
			} else if (nodeName.equals("error")) {//
				
				n2 = n1.getAttributes().item(0);
				if (n2.getNodeName().equals("name")) {
					labelAssertError = n2.getNodeValue();
				}
			} else if (nodeName.equals("heap")) {//
				
				nnm = n1.getAttributes();
				for (int j = 0; j < nnm.getLength(); j++) {
					n2 = nnm.item(j);
					if (n2.getNodeName().equals("name")) {
						heapName = n2.getNodeValue();
					} else if (n2.getNodeName().equals("size")) {
						heapSize = Integer.parseInt(n2.getNodeValue());
					} else if (n2.getNodeName().equals("pointer")) {
						heapPtrName = n2.getNodeValue();
					}
				}
			} else if (nodeName.equals("moped")) {//
				
				n2 = n1.getAttributes().item(0);
				if (n2.getNodeName().equals("path")) {
					mopedPath = n2.getNodeValue();
				}
			} else if (nodeName.equals("stack")) {//
				
				n2 = n1.getAttributes().item(0);
				if (n2.getNodeName().equals("name")) {
					stackName = n2.getNodeValue();
				}
			} else if (nodeName.equals("stringbuilder")) {//
				
				n2 = n1.getAttributes().item(0);
				if (n2.getNodeName().equals("size")) {
					int size = Integer.parseInt(n2.getNodeValue());
					pdsString.setStringBuilderSize(size);
					logger.debug("From jmoped.conf stringbuilder size=" + size);
				}
			} else if (nodeName.equals("var")) {//
				
				n2 = n1.getAttributes().item(0);
				if (n2.getNodeName().equals("name")) {
					varName = n2.getNodeValue();
				}
			}
		}
		
		buildIgnoredTable(doc);
	}

	/**
	 * Fills ignoredTable in such a way that its keys are class names,
	 * which map lists of method names to be ignored. A list of 
	 * size zero implies ignoring every method.
	 */
/*&*/
	public void buildIgnoredTable(Document confDoc) {/*&&*/
		System.out.println("***in buildIgnoredTable***");///////////
                
		NodeList nl1, nl2, nl3;
		Node n1, n2, n3, n4, n5, n6;
		List<String> methodList;

		ignoredTable.clear();
		ignoredFieldSet.clear();

		nl1 = confDoc.getFirstChild().getChildNodes();
		for (int i = 0; i < nl1.getLength(); i++) {
			n1 = nl1.item(i);
			if (n1.getNodeName().equals("ignore")) {	// If n1 is "ignore" node
				nl2 = n1.getChildNodes();
				
				/* For each child node of "ignore" node */
				for (int j = 0; j < nl2.getLength(); j++) {
					n2 = nl2.item(j);
					if (n2.getNodeName().equals("class")) {// If n2 is "class" node
						
						n4 = n2.getAttributes().item(0);	// n4 is "class name" node
						if (!n4.getNodeName().equals("name")) {
							logger.error("Invalid class attribute.");
						}
						System.out.println("Ignored class:"+n4.getNodeValue());
						//logger.info("Ignored class: " + n4.getNodeValue());
						
						methodList = new ArrayList<String>();
						nl3 = n2.getChildNodes();
						
						/* For each child node of "class" node */
						for (int k = 0; k < nl3.getLength(); k++) {
							n3 = nl3.item(k);
							if (n3.getNodeName().equals("method")) {// n3 is "method"
								
								// n5 is "method descriptor"
								n5 = n3.getAttributes().item(0);
								if (!n5.getNodeName().equals("descriptor")) {
									logger.error("Invalid method attribute.");
								}
								
								// n6 is "method name"
								n6 = n3.getAttributes().item(1); 
								if (!n6.getNodeName().equals("name")) {
									logger.error("Invalid method attribute.");
								}
								methodList.add(n6.getNodeValue() + n5.getNodeValue());
								logger.info("\tIgnored method: " + n6.getNodeValue());
							}
						}
						
						ignoredTable.put(n4.getNodeValue(), methodList);
					} else if (n2.getNodeName().equals("field")) {
					
						n3 = n2.getAttributes().item(0);
						if (!n3.getNodeName().equals("class")) {
							logger.error("Invalid field attribute.");
						}
						
						n4 = n2.getAttributes().item(1);
						if (!n4.getNodeName().equals("name")) {
							logger.error("Invalid field attribute.");
						}
						
						ignoredFieldSet.add(PDSField.formatFieldName(
								n3.getNodeValue(), n4.getNodeValue()));
						logger.info("\tIgnored field: " 
								+ n3.getNodeValue() + "." + n4.getNodeValue());
					}
				}
				break;
			}
		}
/*                System.out.println("=====Ignored Table=========");
                for(Enumeration e=ignoredTable.elements();e.hasMoreElements();){
                    System.out.println(e.nextElement().toString());
                }
                System.out.println("============================");*/
	}
	

	
	/**
	 * Returns the value of {@link #getStackName()} with the index
	 * appended to it. 
	 * @param expression
	 * @return
	 */
	public String stackIndex(int index) {
		
		return stackName + String.valueOf(index);
	}
	
	/**
	 * Returns the value of {@link #getLocalVarName()()} with the index
	 * appended to it. 
	 * @param expression
	 * @return
	 */
	public String varIndex(int index) {
		
		return varName + String.valueOf(index);
	}
	
	/*******************************************************************/
	/**                          INFO METHODS                         **/
	/*******************************************************************/
	
	/**
	 * Returns the string of all methods that have been included.
	 */
	public String includedTableInfo() throws InvalidByteCodeException {
		
		ClassFile classFile;
		List<String> methodList;
		String className = null;
		StringBuffer out = new StringBuffer();
		
		for (int i = 0; i < classFileList.size(); i++) {
			
			classFile = classFileList.get(i);
			className = classFile.getThisClassName();
			methodList = includedTable.get(className);
			for (int j = 0; j < methodList.size(); j++) {
				out.append(className);
				out.append(".");
				out.append(methodList.get(j));
				out.append("\n");
			}
		}
		
		return out.toString();
	}
	
	public String fieldIndexTableInfo() {
		
		return fieldTable.toString();
	}
	
	/*******************************************************************/
	/**               PUBLIC TABLES' BUILDING METHODS                 **/
	/*******************************************************************/
	
/***********************buildFieldTable and buildClassTable******************/
	/**
	 * @param pdsClassList
	 */
/*&*/
	public void buildFieldTable(List<PDSClass> pdsClassList)
			throws InvalidByteCodeException {
		
		for (PDSClass pdsClass : pdsClassList) {
			
			buildFieldTable(pdsClass);
		}
		
		logger.info("buildFieldIndexTable completed");
	}
	
	/**
	 * Registers the class fields' indices by their formatted names in
	 * the field index table.
	 * @param pdsClass
	 */
/*&*/
	public void buildFieldTable(PDSClass pdsClass) 
			throws InvalidByteCodeException {
		
		//Integer[] fieldElems;
		int fieldIndex = 0;
		
		for (PDSField pdsField : pdsClass.getNonStaticFieldList()) {
			
			pdsField.setIndex(fieldIndex);
			fieldTable.put(pdsField.getFormattedName(), pdsField);
			
			switch (heapOption) {
				case SIMPLE:
					fieldIndex++;
					break;
				case TWODIMS:
					fieldIndex += pdsField.getBits();
			}
		}
		
		/*for (PDSField pdsField : pdsClass.getStaticFieldList()) {
			
			fieldElems = new Integer[2];
			fieldElems[0] = -1;
			fieldElems[1] = pdsField.getBits();
		}*/
	}

	/**
	 * @param pdsClassList
	 */
/*&*/
	public void buildClassTable(List<PDSClass> pdsClassList) {
		
		// start index at one, to avoid spurious excecutions for null pointer checks 
		int i = 1;
		for (PDSClass pdsClass : pdsClassList) {
			
			pdsClass.setIndex(i++);
			// TODO if i > 2^bits, program execution won't be guaranteed to be correct
			// for virtual calls and instanceof, so throw exception or produce warning
			if (i > Math.pow(2, bits)) {
				// produce a warning of some kind, since bits cannot be set in the constructor
		//		throw new InvalidByteCodeException("The number of included classes ("
		//				+ (i - 1) 
		//				+ ") cannot be handled with the current number of bits, please consider increasing the number of bits.");
			}
			classTable.put(pdsClass.getName(), pdsClass);
		}
		
		logger.info("buildClassSizeTable completed");
	}
	
	/**
	 * Registers the class' size by its name in the class size table.
	 * @param pdsClass
	 */
	/*public void buildClassSizeTable(PDSClass pdsClass) {
		classSizeTable.put(pdsClass.getName(), new Integer(pdsClass.size()));
	}*/

/**************************************************************/

/*&*/
	private void addMethod(MethodInfo method)
		throws InvalidByteCodeException, IOException
	{
		if (isIncluded(method) || isIgnored(method)) {
			return;
		}
		addClass(method.getClassFile());
		include(method);
		
		CodeAttribute code =
			(CodeAttribute)method.findAttribute(CodeAttribute.class);
		if (code != null) {
			List<AbstractInstruction> instructions = 
				ByteCodeReader.readByteCode(code.getCode());
			//System.out.println("===================================");
			//System.out.println("parseInstructions for method:"+method.getName());
			parseInstructions(method, instructions);
			//System.out.println("===========================");
		}
	}
	
/************************ insertIntoClassHierarchy********************/
/*&*/
	private ClassNode insertIntoClassHierarchy(ClassFile classFile)
		throws InvalidByteCodeException, IOException
	{
		String className = classFile.getThisClassName();
		ClassNode node = getClassNode(className);
		
		if (node != null) {
			return node;
		}
		
		node = new ClassNode(className,insertIntoInterfaceHierarchy(classFile));
		classNodeByName.put(className, node);

		// add to parent
		String superClassName = classFile.getSuperClassName();
		if (superClassName == null) {
			return node;
		}
		
		ClassFile superClassFile = addClass(superClassName);
		
		if (superClassFile != null) {
			ClassNode parent = insertIntoClassHierarchy(superClassFile);
			parent.add(node);
		}
		
		return node;
	}
/*&*/
	private List<InterfaceNode> insertIntoInterfaceHierarchy
		(ClassFile classFile)
		throws InvalidByteCodeException, IOException
	{
		int[] interfaces = classFile.getInterfaces();
		String className = classFile.getThisClassName();
		List<InterfaceNode> list = new ArrayList<InterfaceNode>(interfaces.length);
		for (int i : interfaces) {
			InterfaceNode interfaceNode = 
				addInterface(getInterfaceName(classFile, i));
			interfaceNode.addImplementer(className);
			list.add(interfaceNode);
		}
		return list;
	}
/*&*/
	private static String getInterfaceName(ClassFile classFile, int interfaceIndex)
		throws InvalidByteCodeException
	{
		ConstantClassInfo info =
			(ConstantClassInfo)classFile.getConstantPoolEntry(interfaceIndex, ConstantClassInfo.class);
		return info.getName();
	}
/*&*/
	private InterfaceNode addInterface(String interfaceName) 
		throws InvalidByteCodeException, IOException
	{
		InterfaceNode node = interfaceByName.get(interfaceName);
		if (node != null) {
			return node;
		}
		
		node = new InterfaceNode(interfaceName);
		interfaceByName.put(interfaceName, node);
		
		ClassFile interfaceClassFile = newClassFile(interfaceName);
		int[] interfaceIndices = interfaceClassFile.getInterfaces();
		if (interfaceIndices.length > 0) {
			String superInterfaceName = getInterfaceName(interfaceClassFile, interfaceIndices[0]);
			InterfaceNode parent = addInterface(superInterfaceName);
			parent.add(node);
		}
		return node;
	}
/*****************************************************************/

/**************************addVirtualMethodsForClass***********************/
/*&*/
	private void addVirtualMethodsForClass(String className)
		throws InvalidByteCodeException, IOException 
	{
		ClassNode node = getClassNode(className);
		
		// copy existing invocations, because new ones could be added in the
		// process which will handle themselves
		for (MethodInvocation invocation:
			virtualInvocations.toArray(new MethodInvocation[0])) {
			ClassNode candidate = getClassNode(invocation.className);
			if (node.isNodeAncestor(candidate)) {
				addVirtualMethodForClass(className, invocation);
			}
		}
	}

	/**
	 * Search through the list of class hierarchy, and return the tree node
	 * of className inside the list.
	 * 
	 * @param className the name of class in searchrn the tree node
	 * of className inside the list.
	 * 
	 * @param className the name of class in search
	 * @return tree node of className in class in the list, null if not found
	 */
/*&*/
	public ClassNode getClassNode(String className) {
		
		logger.debug("Entering getClassNode(" + className + ")");
		return  classNodeByName.get(className);		
	}

	/**
	 * Adds the method denoted by {@link MethodInvocation} if the class
	 * <code>className</code> has been instantiated somewhere on the path
	 * from the initial method.
	 * @param className
	 * @param invocation
	 * @throws InvalidByteCodeException
	 * @throws IOException
	 */
/*&*/
	private void addVirtualMethodForClass(String className, 
			MethodInvocation invocation) throws InvalidByteCodeException, 
			IOException
	{
		if (isInstantiatedClass(className)) {
			ClassFile classFile = getClassFile(className);
			MethodInfo method = classFile.getMethod(invocation.methodName,
					invocation.methodDescriptor);
			if (method != null
				&& (method.getAccessFlags() & AccessFlags.ACC_PRIVATE) == 0
				&& (method.getAccessFlags() & AccessFlags.ACC_ABSTRACT) == 0) { 
				addMethod(method);
			}
		}
	}

	/**
	 * Returns true if class <code>className</code> is possibly instantiated 
	 * somewhere on the static code path.
	 */
/*&*/
	private boolean isInstantiatedClass(String className)
	{
		return isIncludedMethodName(className, "<init>");
	}

	/**
	 * Returns true if a method with name <code>methodName</code> and any
	 * descriptor has been included for class <code>className</code>.
	 */
/*&*/
	private boolean isIncludedMethodName(String className, String methodName)
	{
		List<String> methodList = includedTable.get(className);
		if (methodList == null) {
			return false;
		}
		for (String method : methodList) {
			if (method.startsWith(methodName)) {
				return true;
			}
		}
		return false;
	}
/*****************************************************************/
/**********************addInterfaceMethodsForClass********************/
/*&*/
	private void addInterfaceMethodsForClass(String className)
		throws InvalidByteCodeException, IOException
	{
		Set<String> interfaces = getImplementedInterfaces(className);
		// copy existing invocations, because new ones could be added in the
		// process which will handle themselves
		for (MethodInvocation invocation:
			interfaceInvocations.toArray(new MethodInvocation[0])) {
			if (interfaces.contains(invocation.className)) {
				addVirtualMethodForClass(className, invocation);
			}
		}
	}
/*&*/
	Set<String> getImplementedInterfaces(String className)
	{
		ClassNode node = getClassNode(className);
		if (node == null) {
			return Collections.emptySet();
		}
		HashSet<String> interfaces = new HashSet<String>();
		
		while (node != null) {
			for (InterfaceNode interfaceNode : node.getInterfaces()) {
				while (interfaceNode != null) {
					interfaces.add(interfaceNode.getInterfaceName());
					interfaceNode = (InterfaceNode) interfaceNode.getParent();
				}
			}
			node = (ClassNode) node.getParent();
		}
		return interfaces;
	}

/********************************************************/
	
	// Move to buildClassTable
	/*public void buildClassIndexTable(List<PDSClass> pdsClassList) 
		throws InvalidByteCodeException
	{
		
		// start index at one, to avoid spurious excecutions for null pointer checks 
		int i = 1;
		
		for (PDSClass pdsClass : pdsClassList) {
			
			classIndexTable.put(pdsClass.getName(), new Integer(i++));
		}
		// TODO if i > 2^bits, program execution won't be guaranteed to be correct
		// for virtual calls and instanceof, so throw exception or produce warning
		if (i > Math.pow(2, bits)) {
			// produce a warning of some kind, since bits cannot be set in the constructor
	//		throw new InvalidByteCodeException("The number of included classes ("
	//				+ (i - 1) 
	//				+ ") cannot be handled with the current number of bits, please consider increasing the number of bits.");
		}
	}*/

/**************************parseInstructions***************************/
/*&*/
	private void parseInstructions(MethodInfo method,
								   List<AbstractInstruction> instructions) 
		throws InvalidByteCodeException, IOException
	{
		for (AbstractInstruction instruction : instructions) {
			switch (instruction.getOpcode()) {
			case Opcodes.OPCODE_INVOKESTATIC:
			case Opcodes.OPCODE_INVOKESPECIAL:
				handleInvokeStaticInstruction(method, instruction);
				break;
			case Opcodes.OPCODE_PUTSTATIC:
			case Opcodes.OPCODE_GETSTATIC:
				handleStaticFieldInstruction(method, instruction);
				break;
			case Opcodes.OPCODE_INVOKEVIRTUAL:
				handleInvokeVirtualInstruction(method, instruction);
				break;
			case Opcodes.OPCODE_INVOKEINTERFACE:
				handleInvokeInterfaceInstruction(method, instruction);
				break;
			case Opcodes.OPCODE_MULTIANEWARRAY:
				includeMultiNewArrayInitializerMethod = true;
				break;
			}
		}
	}
/*&*/
	private void handleInvokeInterfaceInstruction
		(MethodInfo method, AbstractInstruction instruction)
		throws InvalidByteCodeException, IOException
	{
		//String[] names = PDSInstOld.getReferencedName(method.getClassFile(), instruction); to discard PDSInstOld
		String[] names = PDSInst.getReferencedName(method.getClassFile(), instruction);
		if (!isIgnoredClass(names[0])) {
			addInterfaceMethod(new MethodInvocation(names[0], names[1], names[2]));
		}
	}
/*&*/
	private void handleInvokeVirtualInstruction(MethodInfo method,
												AbstractInstruction instruction)
		throws InvalidByteCodeException, IOException 
	{
		//String[] names = PDSInstOld.getReferencedName(method.getClassFile(), instruction); discard PDSInstOld
		String[] names = PDSInst.getReferencedName(method.getClassFile(), instruction);
		//System.out.println("handle InvokeVirtual:"+names[0]+"#"+names[1]+"#"+names[2]);
		if (!isIgnoredClass(names[0])) {
			//System.out.println("class "+names[0]+" not ignored, addVirtualMethod "+names[0]+"#"+names[1]+"#"+names[2]);
			addVirtualMethod(new MethodInvocation(names[0], names[1], names[2]));
		}
	}
/*&*/
	private void handleStaticFieldInstruction(MethodInfo method, AbstractInstruction instruction)
		throws InvalidByteCodeException, IOException
	{
		//String[] names = PDSInstOld.getReferencedName(method.getClassFile(), instruction); discard PDSInstOld
		String[] names = PDSInst.getReferencedName(method.getClassFile(), instruction);
		addClass(names[0]);
	}
/*&*/
	private void handleInvokeStaticInstruction(MethodInfo method,
											   AbstractInstruction instruction)
		throws InvalidByteCodeException, IOException 
	{
		//String[] names = PDSInstOld.getReferencedName(method.getClassFile(),instruction);discard PDSInstOld
		String[] names = PDSInst.getReferencedName(method.getClassFile(),instruction);
		//System.out.println("********************************");
		//System.out.println("handle InvokeStatic and InvokeSpecial:"+names[0]+"#"+names[1]+"#"+names[2]);
		if (isIgnoredClass(names[0])) {
			//System.out.println("class "+names[0]+" is ignored");
			return;
		}
		//System.out.println("class "+names[0]+" not ignored");
		ClassFile classFile = getClassFile(names[0]);
		if (classFile == null) {
			//System.out.println("class "+names[0]+" not found");
			classFile = newClassFile(names[0]);
		}
		
		MethodInfo calledMethod = classFile.getMethod(names[1], names[2]);
		if (calledMethod != null) {
			//System.out.println("addMethod "+names[1]+"("+names[2]+")");
			addMethod(calledMethod);
		}
		//System.out.println("***************************");
	}
/*&*/
	private void addInterfaceMethod(MethodInvocation invocation)
		throws InvalidByteCodeException, IOException
	{
		if (!interfaceInvocations.add(invocation)) {
			return;
		}
		
		List<String> implementers = getDirectImplementers(invocation.className);

		for (String className : implementers) {
			ClassNode classNode = getClassNode(className);
			Enumeration offspring = classNode.breadthFirstEnumeration();
			while (offspring.hasMoreElements()) {
				ClassNode next = (ClassNode) offspring.nextElement();
				addVirtualMethodForClass(next.getClassName(), invocation);
			}
		}
	}

	/**
	 * Looks at the hierarchy of the classes that implement the method 
	 * @param invocation
	 * @throws InvalidByteCodeException
	 * @throws IOException
	 */
/*&*/
	private void addVirtualMethod(MethodInvocation invocation)
		throws InvalidByteCodeException, IOException
	{
		if (!virtualInvocations.add(invocation)) {
			return;
		}
		
		ClassNode classNode = getClassNode(invocation.className);
		// class node can be null, if virtual method is called on a null pointer
		if (classNode == null) {
			//System.out.println("The class of method invocation ["+invocation.className+","+invocation.methodName+","+invocation.methodDescriptor+"] is not in current classNodeByName table.");
			return;
		}
		
		// include parent methods
		ClassNode node = (ClassNode) classNode.getParent();
		while (node != null) {
			addVirtualMethodForClass(node.getClassName(), invocation);
			node = (ClassNode)node.getParent();
		}

		// include offspring methods, includes node itself as well
		Enumeration offspring = classNode.breadthFirstEnumeration();
		while (offspring.hasMoreElements()) {
			ClassNode next = (ClassNode) offspring.nextElement();
			addVirtualMethodForClass(next.getClassName(), invocation);
		}
	}



	/**
	 * Returns a list of class names that are direct implementers of 
	 * <code>interfaceName</code> or one of its sub interfaces.
	 * @param interfaceName
	 * @return
	 */
/*&*/
	private List<String> getDirectImplementers(String interfaceName)
	{
		InterfaceNode node = interfaceByName.get(interfaceName);
		if (node == null) {
			return Collections.emptyList();
		}
		Enumeration enumeration = node.breadthFirstEnumeration();
		List<String> implementers = new ArrayList<String>();
		while (enumeration.hasMoreElements()) {
			node = (InterfaceNode)enumeration.nextElement();
			implementers.addAll(node.getImplementers());
		}
		return implementers;
	}
	

/****************************************************************/
/*&*/	
	public ClassFile getClassFile(int index) {
		return classFileList.get(index);
	}
/*&*/
	public ClassFile getClassFile(String className)
	{
		return classFileByName.get(className);
	}
/*&*/
	private ClassFile addClass(String className) 
		throws InvalidByteCodeException, IOException
	{
		if (isIgnoredClass(className)) {
			return null;
		}
		if (isIncludedClass(className)) {
			return getClassFile(className);
		}
		ClassFile classFile = newClassFile(className);
		addClass(classFile);
		return classFile;
	}
/*&*/
	private void addClass(ClassFile classFile) throws InvalidByteCodeException, 
													  IOException
	{
		String className = classFile.getThisClassName(); 
		if (isIncludedClass(className) || isIgnoredClass(className)) {
			return;
		}
		
		include(classFile);
		
		MethodInfo method = classFile.getMethod("<clinit>", "()V");
		if (method != null) {
			addMethod(method);
		}
	}
	
	/**
	 * Adds method to inclusion table, if method is a constructor call, it's
	 * class is inserted into the hierarchy of instantiated classes.
	 * @param method
	 * @throws InvalidByteCodeException
	 */
/*&*/
	private void include(MethodInfo method) throws InvalidByteCodeException,
												   IOException
	{
		String className = method.getClassFile().getThisClassName(); 
		includedTable.get(className).add(method.getName() 
										 + method.getDescriptor());
	
	//comment by suncong:	
		if (method.getName().equals("<init>")) {
			// class is instantiated, insert it into class hierarchy
			insertIntoClassHierarchy(method.getClassFile());
			addVirtualMethodsForClass(className);
			addInterfaceMethodsForClass(className);
		}
	}
	
	/**
	 * Adds <code>classFile</code> to list of class files, into included
	 * table, to classFilesByName.
	 * @param classFile
	 * @throws InvalidByteCodeException
	 */
/*&*/
	private void include(ClassFile classFile) throws InvalidByteCodeException
	{
		classFileList.add(classFile);
		String className = classFile.getThisClassName();
		classFileByName.put(className, classFile);
		includedTable.put(className, new ArrayList<String>());
	}

/*&*/
	private ClassFile newClassFile(String className)
		throws InvalidByteCodeException, IOException
	{
		
		logger.debug("newClassFile(" + className + ")");
		
		String  calledFileName = formatFileName(className);
		logger.debug("calledFileName=" + calledFileName);

		File calledFile = new File(calledFileName);
		if (calledFile.exists()) {
			logger.debug("classFile found in current directory");
			return ClassFileReader.readFromFile(calledFile);
		} 

		logger.debug("classFile NOT found in current directory");

		// search for class in class path
		return findClassFile(className, searchPaths);
	}
/*&*/
	public static ClassFile findClassFile(String className) throws IOException,
		InvalidByteCodeException
	{
		return findClassFile(className, new String[0]);
	}
	
	/**
	 * Searches all classpath directories for the file and all jars, otherwise
	 * looks into the rt.jar
	 * 
	 * @param className packages are expected to be separated by a '/' 
	 * or '.' , e.g. "java/util/StringTokenizer" or "java.util.StringTokenizer"
	 * @throws Exception if there was an error reading the class file or the
	 * file was not found
	 */
/*&*/
	private static ClassFile findClassFile(String className,
										   String[] pathEntries)
		throws InvalidByteCodeException, IOException 
	{
		
		// join given paths and class path entries
		String classPath = System.getProperty("java.class.path",".");
		String[] splitClassPath = classPath.split(System.getProperty("path.separator"));
		String[] resultPaths = new String[pathEntries.length + splitClassPath.length];
		System.arraycopy(pathEntries, 0, resultPaths, 0, pathEntries.length);
		System.arraycopy(splitClassPath, 0, resultPaths, pathEntries.length, splitClassPath.length);
		
		String subFilename = className.replace('.', File.separatorChar);
		subFilename = subFilename.replace('/', File.separatorChar) + ".class";
		
		for (int i = 0; i < resultPaths.length; i++) {
			File f = new File(resultPaths[i]);
			if (f.isFile()) {
				ClassFile classFile = findClassFile(new JarFile(f), subFilename);
				if (classFile != null) {
					return classFile;
				}
			}
			else {
				File possibleClassFile = new File(f, subFilename);
				if (possibleClassFile.isFile()) {
					return ClassFileReader.readFromFile(possibleClassFile);
				}
			}
		}
		// fall back on rt.jar.
		File rtFile = new File(PDSDefault.JAVA_HOME,  File.separator + "jre" 
				+ File.separator + "lib" + File.separator + "rt.jar");
		if (rtFile.isFile()) {
			ClassFile classFile = findClassFile(new JarFile(rtFile), subFilename);
			if (classFile != null) {
				return classFile;
			}
		}
		throw new FileNotFoundException("File for class " + className 
										+ " not found");
	}
	
	/**
	 * Converts the filesystem path to a jar path and looks it up in the
	 * jarFile. 
	 * <p>
	 * If successful a class file object is returned.
	 * @throws Exception
	 */
/*&*/
	private static ClassFile findClassFile(JarFile jarFile, String path)
		throws InvalidByteCodeException, IOException 
	{
        try {
        	String jarPath = path.replace(File.separatorChar, '/');
            JarEntry jarEntry = jarFile.getJarEntry(jarPath);
            if (jarEntry != null) {
            	return ClassFileReader.readFromInputStream
					(jarFile.getInputStream(jarEntry));
            }
        } 
        finally {
            jarFile.close();
        }	
        return null;
	}
/*&*/	
	private String formatFileName(String className) {
		
		StringBuffer out = new StringBuffer();
		
		if (rootFile != null && rootFile.getParent() != null) {
			logger.debug(rootFile.getName());
			logger.debug("rootFile.getParent()=" + rootFile.getParent());
			out.append(rootFile.getParent());
			out.append(File.separator);
		}
		out.append(className.substring(className.lastIndexOf("/") + 1));
		out.append(".class");
		
		return out.toString();
	}
	
	private static class MethodInvocation
	{
		public String className;
		public String methodName;
		public String methodDescriptor;

		public MethodInvocation(String className, String methodName, 
				String methodDescriptor) 
		{
			this.className = className;
			this.methodName = methodName;
			this.methodDescriptor = methodDescriptor;
		}
		
		@Override
		public boolean equals(Object obj) 
		{
			MethodInvocation other = (MethodInvocation)obj;
			return className.equals(other.className) 
				&& methodName.equals(other.methodName)
				&& methodDescriptor.equals(other.methodDescriptor);
		}
		
		@Override
		public int hashCode() 
		{
			return className.hashCode() + 31 * methodName.hashCode()
				+ 31 * 31 * methodDescriptor.hashCode();
		}
	}
		
	private static class InterfaceNode extends DefaultMutableTreeNode
	{
		private List<String> implementers = null;
		private String interfaceName;
		
		public InterfaceNode(String interfaceName)
		{
			this.interfaceName = interfaceName;
		}
		
		public String getInterfaceName()
		{
			return interfaceName;
		}
		
		public void addImplementer(String implementer)
		{
			if (implementers == null) {
				implementers = new ArrayList<String>(5);
			}
			implementers.add(implementer);
		}
		
		public List<String> getImplementers()
		{
			if (implementers != null) {
				return Collections.unmodifiableList(implementers);
			}
			else {
				return Collections.emptyList();
			}
		}
	}
	
	public static class ClassNode extends DefaultMutableTreeNode
	{
		private List<InterfaceNode> interfaces;
		private String className;
		
		private ClassNode(String className, List<InterfaceNode> interfaces)
		{
			super(className);
			this.className = className;
			this.interfaces = interfaces;
		}
		
		public String getClassName()
		{
			return className;
		}
		
		public List<InterfaceNode> getInterfaces()
		{
			if (interfaces == null) {
				return Collections.emptyList();
			}
			else {
				return Collections.unmodifiableList(interfaces);
			}
		}
	}
}
