package jmoped.automata;

public class RecordStruct{
	private int startPos;
	private int endPos;
	
	public RecordStruct(){
		startPos=endPos=-1;
	}
	public void setStartPos(int pos){
		startPos=pos;
	}
	public void setEndPos(int pos){
		endPos=pos;
	}
	public void setBack(){
		startPos=endPos=-1;
	}
	public int getStartPos(){
		return startPos;
	}
	public int getEndPos(){
		return endPos;
	}
}