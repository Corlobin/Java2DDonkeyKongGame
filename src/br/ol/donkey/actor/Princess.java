package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;

/**
 * Princess actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Princess extends DonkeyActor {

    public Princess(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setKinematic(true);
        playAnimation("princess_help");
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_START_DYING
                || game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.GAME_OVER);
        setVisible(game.getState() == State.PLAYING 
                || game.getState() == State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == State.PLAYING_MARIO_START_DYING
                || game.getState() == State.PLAYING_MARIO_DIED
                || game.getState() == State.GAME_OVER);
    }
    
}
