package br.ol.donkey.actor;


import br.ol.donkey.*;
import br.ol.donkey.DonkeyGame.State;

/**
 * IntroHowHigh actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class IntroHowHigh extends DonkeyActor {
    
    public IntroHowHigh(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setCollisionCheckEnabled(false);
        setKinematic(true);
        setAnimation("intro_how_high");
    }
    
    @Override
    public void updateIntroHowHigh() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    getAnimation().currentFrameIndex = game.getCurrentLevel();
                    waitTime = System.currentTimeMillis();
                    instructionPointer = 1;
                case 1:
                    if (System.currentTimeMillis() - waitTime < 3000) {
                        break yield;
                    }
                    game.setState(State.PLAYING);
                    break yield;
            }
        }        
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != State.INTRO_HOW_HIGH);
        setVisible(game.getState() == State.INTRO_HOW_HIGH);
        if (game.getState() == State.INTRO_HOW_HIGH) {
            instructionPointer = 0;
        }
    }    
        
}
