/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import model.Route;

/**
 *
 * @author Admin
 */
public class SwitchButton {
    
    GraphicsContext gc;
    int x,y,width,height;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    /**
     * Erstellt einen Button auf dem Grafikkontext an der Postion (x,y)
     * 
     * @param gc der Grafikkontext
     * @param x die x-Position des Buttons
     * @param y die y-Position des Buttons
     */
    public SwitchButton( GraphicsContext gc,int x, int y) {
        this.gc = gc;
        this.x = x;
        this.y = y;
        width = 110;
        height = 40;
        
        String buttonText;
        if(Route.getStrategy() == Route.Strategy.BASIC)
            buttonText = "Zur intelligenten\nStrategie wechseln";
        else
            buttonText = "Zur Standard-\nStrategie wechseln";
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        gc.fillRect(x,y,width,height);
        gc.setFill(Color.BLACK);
        gc.fillText(buttonText, x+width/2, y+height/2 -3);
        gc.strokeRect(x,y,width,height);
        gc.setTextAlign(TextAlignment.LEFT);
    }
    public void buttonPressed() {
    	Route.switchStrategy();
    }
    public boolean wasClicked(int posX, int posY) {
        if(posX > x && posX < x+width && posY > y && posY < y+height)
            return true;
        return false;
    }
}
