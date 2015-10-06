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
public abstract class Rule {
    
    public Report createReport(Mesh mesh) {
        Report report = new Report(mesh);
        addToReport(report, mesh);
        return report;
    }

    abstract boolean addToReport(Report report, Mesh mesh);
    
}
