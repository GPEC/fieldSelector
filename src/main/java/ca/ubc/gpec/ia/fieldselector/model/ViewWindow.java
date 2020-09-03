/*
 * ViewWindow for zooming
 * 
 * NOTE: there are 3 coordinate system:
 * 1. original image (original x,y)
 * 2. preview/lowres image (x,y)
 * 3. view window (real x,y)
 * 
 */
package ca.ubc.gpec.ia.fieldselector.model;

/**
 *
 * @author Samuel
 */
public class ViewWindow {
    // define x/y coordinates and width height of actual image

    public static float ZOOM_INCREMENT = 0.04f; // amount of magnification change per mouse click
    public static float ZOOM_MIN = 2f; // max zoom-in (bigger, the more zoom)
    private int x; // with respect to the input image ... may not necessarily equal to the original image depending on scaleToOriginal
    private int y; // with respect to the input image ... may not necessarily equal to the original image depending on scaleToOriginal
    private int viewableWidth; // width of image window
    private int viewableHeight; // height of image window
    private int panelWidth; // width of panel, don't make final ... in case want to support reziable panel
    private int panelHeight; // height of panel ... in case want to support reziable panel
    private int viewableXOffset; // (panelWidth - viewableWidth)/2
    private int viewableYOffset; // (panelHeight - viewableHeight)/2
    private final int imageWidth; // real image width
    private final int imageHeight; // real image height
    // magnification == 1 => highest magnification
    private float magnification; // magnification 1 to >0 NOTE: magnification is w.r.t preview/lowres image (i.e. NOT original image)
    private final float maxZoomMagnification; // max zoom-out ... so make sure view width < width and view height < height
    private final float minZoomMagnification; // max zoom-in 
    private final float scaleToOriginal; // a scaling factor (>1) that scales the coordinate system back to that of the original image
    // this would be the ratio between the preview/lowres image and the original image.

    public ViewWindow(int x, int y, int width, int height, int imageWidth, int imageHeight, float magnification, float scaleToOriginal) {
        this.x = x;
        this.y = y;
        this.panelWidth = width;
        this.panelHeight = height;

        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        adjustViewableWidthHeight(imageWidth, imageHeight);

        this.magnification = magnification;
        this.scaleToOriginal = scaleToOriginal;

        // figure out max zoom-out ... so make sure view width < width and view height < height
        this.maxZoomMagnification = Math.min( //Math.max(
                ((float) width / (float) imageWidth),
                ((float) height / (float) imageHeight));

        // max zoom-in
        minZoomMagnification = ZOOM_MIN;
    }

    /**
     * adjust viewableWidth and height depending on the actual viewing area
     * described by realViewingImageWidth and realViewingImageHeight
     *
     * updates viewWindow if its available
     *
     * @param realViewingImageWidth
     * @param realViewingImageHeight
     */
    private void adjustViewableWidthHeight(int realViewingImageWidth, int realViewingImageHeight) {
        if (realViewingImageWidth > realViewingImageHeight) {
            viewableWidth = panelWidth;
            viewableHeight = Math.round(((float) panelHeight) * ((float) realViewingImageHeight) / ((float) realViewingImageWidth));
        } else {
            viewableWidth = Math.round(((float) panelWidth) * ((float) realViewingImageWidth) / ((float) realViewingImageHeight));
            viewableHeight = panelHeight;
        }
        viewableXOffset = Math.round((float) (panelWidth - viewableWidth) / 2f);
        viewableYOffset = Math.round((float) (panelHeight - viewableHeight) / 2f);
    }

    /**
     *
     * @param delta = number of mouse click
     */
    public void changeMagnification(float delta) {

        // capture centre point of view window BEFORE magnification change
        int midX = (getX2() - x) / 2 + x;
        int midY = (getY2() - y) / 2 + y;

        magnification = Math.max(maxZoomMagnification, Math.min(minZoomMagnification, magnification - delta * ZOOM_INCREMENT));

        // need to update x/y
        // 1. want x2-x and y2-y to remain the same 
        x = Math.max(0, midX - Math.round((float) panelWidth / magnification * 0.5f));
        y = Math.max(0, midY - Math.round((float) panelHeight / magnification * 0.5f));

        // 2. update viewableWidth and viewableHeight of this viewWindow
        adjustViewableWidthHeight(
                Math.min(Math.round((float) panelWidth / magnification), imageWidth),
                Math.min(Math.round((float) panelHeight / magnification), imageHeight));

        // 2. make sure x2/y2 do not get out of bound
        if (getX2() > imageWidth) {
            x = imageWidth - Math.round((float) viewableWidth / magnification);
        }
        if (getY2() > imageHeight) {
            y = imageHeight - Math.round((float) viewableHeight / magnification);
        }
    }

