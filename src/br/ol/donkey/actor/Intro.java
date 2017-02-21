package br.ol.donkey.actor;


import br.ol.donkey.*;
import br.ol.donkey.DonkeyGame.State;

/**
 * Intro Actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Intro extends DonkeyActor {
    
    public Intro(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setCollisionCheckEnabled(false);
        setKinematic(true);
    }
    
    @Override
    public void updateIntro() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    setAnimation("intro");
                    getAnimation().stop();
                    getAnimation().play();
                    instructionPointer = 1;
                case 1:
                    if (getAnimation().playing) {
                        break yield;
                    }
                    game.setState(State.INTRO_HOW_HIGH);
                    break yield;
            }
        }        
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != State.INTRO);
        setVisible(game.getState() == State.INTRO);
        if (game.getState() == State.INTRO) {
            game.setTextBitmapScreenVisible(false);
            instructionPointer = 0;
        }
    }    
        
}
