/*
 * store data relating to the field of view
 */
package ca.ubc.gpec.ia.fieldselector.model;

/**
 *
 * @author samuelc
 */
public class FieldOfView {

    /**
     * viewing state of field of view
     */
    public enum ViewingState {
        CURRENT, PREVIEW, NOT_CURRENT
    }

    /**
     * scoring state of field of view
     */
    public enum ScoringState {
        NOT_SCORED, SCORING, SCORED
    }

    /**
     * Ki67 state of the field of view
     */
    public enum Ki67State {
        NEGLIGIBLE, LOW, MEDIUM, HIGH, HOT_SPOT
    }
    
    
    private int x; // x-coord of the middle of the field of view in pixel, in coordinate system of original scanned image (i.e. not preview image)
    private int y; // y-coord of the middle of the field of view in pixel, in coordinate system of original scanned image (i.e. not preview image)
    private int diameter; // in pixel
    private ScoringState scoringState; // indicate if this has not been scored, scoring or scored
    private ViewingState viewingState; // indicate if this is the current scoring field of view
    private Ki67State ki67State; // indicate the Ki67 state of this field of view

    /**
     * constructor
     *
     * @param x
     * @param y
     * @param diameter
     * @param viewingState
     * @param scoringState
     * @param ki67State
     */
    public FieldOfView(int x, int y, int diameter, ViewingState viewingState, ScoringState scoringState, Ki67State ki67State) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.viewingState = viewingState;
        this.scoringState = scoringState;
        this.ki67State = ki67State;
    }

    /**
     * PROBLEM!!! currently assume to compare via x,y,diameter, scoringState ...
     * it MAY BE logical for two fields of view to be pointing to the same
     * location
     *
     * TO COUNTER THIS PROBLEM ... every time a new field is selected, make sure they don't 
     * overlap completely by add/delete 1 or more pixels in X direction
     * 
     * @param obj
     * @return
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FieldOfView)) {
            return false;
        }
        FieldOfView f = (FieldOfView) obj;
        return (x == f.getX()
                && y == f.getY()
                && diameter == f.getDiamter()
                && scoringState == f.scoringState);
    }

    ///////////////////////////////////////////////
    /// getters & setters                       ///
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDiamter() {
        return diameter;
    }

    /**
     * return viewingState
     *
     * @return
     */
    public ViewingState getViewingState() {
        return viewingState;
    }

    /**
     * return scoringState
     *
     * @return
     */
    public ScoringState getScoringState() {
        return scoringState;
    }

    /**
     * return the ki67 state
     *
     * @return
     */
    public Ki67State getKi67State() {
        return ki67State;
    }

    /**
     * is current scoring?
     *
     * @return
     */
    public boolean isCurrentViewing() {
        return viewingState == ViewingState.CURRENT;
    }

    /**
     * is preview scoring?
     *
     * @return
     */
    public boolean isPreviewing() {
        return viewingState == ViewingState.PREVIEW;
    }

    /**
     * is the region scored?
     *
     * @return
     */
    public boolean isScored() {
        return scoringState == ScoringState.SCORED;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }

    public void setScoringState(ScoringState scoringState) {
        this.scoringState = scoringState;
    }

    public void setViewingState(ViewingState viewingState) {
        this.viewingState = viewingState;
    }
    /// end of getters & setters                ///
    ///////////////////////////////////////////////

    /**
     * check to see if this field is a hotspot
     * @return 
     */
    public boolean isHotspot() {
        return ki67State == Ki67State.HOT_SPOT;
    }
    
    /**
     * check to see if the specified x/y is within field of view
     *
     * @param inputX
     * @param inputY
     * @return
     */
    public boolean inView(int inputX, int inputY) {
        return (inputX <= (x + diameter / 2)
                && inputX >= (x - diameter / 2)
                && inputY <= (y + diameter / 2)
                && inputY >= (y - diameter / 2));
    }

    /**
     * determine if this nuclei is in view i.e. do I need to draw it
     *
     * NOTE: ViewWindow uses coordinate system of preview/lowres image while
     * FieldOfView uses coordinate system of ORIGINAL image - therefore, need
     * transformation!!!
     *
     * @param viewWindow
     * @return
     */
    public boolean inView(ViewWindow viewWindow) {
        //System.out.println("x/y: "+x+"/"+y+" viewport ... x/x2: "+viewWindow.getOriginalX()+"/"+viewWindow.getOriginalX2()+"; y/y2: "+viewWindow.getOriginalY()+"/"+viewWindow.getOriginalY2());
        return (viewWindow.getOriginalX() <= (x + diameter / 2)
                && viewWindow.getOriginalX2() >= (x - diameter / 2)
                && viewWindow.getOriginalY() <= (y + diameter / 2)
                && viewWindow.getOriginalY2() >= (y - diameter / 2));
    }
}
