/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.misztal.OptimalShift;


import pl.edu.misztal.OptimalShift.image.Image;
import pl.edu.misztal.OptimalShift.image.ImageFrame;

/**
 *
 * @author Krzysztof
 */
public class Exp {

    public static void main(String[] args) {
        //wczytanie obrazÄ‚Ĺ‚w
        Image a = new Image("images/map/input.png");
        ImageFrame.display(a);

//        for(int x = 0; x < 334/*a.getWidth()*/; ++x)
//            for(int y = 0; y < a.getHeight(); ++y)
//                if(x % 16 == 0 && y % 16 == 0)
//                    System.out.println(a.getRed(x, y)+","+a.getGreen(x, y)+","+a.getBlue(x, y)+",");
//        for(int x = 334; x < a.getWidth(); ++x)
//            for(int y = 0; y < a.getHeight(); ++y)
//                if(x % 6 == 0 && y % 16 == 0)
//                    System.out.println(a.getRed(x, y)+","+a.getGreen(x, y)+","+a.getBlue(x, y)+",");
        int y = 200;
        for (int x = 0; x < a.getWidth(); ++x) {
            System.out.println(a.getRed(x, y) + "," + a.getGreen(x, y) + "," + a.getBlue(x, y) + ",");            
        }

    }
}
