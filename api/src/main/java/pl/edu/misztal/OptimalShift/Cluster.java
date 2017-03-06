/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.misztal.OptimalShift;

import java.util.List;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author chris_pi
 */
public class Cluster {

    /**
     * dimension of tha data
     */
    private final int DIM;
    /**
     * mean of the cluster
     */
    private SimpleMatrix mean;
    /**
     * weight of the cluster
     */
    private double weight;
    /**
     * cardinality of the cluster
     */
    private int cardinality;
    /**
     * covaraince of the cluster
     */
    private SimpleMatrix cov;

    public Cluster(double size, double weight, double... coordinates) throws ClusterException {
        DIM = coordinates.length;
        mean = new SimpleMatrix(DIM, 1);
        if (size <= 0) {
            throw new ClusterException("Atomic size error");
        }
        for (int i = 0; i < DIM; ++i) {
            mean.set(i, 0, coordinates[i]);
        }
        this.weight = weight;
        this.cardinality = 1;

        cov = new SimpleMatrix(DIM, DIM);
        for (int i = 0; i < DIM; ++i) {
            cov.set(i, i, Math.pow(size, DIM + 2) / 12.0);
        }
    }

    public Cluster(int dimension) {
        DIM = dimension;
        mean = new SimpleMatrix(DIM, 1);

        for (int i = 0; i < DIM; ++i) {
            mean.set(i, 0, 0);
        }
        this.weight = 0;
        this.cardinality = 0;

        cov = new SimpleMatrix(DIM, DIM);
        for (int i = 0; i < DIM; ++i) {
            cov.set(i, i, 0);
        }
    }

    public Cluster addTo(Cluster B) {//A=A+B        
        final double weightSum = this.getWeight() + B.getWeight();

        final double pA = this.getWeight() / weightSum;
        final double pB = B.getWeight() / weightSum;
        this.setWeight(weightSum);

        this.setCov(this.getCov().scale(pA).plus(pB, B.getCov()).plus(this.getMean().minus(B.getMean()).scale(pA * pB).mult(this.getMean().minus(B.getMean()).transpose())));
        this.setMean(this.getMean().scale(pA).plus(pB, B.getMean()));
        this.setCardinality(this.getCardinality() + B.getCardinality());

        return this;
    }
    
    public Cluster addAll(List<Cluster> d){
        d.stream().forEach((p) -> addTo(p));
        return this;
    }

    private double getWeight() {
        return this.weight;
    }

    private void setWeight(double val) {
        this.weight = val;
    }

    public SimpleMatrix getMean() {
        return mean;
    }

    private void setMean(SimpleMatrix mean_) {
        this.mean = mean_;
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(int val) {
        this.cardinality = val;
    }

    public SimpleMatrix getCov() {
        return cov;
    }

    private void setCov(SimpleMatrix cov_) {
        this.cov = cov_;
    }

    public static class ClusterException extends Exception {

        public ClusterException(String s) {
            super(s);
        }
    }

}
