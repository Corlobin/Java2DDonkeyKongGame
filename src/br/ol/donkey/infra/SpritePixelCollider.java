package br.ol.donkey.infra;

import br.ol.g2d.Sprite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * SpritePixelCollider class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class SpritePixelCollider {

    private static Actor emptyActor = new Actor(null);
    private Actor actor;
    private Sprite sprite;
    private final Rectangle checkArea = new Rectangle();
    private int nonCollidingColor = 0;
    private final Point lastCollisionScreenPoint = new Point();
    
    public SpritePixelCollider() {
    }

    public Point getLastCollisionScreenPoint() {
        return lastCollisionScreenPoint;
    }

    public SpritePixelCollider(Actor actor, Sprite sprite) {
        this.actor = actor;
        this.sprite = sprite;
        if (sprite != null) {
            checkArea.setBounds(0, 0, sprite.getWidth(), sprite.getHeight());
        }
    }
    
    public Actor getActor() {
        if (actor == null) {
            return emptyActor;
        }
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        if (sprite != null) {
            checkArea.setBounds(0, 0, sprite.getWidth(), sprite.getHeight());
        }
    }

    public void setCheckArea(int x, int y, int w, int h) {
        checkArea.setBounds(x, y, w, h);
    }

    public void setEntireCheckArea() {
        checkArea.setBounds(0, 0, sprite.getWidth(), sprite.getHeight());
    }

    public void setBottomCheckArea(int h) {
        checkArea.setBounds(0, sprite.getHeight() - h, sprite.getWidth(), h);
    }

    public Rectangle getCheckArea() {
        return checkArea;
    }

    public int getNonCollidingColor() {
        return nonCollidingColor;
    }

    public void setNonCollidingColor(int nonCollidingColor) {
        this.nonCollidingColor = nonCollidingColor;
    }

    public boolean insideScreenCheckArea(int sx, int sy) {
        if (sprite == null) {
            return false;
        }
        int x = sx - (int) getActor().getX() + sprite.getPivotX();
        int y = sy - (int) getActor().getY() + sprite.getPivotY();
        return checkArea.contains(x, y);
    }
    
    public int getScreenPixel(int sx, int sy) {
        if (sprite == null) {
            return nonCollidingColor;
        }
        int x = sx - (int) getActor().getX() + sprite.getPivotX();
        int y = sy - (int) getActor().getY() + sprite.getPivotY();
        if (sprite.isOutOfRectangle(x, y)) {
            return nonCollidingColor;
        }
        return sprite.getPixel(x, y);
    }
    
    public boolean collides(SpritePixelCollider c) {
        return SpritePixelCollider.this.collides(0, 0, c);
    }
    
    public boolean collides(int dx, int dy, SpritePixelCollider c) {
        if (sprite == null || c.getSprite() == null) {
            return false;
        }

        double oex = c.getActor().getX();
        double oey = c.getActor().getY();
        c.getActor().translate(dx, dy);
        
        SpritePixelCollider c1 = this;
        SpritePixelCollider c2 = c;
        int a1 = c1.getSprite().getWidth() * c1.getSprite().getHeight();
        int a2 = c2.getSprite().getWidth() * c2.getSprite().getHeight();
        
        if (a2 < a1) {
            c2 = this;
            c1 = c;
        }
        
        int sx1 = (int) c1.getActor().getX() - c1.getSprite().getPivotX();
        int sx2 = sx1 + c1.getSprite().getWidth();
        int sy1 = (int) c1.getActor().getY() - c1.getSprite().getPivotY();
        int sy2 = sy1 + c1.getSprite().getHeight();
        for (int sy = sy1; sy < sy2; sy++) {
            for (int sx = sx1; sx < sx2; sx++) {
                if (!c1.insideScreenCheckArea(sx, sy) || !c2.insideScreenCheckArea(sx, sy)) {
                    continue;
                }
                int p1 = c1.getScreenPixel(sx, sy);
                int p2 = c2.getScreenPixel(sx, sy);
                if (p1 != c1.getNonCollidingColor() && p2 != c2.getNonCollidingColor()) {
                    c.getActor().set(oex, oey);
                    lastCollisionScreenPoint.setLocation(sx, sy);
                    return true;
                }
            }
        }
        c.getActor().set(oex, oey);
        return false;
    }
    
    private final Set<Integer> collidersColorsCheck = new HashSet<Integer>();
    
    public boolean collides(SpritePixelCollider c, Integer ... colors) {
        return collides(0, 0, c, colors);
    }
    
    public boolean collides(int dx, int dy, SpritePixelCollider c, Integer ... colors) {
        if (sprite == null || c.getSprite() == null) {
            return false;
        }
        collidersColorsCheck.clear();
        collidersColorsCheck.addAll(Arrays.asList(colors));
        
        double oex = c.getActor().getX();
        double oey = c.getActor().getY();
        c.getActor().translate(dx, dy);
        
        SpritePixelCollider c1 = this;
        SpritePixelCollider c2 = c;
        int a1 = c1.getSprite().getWidth() * c1.getSprite().getHeight();
        int a2 = c2.getSprite().getWidth() * c2.getSprite().getHeight();
        
        if (a2 < a1) {
            c2 = this;
            c1 = c;
        }
        
        int sx1 = (int) c1.getActor().getX() - c1.getSprite().getPivotX();
        int sx2 = sx1 + c1.getSprite().getWidth();
        int sy1 = (int) c1.getActor().getY() - c1.getSprite().getPivotY();
        int sy2 = sy1 + c1.getSprite().getHeight();
        for (int sy = sy1; sy < sy2; sy++) {
            for (int sx = sx1; sx < sx2; sx++) {
                if (!c1.insideScreenCheckArea(sx, sy) 
                        || !c2.insideScreenCheckArea(sx, sy)) {
                    
                    continue;
                }
                int p1 = c1.getScreenPixel(sx, sy);
                int p2 = c2.getScreenPixel(sx, sy);
                int csp = getScreenPixel(sx, sy);
                if (p1 != c1.getNonCollidingColor() && p2 != c2.getNonCollidingColor() 
                        && collidersColorsCheck.contains(csp)) {
                    
                    collidersColorsCheck.remove(csp);
                    if (collidersColorsCheck.isEmpty()) {
                        c.getActor().set(oex, oey);
                        lastCollisionScreenPoint.setLocation(sx, sy);
                        return true;
                    }
                }
            }
        }
        c.getActor().set(oex, oey);
        return false;
    }
    
    public void draw(Graphics2D g) {
        if (sprite == null) {
            return;
        }
        AffineTransform at = g.getTransform();
        g.translate((int) getActor().getX(), (int) getActor().getY());
        sprite.draw(g);
        g.setXORMode(Color.BLUE);
        g.fillRect(checkArea.x - sprite.getPivotX(), checkArea.y 
                - sprite.getPivotY(), checkArea.width, checkArea.height);
        
        g.setPaintMode();
        g.setTransform(at);
    }
    
}
