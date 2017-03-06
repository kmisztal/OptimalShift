/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.misztal.OptimalShift;

import pl.edu.misztal.OptimalShift.image.Image;
import pl.edu.misztal.OptimalShift.image.ImageFrame;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Krzysztof
 */
public class PrepareExperiment {

    public static void main(String[] args) {
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_minus_1.png", "images/Phos2_scene7/inputs_minus_1");
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_minus_2.png", "images/Phos2_scene7/inputs_minus_2");
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_minus_3.png", "images/Phos2_scene7/inputs_minus_3");
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_minus_4.png", "images/Phos2_scene7/inputs_minus_4");
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_plus_1.png", "images/Phos2_scene7/inputs_plus_1");
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_plus_2.png", "images/Phos2_scene7/inputs_plus_2");
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_plus_3.png", "images/Phos2_scene7/inputs_plus_3");
//        one(0.5, "images/Phos2_scene7/small/Phos2_uni_sc7_0.png", "images/Phos2_scene7/small/Phos2_uni_sc7_plus_4.png", "images/Phos2_scene7/inputs_plus_4");
        
        one(0.5, "images/Phos2_scene7/noise/Phos2_uni_sc7_0.png", "images/Phos2_scene7/noise/Phos2_uni_sc7_2.png", "images/Phos2_scene7/noise");
    }

    private static void one(double prop, String in1, String in2, String out) {
        Path path = Paths.get(out);

        // if the sub-directory doesn't exist then create it
        if (Files.notExists(path)) {
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Image a1 = new Image(in1);
        ImageFrame.display(a1);
        a1.save(out + "/left.png");

        Image a2 = new Image(in2);
        ImageFrame.display(a2);
        a2.save(out + "/right.png");

        Image comb = combine(a1, a2, prop);
        ImageFrame.display(comb);
        comb.save(out + "/input.png");

        Image label = labels(a1, a2, prop);
        ImageFrame.display(label);
        label.save(out + "/labels.png");

        Image[] r = parts(a1, a2, prop);
        ImageFrame.display(r[0]);
        ImageFrame.display(r[1]);
        r[0].save(out + "/left_part.png");
        r[1].save(out + "/right_part.png");

    }

    private static Image[] parts(Image in1, Image in2, double prop) {
        if (in1.getWidth() > in2.getWidth()) {
            throw new RuntimeException("Second image is to small");
        }
        int limit = (int) (prop * in1.getWidth());
        Image ret1 = new Image(limit, in1.getHeight());

        for (int x = 0; x < limit; ++x) {
            for (int y = 0; y < ret1.getHeight(); ++y) {
                ret1.setRGB(x, y, in1.getRGB(x, y));
            }
        }

        Image ret2 = new Image(in1.getWidth() - limit, in1.getHeight());
        for (int x = limit; x < in1.getWidth(); ++x) {
            for (int y = 0; y < ret2.getHeight(); ++y) {
                ret2.setRGB(x - limit, y, in2.getRGB(x, y));
            }
        }

        return new Image[]{ret1, ret2};
    }

    private static Image combine(Image in1, Image in2, double prop) {
        if (in1.getWidth() > in2.getWidth()) {
            throw new RuntimeException("Second image is to small");
        }
        Image ret = new Image(in1.getWidth(), in1.getHeight());

        int limit = (int) (prop * in1.getWidth());
        for (int x = 0; x < limit; ++x) {
            for (int y = 0; y < ret.getHeight(); ++y) {
                ret.setRGB(x, y, in1.getRGB(x, y));
            }
        }

        for (int x = limit; x < ret.getWidth(); ++x) {
            for (int y = 0; y < ret.getHeight(); ++y) {
                ret.setRGB(x, y, in2.getRGB(x, y));
            }
        }

        return ret;
    }

    private static Image labels(Image in1, Image in2, double prop) {
        if (in1.getWidth() > in2.getWidth()) {
            throw new RuntimeException("Second image is to small");
        }
        Image ret = new Image(in1.getWidth(), in1.getHeight());

        int limit = (int) (prop * in1.getWidth());
        for (int x = 0; x < limit; ++x) {
            for (int y = 0; y < ret.getHeight(); ++y) {
                ret.setRGB(x, y, Color.GRAY.getRGB());
            }
        }

        for (int x = limit; x < ret.getWidth(); ++x) {
            for (int y = 0; y < ret.getHeight(); ++y) {
                ret.setRGB(x, y, Color.DARK_GRAY.getRGB());
            }
        }

        return ret;
    }

}
