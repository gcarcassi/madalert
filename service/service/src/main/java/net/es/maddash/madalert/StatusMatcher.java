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
public abstract class StatusMatcher {
    abstract class Instance {
        public abstract void match(int row, int column, Mesh.CellHalf cellHalf, int status);
        public abstract boolean isMatched();
    }
    
    abstract Instance prepareInstance(Mesh mesh);
    
    static StatusMatcher matchStatus(final int status) {
        return new StatusMatcher() {

            @Override
            public Instance prepareInstance(Mesh mesh) {
                return new Instance() {
            
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
            public Instance prepareInstance(Mesh mesh) {
                return new Instance() {
            
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
            public Instance prepareInstance(Mesh mesh) {
                return new Instance() {
            
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
}
