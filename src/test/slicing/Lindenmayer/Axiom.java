//package com.javayogi.fractalplay;
package test.slicing.Lindenmayer;
//import java.util.*;

public class Axiom {
    private String _start;
    private ReplacementRule[] _rules;
//    private ArrayList _cached= new ArrayList();
	private String[] _cached=new String[20];
	private int cache_rng=0;
    
    /** Creates a new instance of Axiom */
    public Axiom(String start, ReplacementRule[] rules) {
        _start = start;
        _rules = rules;
//        _cached.add(_start);
		_cached[cache_rng]=_start;
		cache_rng++;
    }
    
    public String permutate() {
        StringBuffer result = new StringBuffer();
        
        nextChar: for (int i = 0; i < _start.length(); i++) {
            for (int j = 0; j < _rules.length; j++) {
                if (_rules[j].test(_start.charAt(i))) {
                    result.append(_rules[j].apply(_start.charAt(i)));
                    continue nextChar;
                }
            }
            result.append(_start.charAt(i));
        }
            
        return result.toString();
    }
/*
    public String permutate(int reps) {
//        for (int i = _cached.size(); i <= reps; i++) {
		for (int i = cache_rng; i <= reps; i++) {
//            Axiom tmp = new Axiom((String) _cached.get(i - 1), _rules);
            Axiom tmp = new Axiom((String) _cached[i-1], _rules);
//            _cached.add(tmp.permutate());
			_cached[cache_rng]=tmp.permutate();
			cache_rng++;
        }
//        return (String) _cached.get(reps);
		return (String) _cached[reps];
    }

    public String getStartString() {
        return _start;
    }
    */
    
}