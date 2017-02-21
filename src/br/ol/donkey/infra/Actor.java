package br.ol.donkey.infra;

import java.awt.Graphics2D;

/**
 * Actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Actor<T extends Game> {
    
    protected T game;
    private double x, y;
    private double minX = Double.MIN_VALUE, maxX = Double.MAX_VALUE;
    private double minY = Double.MIN_VALUE, maxY = Double.MAX_VALUE;
    private boolean visible;
    private boolean updatebleJustWhenVisible = true;
    
    protected int instructionPointer;
    protected long waitTime;
    
    public Actor(T game) {
        this.game = game;
    }
    
    public void init() {
    }
    
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
        limitMovement();
    }

    public void setY(double y) {
        this.y = y;
        limitMovement();
    }
    
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
        limitMovement();
    }

    public void translate(double x, double y) {
        this.x += x;
        this.y += y;
        limitMovement();
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxY() {
        return maxY;
    }
    
    public void limitX(double minX, double maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    public void limitY(double minY, double maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }
    
    private void limitMovement() {
        x = x < minX ? minX : x;
        x = x > maxX ? maxX : x;
        y = y < minY ? minY : y;
        y = y > maxY ? maxY : y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isUpdatebleJustWhenVisible() {
        return updatebleJustWhenVisible;
    }

    public void setUpdatebleJustWhenVisible(boolean updatebleJustWhenVisible) {
        this.updatebleJustWhenVisible = updatebleJustWhenVisible;
    }

    protected void internalUpdate() {
        if (!updatebleJustWhenVisible || (updatebleJustWhenVisible && visible)) {
            update();
        }
    }
    
    public void update() {
    }
    
    
    protected void internalDraw(Graphics2D g) {
        if (visible) {
            draw(g);
        }
    }
    
    protected void draw(Graphics2D g) {
    }

    public void hideAll() {
        setVisible(false);
    }
    
    public void showAll() {
        setVisible(true);
    }
    
}
