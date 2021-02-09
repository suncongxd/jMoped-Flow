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
import test.Scalability.section2.series.*;
import test.Scalability.section2.heapsort.*;
import test.Scalability.section2.lufact.*;
import test.Scalability.section2.sor.*;
import test.Scalability.section2.fft.*;
import test.Scalability.section2.sparsematmult.*;
import test.Scalability.jgfutil.*;

public class JGFAllSizeC{

  public static void main(String argv[]){
   
    int size = 2; 

    JGFInstrumentor.printHeader(2,size);

    JGFCryptBench cb = new JGFCryptBench();
    cb.JGFrun(size);    

    JGFSeriesBench se = new JGFSeriesBench(); 
    se.JGFrun(size);

    JGFLUFactBench lub = new JGFLUFactBench();
    lub.JGFrun(size);    

    JGFHeapSortBench hb = new JGFHeapSortBench();
    hb.JGFrun(size);    

    JGFFFTBench fft = new JGFFFTBench(); 
    fft.JGFrun(size);
   
    JGFSORBench jb = new JGFSORBench(); 
    jb.JGFrun(size);
   
    JGFSparseMatmultBench smm = new JGFSparseMatmultBench(); 
    smm.JGFrun(size);
    
    
  }
}
