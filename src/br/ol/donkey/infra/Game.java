package br.ol.donkey.infra;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Game class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Game {
    
    public Dimension screenSize = new Dimension(320, 240);
    public Point2D screenScale = new Point2D.Double(2, 2);
    
    public List<Actor> actors = new ArrayList<Actor>();

    public void init() {
    }
    
    public void update() {
        for (Actor actor : actors) {
            actor.internalUpdate();
        }
    }
    
    public void draw(Graphics2D g) {
        for (Actor actor : actors) {
            actor.internalDraw(g);
        }
    }

    public void broadcastMessage(String message) {
        for (Actor obj : actors) {
            try {
                Method method = obj.getClass().getMethod(message);
                if (method != null) {
                    method.invoke(obj);
                }
            } catch (Exception ex) {
            }
        }
    }

}
