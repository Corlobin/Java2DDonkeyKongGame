package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * BarrelJumpPointCollider actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class BarrelJumpPointCollider extends DonkeyActor {
    
    private Barrel barrel;
    
    public BarrelJumpPointCollider(DonkeyGame game, Barrel barrel) {
        super(game);
        this.barrel = barrel;
    }

    @Override
    public void init() {
        setAnimation("barrel_jump_point_collider");
        setAffectedByGravity(false);
        setAffectedByTerrain(false);
    }

    @Override
    protected void updatePlaying() {
        set(barrel.getX() + 6, barrel.getY());
    }

    @Override
    public void draw(Graphics2D g) {
        // show collider for debbugin purposes
        //super.draw(g);
    }
    
    @Override
    public void onCollision(DonkeyActor collidedActor, Point collisionPoint) {
        if (!(collidedActor instanceof Mario)) {
            return;
        }
        Mario mario = (Mario) collidedActor;
        if (!mario.isJumping()) {
            return;
        }        
        setDestroyed(true);
        setVisible(false);
        game.showScorePoint((int) barrel.getX(), (int) barrel.getY(), 100);
    }
    
    @Override
    public void stateChanged() {
        setVisible(!isDestroyed()
                && (game.getState() == State.PLAYING 
                || game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.PLAYING_MARIO_START_DYING));
        
        if (game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.LEVEL_CLEARED) {
            setDestroyed(true);
            setVisible(false);
            setCollisionCheckEnabled(true);
        }
    }
    
}
