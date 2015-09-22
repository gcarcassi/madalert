/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

/**
 *
 * @author carcassi
 */
public class Rule {
    public static Rule rule (TestSet testSet, StatusMatcher matcher, Problem problem) {
        return null;
    }
    
    public static StatusMatcher matchStatus(int status) {
        return StatusMatcher.matchStatus(status);
    }
    
    public static StatusMatcher matchStatus(int status, double threshold) {
        return StatusMatcher.matchStatus(status, threshold);
    }
    
    public static StatusMatcher matchStatus(double[] weights, double threshold) {
        return StatusMatcher.matchStatus(weights, threshold);
    }
}
