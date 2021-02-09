package jmoped.policygen;

class PolicyMapStruct{
	//used in heap equivalence
	FieldStruct fld;
	int heapIndex;
	//used in stack equivalence
	ParamStruct par;
	int stackIndex;
	
	boolean bHp;//whether for heap equivalence mapping. true for yes, false for no.
	
	public PolicyMapStruct(FieldStruct fs, int heapidx){
		fld=fs;
		heapIndex=heapidx;
		par=null;
		stackIndex=-1;
	}
	public PolicyMapStruct(ParamStruct p, int stackidx){
		par=p;
		stackIndex=stackidx;
		fld=null;
		heapIndex=-1;
	}
}