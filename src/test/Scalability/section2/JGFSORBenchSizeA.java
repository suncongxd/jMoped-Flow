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

import test.Scalability.section2.sor.*;
import test.Scalability.jgfutil.*;

public class JGFSORBenchSizeA{ 

  public static void main(String argv[]){

    JGFInstrumentor.printHeader(2,0);

    JGFSORBench sor = new JGFSORBench(); 
    sor.JGFrun(0);
 
  }
}
