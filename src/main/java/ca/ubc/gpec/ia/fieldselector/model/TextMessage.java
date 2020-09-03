/*
 * Text message to be drawn on panel
 */
package ca.ubc.gpec.ia.fieldselector.model;

import java.awt.Color;

/**
 *
 * @author samuelc
 */
public class TextMessage {
    private String message;
    private Color color;
    private int x; // x coordinate w.r.t viewWindow image
    private int y; // y coordinate w.r.t viewWindow image
    
    public TextMessage(String message, Color color, int x, int y) {
        this.message = message;
        this.color = color;
        this.x = x;
        this.y = y;
    }
    
    /**
     * get message
     * @return 
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * get color the message should be drawn with
     * @return 
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * return x
     * @return 
     */
    public int getX() {
        return x;
    }
    
    /**
     * return y
     * @return 
     */
    public int getY() {
        return y;
    }
}
