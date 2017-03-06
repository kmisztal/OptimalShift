/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.misztal.OptimalShift;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

import org.ejml.simple.SimpleMatrix;

/**
 * @author chris_pi
 */
public class Main {

    static void print(String title, SimpleMatrix a) {
        StringBuilder sb = new StringBuilder("[");
        for (int x = 0; x < a.numRows(); ++x) {
            for (int y = 0; y < a.numCols(); ++y) {
                sb.append(String.format(Locale.ENGLISH, "%4f", a.get(x, y)));
                if (y != a.numCols() - 1) {
                    sb.append(",");
                }
            }
            if (x != a.numRows() - 1) {
                sb.append(";");
            }
        }
        sb.append("]");
        System.out.println(title + "=" + sb.toString());
    }

    public static void main(String[] args) throws Cluster.ClusterException {
        final double size = 0.01;

        Function<Double, Double> f1 = (x) -> -x + 2;
        Function<Double, Double> f2 = (x) -> -x - 2;

        List<Cluster> X = new ArrayList();
        Random rand = new Random(23);
        for (int i = 0; i < 100; i++) {
            final double x = rand.nextDouble() - 1;
            X.add(new Cluster(size, 1., x, f1.apply(x) + rand.nextDouble() / 10.));
        }

        Cluster X_cl = new Cluster(2);
        X_cl.addAll(X);
        print("X(COV)", X_cl.getCov());
        print("X(MEAN)", X_cl.getMean());

        List<Cluster> Y = new ArrayList();
        for (int i = 0; i < 100; i++) {
            final double x = rand.nextDouble();
            Y.add(new Cluster(size, 1., x, f2.apply(x) + rand.nextDouble() / 10.));
        }

        Cluster Y_cl = new Cluster(2);
        Y_cl.addAll(Y);
        print("Y(COV)", Y_cl.getCov());
        print("Y(MEAN)", Y_cl.getMean());

        new LinePlotTest(X, Y).setVisible(true);

        ////////////////////////////////////////////////////////////////////////
        int card = X_cl.getCardinality() + Y_cl.getCardinality();

        double p_X = 1. * X_cl.getCardinality() / card;
        double p_Y = 1. * Y_cl.getCardinality() / card;

        System.out.println("p_X=" + p_X);
        System.out.println("p_Y=" + p_Y);

        SimpleMatrix A = X_cl.getCov().scale(p_X).plus(Y_cl.getCov().scale(p_Y));
        print("A", A);

        SimpleMatrix V_base = new SimpleMatrix(
                new double[][]{
                        {0},
                        {1}
                        // lub
                        //{0,1},
                        //{1,0}
                }
        );
        print("V_base", V_base);

        SimpleMatrix V_ort_base = Util.orthonormalizeColumns(A, V_base);
        print("V_ort_base", V_ort_base);

        SimpleMatrix w = X_cl.getMean().minus(Y_cl.getMean());
        print("w", w);

        SimpleMatrix v = Util.projection(A, V_ort_base, w);
        print("v", v);


        List<Cluster> Y_plus_v = new ArrayList();
        for (Cluster c : Y) {
            SimpleMatrix mean = c.getMean();
            final double x = mean.get(0, 0);
            final double y = mean.get(1, 0);
            Y_plus_v.add(new Cluster(size, 1., x + v.get(0, 0), y + v.get(1, 0)));
        }

        Cluster Y_cl_plus_v = new Cluster(2);
        Y_cl.addAll(Y_plus_v);

        new LinePlotTest(X, Y_plus_v).setVisible(true);


        for (Cluster c : X) {
            System.out.println(c.getMean().get(0, 0) + "," + c.getMean().get(1, 0) + ",");
        }
    }
}
