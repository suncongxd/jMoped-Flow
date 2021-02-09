/**************************************************************************
*                                                                         *
*             Java Grande Forum Benchmark Suite - Version 2.0             *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         * 
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/
package test.Scalability.section2;

import test.Scalability.section2.lufact.*;
import test.Scalability.jgfutil.*;

public class JGFLUFactBenchSizeA{ 

  public static void main(String argv[]){

    JGFInstrumentor.printHeader(2,0);

    JGFLUFactBench lub = new JGFLUFactBench(); 
    lub.JGFrun(0);
 
  }
}
 