    /**
     * change magnification to 1:1 i.e. 100%
     */
    public void changeMagnificationToOne() {
        changeMagnification((magnification - 1) / ZOOM_INCREMENT);
    }

    /**
     * change magnification so that whole image fit the applet window
     */
    public void changeMagnificationToFitWindow() {
        changeMagnification((magnification - maxZoomMagnification) / ZOOM_INCREMENT);
    }

    /**
     * return the current magnification
     *
     * @return
     */
    public float getMagnification() {
        return magnification;
    }

    /**
     * return the ratio between the preview/lowres image and the original image
     * NOTE: scaleToOriginal must be > 1
     *
     * @return
     */
    public float getScaleToOriginal() {
        return scaleToOriginal;
    }

    /**
     * return width of image window
     *
     * @return
     */
    public int getWidth() {
        return viewableWidth;
    }

    /**
     * return height of image window
     *
     * @return
     */
    public int getHeight() {
        return viewableHeight;
    }

    /**
     * return width of the parent panel
     * 
     * @return 
     */
    public int getPanelWidth() {
        return panelWidth;
    }
    
    /**
     * return height of the parent panel
     * 
     * @return 
     */
    public int getPanelHeight() {
        return panelHeight;
    }
    
    /**
     * return image width - not the original image width, if image is a preview
     * image
     *
     * @return
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * return image height - not the original image height, if image is a
     * preview image
     *
     * @return
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * return width of original image width, which may not necessarily be same
     * as imageWidth
     *
     * @return
     */
    public int getOriginalImageWidth() {
        return Math.round(imageWidth * scaleToOriginal);
    }

    /**
     * return width of original image height, which may not necessarily be same
     * as imageHeight
     *
     * @return
     */
    public int getOriginalImageHeight() {
        return Math.round(imageHeight * scaleToOriginal);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getX2() {
        return x + Math.round((float) viewableWidth / magnification);
    }

    public int getY2() {
        return y + Math.round((float) viewableHeight / magnification);
    }

    /**
     * get x in coordinate system of the original image
     *
     * @return
     */
    public int getOriginalX() {
        return Math.round((float) x * scaleToOriginal);
    }

    /**
     * get y in coordinate system of the original image.
     *
     * @return
     */
    public int getOriginalY() {
        return Math.round((float) y * scaleToOriginal);
    }

    /**
     * get x in coordinate system of the original image
     *
     * @return
     */
    public int getOriginalX2() {
        return Math.round((float) getX2() * scaleToOriginal);
    }

    /**
     * get y in coordinate system of the original image.
     *
     * @return
     */
    public int getOriginalY2() {
        return Math.round((float) getY2() * scaleToOriginal);
    }

    /**
     * project view X coordinate to x coordinate of original image
     *
     * @param viewX
     * @return
     */
    public int projectOriginalX(int viewX) {
        return Math.round((float) projectRealX(viewX) * scaleToOriginal);
    }

    /**
     * project view Y coordinate to y coordinate of original image
     *
     * @param viewY
     * @return
     */
    public int projectOriginalY(int viewY) {
        return Math.round((float) projectRealY(viewY) * scaleToOriginal);
    }

    /**
     * project view X coordinate to real X coordinate
     *
     * @param viewX
     * @return
     */
    public int projectRealX(int viewX) {
        return x + Math.round((((float) viewX) - viewableXOffset) / magnification);
    }

    /**
     * project view Y coordinate to real Y coordinate
     *
     * @param viewY
     * @return
     */
    public int projectRealY(int viewY) {
        return y + Math.round((((float) viewY) - viewableYOffset) / magnification);
    }

    /**
     * project real X coordinate to view coordinate
     *
     * @param realX
     * @return
     */
    public int projectViewX(int realX) {
        return Math.round((float) (realX - x) * magnification);
    }

    /**
     * project real Y coordinate to view coordinate
     *
     * @param realY
     * @return
     */
    public int projectViewY(int realY) {
        return Math.round((float) (realY - y) * magnification);
    }

    /**
     * project real length to view length
     *
     * @param realLength
     * @return
     */
    public int projectViewLength(int realLength) {
        return Math.round((float) realLength * magnification);
    }

    /**
     * move
     *
     * @param dX - in view coordinate
     * @param dY - in view coordinate
     */
    public void move(int dX, int dY) {
        x = Math.min(
                imageWidth - Math.round((float) viewableWidth / magnification),
                Math.max(0, x + Math.round((float) dX / magnification)));
        y = Math.min(
                imageHeight - Math.round((float) viewableHeight / magnification),
                Math.max(0, y + Math.round((float) dY / magnification)));
    }
}
