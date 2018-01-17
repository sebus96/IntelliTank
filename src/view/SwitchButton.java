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
 * Enthält einen Button, mit dem es möglich ist, auf der Routenansicht zwischen der Naiven und intelligenten Tankstrategie zu wechseln
 * @author Axel Claassen
 */
public class SwitchButton {
    
    GraphicsContext gc;
    Route route;
    int x,y,width,height;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    /**
     * Initialisiert den Switchbutton
     * @param r Die Route, für die der Switchbutten dargestellt wird
     * @param gc der Grafikkontext des Canvas
     * @param x die x-Position des Buttons
     * @param y die y-Position des Buttons
     */
    public SwitchButton(Route r, GraphicsContext gc,int x, int y) {
        this.route = r;
        this.gc = gc;
        this.x = x;
        this.y = y;
        width = 110;
        height = 40;
        
        String buttonText;
        if(route.showBasicStrategy())
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
    /**
     * Legt den Button um, um die Tankstrategie zu wechseln
     */
    public void buttonPressed() {
        route.setShowBasicStrategy(!route.showBasicStrategy());
    }
    /**
     * 
     * @param posX x-Position des Klicks
     * @param posY y-Position des Klicks
     * @return ob der Button angeklickt wurde
     */
    public boolean wasClicked(int posX, int posY) {
        if(posX > x && posX < x+width && posY > y && posY < y+height)
            return true;
        return false;
    }
}
