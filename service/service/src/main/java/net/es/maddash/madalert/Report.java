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
    
    public String toJson() {
        JsonObjectBuilder root = Json.createObjectBuilder();
        JsonObjectBuilder globalSite = Json.createObjectBuilder();
        // TODO: add global stats
        // TODO: add global severity
//        globalSite = {"stats": self.stats.total, "severity": self.maxSeverityForSite()}
        if (!getGlobalProblems().isEmpty()) {
            JsonArrayBuilder globalProblems = Json.createArrayBuilder();
            for (Problem problem : getGlobalProblems()) {
                globalProblems.add(Json.createObjectBuilder()
                    .add("name", problem.getName())
                    .add("severity", problem.getSeverity())
                    .add("category", problem.getCategory()));
            }
            globalSite.add("problems", globalProblems);
        }
        root.add("global", globalSite);

//        TODO: add sites
//        nSites = len(self.data["columnNames"])
//        for site in range(0, nSites):
//            siteName = self.data["columnNames"][site].replace(" ", "_")
//            newSite = {"stats": self.stats.site[site], "severity": self.maxSeverityForSite(site)}
//            problems = self.siteProblems[site]
//            if (len(problems) > 0):
//                newSiteProblems = []
//                for problem in range(0, len(problems)):
//                    newSiteProblems.append({"name": problems[problem].name, "severity": problems[problem].severity, "category": problems[problem].category})
//                newSite["problems"] = newSiteProblems
//
//            jsonReport["sites"][siteName] = newSite
//        json.dump(jsonReport, out)    }
        return root.build().toString();
    }
}
