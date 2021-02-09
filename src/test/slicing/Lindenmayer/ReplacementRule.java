//package com.javayogi.fractalplay;
package test.slicing.Lindenmayer;
/*
 * ReplacementRule.java
 *
 * Created on February 21, 2004, 4:29 PM
 */

/**
 *
 * @author  Administrator
 */
public class ReplacementRule{// implements IPatternRule  {
    private String _original, _replacement;
    
    /** Creates a new instance of ReplacementRule */
    public ReplacementRule(String original, String replacement){
        _original = original;
        _replacement = replacement;
    }
        
    public boolean test(char c) {
        return (_original.length() == 1)
            && (_original.charAt(0) == c);
    }

    public String apply(char c) {
        if (test(c)) {
            return _replacement;
        } else {
            return "" + _replacement;
        }
    }
    
    public String getBefore() {
        return _original;
    }
    
    public String getAfter() {
        return _replacement;
    }
}
