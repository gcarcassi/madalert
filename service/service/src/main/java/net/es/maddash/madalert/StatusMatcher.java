/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.es.maddash.madalert;

import net.es.maddash.madalert.Mesh;

/**
 *
 * @author carcassi
 */
abstract class StatusMatcher {
    public abstract class Instance {
        public abstract void match(int row, int column, Mesh.CellHalf cellHalf, int status);
        public abstract boolean isMatched();
    }
    
    public abstract Instance prepareInstance(Mesh mesh);
    
    public static StatusMatcher matchStatus(final int status) {
        return new StatusMatcher() {
            
            private boolean result = true;

            @Override
            public Instance prepareInstance(Mesh mesh) {
                return new Instance() {

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
    
    public static StatusMatcher matchStatus(final int status, final double threshold) {
        return new StatusMatcher() {
            
            private double matches = 0.0;
            private double total = 0.0;

            @Override
            public Instance prepareInstance(Mesh mesh) {
                return new Instance() {

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
    
    public static StatusMatcher matchStatus(final double[] weights, final double threshold) {
        return new StatusMatcher() {
            
            private double matches = 0.0;
            private double total = 0.0;

            @Override
            public Instance prepareInstance(Mesh mesh) {
                return new Instance() {

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
}
