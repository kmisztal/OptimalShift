/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.misztal.OptimalShift;

import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author chris_pi
 */
public class Util {

    private static SimpleMatrix getColumn(SimpleMatrix a, int n) {
        double data[][] = new double[a.numRows()][1];
        for (int i = 0; i < a.numRows(); ++i) {
            data[i][0] = a.get(i, n);
        }
        return new SimpleMatrix(data);
    }

    private static double dot(SimpleMatrix x, SimpleMatrix A, SimpleMatrix y) {
        return x.transpose().mult(A.invert()).mult(y).get(0, 0);
    }

    private static double norm(SimpleMatrix A, SimpleMatrix a) {        
        return Math.sqrt(a.transpose().mult(A.invert()).mult(a).get(0, 0));
    }

    public static SimpleMatrix orthonormalizeColumns(SimpleMatrix A, SimpleMatrix v) {
        SimpleMatrix mx = v.copy();
        int n = mx.numCols();

        for (int c = 0; c < n; c++) {
            SimpleMatrix col = getColumn(mx, c);
            for (int c1 = 0; c1 < c; c1++) {
                SimpleMatrix viewC1 = getColumn(mx, c1);
                col = col.minus(viewC1.scale(dot(viewC1, A, col)));

            }
            final double norm2 = norm(A, col);
            for (int i = 0; i < col.numRows(); ++i) {
                mx.set(i, c, col.get(i, 0) / norm2);
            }
        }
        return mx;
    }
    
    public static SimpleMatrix projection(SimpleMatrix A, SimpleMatrix V_orth_base, SimpleMatrix w) {
        SimpleMatrix mx = new SimpleMatrix(w.numRows(),1);
        int n = V_orth_base.numCols();

        for (int c = 0; c < n; c++) {
            SimpleMatrix col = getColumn(V_orth_base, c);
            col = col.scale(dot(w, A, col));
            mx = mx.plus(col);
        }
        return mx;
    }
}
