package test.Julia.JLex;

class CDTrans
{
  int m_dtrans[];
//  CAccept m_accept;
  int m_anchor;
  int m_label;

  static final int F = -1;

  CDTrans(int label,CSpec spec){
	m_dtrans = new int[spec.m_dtrans_ncols];
//	m_accept = null;
	m_anchor = 0;
	m_label = label;
  }
}