/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author carcassi
 */
public abstract class Rule {
    
    public Report createReport(Mesh mesh) {
        Report report = new Report();
        addToReport(report, mesh);
        return report;
    }

    abstract void addToReport(Report report, Mesh mesh);
    
    public static Rule rule(final TestSet testSet, final StatusMatcher matcher, final Problem problem) {
        return new Rule() {

            @Override
            void addToReport(Report report, Mesh mesh) {
                if (testSet.match(mesh, matcher)) {
                    report.addGlobalProblem(problem);
                }
            }
            
        };
    }
    
    public static Rule siteRule(final SiteTestSet siteTestSet, final StatusMatcher matcher, final Problem problem) {
        return new Rule() {

            @Override
            void addToReport(Report report, Mesh mesh) {
                for (int site = 0; site < mesh.getSites().size(); site++) {
                    TestSet testSet = siteTestSet.site(site);
                    if (testSet.match(mesh, matcher)) {
                        report.addProblem(site, problem);
                    }
                }
            }
        };
    }
    
    public static Rule runAll(Rule... rules) {
        return runAll(Arrays.asList(rules));
    }
    
    public static Rule runAll(final List<Rule> rules) {
        return new Rule() {

            @Override
            void addToReport(Report report, Mesh mesh) {
                for (Rule rule : rules) {
                    rule.addToReport(report, mesh);
                }
            }
        };
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
    
    public static TestSet forAllSites() {
        return TestSet.forAllSites();
    }
    
    public static SiteTestSet forSite() {
        return SiteTestSet.forSite();
    }
    
    public static SiteTestSet forInitiatedBySite() {
        return SiteTestSet.forInitiatedBySite();
    }
    
    public static SiteTestSet forInitiatedOnSite() {
        return SiteTestSet.forInitiatedOnSite();
    }
}
