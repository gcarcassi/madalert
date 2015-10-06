/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

/**
 * A rule that can be applied to a site on the mesh.
 *
 * @author carcassi
 */
public abstract class SiteRule {
    
    abstract Rule site(int site);
    
}
