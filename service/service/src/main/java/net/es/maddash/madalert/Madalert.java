/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author carcassi
 */
public class Madalert {
    public static final String INFRASTRUCTURE = "INFRASTRUCTURE";
    public static final String ACTUAL = "ACTUAL";
    
    
    public static Rule defaultRule() {
        return matchFirst(
                          rule(forAllSites(), matchStatus(3), new Problem("Grid is down", 3, INFRASTRUCTURE)),
                          rule(forAllSites(), matchStatus(0), new Problem("No issues found", 0, INFRASTRUCTURE)),
                          forEachSite(
                                      matchFirst(
                                                 rule(forSite(), matchStatus(3), new Problem("Site is down", 3, INFRASTRUCTURE)),
                                                 matchAll(
                                                          matchFirst(
                                                                     rule(forInitiatedBySite(), matchStatus(3), new Problem("Site can't test", 3, INFRASTRUCTURE)),
                                                                     rule(forInitiatedBySite(), matchStatus(3, 0.7), new Problem("Site mostly can't test", 3, INFRASTRUCTURE))),
                                                          matchFirst(
                                                                     rule(forInitiatedOnSite(), matchStatus(3), new Problem("Site can't be tested", 3, INFRASTRUCTURE)),
                                                                     rule(forInitiatedOnSite(), matchStatus(3, 0.7), new Problem("Site mostly can't be tested", 3, INFRASTRUCTURE))),
                                                          rule(forSiteOutgoing(), matchStatus(new double[]{0.0, 0.5, 1.0, -1.0}, 0.7), new Problem("Outgoing test failures", 2, ACTUAL)),
                                                          rule(forSiteIncoming(), matchStatus(new double[]{0.0, 0.5, 1.0, -1.0}, 0.7), new Problem("Incoming test failures", 2, ACTUAL))))));
//    matchfirst
//        * all down
//        * all fine
//        * for each site
//            * matchfirst
//                * Site is down
//                * match all
//                    * matchfirst
//                        * site can't test
//                        * site mostly can't test
//                    * matchfirst 
//                        * site can't be tested
//                        * site can't mostly be tested
//                    * outgoing tests failure
//                    * incoming tests failure
        
    }
    
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
    
