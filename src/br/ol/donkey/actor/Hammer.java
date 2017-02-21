package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import java.awt.Point;

/**
 * Mario Actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Hammer extends DonkeyActor {
    
    public Hammer(DonkeyGame game, int x, int y) {
        super(game);
        set(x, y);
    }

    @Override
    public void init() {
        setPaused(true);
        setAnimation("hammer");
    }

    @Override
    public void onCollision(DonkeyActor collidedActor, Point collisionPoint) {
        if (!(collidedActor instanceof Mario)) {
            return;
        }
        setDestroyed(true);
        setVisible(false);
    }
    
    @Override
    public void stateChanged() {
        if (game.getState() == DonkeyGame.State.INTRO) {
            setDestroyed(false);
        }
        setVisible(!isDestroyed()
                && (game.getState() == DonkeyGame.State.PLAYING 
                || game.getState() == DonkeyGame.State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_DIED
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_START_DYING));
    }
    
}
