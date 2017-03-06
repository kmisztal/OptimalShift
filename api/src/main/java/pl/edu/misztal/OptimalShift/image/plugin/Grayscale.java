package pl.edu.misztal.OptimalShift.image.plugin;


import pl.edu.misztal.OptimalShift.image.Image;

import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;

/**
 * @author Krzysztof
 */
public class Grayscale extends Plugin {

    @Override
    public void process(Image imgIn, Image imgOut) {
        new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
                .filter(imgIn.getBufferedImage(), imgOut.getBufferedImage()
                );
    }


}
