/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.misztal.OptimalShift;

import org.ejml.simple.SimpleMatrix;
import pl.edu.misztal.OptimalShift.image.Image;
import pl.edu.misztal.OptimalShift.image.plugin.Grayscale;
import pl.edu.misztal.OptimalShift.image.plugin.Plugin;
import pl.edu.misztal.OptimalShift.image.ui.ImageFrame;

import java.util.ArrayList;
import java.util.List;

import static pl.edu.misztal.OptimalShift.Main.print;

/**
 *
 * @author Krzysztof
 */
public class MainImageGrayscale {
    final static int DIM = 3;

    final static double size = 1.0;

    public static List<Cluster> read(Image a) throws Cluster.ClusterException {
        final int width = a.getWidth();
        final int height = a.getHeight();

        List<Cluster> X = new ArrayList();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                X.add(new Cluster(size, 1.,
                        x,
                        y,
                        a.getRed(x, y),
                        a.getGreen(x, y),
                        a.getBlue(x, y))
                );
            }
        }

        return X;
    }

    public static List<Cluster> readGrayscale(Image a) throws Cluster.ClusterException {
        final int width = a.getWidth();
        final int height = a.getHeight();

        List<Cluster> X = new ArrayList();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                X.add(new Cluster(size, 1.,
                        x,
                        y,
                        a.getRed(x, y)
                )
                );
            }
        }

        return X;
    }

    public static void main(String[] args) throws Cluster.ClusterException {
        Plugin pl = new Grayscale();

        //wczytanie obrazów
        Image a1 = new Image("a1.png");
        pl.process(a1);
        ImageFrame.display(a1);

        Image a2 = new Image("a2.png");
        pl.process(a2);
        ImageFrame.display(a2);

        //wczytanie punktów z obrazów
        List<Cluster> X = readGrayscale(a1);
        System.out.println("a1 - size: " + X.size());
        List<Cluster> Y = readGrayscale(a2);
        System.out.println("a2 - size: " + Y.size());

        //klastry danych
        Cluster X_cl = new Cluster(DIM);
        X_cl.addAll(X);
        print("X(COV)", X_cl.getCov());
        print("X(MEAN)", X_cl.getMean());

        Cluster Y_cl = new Cluster(DIM);
        Y_cl.addAll(Y);
        print("Y(COV)", Y_cl.getCov());
        print("Y(MEAN)", Y_cl.getMean());

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
                    {0},
                    {1}
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
            SimpleMatrix x = c.getMean().plus(v);
            Y_plus_v.add(new Cluster(size, 1., x.getMatrix().data));
        }

        Cluster Y_cl_plus_v = new Cluster(DIM);
        Y_cl_plus_v.addAll(Y_plus_v);

        print("Y_cl_plus_v(COV)", Y_cl_plus_v.getCov());
        print("Y_cl_plus_v(MEAN)", Y_cl_plus_v.getMean());

        Image ret = new Image(a1.getWidth() + a2.getWidth(), Math.max(a1.getHeight(), a2.getHeight()));

        for (Cluster c : X) {
            double[] s = c.getMean().getMatrix().data;
            ret.setRGB((int) (s[0]),
                    (int) (s[1]),
                    (int) (s[2]),
                    (int) (s[2]),
                    (int) (s[2]));
        }

        for (Cluster c : Y_plus_v) {
            double[] s = c.getMean().getMatrix().data;
            ret.setRGB((int) Math.round(s[0]) + a1.getWidth(),
                    (int) Math.round(s[1]),
                    (int) Math.round(s[2]),
                    (int) Math.round(s[2]),
                    (int) Math.round(s[2]));
        }

        ImageFrame.display(ret);
    }

}
