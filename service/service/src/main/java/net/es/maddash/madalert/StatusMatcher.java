/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

/**
 * A set of conditions that can be verified on a set of test results.
 *
 * @author carcassi
 */
public abstract class StatusMatcher {
    abstract class Instance {
        public abstract void match(int row, int column, Mesh.CellHalf cellHalf, int status);
        public abstract boolean isMatched();
    }
    
    abstract Instance prepareInstance(Mesh mesh);
}
