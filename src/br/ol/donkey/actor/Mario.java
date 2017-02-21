package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import br.ol.donkey.infra.Time;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

/**
 * Mario Actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Mario extends DonkeyActor {
    
    private boolean climbing;
    
    private boolean powerUp;
    private long powerUpStartTime;
    
    private boolean jumping;
    private boolean died;
    private long marioDiedStartTime;
    
    private boolean collidingWithFloorUp;
    private boolean collidingWithFloorDown;
    private boolean canKeepClimbingStair;

    public Mario(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        limitX(4, 220);
        reset();
    }

    private void reset() {
        set(50, 247);
        setLastDirection(Direction.RIGHT);
    }
    
    public boolean isJumping() {
        return jumping;
    }

    public boolean isDied() {
        return died;
    }

    public boolean isClimbing() {
        return climbing;
    }

    public boolean isPowerUp() {
        return powerUp;
    }

    @Override
    public void updatePlaying() {
        collidingWithFloorUp = isCollidingWithFloorUp();
        collidingWithFloorDown = isCollidingWithFloorDown();
        canKeepClimbingStair = canKeepClimbingStair();
        if (climbing) {
            updateClimbing();
        }
        else {
            updateMovement();
        }
        updateMarioPowerUpState();
        updateMarioAnimation();
        updateBonus();
        checkLevelCleared();
    }
    
    private void updateMarioPowerUpState() {
        if (System.currentTimeMillis() - powerUpStartTime < 10000) {
            return;
        }
        powerUp = false;
        game.broadcastMessage("marioEndPowerUp");
    }
    
    public void updateMovement() {
        if (!collidingWithFloorDown) {
            return;
        }
        jumping = false;
        // up, down stairs
        if (isKeyDown(KeyEvent.VK_UP) && isCollidingWithStair() && !powerUp) {
            setClimbing(true, getY());
        }
        else if (isKeyDown(KeyEvent.VK_DOWN) && canDownStair() && !powerUp) {
            setClimbing(true, getY() + 5);
        }
        // left, right
        else if (isKeyDown(KeyEvent.VK_LEFT)) {
            setVx(-1);
        }
        else if (isKeyDown(KeyEvent.VK_RIGHT)) {
            setVx(1);
        }
        // idle
        else if (getLastDirection() == Direction.LEFT) {
            setVx(0);
        }
        else if (getLastDirection() == Direction.RIGHT) {
            setVx(0);
        }
        // jump
        if (isKeyPressed(KeyEvent.VK_SPACE)) {
            tryToJump();
        }

        // debugging purpose
        if (isKeyPressed(KeyEvent.VK_S)) {
            game.levelCleared();
        }
    }
    
    private void tryToJump() {
        if (!powerUp) {
            jumping = true;
            setVy(-2.1);
            setVx(getVx() * 1.5);
            if (getLastDirection() == Direction.NONE) {
                setLastDirection(Math.random() > 0.5 ? Direction.LEFT : Direction.RIGHT);
            }
        }
    }
    
    public void updateClimbing() {
        // ensure keep climbing just if mario is holding stair
        if (!isCollidingWithStair()) {
            setClimbing(false, getY());
        }
        // up, down, left, right
        else if (isKeyDown(KeyEvent.VK_UP) && collidingWithFloorUp) {
            setClimbing(false, getY() - 5);
        }
        else if (isKeyDown(KeyEvent.VK_DOWN) && collidingWithFloorDown) {
            setClimbing(false, getY());
        }
        else if (isKeyDown(KeyEvent.VK_LEFT) && !canKeepClimbingStair) {
            setClimbing(false, getY());
            setLastDirection(Direction.LEFT);
        }
        else if (isKeyDown(KeyEvent.VK_RIGHT) && !canKeepClimbingStair) {
            setClimbing(false, getY());
            setLastDirection(Direction.RIGHT);
        }
        else if (isKeyDown(KeyEvent.VK_UP) && canKeepClimbingStair) {
            setVy(-0.5);
        }
        else if (isKeyDown(KeyEvent.VK_DOWN)) {
            setVy(0.5);
        }
        else {
            setVy(0);
        }
    }
    
    private void setClimbing(boolean climbing, double y) {
        this.climbing = climbing;
        setY(y);
        setVy(0);
        setVx(0);
        setAffectedByGravity(!climbing);
        setLastDirection(Direction.NONE);
    }
    
    private void updateBonus() {
        game.setBonus(game.getBonus() - Time.delta * 0.001);
    }

    private void checkLevelCleared() {
        if (isCollidingWithLevelCleared()) {
            game.levelCleared();
        }
    }
    
    private void updateMarioAnimation() {
        if (climbing) {
            updateClimbingAnimation();
        }
        else if (collidingWithFloorDown) {
            updateIdleOrWalkingAnimation(); // includes power up
        }
        else {
            updateJumpingAnimation();
        }
    }
    
    private void updateClimbingAnimation() {
        if (getVy() != 0) {
            playAnimation("mario_climbing");
        }
        else if (getVx() > 0) {
            getAnimation().pause();
        }
    }
    
    private void updateIdleOrWalkingAnimation() {
        String powerUpStr = powerUp ? "powerup_" : "";
        if (getVx() < 0) {
            playAnimation("mario_" + powerUpStr + "walking_left");
        }
        else if (getVx() > 0) {
            playAnimation("mario_" + powerUpStr + "walking_right");
        }
        else if (getLastDirection() == Direction.LEFT) {
            playAnimation("mario_" + powerUpStr + "idle_left");
        }
        else if (getLastDirection() == Direction.RIGHT) {
            playAnimation("mario_" + powerUpStr + "idle_right");
        }
        else {
            playAnimation("mario_climbing_finished");
        }
    }
    
    private void updateJumpingAnimation() {
        if (getLastDirection() == Direction.LEFT && powerUp) {
            playAnimation("mario_powerup_idle_left");
        }
        else if (getLastDirection() == Direction.RIGHT && powerUp) {
            playAnimation("mario_powerup_idle_right");
        }
        else if (getLastDirection() == Direction.LEFT) {
            playAnimation("mario_jumping_left");
        }
        else if (getLastDirection() == Direction.RIGHT) {
            playAnimation("mario_jumping_right");
        }
        else {
            playAnimation("mario_climbing_finished");
        }
    }

    @Override
    protected void updatePlayingMarioStartDying() {
        if (System.currentTimeMillis() - marioDiedStartTime < 1500) {
            return;
        }
        game.setState(State.PLAYING_MARIO_DIED);
    }
    
    @Override
    protected void updatePlayingMarioDied() {
        if (getAnimation().playing) {
            return;
        }
        game.marioDied();
    }
    
    @Override
    public void onCollision(DonkeyActor collidedActor, Point collisionPoint) {
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != DonkeyGame.State.PLAYING
                && game.getState() != DonkeyGame.State.PLAYING_MARIO_DIED);
        
        setVisible(game.getState() == DonkeyGame.State.PLAYING 
                || game.getState() == DonkeyGame.State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_START_DYING
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_DIED);
        
        switch (game.getState()) {
            case INTRO_HOW_HIGH:
                reset();
                break;
            case PLAYING:
                died = false;
                setAffectedByGravity(true);
                break;
            case PLAYING_MARIO_START_DYING:
                setVx(0);
                setVy(0);
                setAffectedByGravity(false);
                break;
            case PLAYING_MARIO_DIED:
                playAnimation("mario_died");
                break;
        }
    }

    public void marioStartPowerUp() {
        powerUp = true;
        powerUpStartTime = System.currentTimeMillis();
    }
    
    public void died() {
        if (died) {
            return;
        }
        died = true;
        game.setState(State.PLAYING_MARIO_START_DYING);
        marioDiedStartTime = System.currentTimeMillis();
    }

}
