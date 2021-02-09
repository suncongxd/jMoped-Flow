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

import test.Scalability.section2.crypt.*;
import test.Scalability.jgfutil.*;

public class JGFCryptBenchSizeA{ 

  public static void main(String argv[]){

    JGFInstrumentor.printHeader(2,0);

    JGFCryptBench cb = new JGFCryptBench(); 
    cb.JGFrun(0);
 
  }
}
