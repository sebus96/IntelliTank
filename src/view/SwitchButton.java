/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.GasStationController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author Admin
 */
public class SwitchButton {
    
    GraphicsContext gc;
    GasStationController gsc;
    int x,y,width,height;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public SwitchButton(GasStationController gsc, GraphicsContext gc,int x, int y) {
        this.gsc = gsc;
        this.gc = gc;
        this.x = x;
        this.y = y;
        width = 110;
        height = 40;
        
        String buttonText;
        if(gsc.getRoute().showBasicStrategy())
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
        gsc.getRoute().setShowBasicStrategy(!gsc.getRoute().showBasicStrategy());
    }
    public boolean wasClicked(int posX, int posY) {
        if(posX > x && posX < x+width && posY > y && posY < y+height)
            return true;
        return false;
    }
}
