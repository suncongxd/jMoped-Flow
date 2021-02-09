package jmoped;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import jmoped.PDSInfo;

/**
 * Represents a list of Remopla statements. A statement can be either parallel
 * assignment or if-statement. The class is supposed to be used in the 
 * following ways:
 * 
 * 1) Use addNext(stmt) to add stmt that will be executed after previously-
 * 		added statements.
 * 2) For parallel assignment, use addPar(stmt). stmt will be executed in 
 * 		parallel with the previously-added statements (if any).
 * 3) For if-statement, construct an object of type TwoLists, which indeed
 * 		simply contains two Lists. The first list contains conditions.
 * 		The second list are statements to be executed in parallel if
 * 		the conditions are fulfilled. Then, use addIf(twoLists). 
 * 		twoLists will be executed after previously-added statements.
 * 
 *  For example, if you want to add the following statments:
 *  	stmt1, stmt2;
 *  	if-stmt;
 *  	stmt3;
 *  Call the methods as follows: 
 *  	addPar(stmt1); addPar(stmt2); addIf(if-stmt); addNext(stmt3);
 * 
 * @author suwimodh
 *
 */
public class PDSStmt {

	private List<TwoLists> stmt = new ArrayList<TwoLists>();

	/*added by suncong, need to call the methods of PDSInfo*/
	//private PDSInfo pdsInfo;
	/*************/
	
	static Logger logger = Logger.getLogger(PDSStmt.class);

	public PDSStmt() {}
	
	public PDSStmt(List<String> stmtList, List<String> stmtListPair){
		stmt.add(new TwoLists(stmtList,stmtListPair));
	}
	
/*	public PDSStmt(PDSInfo info) {pdsInfo=info;}

	public PDSStmt(List<String> stmtList, List<String> stmt_pairList, PDSInfo info) {
		stmt.add(new TwoLists(stmtList,stmt_pairList));
		pdsInfo=info;
	}*/
	
	public void addNext(String normalStmt, String normalStmtPair) {
		stmt.add(new TwoLists(normalStmt,normalStmtPair));
	}
	
	public void addNext(List<String> stmtList, List<String> stmt_pairList) {
		stmt.add(new TwoLists(stmtList,stmt_pairList));
	}
	
	public void addIf(TwoLists ifStmt) {
		stmt.add(ifStmt);
	}
	
	public void addPar(String normalStmt, String normalStmtPair) {
		List<String> wrapper = new ArrayList<String>();
		wrapper.add(normalStmt);
		List<String> wrapper_pair=new ArrayList<String>();
		wrapper_pair.add(normalStmtPair);
		addPar(wrapper,wrapper_pair);
	}
	/*<stmtList,stmtListPair> to <first,first_pair> of the last TwoLists struct of stmt*/
	public void addPar(List<String> stmtList, List<String> stmtListPair) {
		int size = stmt.size();
		TwoLists lastTL;
		if (size == 0) { 
			lastTL = new TwoLists(stmtList, stmtListPair);
		}
		else {
			lastTL = stmt.remove(size - 1);
			lastTL.add(stmtList,stmtListPair);
		}
		stmt.add(lastTL);
	}
	
	public boolean isEmpty() {
		
		return stmt.isEmpty();
	}
	
	public String toRemopla() {
		StringBuilder out = new StringBuilder();
		boolean isIf = false;
		
		for (TwoLists tl : stmt) {
			if (tl.isSecondNull()) {
				if (isIf) out.append("fi;\n");
				isIf = false;
				out.append(commaList(tl.getFirst()));
				out.append(";\n");
			} else {
				if (!isIf) out.append("if\n");
				isIf = true;
				out.append("\t:: ");
				out.append(andList(tl.getFirst()));
				out.append(" -> ");
				out.append(commaList(tl.getSecond()));
				out.append(";\n");
			}
		}
		if (isIf) out.append("fi;\n");
		
		return out.toString();
	}
	/*by suncong*/
	public String toRemoplaPair() {
		StringBuilder out = new StringBuilder();
		boolean isIf = false;
		for (TwoLists tl : stmt) {
			if (tl.isSecondPairNull()) {
				if (isIf) out.append("fi;\n");
				isIf = false;
				out.append(commaList(tl.getFirstPair())).append(";\n");
			} else {
				if (!isIf) out.append("if\n");
				isIf = true;
				out.append("\t:: ").append(andList(tl.getFirstPair()));
				out.append(" -> ").append(commaList(tl.getSecondPair())).append(";\n");
			}
		}
		if (isIf) out.append("fi;\n");
		
		return out.toString();
	}
	/*******************/
	public static String andList(List<String> list) {
		
		return andList(list, list.size());
	}
	
	public static String andList(List<String> list, int to) {
		
		return createList(list, to, " && ");
	}
	
	public static String commaList(List<String> list) {
		
		return commaList(list, list.size());
	}
	
	public static String commaList(List<String> list, int to) {

		return createList(list, to, ", ");
	}

	/**
	 * Concatenates elements of the list to a symbol-separated list and 
	 * returns it as a string.
	 *
	 * @throws IndexOutOfBoundsException if <code>to > list.size() </code>
	 */
	public static String createList(List<String> list, int to, String symbol) {

		if (list.size() == 0) {
			//logger.error("Error: size of instruction list (inst) = 0");
			return "skip";
		}

		if (list.size() < to) {
			throw new IndexOutOfBoundsException(
					"to is greater than size of list");
		}
		StringBuilder out = new StringBuilder();
		Iterator<String> iter = list.iterator();
		
		out.append(iter.next());
		for (int i = 1; i < to; i++) {
			out.append(symbol);
			out.append(iter.next());
		}
		
		return out.toString();
	}
}

