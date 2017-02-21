package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;

/**
 * Initialization actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Initialization extends DonkeyActor {

    public Initialization(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setPaused(true);
    }

    @Override
    protected void updateInitializing() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    game.setTextBitmapScreenVisible(false);
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    game.setState(State.OL_PRESENTS);
                    break yield;
            }
        }
    }

    @Override
    public void stateChanged() {
        if (game.getState() == State.INITIALIZING) {
            instructionPointer = 0;
        }
    }
    
}
