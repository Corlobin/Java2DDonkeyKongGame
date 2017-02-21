package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;

/**
 * Barrels actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Barrels extends DonkeyActor {
    
    public Barrels(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setKinematic(true);
        setAnimation("barrels");
    }

    @Override
    public void stateChanged() {
        setPaused(game.getState() == DonkeyGame.State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_START_DYING
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_DIED);
        setVisible(game.getState() == DonkeyGame.State.PLAYING 
                || game.getState() == DonkeyGame.State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_START_DYING
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_DIED
                || game.getState() == DonkeyGame.State.GAME_OVER
                || game.getState() == DonkeyGame.State.LEVEL_CLEARED);
    }
    
}
