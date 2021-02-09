package jmoped;

/**
 * Statically generates the initialization code for multidimensional arrays.
 * <p>
 * The algorithm the Remopla code is based upon can be found in
 * {@link de.uni_stuttgart.fmi.szs.jmoped.OutputGeneratorTest.TestOutput#main(String[])}.
 * <p>
 * The current implementation is limited to 4 dimensions but could easily be changed
 * to handle more.
 */
public class PDSMultiArrayInitializerMethod 
{
	
	private PDSInfo pdsInfo;
	
	/**
	 * The name of the array initializer method.
	 */
	public final static String NAME = "initializeMultiArray";
	
	/**
	 * The maximum number of dimensions which is supported.
	 */
	public final static int MAX_DIMENSIONS = 4;

	public PDSMultiArrayInitializerMethod(PDSInfo pdsInfo) 
	{
		this.pdsInfo = pdsInfo;
	}
	
	public String toRemoplaHead()
	{
		return "module void initializeMultiArray(int array, int size1, int v2, int v3, int v4)";
	}
	
	/**
	 * The code is based on the Java implementation in 
	 * {@link OutputGeneratorTest.TestOutput#main(String[])} and has been fine 
	 * tuned in moped/tests/Multitest.rem, change it there and copy it back here.
	 */
	public String toRemopla()
	{
		// TODO if we're really paranoid, add unique offset to each variable to avoid clashes
		// with future global variables
		String output = 
			"module void initializeMultiArray(int array, int size1, int v2, int v3, int v4)\n"
			+ "{\n"
			+ "int offset;\n"
			+ "int newoffset;\n"
			+ "int size;\n"
			+ "int dim;\n"
			+ "int s1;\n"
			+ "int s2;\n"
			+ "int i;\n"
			+ "int j;\n"
			+ "int sizeoffset;\n"
			+ "int dims[4];\n"
			+ "int modtmp;\n"
			+ "\n"
			+ "initializeMultiArray:	dims[0] = size1, dims[1] = v2, dims[2] = v3, dims[3] = v4,\n"
			+ "			   heap[array] = size1, offset=0, newoffset=0, size=1, dim=1;\n"
			+ "\n"
			+ "initializeMultiArray5: goto loopguard;\n"
			+ "loopstart:	s1 = dims[dim-1], s2 = dims[dim], offset = newoffset;\n"
			+ "\n"
			+ "newoffset = newoffset + (s1 + 1), size = size * s1, i = 0, j = 0;\n"
			+ "\n"
			+ "goto innerloopguard;\n"
			+ "// todo heap name could change\n"
			+ "innerloopstart:	sizeoffset = array + newoffset + i * (s2 + 1);\n"
			+ "heap[sizeoffset] = s2;\n"
			+ "if\n"
			+ "	   :: j==0 -> goto noextrainc;\n"
			+ "	   :: else -> skip;\n"
			+ "fi\n"
			+ "modtmp = s1 + 1;\n"
			+ "modtmp = j - (modtmp * (j/modtmp));\n"
			+ "if\n"
			+ "	   :: modtmp!=0 -> goto noextrainc;\n"
			+ "	   :: else -> skip;\n"
			+ "fi\n"
			+ "j = j + 1;\n"
			+ "\n"
			+ "noextrainc: heap[1 + array + offset + j] = sizeoffset, i = i + 1, j = j + 1;\n"
			+ "innerloopguard:	if \n"
			+ "	   :: i < size -> goto innerloopstart;\n"
			+ "	   :: else-> skip;\n"
			+ "fi\n"
			+ "\n"
			+ "dim = dim + 1;\n"
			+ "\n"
			+ "loopguard: if\n"
			+ "	   :: dim>=4 -> goto exit;\n"
			+ "	   :: else -> skip;\n"
			+ "fi\n"
			+ "if\n"
			+ "	   :: dims[dim] !=0 -> goto loopstart;\n"
			+ "	   :: else-> skip;\n"
			+ "fi\n"
			+ "\n"
			+ "exit:	return;\n"
			+ "}\n";
		return output.replaceAll("heap", pdsInfo.getHeapName());
	}
}