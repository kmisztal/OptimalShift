package pl.edu.misztal.OptimalShift.image.plugin;


import pl.edu.misztal.OptimalShift.image.Image;

/**
 * @author Krzysztof
 */
public abstract class Plugin {


    /**
     * Executes the algorithm.
     *
     * @param imgIn  input image.
     * @param imgOut output image
     */
    public abstract void process(
            Image imgIn,
            Image imgOut
    );

    /**
     * Executes the algorithm.
     *
     * @param imgInAndOut input and output image.
     */
    public final void process(
            Image imgInAndOut
    ) {
        process(imgInAndOut, imgInAndOut);
    }

    public String getName() {
        String[] name = this.getClass().getName().split("\\.");
        return name[name.length - 1];
    }

}
