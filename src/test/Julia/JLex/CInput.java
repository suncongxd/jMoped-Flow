package test.Julia.JLex;

class CInput
{
//  private java.io.BufferedReader m_input; /* JLex specification file. */

  boolean m_eof_reached; /* Whether EOF has been encountered. */
  boolean m_pushback_line; 
  char m_line[]; /* Line buffer. */
  int m_line_read; /* Number of bytes read into line buffer. */
  int m_line_index; /* Current index into line buffer. */
  int m_line_number; /* Current line number. */

  static final boolean EOF = true;
  static final boolean NOT_EOF = false;

  CInput(){//java.io.Reader input){
//	if (CUtility.DEBUG){CUtility.ASSERT(null != input);}
//	m_input = new java.io.BufferedReader(input);
	m_line = null;
	m_line_read = 0;
	m_line_index = 0;
	m_eof_reached = false;
	m_line_number = 0;
	m_pushback_line = false;
  }

  boolean getLine(){// throws java.io.IOException {
	String lineStr;
	int elem;
	if (m_eof_reached){
	    return EOF;
	  }

	if (m_pushback_line) {
	    m_pushback_line = false;

	    for (elem = 0; elem < m_line_read; ++elem) {
			if (false == isspace(m_line[elem])){
			    break;
			  }
	    }

	    if (elem < m_line_read) {
			m_line_index = 0;
			return NOT_EOF;
	      }
	}

	while (true) {
	    if (null == (lineStr ="abcdefg")){// m_input.readLine())) {
			m_eof_reached = true;
			m_line_index = 0;
			return EOF;
	      }
	    m_line = (lineStr + "\n").toCharArray();
	    m_line_read=m_line.length;
	    ++m_line_number;

	    elem = 0;
	    while (isspace(m_line[elem])) {
			++elem;
			if (elem == m_line_read) {
			    break;
			  }
	      }
	    
	    if (elem < m_line_read) {
			break;
	    }
	}
	m_line_index = 0;
	return NOT_EOF;
  }
  /*from CUtility*/
  boolean isspace(char c){
	if ('\b' == c || '\t' == c || '\n' == c || '\f' == c || '\r' == c || ' ' == c){
	    return true;
	  }
	return false;
  }
}