package test.Julia.JLex;

class CNfa
{
  int m_edge;  
  CSet m_set;  /* Set to store character classes. */
  CNfa m_next;  /* Next state (or null if none). */
  CNfa m_next2;  
  CAccept m_accept;  /* Set to null if nonaccepting state. */
  int m_anchor;  /* Says if and where pattern is anchored. */
  int m_label;
  SparseBitSet m_states;

  static final int NO_LABEL = -1;
  static final int CCL = -1;
  static final int EMPTY = -2;
  static final int EPSILON = -3;

 CNfa(){
      m_edge = EMPTY;
      m_set = null;
      m_next = null;
      m_next2 = null;
      m_accept = null;
      m_anchor = 0;
      m_label = NO_LABEL;
      m_states = null;
    }

  void mimic(CNfa nfa){
	m_edge = nfa.m_edge;
	if (null != nfa.m_set){
	    if (null == m_set){
			m_set = new CSet();
	      }
	    m_set.mimic(nfa.m_set);
	  }
	else{
	    m_set = null;
	  }
	m_next = nfa.m_next;
	m_next2 = nfa.m_next2;
	m_accept = nfa.m_accept;
	m_anchor = nfa.m_anchor;
	if (null != nfa.m_states){
	    m_states = (SparseBitSet) nfa.m_states.clone();
	  }
	else{
	    m_states = null;
	  }
  }
}