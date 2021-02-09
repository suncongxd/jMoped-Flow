				    
package test.Julia.java_cup.runtime;

import java.util.Stack;

public abstract class lr_parser {

  public lr_parser()
    {
    }

  /** Constructor that sets the default scanner. [CSA/davidm] */
//  public lr_parser(Scanner s) {
//    this(); /* in case default constructor someday does something */
//    setScanner(s);
//  }

  protected final static int _error_sync_size = 3;

  protected int error_sync_size() {return _error_sync_size; }

  public abstract short[][] production_table();

  public abstract short[][] action_table();

  public abstract short[][] reduce_table();

  public abstract int start_state();

  public abstract int start_production();

  public abstract int EOF_sym();

  public abstract int error_sym();

  protected boolean _done_parsing = false;

  public void done_parsing()
    {
      _done_parsing = true;
    }

  protected int tos;

  protected Symbol cur_token;

  protected Stack stack = new Stack();

  protected short[][] production_tab;

  protected short[][] action_tab;

  protected short[][] reduce_tab;

//  private Scanner _scanner;
//  public void setScanner(Scanner s) { _scanner = s; }
//  public Scanner getScanner() { return _scanner; }

  public abstract Symbol do_action(
    int       act_num, 
    lr_parser parser, 
    Stack     stack, 
    int       top) 
    throws java.lang.Exception;

//  public void user_init() throws java.lang.Exception { }

  protected abstract void init_actions() throws java.lang.Exception;

  public Symbol scan() throws java.lang.Exception {
    return new Symbol(5);//getScanner().next_token();
  }

/*
  public void report_fatal_error(String   message, Object   info) throws java.lang.Exception
    {
      done_parsing();
//      report_error(message, info);
//      throw new Exception("Can't recover from previous error(s)");
    }*/
/*
  public void report_error(String message, Object info)
    {
      System.err.print(message);
      if (info instanceof Symbol)
	if (((Symbol)info).left != -1)
	System.err.println(" at character " + ((Symbol)info).left + 
			   " of input");
	else System.err.println("");
      else System.err.println("");
    }
*/
/*  public void syntax_error(Symbol cur_token)
    {
//      report_error("Syntax error", cur_token);
    }
*/
/*
  public void unrecovered_syntax_error(Symbol cur_token)
    throws java.lang.Exception
    {
      report_fatal_error("Couldn't repair and continue parse", cur_token);
    }
*/
  protected final short get_action(int state, int sym)
    {
      short tag;
      int first, last, probe;
      short[] row = action_tab[state];

      /* linear search if we are < 10 entries */
      if (row.length < 20)
        for (probe = 0; probe < row.length; probe++)
	  {
	    /* is this entry labeled with our Symbol or the default? */
	    tag = row[probe++];
	    if (tag == sym || tag == -1)
	      {
	        /* return the next entry */
	        return row[probe];
	      }
	  }
      /* otherwise binary search */
      else
	{
	  first = 0; 
	  last = (row.length-1)/2 - 1;  /* leave out trailing default entry */
	  while (first <= last)
	    {
	      probe = (first+last)/2;
	      if (sym == row[probe*2])
		return row[probe*2+1];
	      else if (sym > row[probe*2])
		first = probe+1;
	      else
	        last = probe-1;
	    }

	  /* not found, use the default at the end */
	  return row[row.length-1];
	}

      /* shouldn't happened, but if we run off the end we return the 
	 default (error == 0) */
      return 0;
    }

