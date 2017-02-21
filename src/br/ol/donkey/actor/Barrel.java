package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import java.awt.Point;

/**
 * Barrel actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Barrel extends DonkeyActor {
    
    private boolean blue;
    private boolean rollingDown;
    private double lastRollingVx;
    private double lastStairY;
    private BarrelJumpPointCollider barrelJumpPointCollider;
    
    public Barrel(DonkeyGame game) {
        super(game);
        barrelJumpPointCollider = new BarrelJumpPointCollider(game, this);
    }

    public boolean isBlue() {
        return blue;
    }

    public BarrelJumpPointCollider getBarrelJumpPointCollider() {
        return barrelJumpPointCollider;
    }
    
    private void setJumPointColliderVisible(boolean visible) {
        barrelJumpPointCollider.setVisible(visible);
        barrelJumpPointCollider.setDestroyed(!visible);
    }
    
    @Override
    public void init() {
        limitX(-8, 224);
        setRestitution(0.5);
    }

    @Override
    public void updatePlaying() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 100) {
                        break yield;
                    }
                    getAnimation().stop();
                    getAnimation().play();
                    instructionPointer = 2;
                case 2:
                    if (getAnimation().playing) {
                        break yield;
                    }
                    setVx(-1);
                    setVy(0);
                    set(36, 237);
                    setJumPointColliderVisible(true);
                    setKinematic(false);
                    instructionPointer = 3;
                case 3:
                    if (decideRollingDown()) {
                        instructionPointer = 4;
                    }
                    updateBarrelRollingHorizontally();
                    break yield;
                case 4:
                    if (rollingDownFinished()) {
                        instructionPointer = 3;
                    }
                    break yield;
            }
        }
    }
    
    private void updateBarrelRollingHorizontally() {
        if (isBlue() && getY() > 230 && getX() < 18 && getVx() < 0) {
            setDestroyed(true);
            setVisible(false);
            setJumPointColliderVisible(false);
            game.broadcastMessage("spawnFireEnemy");
            return;
        }
        else if (getY() > 230 && getX() <= getMinX() + 2) {
            setDestroyed(true);
            setVisible(false);
            setJumPointColliderVisible(false);
            return;
        }
        else if (getX() <= getMinX() + 2) {
            setVx(-getVx());
            setX(getMinX() + 3);
        }
        else if (getX() >= getMaxX() - 2) {
            setVx(-getVx());
            setX(getMaxX() - 3);
        }
        updateBarrelAnimation();
    }
    
    private boolean decideRollingDown() {
        if (Math.random() < 0.05 && canActorDownStair()) {
            setX(game.getTerrainCollider().getLastCollisionScreenPoint().getX() - 7);
            setAffectedByGravity(false);
            setAffectedByTerrain(false);
            setJumPointColliderVisible(false);
            lastRollingVx = getVx();
            lastStairY = getY();
            setVx(0);
            setVy(0.5);
            return rollingDown = true;
        }
        return rollingDown = false;
    }
    
    int seq = 0;
    
    private boolean rollingDownFinished() {
        if (isCollidingWithFloorDown() && getY() > lastStairY + 10) {
            setAffectedByGravity(true);
            setAffectedByTerrain(true);
            setJumPointColliderVisible(true);
            rollingDown = false;
            setVx(-lastRollingVx);
            setVy(0);
            return true;
        }
        return false;
    }
    
    private void updateBarrelAnimation() {
        String blueStr = isBlue() ? "blue_" : "";
        if (rollingDown) {
            playAnimation("barrel_" + blueStr + "rolling_down");
        }
        else if (getVx() > 0) {
            playAnimation("barrel_" + blueStr + "rolling_right");
        }
        else {
            playAnimation("barrel_" + blueStr + "rolling_left");
        }
    }
    
    @Override
    public void onCollision(DonkeyActor collidedActor, Point collisionPoint) {
        if (!(collidedActor instanceof Mario) || isDestroyed()) {
            return;
        }
        Mario mario = (Mario) collidedActor;
        if (mario.isPowerUp()) {
            setDestroyed(true);
            setVisible(false);
            setJumPointColliderVisible(false);
            game.showSpark(collisionPoint.x, collisionPoint.y, 300);
        }
        else {
            setCollisionCheckEnabled(false);
            mario.died();
        }
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != DonkeyGame.State.PLAYING
                && game.getState() != DonkeyGame.State.PLAYING_MARIO_DIED);
        
        setVisible(!isDestroyed() && (game.getState() == State.PLAYING 
                || game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_START_DYING));
        
        if (game.getState() == State.INTRO_HOW_HIGH) {
            setDestroyed(true);
            setVisible(false);
            setCollisionCheckEnabled(true);
            game.resetFireEnemiesCount();
        }
    }

    public void spawn(boolean isBlue) {
        blue = isBlue;
        setKinematic(false);
        setAffectedByGravity(true);
        setAffectedByTerrain(true);
        setVx(1);
        setVy(0);
        set(70, 74);
        setVisible(true);
        setDestroyed(false);
        setJumPointColliderVisible(true);
        if (isBlue) {
            game.incFireEnemiesCount();
        }
        instructionPointer = 3;
    }

    public void spawnFirstBlue() {
        blue = true;
        setKinematic(true);
        setAnimation("first_blue_barrel_rolling_down");
        getAnimation().currentFrameIndex = 0;
        setVisible(true);
        setDestroyed(false);
        game.incFireEnemiesCount();
        instructionPointer = 0;
    }
    
}
