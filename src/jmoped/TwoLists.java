package jmoped;

import java.util.ArrayList;
import java.util.List;

public class TwoLists {

	private List<String> first;
	private List<String> first_pair;// by suncong
	private List<String> second;
	private List<String> second_pair;// by suncong
	
	public TwoLists(String s, String s_pair) {
		first = new ArrayList<String>();
		first.add(s);
		first_pair=new ArrayList<String>();
		first_pair.add(s_pair);
	}

	public TwoLists(List<String> a, List<String> a_pair) {
		this(a, null, a_pair, null);
	}

	public TwoLists(List<String> a, List<String> b, List<String> a_pair, List<String> b_pair) {
		first = a;
		second = b;
		first_pair=a_pair;
		second_pair=b_pair;
	}
	
	public void add(String s, String s_pair) {
		first.add(s);
		first_pair.add(s_pair);
	}
	
	public void add(List<String> ls, List<String> ls_pair) {
		first.addAll(ls);
		first_pair.addAll(ls_pair);
	}
	
	public List<String> getFirst() {
		return first;
	}
	public List<String> getFirstPair(){
		return first_pair;
	}
	
	public List<String> getSecond() {
		return second;
	}
	public List<String> getSecondPair(){
		return second_pair;
	}
	
	public boolean isSecondNull() {
		return (second == null);
	}
	public boolean isSecondPairNull(){
		return (second_pair==null);
	}
}
