package pl.edu.misztal.OptimalShift;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.ejml.simple.SimpleMatrix;
import pl.edu.misztal.OptimalShift.image.Image;
import pl.edu.misztal.OptimalShift.image.ImageFrame;

import static pl.edu.misztal.OptimalShift.Main.print;

/**
 *
 * @author Krzysztof
 */
public class MainImageLabels {

    final static int DIM = 6;
    final static double size = 1.0;

    private static List<Cluster> read(Image a, Image labels, boolean first) throws Cluster.ClusterException {
        final int width = a.getWidth();
        final int height = a.getHeight();
        
        int min_col = 255;
        int max_col = 0;
        outer:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int v = labels.getBlue(x, y);
                if(min_col > v){
                    min_col = v;
                    continue;
                }
                if(max_col < v){
                    max_col = v;
                    continue;
                }
            }
        }
        System.out.println("labels: " + min_col + " " + max_col);

        List<Cluster> ret = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if((first && labels.getRed(x, y) == max_col)
                        ||(!first && labels.getRed(x, y) == min_col))
                ret.add(new Cluster(size, 1.,
                        x,
                        y,
                        a.getRed(x, y),
                        a.getGreen(x, y),
                        a.getBlue(x, y),
                        (a.getRGB(x, y) >> 24) & 0xff)
                );
            }
        }

        return ret;
    }

    private static Image combine(List<Cluster> X, List<Cluster> Y, int X_width, int X_height, int Y_width, int Y_height) {
        Image ret = new Image(X_width,X_height);

        X.stream().map((c) -> c.getMean().getMatrix().data).forEach((s) -> {
            ret.setRGB(
                    (int) Math.round(s[0]),
                    (int) Math.round(s[1]),
                    new Color(c(s[2]),
                            c(s[3]),
                            c(s[4]),
                            c(s[5])).getRGB());
        });

        Y.stream().map((c) -> c.getMean().getMatrix().data).forEach((s) -> {
//            System.out.println((int) Math.round(s[0])+Y_width + " "+
//                    (int) Math.round(s[1]));
            ret.setRGB(
                    (int) Math.round(s[0]),
                    (int) Math.round(s[1]),
                    new Color(c(s[2]),
                            c(s[3]),
                            c(s[4]),
                            c(s[5])).getRGB());
        });

        return ret;
    }

    public static void main(String[] args) throws Cluster.ClusterException {
        
//        String dir = "d:\\Dropbox\\article\\optimal-shift\\OptimalShift\\images\\p11_1\\";
        String dir = "d:\\Dropbox\\article\\optimal-shift\\OptimalShift\\images\\Phos2_scene7\\noise\\";
        

        //wczytanie obrazÄ‚Ĺ‚w
        Image a1 = new Image(dir+"input.png");
        ImageFrame.display(a1);

        Image a2 = new Image(dir+"labels.png");
        ImageFrame.display(a2);

        //wczytanie punktÄ‚Ĺ‚w z obrazÄ‚Ĺ‚w
        List<Cluster> X = read(a1, a2, true);
        System.out.println("a1 - size: " + X.size());
        List<Cluster> Y = read(a1, a2, false);
        System.out.println("a2 - size: " + Y.size());

        //originalne zdjĂ„â„˘cie
        ImageFrame.display(combine(X, Y, a1.getWidth(), a2.getHeight(), a2.getWidth(), a2.getHeight()));

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
                    {0, 0, 0},
                    {0, 0, 0},
                    {1, 0, 0},
                    {0, 1, 0},
                    {0, 0, 1},
                    {0, 0, 0}
//                    {0},
//                    {0},
//                    {1},
//                    {1},
//                    {1},
//                    {0}
                }
        );
        print("V_base", V_base);

        SimpleMatrix V_ort_base = Util.orthonormalizeColumns(A, V_base);
        print("V_ort_base", V_ort_base);

        SimpleMatrix w = X_cl.getMean().minus(Y_cl.getMean());
        print("w", w);

        SimpleMatrix v = Util.projection(A, V_ort_base, w);
        print("v", v);

        List<Cluster> Y_plus_v = new ArrayList<>();
        for (Cluster c : Y) {
            SimpleMatrix x = c.getMean().plus(v);
            Y_plus_v.add(new Cluster(size, 1., (x.getMatrix().data)));
        }

        Cluster Y_cl_plus_v = new Cluster(DIM);
        Y_cl_plus_v.addAll(Y_plus_v);

        print("Y_cl_plus_v(COV)", Y_cl_plus_v.getCov());
        print("Y_cl_plus_v(MEAN)", Y_cl_plus_v.getMean());

        //originalne zdjĂ„â„˘cie
        ImageFrame.display(combine(X, Y_plus_v, a1.getWidth(), a1.getHeight(), a2.getWidth(), a2.getHeight()));
    }

    private static int c(double v) {
        return (int) Math.max(0, Math.min(v, 255));
    }

}

