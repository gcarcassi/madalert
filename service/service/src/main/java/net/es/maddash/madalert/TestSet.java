/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

/**
 * A set of tests in the mesh.
 *
 * @author carcassi
 */
public abstract class TestSet {
    
    abstract boolean match(Mesh mesh, StatusMatcher matcher);
    
}
