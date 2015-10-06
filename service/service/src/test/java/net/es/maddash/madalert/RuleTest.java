/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.Arrays;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;
import static net.es.maddash.madalert.Madalert.*;

/**
 *
 * @author carcassi
 */
public class RuleTest {

    @Test
    public void allSites() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("allWell.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            TestSet testSet = forAllSites();
            assertThat(testSet.match(mesh, matchStatus(0)), equalTo(true));
            assertThat(testSet.match(mesh, matchStatus(3)), equalTo(false));
        }
    }

    @Test
    public void site3Down() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("site3Down.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            assertThat(Rule.forSite().site(3).match(mesh, matchStatus(3)), equalTo(true));
            assertThat(Rule.forSite().site(2).match(mesh, matchStatus(3)), equalTo(false));
        }
    }

    @Test
    public void site2CantTest() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("site2CantTest.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            assertThat(Rule.forInitiatedBySite().site(2).match(mesh, matchStatus(3)), equalTo(true));
            assertThat(Rule.forInitiatedOnSite().site(2).match(mesh, matchStatus(3)), equalTo(false));
        }
    }

    @Test
    public void allSitesRules1() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("allWell.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            Problem problem = new Problem("Grid is down", 3, "INFRASTRUCTURE");
            Report report = Rule.rule(forAllSites(), matchStatus(3), problem).createReport(mesh);
            
            assertThat(report.getGlobalProblems().isEmpty(), equalTo(true));
            for (int i = 0; i < mesh.getSites().size(); i++) {
                assertThat(report.getSiteProblems(i).isEmpty(), equalTo(true));
            }
        }
    }

    @Test
    public void allSitesRules2() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("allMissing.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            Problem problem = new Problem("Grid is down", 3, "INFRASTRUCTURE");
            Report report = Rule.rule(forAllSites(), matchStatus(3), problem).createReport(mesh);
            
            assertThat(report.getGlobalProblems(), equalTo(Arrays.asList(problem)));
            for (int i = 0; i < mesh.getSites().size(); i++) {
                assertThat(report.getSiteProblems(i).isEmpty(), equalTo(true));
            }
        }
    }

    @Test
    public void sitesRule1() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("allMissing.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            Problem problem = new Problem("Site is down", 3, "INFRASTRUCTURE");
            Report report = Rule.siteRule(forSite(), matchStatus(3), problem).createReport(mesh);
            
            assertThat(report.getGlobalProblems().isEmpty(), equalTo(true));
            for (int site = 0; site < mesh.getSites().size(); site++) {
                assertThat(report.getSiteProblems(site), equalTo(Arrays.asList(problem)));
            }
        }
    }

    @Test
    public void sitesRule2() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("site3Down.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            Problem problem = new Problem("Site is down", 3, "INFRASTRUCTURE");
            Report report = Rule.siteRule(forSite(), matchStatus(3), problem).createReport(mesh);
            
            assertThat(report.getGlobalProblems().isEmpty(), equalTo(true));
            for (int site = 0; site < mesh.getSites().size(); site++) {
                if (site == 3) {
                    assertThat(report.getSiteProblems(site), equalTo(Arrays.asList(problem)));
                } else {
                    assertThat(report.getSiteProblems(site).isEmpty(), equalTo(true));
                }
            }
        }
    }
    
    @Test
    public void runAllRule1() {
        try (JsonReader reader = Json.createReader(getClass().getResourceAsStream("allMissing.json"))) {
            Mesh mesh = Mesh.from(reader.readObject());
            Problem globalProblem = new Problem("Mesh is down", 3, "INFRASTRUCTURE");
            Problem siteProblem = new Problem("Site is down", 3, "INFRASTRUCTURE");
            Report report = Rule.runAll(rule(forAllSites(), matchStatus(3), globalProblem),
                                   Rule.siteRule(forSite(), matchStatus(3), siteProblem)
                                  ).createReport(mesh);
            
            assertThat(report.getGlobalProblems(), equalTo(Arrays.asList(globalProblem)));
            for (int site = 0; site < mesh.getSites().size(); site++) {
                assertThat(report.getSiteProblems(site), equalTo(Arrays.asList(siteProblem)));
            }
        }
    }
    
    

}
