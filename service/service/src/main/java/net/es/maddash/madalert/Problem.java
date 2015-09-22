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
public class Problem {
    private final String name;
    private final int severity;
    private final String category;

    public Problem(String name, int severity, String category) {
        this.name = name;
        this.severity = severity;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getSeverity() {
        return severity;
    }
    
}