  protected final short get_reduce(int state, int sym)
    {
      short tag;
      short[] row = reduce_tab[state];

      if (row == null)
        return -1;

      for (int probe = 0; probe < row.length; probe++)
	{
	  tag = row[probe++];
	  if (tag == sym || tag == -1)
	    {
	      /* return the next entry */
	      return row[probe];
	    }
	}
      return -1;
    }
/*
  public Symbol parse() throws java.lang.Exception
    {
      int act;

      Symbol lhs_sym = null;
      short handle_size, lhs_sym_num;

      production_tab = production_table();
      action_tab     = action_table();
      reduce_tab     = reduce_table();

      init_actions();
//      user_init();
      cur_token = scan(); 

      stack.removeAllElements();
      stack.push(new Symbol(0, start_state()));
      tos = 0;

      for (_done_parsing = false; !_done_parsing; )
	{
//	  if (cur_token.used_by_parser)
//	    throw new Error("Symbol recycling detected (fix your scanner).");

//	  act = get_action(((Symbol)stack.peek()).parse_state, cur_token.sym);
          act=1;

	  if (act > 0)
	    {
	      cur_token.parse_state = act-1;
	      cur_token.used_by_parser = true;
	      stack.push(cur_token);
	      tos++;
	      cur_token = scan();
	    }
	  else if (act < 0)
	    {
	      lhs_sym = do_action((-act)-1, this, stack, tos);
	      lhs_sym_num = production_tab[(-act)-1][0];
	      handle_size = production_tab[(-act)-1][1];
	      for (int i = 0; i < handle_size; i++)
		{
		  stack.pop();
		  tos--;
		}
//	      act = get_reduce(((Symbol)stack.peek()).parse_state, lhs_sym_num);
              act=1;
	      lhs_sym.parse_state = act;
	      lhs_sym.used_by_parser = true;
	      stack.push(lhs_sym);
	      tos++;
	    }
	  else if (act == 0)
	    {
//	      syntax_error(cur_token);
	      if (!error_recovery(false))
		{
//		  unrecovered_syntax_error(cur_token);
		  done_parsing();
		} else {
		  lhs_sym = (Symbol)stack.peek();
		}
	    }
	}
      return lhs_sym;
    }
*/
  public void debug_message(String mess)
    {
      System.err.println(mess);
    }

  public void dump_stack()
    {
      if (stack == null)
	{
	  debug_message("# Stack dump requested, but stack is null");
	  return;
	}

      debug_message("============ Parse Stack Dump ============");

      /* dump the stack */
      for (int i=0; i<stack.size(); i++)
	{
	  debug_message("Symbol: " + ((Symbol)stack.elementAt(i)).sym +
			" State: " + ((Symbol)stack.elementAt(i)).parse_state);
	}
      debug_message("==========================================");
    }

  public void debug_reduce(int prod_num, int nt_num, int rhs_size)
    {
      debug_message("# Reduce with prod #" + prod_num + " [NT=" + nt_num + 
	            ", " + "SZ=" + rhs_size + "]");
    }

  public void debug_shift(Symbol shift_tkn)
    {
      debug_message("# Shift under term #" + shift_tkn.sym + 
		    " to state #" + shift_tkn.parse_state);
    }
/*
  public void debug_stack() {
      StringBuffer sb=new StringBuffer("## STACK:");
      for (int i=0; i<stack.size(); i++) {
	  Symbol s = (Symbol) stack.elementAt(i);
	  sb.append(" <state "+s.parse_state+", sym "+s.sym+">");
	  if ((i%3)==2 || (i==(stack.size()-1))) {
	      debug_message(sb.toString());
	      sb = new StringBuffer("         ");
	  }
      }
  }
*/
/*
  public Symbol debug_parse()
    throws java.lang.Exception
    {
      int act;
      Symbol lhs_sym = null;
      short handle_size, lhs_sym_num;

      production_tab = production_table();
      action_tab     = action_table();
      reduce_tab     = reduce_table();

      debug_message("# Initializing parser");

      init_actions();

//      user_init();

      cur_token = scan(); 

      debug_message("# Current Symbol is #" + cur_token.sym);

      stack.removeAllElements();
      stack.push(new Symbol(0, start_state()));
      tos = 0;

      for (_done_parsing = false; !_done_parsing; )
	{
	  if (cur_token.used_by_parser)
	    throw new Error("Symbol recycling detected (fix your scanner).");

	  //debug_stack();

	  act = get_action(((Symbol)stack.peek()).parse_state, cur_token.sym);

	  if (act > 0)
	    {
	      cur_token.parse_state = act-1;
	      cur_token.used_by_parser = true;
	      debug_shift(cur_token);
	      stack.push(cur_token);
	      tos++;

	      cur_token = scan();
              debug_message("# Current token is " + cur_token);
	    }
	  else if (act < 0)
	    {
	      lhs_sym = do_action((-act)-1, this, stack, tos);

	      lhs_sym_num = production_tab[(-act)-1][0];
	      handle_size = production_tab[(-act)-1][1];

	      debug_reduce((-act)-1, lhs_sym_num, handle_size);

	      for (int i = 0; i < handle_size; i++)
		{
		  stack.pop();
		  tos--;
		}

	      act = get_reduce(((Symbol)stack.peek()).parse_state, lhs_sym_num);
	      debug_message("# Reduce rule: top state " +
			     ((Symbol)stack.peek()).parse_state +
			     ", lhs sym " + lhs_sym_num + " -> state " + act); 

	      lhs_sym.parse_state = act;
	      lhs_sym.used_by_parser = true;
	      stack.push(lhs_sym);
	      tos++;

	      debug_message("# Goto state #" + act);
	    }
	  else if (act == 0)
	    {
//	      syntax_error(cur_token);

	      if (!error_recovery(true))
		{
//		  unrecovered_syntax_error(cur_token);
		  done_parsing();
		} else {
		  lhs_sym = (Symbol)stack.peek();
		}
	    }
	}
      return lhs_sym;
    }*/
/*
  protected boolean error_recovery(boolean debug)
    throws java.lang.Exception
    {
      if (debug) debug_message("# Attempting error recovery");

      if (!find_recovery_config(debug))
	{
	  if (debug) debug_message("# Error recovery fails");
	  return false;
	}

      read_lookahead();

      for (;;)
	{
	  if (debug) debug_message("# Trying to parse ahead");
	  if (try_parse_ahead(debug))
	    {
	      break;
	    }
  if (lookahead[0].sym == EOF_sym()) 
	    {
	      if (debug) debug_message("# Error recovery fails at EOF");
	      return false;
	    }
	  if (debug) 
	  debug_message("# Consuming Symbol #" + cur_err_token().sym);
	  restart_lookahead();
	}
      if (debug) debug_message("# Parse-ahead ok, going back to normal parse");
      parse_lookahead(debug);
      return true;
    }
*/
  protected boolean shift_under_error()
    {
      /* is there a shift under error Symbol */
      return get_action(((Symbol)stack.peek()).parse_state, error_sym()) > 0;
    }

