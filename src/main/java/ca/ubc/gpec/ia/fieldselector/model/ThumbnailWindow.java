/*
 * keep tracks of coordinates relating to a thumbnail window
 */
package ca.ubc.gpec.ia.fieldselector.model;

/**
 *
 * @author samuelc
 */
public class ThumbnailWindow {

    private ViewWindow viewWindow;
    private int width; // actual width of thumbnail window
    private int height; // actual width of thumbnail window
    private float scale; // scale between thumbnail and view window

    public ThumbnailWindow(ViewWindow viewWindow, int width, int height) {
        this.viewWindow = viewWindow;
        this.width = width;
        this.height = height;
        scale = ((float) width) / ((float) viewWindow.getImageWidth());
    }

    /**
     * return thumbnail width
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * return thumbnail height
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * return scale between thumbnail and view window
     *
     * @return
     */
    public float getScale() {
        return scale;
    }

    /**
     * project image (may not be original image) X to real X coordinate to be
     * drawn
     *
     * @param imageX
     * @return
     */
    public int projectRealX(int imageX) {
        //int thumbnailRealBaseX = viewWindow.getWidth() - width;
        int thumbnailRealBaseX = viewWindow.getPanelWidth() - width;
        return Math.round(thumbnailRealBaseX + imageX * scale);
    }

    /**
     * project image (may not be original image) Y to real Y coordinate to be
     * drawn
     *
     * @param imageY
     * @return
     */
    public int projectRealY(int imageY) {
        int thumbnailRealBaseY = 0;
        return Math.round(thumbnailRealBaseY + imageY * scale);
    }
}
