package test.Julia.JLex;

import java.util.Vector;


class CBunch
{
  Vector m_nfa_set; /* Vector of CNfa states in dfa state. */
  SparseBitSet m_nfa_bit; /* BitSet representation of CNfa labels. */
  CAccept m_accept; /* Accepting actions, or null if nonaccepting state. */
  int m_anchor; /* Anchors on regular expression. */
  int m_accept_index; /* CNfa index corresponding to accepting actions. */

  CBunch(){
	m_nfa_set = null;
	m_nfa_bit = null;
	m_accept = null;
	m_anchor = 0;
	m_accept_index = -1;
      }
}