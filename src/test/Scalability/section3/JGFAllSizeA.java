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
package test.Scalability.section3;

import test.Scalability.section3.euler.*;
import test.Scalability.section3.moldyn.*;
import test.Scalability.section3.montecarlo.*;
import test.Scalability.section3.raytracer.*;
import test.Scalability.section3.search.*;

import test.Scalability.jgfutil.*;

public class JGFAllSizeA{

  public static void main(String argv[]){
   
    int size = 0; 

    JGFInstrumentor.printHeader(3,size);

    JGFEulerBench eb = new JGFEulerBench(); 
    eb.JGFrun(size);

    JGFMolDynBench mdb = new JGFMolDynBench();
    mdb.JGFrun(size);

    JGFMonteCarloBench mcb = new JGFMonteCarloBench();
    mcb.JGFrun(size);

    JGFRayTracerBench rtb = new JGFRayTracerBench();
    rtb.JGFrun(size);

    JGFSearchBench sb = new JGFSearchBench();
    sb.JGFrun(size);

  }
}
