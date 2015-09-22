/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

/**
 * A set of tests on a site
 *
 * @author carcassi
 */
public abstract class SiteTestSet {
    abstract TestSet site(int site);
    
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
                                    instance.match(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                            mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN));
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
                                        instance.match(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN,
                                                mesh.statusFor(row, column, Mesh.CellHalf.INITIATED_BY_COLUMN));
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
}
