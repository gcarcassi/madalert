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
        Report report = new Report(mesh);
        addToReport(report, mesh);
        return report;
    }

    abstract boolean addToReport(Report report, Mesh mesh);
    
    public static Rule rule(final TestSet testSet, final StatusMatcher matcher, final Problem problem) {
        return new Rule() {

            @Override
            boolean addToReport(Report report, Mesh mesh) {
                if (testSet.match(mesh, matcher)) {
                    report.addGlobalProblem(problem);
                    return true;
                } else {
                    return false;
                }
            }
            
        };
    }
    
    public static Rule siteRule(final SiteTestSet siteTestSet, final StatusMatcher matcher, final Problem problem) {
        return new Rule() {

            @Override
            boolean addToReport(Report report, Mesh mesh) {
                boolean matched = false;
                for (int site = 0; site < mesh.getSites().size(); site++) {
                    TestSet testSet = siteTestSet.site(site);
                    if (testSet.match(mesh, matcher)) {
                        report.addProblem(site, problem);
                        matched = true;
                    }
                }
                return matched;
            }
        };
    }
    
    public static Rule runAll(Rule... rules) {
        return runAll(Arrays.asList(rules));
    }
    
    public static Rule runAll(final List<Rule> rules) {
        return new Rule() {

            @Override
            boolean addToReport(Report report, Mesh mesh) {
                boolean matched = false;
                for (Rule rule : rules) {
                    matched = rule.addToReport(report, mesh) || matched;
                }
                return matched;
            }
        };
    }
    
    public static Rule matchFirst(Rule... rules) {
        return matchFirst(Arrays.asList(rules));
    }
    
    public static Rule matchFirst(final List<Rule> rules) {
        return new Rule() {

            @Override
            boolean addToReport(Report report, Mesh mesh) {
                for (Rule rule : rules) {
                    if (rule.addToReport(report, mesh)) {
                        return true;
                    }
                }
                return false;
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
