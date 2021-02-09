//package com.javayogi.fractalplay;
package test.slicing.Lindenmayer;

/*
 * Main.java
 *
 * Created on February 21, 2004, 4:16 PM
 */

/**
 *
 * @author  Administrator
 */
public class Lindenmayer {
    
    /** Creates a new instance of Main */
    public Lindenmayer() {
    }
    
    public static void test() {
        ReplacementRule[] rules = new ReplacementRule[2];
        rules[0] = new ReplacementRule("a", "ab");
        rules[1] = new ReplacementRule("b", "b");
        
        Axiom a = new Axiom("ab", rules);
        Axiom b = new Axiom(a.permutate(), rules);
        Axiom c = new Axiom(b.permutate(), rules);
        Axiom d = new Axiom(c.permutate(), rules);
        
/*        System.out.println(a.getStartString());
        System.out.println(b.getStartString());
        System.out.println(c.getStartString());
        System.out.println(d.getStartString());
        System.out.println(d.permutate());*/
    }
    
    /**
     * @param args the command line arguments
     */
/*    public static void main(String[] args) {
        System.out.println("Hello, here we go");
        test();
        
        //new TestFrame();
        
//        new FractalFrame();
    }*/
    
}
