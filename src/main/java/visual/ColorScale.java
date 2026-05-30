package visual;

import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 * Produces a 1-D colour-scale image used as a contour texture.
 *
 * <p>The colour ramp runs Magenta → Blue → Cyan → Green → Yellow → Red
 * across {@link VisData#textureSize} horizontal pixels.
 * Each of the {@link VisData#nContours} contour bands is painted as a
 * uniform block of pixels.</p>
 */
class ColorScale {

    /**
     * Returns a {@link WritableImage} of size
     * {@code textureSize × 1} containing the colour-scale ramp.
     * The image is used as the diffuse map of the surface
     * {@link javafx.scene.paint.PhongMaterial}.
     *
     * @return colour-scale image
     */
    WritableImage getImage() {

        int size = VisData.textureSize;
        WritableImage img = new WritableImage(size, 1);
        PixelWriter pw = img.getPixelWriter();

        double delta = (double) size / VisData.nContours;
        int n2 = 0;
        for (int i = 0; i < VisData.nContours; i++) {
            int n1 = n2;
            n2 = (int) ((i + 1) * delta + 0.5);
            int argb = computeScaleColor(1.0 / VisData.nContours * (i + 0.5));
            int r = (argb >> 16) & 0xFF;
            int g = (argb >>  8) & 0xFF;
            int b =  argb        & 0xFF;
            Color c = Color.rgb(r, g, b);
            for (int j = n1; j < n2; j++) {
                pw.setColor(j, 0, c);
            }
        }
        return img;
    }

    /**
     * Computes the ARGB colour for a normalised position {@code v} in
     * the colour ramp.
     *
     * <p>Ramp knots (v → RGB):
     * 0.0 = (1,0,1) magenta; 0.2 = (0,0,1) blue;
     * 0.4 = (0,1,1) cyan;    0.6 = (0,1,0) green;
     * 0.8 = (1,1,0) yellow;  1.0 = (1,0,0) red.</p>
     *
     * @param v normalised position in [0, 1]
     * @return packed ARGB integer (alpha = 0xFF)
     */
    static int computeScaleColor(double v) {

        double R, G, B;
        if (v < 0.2) {
            R = 1 - 5 * v;
            G = 0;
            B = 1;
        } else if (v < 0.4) {
            R = 0;
            G = 5 * (v - 0.2);
            B = 1;
        } else if (v < 0.6) {
            R = 0;
            G = 1;
            B = 1 - 5 * (v - 0.4);
        } else if (v < 0.8) {
            R = 5 * (v - 0.6);
            G = 1;
            B = 0;
        } else {
            R = 1;
            G = 1 - 5 * (v - 0.8);
            B = 0;
        }
        int iR = (int) (R * 255 + 0.5);
        int iG = (int) (G * 255 + 0.5);
        int iB = (int) (B * 255 + 0.5);
        return (0xFF << 24) | (iR << 16) | (iG << 8) | iB;
    }
}
