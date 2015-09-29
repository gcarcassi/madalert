/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author carcassi
 */
public class Report {
    private final Map<Integer, List<Problem>> siteProblems = new HashMap<>();
    private final List<Problem> globalProblems = new ArrayList<>();
    private final int[] globalStats;
    private final Map<Integer, int[]> siteStats = new HashMap<>();
    private final List<String> sites;
    
    Report(Mesh mesh) {
        sites = mesh.getSites();
        globalStats = new int[mesh.nSeverityLevels()];
        for (int site = 0; site < mesh.getSites().size(); site++) {
            int[] newSiteStats = new int[mesh.nSeverityLevels()];
            for (int column = 0; column < mesh.getSites().size(); column++) {
                if (column != site) {
                    newSiteStats[mesh.statusFor(site, column, Mesh.CellHalf.INITIATED_BY_ROW)]++;
                    newSiteStats[mesh.statusFor(site, column, Mesh.CellHalf.INITIATED_BY_COLUMN)]++;
                }
            }
            for (int row = 0; row < mesh.getSites().size(); row++) {
                if (row != site) {
                    newSiteStats[mesh.statusFor(row, site, Mesh.CellHalf.INITIATED_BY_ROW)]++;
                    newSiteStats[mesh.statusFor(row, site, Mesh.CellHalf.INITIATED_BY_COLUMN)]++;
                    globalStats[mesh.statusFor(row, site, Mesh.CellHalf.INITIATED_BY_ROW)]++;
                    globalStats[mesh.statusFor(row, site, Mesh.CellHalf.INITIATED_BY_COLUMN)]++;
                }
            }
            siteStats.put(site, newSiteStats);
        }
    }
    
    void addProblem(int site, Problem problem) {
        List<Problem> problems = siteProblems.get(site);
        if (problems == null) {
            problems = new ArrayList<>();
            siteProblems.put(site, problems);
        }
        problems.add(problem);
    }
    
    void addGlobalProblem(Problem problem) {
        globalProblems.add(problem);
    }

    public List<Problem> getGlobalProblems() {
        return globalProblems;
    }
    
    public List<Problem> getSiteProblems(int site) {
        return (siteProblems.get(site) == null) ? Collections.emptyList() : siteProblems.get(site);
    }
    
    private static void addStats(JsonObjectBuilder jsonSite, int[] stats) {
        JsonArrayBuilder jsonGlobalStats = Json.createArrayBuilder();
        for (int i = 0; i < stats.length; i++) {
            jsonGlobalStats.add(stats[i]);
        }
        jsonSite.add("stats", jsonGlobalStats);
    }
    
    private static void addProblems(JsonObjectBuilder jsonSite, List<Problem> problems) {
        if (problems != null && !problems.isEmpty()) {
            JsonArrayBuilder globalProblems = Json.createArrayBuilder();
            for (Problem problem : problems) {
                globalProblems.add(Json.createObjectBuilder()
                    .add("name", problem.getName())
                    .add("severity", problem.getSeverity())
                    .add("category", problem.getCategory()));
            }
            jsonSite.add("problems", globalProblems);
        }
    }
    
    public JsonObject toJson() {
        JsonObjectBuilder root = Json.createObjectBuilder();
        JsonObjectBuilder globalSite = Json.createObjectBuilder();
        addStats(globalSite, globalStats);
        // TODO: add global severity
//        globalSite = {"stats": self.stats.total, "severity": self.maxSeverityForSite()}
        addProblems(globalSite, getGlobalProblems());
        root.add("global", globalSite);

        JsonObjectBuilder jsonSites = Json.createObjectBuilder();
        for (int site = 0; site < sites.size(); site++) {
            String siteName = sites.get(site);
            JsonObjectBuilder jsonSite = Json.createObjectBuilder();
            addStats(jsonSite, siteStats.get(site));
            // TODO: add max severity
            addProblems(jsonSite, siteProblems.get(site));
            jsonSites.add(siteName, jsonSite);
        }
        root.add("sites", jsonSites);
        return root.build();
    }
}
