package test.Julia.JLex;

import java.util.Enumeration;

class CSet 
{
  private SparseBitSet m_set;
  private boolean m_complement;

  CSet(){
      m_set = new SparseBitSet();
      m_complement = false;
  }
  void complement(){
	m_complement = true;
  }

  void add(int i){
	m_set.set(i);
  }

  void addncase(char c){
	add(c);
	add(Character.toLowerCase(c));
	add(Character.toTitleCase(c));
	add(Character.toUpperCase(c));
  }

  boolean contains(int i){
	boolean result;
	result = m_set.get(i);
	if (m_complement){
	    return (false == result);
	}
	return result;
  }

  void mimic(CSet set) {
	m_complement = set.m_complement;
	m_set = (SparseBitSet) set.m_set.clone();
  } 

  void map(CSet set, int[] mapping) {
    m_complement = set.m_complement;
    m_set.clearAll();
    for (Enumeration e=set.m_set.elements(); e.hasMoreElements(); ) {
      int old_value =((Integer)e.nextElement()).intValue();
      if (old_value<mapping.length) // skip unmapped characters
	m_set.set(mapping[old_value]);
    }
  }
}