    public static SiteRule rule(final SiteTestSet siteTestSet, final StatusMatcher matcher, final Problem problem) {
        return new SiteRule() {

            @Override
            Rule site(int site) {
                return new Rule() {

                    @Override
                    boolean addToReport(Report report, Mesh mesh) {
                        TestSet testSet = siteTestSet.site(site);
                        if (testSet.match(mesh, matcher)) {
                            report.addProblem(site, problem);
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
            }
            
        };
    }
    
    public static Rule forEachSite(final SiteRule siteRule) {
        return new Rule() {

            @Override
            boolean addToReport(Report report, Mesh mesh) {
                boolean matched = false;
                for (int site = 0; site < mesh.getSites().size(); site++) {
                    Rule rule = siteRule.site(site);
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
    
    public static SiteRule matchFirst(final SiteRule... siteRules) {
        return new SiteRule() {

            @Override
            Rule site(int site) {
                return matchFirst(Arrays.asList(siteRules).stream()
                        .map(s -> s.site(site))
                        .collect(Collectors.toList()));
            }
            
        };
    }
    
    public static Rule matchAll(Rule... rules) {
        return matchAll(Arrays.asList(rules));
    }
    
    public static Rule matchAll(final List<Rule> rules) {
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
    
    public static SiteRule matchAll(final SiteRule... siteRules) {
        return new SiteRule() {

            @Override
            Rule site(int site) {
                return matchAll(Arrays.asList(siteRules).stream()
                        .map(s -> s.site(site))
                        .collect(Collectors.toList()));
            }
            
        };
    }
    
    public static TestSet forAllSites() {
        return new TestSet() {

            @Override
            boolean match(Mesh mesh, StatusMatcher matcher) {
                int nSites = mesh.getSites().size();
                StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
                for (int column = 0; column < nSites; column++) {
                    for (int row = 0; row < nSites; row++) {
                        if (column != row) {
                            instance.match(row, column, Mesh.CellHalf.INITIATED_BY_ROW,
                                    mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW));
                            if (mesh.isSplitCell()) {
                                instance.match(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                        mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN));
                            }
                        }
                    }
                }
                return instance.isMatched();
            }
        };
    }
    
    static StatusMatcher matchStatus(final int status) {
        return new StatusMatcher() {

            @Override
            public StatusMatcher.Instance prepareInstance(Mesh mesh) {
                return new StatusMatcher.Instance() {
            
                    private boolean result = true;

                    @Override
                    public void match(int row, int column, Mesh.CellHalf cellHalf, int matchStatus) {
                        if (status != matchStatus) {
                            result = false;
                        }
                    }

                    @Override
                    public boolean isMatched() {
                        return result;
                    }
                };
            }
        };
    }
    
    static StatusMatcher matchStatus(final int status, final double threshold) {
        return new StatusMatcher() {

            @Override
            public StatusMatcher.Instance prepareInstance(Mesh mesh) {
                return new StatusMatcher.Instance() {
            
                    private double matches = 0.0;
                    private double total = 0.0;

                    @Override
                    public void match(int row, int column, Mesh.CellHalf cellHalf, int matchStatus) {
                        total += 1.0;
                        if (status == matchStatus) {
                            matches += 1.0;
                        }
                    }

                    @Override
                    public boolean isMatched() {
                        return total == 0.0 || (matches / total) >= threshold;
                    }
                };
            }
        };
    }
    
    static StatusMatcher matchStatus(final double[] weights, final double threshold) {
        return new StatusMatcher() {

            @Override
            public StatusMatcher.Instance prepareInstance(Mesh mesh) {
                return new StatusMatcher.Instance() {
            
                    private double matches = 0.0;
                    private double total = 0.0;

                    @Override
                    public void match(int row, int column, Mesh.CellHalf cellHalf, int matchStatus) {
                        double weight = weights[matchStatus];
                        if (weight >= 0.0 && weight <= 1.0) {
                            total += 1.0;
                            matches += weight;
                        }
                    }

                    @Override
                    public boolean isMatched() {
                        return total == 0.0 || (matches / total) >= threshold;
                    }
                };
            }
        };
    }
    
    static SiteTestSet forSite() {
        return new SiteTestSet() {

            @Override
            TestSet site(final int site) {
                return new TestSet() {

                    @Override
                    boolean match(Mesh mesh, StatusMatcher matcher) {
                        int nSites = mesh.getSites().size();
                        StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
                        for (int column = 0; column < nSites; column++) {
                            for (int row = 0; row < nSites; row++) {
                                if (column != row && (column == site || row == site)) {
                                    instance.match(row, column, Mesh.CellHalf.INITIATED_BY_ROW,
                                            mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW));
                                    if (mesh.isSplitCell()) {
                                        instance.match(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                                mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN));
                                    }
                                }
                            }
                        }
                        return instance.isMatched();
                    }
                };
            }
        };
    }
    
    static SiteTestSet forInitiatedBySite() {
        return new SiteTestSet() {

            @Override
            TestSet site(final int site) {
                return new TestSet() {

                    @Override
                    boolean match(Mesh mesh, StatusMatcher matcher) {
                        int nSites = mesh.getSites().size();
                        StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
                        for (int column = 0; column < nSites; column++) {
                            for (int row = 0; row < nSites; row++) {
                                if (column != row) {
                                    if (row == site) {
                                        instance.match(row, column, Mesh.CellHalf.INITIATED_BY_ROW,
                                                mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW));
                                    }
                                    if (column == site) {
                                        if (mesh.isSplitCell()) {
                                            instance.match(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                                    mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN));
                                        }
                                    }
                                }
                            }
                        }
                        return instance.isMatched();
                    }
                };
            }
        };
    }
    
    static SiteTestSet forInitiatedOnSite() {
        return new SiteTestSet() {

            @Override
            TestSet site(final int site) {
                return new TestSet() {

                    @Override
                    boolean match(Mesh mesh, StatusMatcher matcher) {
                        int nSites = mesh.getSites().size();
                        StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
                        for (int column = 0; column < nSites; column++) {
                            for (int row = 0; row < nSites; row++) {
                                if (column != row) {
                                    if (row == site) {
                                        if (mesh.isSplitCell()) {
                                            instance.match(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                                    mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN));
                                        }
                                    }
                                    if (column == site) {
                                        instance.match(row, column, Mesh.CellHalf.INITIATED_BY_ROW,
                                                mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_ROW));
                                    }
                                }
                            }
                        }
                        return instance.isMatched();
                    }
                };
            }
        };
    }
    
    static SiteTestSet forSiteOutgoing() {
        return new SiteTestSet() {

            @Override
            TestSet site(final int site) {
                return new TestSet() {

                    @Override
                    boolean match(Mesh mesh, StatusMatcher matcher) {
                        int nSites = mesh.getSites().size();
                        StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
                        for (int column = 0; column < nSites; column++) {
                            if (column != site) {
                                if (mesh.isSplitCell()) {
                                    instance.match(site, column, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                            mesh.statusFor(site, column, Mesh.CellHalf.INITIATED_BY_COLUMN));
                                }
                                instance.match(site, column, Mesh.CellHalf.INITIATED_BY_ROW,
                                        mesh.statusFor(site, column, Mesh.CellHalf.INITIATED_BY_ROW));
                            }
                        }
                        return instance.isMatched();
                    }
                };
            }
        };
    }
    
    static SiteTestSet forSiteIncoming() {
        return new SiteTestSet() {

            @Override
            TestSet site(final int site) {
                return new TestSet() {

                    @Override
                    boolean match(Mesh mesh, StatusMatcher matcher) {
                        int nSites = mesh.getSites().size();
                        StatusMatcher.Instance instance = matcher.prepareInstance(mesh);
                        for (int row = 0; row < nSites; row++) {
                            if (row != site) {
                                if (mesh.isSplitCell()) {
                                    instance.match(row, site, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                            mesh.statusFor(row, site, Mesh.CellHalf.INITIATED_BY_COLUMN));
                                }
                                instance.match(row, site, Mesh.CellHalf.INITIATED_BY_ROW,
                                        mesh.statusFor(row, site, Mesh.CellHalf.INITIATED_BY_ROW));
                            }
                        }
                        return instance.isMatched();
                    }
                };
            }
        };
    }
}
