package test.Julia.JLex;

class CAlloc
{
  static CDfa newCDfa(CSpec spec){
	CDfa dfa;
	dfa = new CDfa(spec.m_dfa_states.size());
	spec.m_dfa_states.addElement(dfa);
	return dfa;
  }

  static CNfaPair newCNfaPair(){
	CNfaPair pair = new CNfaPair();
	return pair;
  }

  static CNfaPair newNLPair(CSpec spec) {
    CNfaPair pair = newCNfaPair();
    pair.m_end=newCNfa(spec); // newline accepting state
    pair.m_start=newCNfa(spec); // new state with two epsilon edges
    pair.m_start.m_next = newCNfa(spec);
    pair.m_start.m_next.m_edge = CNfa.CCL;
    pair.m_start.m_next.m_set = new CSet();
    pair.m_start.m_next.m_set.add('\n');
    if (spec.m_dtrans_ncols-2 > 2029) {
      pair.m_start.m_next.m_set.add(2028); /*U+2028 is LS, the line separator*/
      pair.m_start.m_next.m_set.add(2029); /*U+2029 is PS, the paragraph sep.*/
    }
    pair.m_start.m_next.m_next = pair.m_end; // accept '\n', U+2028, or U+2029
    pair.m_start.m_next2 = newCNfa(spec);
    pair.m_start.m_next2.m_edge = '\r';
    pair.m_start.m_next2.m_next = newCNfa(spec);
    pair.m_start.m_next2.m_next.m_next = pair.m_end; // accept '\r';
    pair.m_start.m_next2.m_next.m_next2 = newCNfa(spec);
    pair.m_start.m_next2.m_next.m_next2.m_edge = '\n';
    pair.m_start.m_next2.m_next.m_next2.m_next = pair.m_end; // accept '\r\n';
    return pair;
  }

  static CNfa newCNfa(CSpec spec){
	CNfa p;
	p = new CNfa();
	spec.m_nfa_states.addElement(p);
	p.m_edge = CNfa.EPSILON;
	return p;
  }
}