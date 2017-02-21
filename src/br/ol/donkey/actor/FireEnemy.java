package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import java.awt.Point;

/**
 * FireEnemy actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class FireEnemy extends DonkeyActor {
    
    private long walkTime;
    private boolean vulnerable;
    private double lastStairY;
    
    public FireEnemy(DonkeyGame game) {
        super(game);
    }

    public boolean isVulnerable() {
        return vulnerable;
    }

    @Override
    public void init() {
        limitX(4, 220);
    }

    @Override
    public void updatePlaying() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    if (!isCollidingWithFloorDown()) {
                        break yield;
                    }
                    setVx(0);
                    instructionPointer = 1;
                case 1:
                    setVx(Math.random() > 0.5 ? 0.25 : -0.25);
                case 2:
                    walkTime = 3000 + (long) (5000 * Math.random());
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 3;
                case 3:
                    if (getVx() > 0 && getX() > 205) {
                        setX(getX() > 205 ? 205 : getX());
                        setVx(-getVx());
                        instructionPointer = 2;
                        break;
                    }
                    else if (getVx() < 0 && getX() < 30) {
                        setX(getX() < 30 ? 30 : getX());
                        setVx(-getVx());
                        instructionPointer = 2;
                        break;
                    }
                    // check up stair
                    if (isCollidingWithStair() && Math.random() < 0.02) {
                        setAffectedByGravity(false);
                        setAffectedByTerrain(false);
                        setVx(0);
                        setVy(-0.2);
                        lastStairY = getY();
                        instructionPointer = 4;
                        break yield;
                    }
                    // check down stair
                    else if (canActorDownStair() && Math.random() < 0.02) {
                        setAffectedByGravity(false);
                        setAffectedByTerrain(false);
                        setX(game.getTerrainCollider().getLastCollisionScreenPoint().getX() - 2);
                        setVx(0);
                        setVy(0.2);
                        lastStairY = getY();
                        instructionPointer = 5;
                        break yield;
                    }
                    if (System.currentTimeMillis() - waitTime < walkTime) {
                        break yield;
                    }
                    instructionPointer = 1;
                    break yield;
                // climb stair    
                case 4:
                    if (canActorDownStair() && getY() < lastStairY - 10) {
                        setAffectedByGravity(true);
                        setAffectedByTerrain(true);
                        setY(game.getTerrainCollider().getLastCollisionScreenPoint().getY() - 5);
                        instructionPointer = 1;
                    }
                    break yield;
                // down stair
                case 5:
                    if (isCollidingWithFloorDown() && getY() > lastStairY + 10) {
                        setAffectedByGravity(true);
                        setAffectedByTerrain(true);
                        setY(game.getTerrainCollider().getLastCollisionScreenPoint().getY() - 5);
                        instructionPointer = 1;
                    }
                    break yield;
            }
        }
        updateFireEnemyAnimation();
    }

    private void updateFireEnemyAnimation() {
        String vulnerableStr = isVulnerable() ? "vulnerable_" : "";
        if (getVx() < 0) {
            playAnimation("fire_enemy_" + vulnerableStr + "left");
        }
        else {
            playAnimation("fire_enemy_" + vulnerableStr + "right");
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
            game.decFireEnemiesCount();
            game.showSpark(collisionPoint.x, collisionPoint.y, 300);
        }
        else {
            setCollisionCheckEnabled(false);
            mario.died();
        }
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != State.PLAYING
                && game.getState() != State.PLAYING_MARIO_DIED);
        
        setVisible(!isDestroyed() && (game.getState() == State.PLAYING 
                || game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_START_DYING));
        
        if (game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.LEVEL_CLEARED) {
            setDestroyed(true);
            setVisible(false);
            setCollisionCheckEnabled(true);
        }
    }
    
    public void marioStartPowerUp() {
        vulnerable = true;
    }
    
    public void marioEndPowerUp() {
        vulnerable = false;
    }
    
    public void spawn() {
        set(28, 230);
        setVx(1);
        setVy(-1);
        setAffectedByGravity(true);
        setAffectedByTerrain(true);
        setVisible(true);
        setDestroyed(false);
        instructionPointer = 0;
    }
    
}