  protected boolean find_recovery_config(boolean debug)
    {
      Symbol error_token;
      int act;

      if (debug) debug_message("# Finding recovery state on stack");

      /* Remember the right-position of the top symbol on the stack */
      int right_pos = ((Symbol)stack.peek()).right;
      int left_pos  = ((Symbol)stack.peek()).left;

      /* pop down until we can shift under error Symbol */
      while (!shift_under_error())
	{
	  /* pop the stack */
	  if (debug) 
	    debug_message("# Pop stack by one, state was # " +
	                  ((Symbol)stack.peek()).parse_state);
          left_pos = ((Symbol)stack.pop()).left;	
	  tos--;

	  /* if we have hit bottom, we fail */
	  if (stack.empty()) 
	    {
	      if (debug) debug_message("# No recovery state found on stack");
	      return false;
	    }
	}

      /* state on top of the stack can shift under error, find the shift */
      act = get_action(((Symbol)stack.peek()).parse_state, error_sym());
      if (debug) 
	{
	  debug_message("# Recover state found (#" + 
			((Symbol)stack.peek()).parse_state + ")");
	  debug_message("# Shifting on error to state #" + (act-1));
	}

      /* build and shift a special error Symbol */
      error_token = new Symbol(error_sym(), left_pos, right_pos);
      error_token.parse_state = act-1;
      error_token.used_by_parser = true;
      stack.push(error_token);
      tos++;

      return true;
    }

  protected Symbol lookahead[];

  /** Position in lookahead input buffer used for "parse ahead". */
  protected int lookahead_pos;

  protected void read_lookahead() throws java.lang.Exception
    {
      /* create the lookahead array */
      lookahead = new Symbol[error_sync_size()];

      /* fill in the array */
      for (int i = 0; i < error_sync_size(); i++)
	{
	  lookahead[i] = cur_token;
	  cur_token = scan();
	}

      /* start at the beginning */
      lookahead_pos = 0;
    }

  protected Symbol cur_err_token() { return lookahead[lookahead_pos]; }

  protected boolean advance_lookahead()
    {
      /* advance the input location */
      lookahead_pos++;

      /* return true if we didn't go off the end */
      return lookahead_pos < error_sync_size();
    }

  protected void restart_lookahead() throws java.lang.Exception
    {
      /* move all the existing input over */
      for (int i = 1; i < error_sync_size(); i++)
	lookahead[i-1] = lookahead[i];

      /* read a new Symbol into the last spot */
      cur_token = scan();
      lookahead[error_sync_size()-1] = cur_token;

      /* reset our internal position marker */
      lookahead_pos = 0;
    }

  protected boolean try_parse_ahead(boolean debug)
    throws java.lang.Exception
    {
      int act;
      short lhs, rhs_size;

      /* create a virtual stack from the real parse stack */
      virtual_parse_stack vstack = new virtual_parse_stack(stack);

      /* parse until we fail or get past the lookahead input */
      for (;;)
	{
	  /* look up the action from the current state (on top of stack) */
	  act = get_action(vstack.top(), cur_err_token().sym);

	  /* if its an error, we fail */
	  if (act == 0) return false;

	  /* > 0 encodes a shift */
	  if (act > 0)
	    {
	      /* push the new state on the stack */
	      vstack.push(act-1);

	      if (debug) debug_message("# Parse-ahead shifts Symbol #" + 
		       cur_err_token().sym + " into state #" + (act-1));

	      /* advance simulated input, if we run off the end, we are done */
	      if (!advance_lookahead()) return true;
	    }
	  /* < 0 encodes a reduce */
	  else
	    {
	      /* if this is a reduce with the start production we are done */
	      if ((-act)-1 == start_production()) 
		{
		  if (debug) debug_message("# Parse-ahead accepts");
		  return true;
		}

	      /* get the lhs Symbol and the rhs size */
	      lhs = production_tab[(-act)-1][0];
	      rhs_size = production_tab[(-act)-1][1];

	      /* pop handle off the stack */
	      for (int i = 0; i < rhs_size; i++)
		vstack.pop();

	      if (debug) 
		debug_message("# Parse-ahead reduces: handle size = " + 
	          rhs_size + " lhs = #" + lhs + " from state #" + vstack.top());

	      /* look up goto and push it onto the stack */
	      vstack.push(get_reduce(vstack.top(), lhs));
	      if (debug) 
		debug_message("# Goto state #" + vstack.top());
	    }
	}
    }
/*
  protected void parse_lookahead(boolean debug)
    throws java.lang.Exception
    {
      int act;
      Symbol lhs_sym = null;

      short handle_size, lhs_sym_num;

      lookahead_pos = 0;

      if (debug) 
	{
	  debug_message("# Reparsing saved input with actions");
	  debug_message("# Current Symbol is #" + cur_err_token().sym);
	  debug_message("# Current state is #" + 
			((Symbol)stack.peek()).parse_state);
	}

      while(!_done_parsing)
	{
	  act = 
	    get_action(((Symbol)stack.peek()).parse_state, cur_err_token().sym);

	  if (act > 0)
	    {
	      cur_err_token().parse_state = act-1;
	      cur_err_token().used_by_parser = true;
	      if (debug) debug_shift(cur_err_token());
	      stack.push(cur_err_token());
	      tos++;
	      if (!advance_lookahead()) 
		{
		  if (debug) debug_message("# Completed reparse");

		  return;
		}
	      
	      if (debug) 
		debug_message("# Current Symbol is #" + cur_err_token().sym);
	    }
	  else if (act < 0)
	    {
	      lhs_sym = do_action((-act)-1, this, stack, tos);
	      lhs_sym_num = production_tab[(-act)-1][0];
	      handle_size = production_tab[(-act)-1][1];

	      if (debug) debug_reduce((-act)-1, lhs_sym_num, handle_size);

	      for (int i = 0; i < handle_size; i++)
		{
		  stack.pop();
		  tos--;
		}
	      act = get_reduce(((Symbol)stack.peek()).parse_state, lhs_sym_num);
	      lhs_sym.parse_state = act;
	      lhs_sym.used_by_parser = true;
	      stack.push(lhs_sym);
	      tos++;
	       
	      if (debug) debug_message("# Goto state #" + act);

	    }
	  else if (act == 0)
	    {
//	      report_fatal_error("Syntax error", lhs_sym);
	      return;
	    }
	}

	
    }
*/
  protected static short[][] unpackFromStrings(String[] sa)
    {
      // Concatanate initialization strings.
      StringBuffer sb = new StringBuffer(sa[0]);
      for (int i=1; i<sa.length; i++)
	sb.append(sa[i]);
      int n=0; // location in initialization string
      int size1 = (((int)sb.charAt(n))<<16) | ((int)sb.charAt(n+1)); n+=2;
      short[][] result = new short[size1][];
      for (int i=0; i<size1; i++) {
        int size2 = (((int)sb.charAt(n))<<16) | ((int)sb.charAt(n+1)); n+=2;
        result[i] = new short[size2];
        for (int j=0; j<size2; j++)
          result[i][j] = (short) (sb.charAt(n++)-2);
      }
      return result;
    }
}

