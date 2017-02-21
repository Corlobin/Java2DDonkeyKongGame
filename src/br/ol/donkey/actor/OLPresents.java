package br.ol.donkey.actor;


import br.ol.donkey.*;
import br.ol.donkey.DonkeyGame.State;

/**
 * OLPresents actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class OLPresents extends DonkeyActor {
    
    public OLPresents(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setCollisionCheckEnabled(false);
        setKinematic(true);
    }

    @Override
    protected void updateOLPresents() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    setAnimation("ol_presents");
                    getAnimation().stop();
                    getAnimation().play();
                    instructionPointer = 1;
                case 1:
                    if (getAnimation().playing) {
                        break yield;
                    }
                    game.setState(State.TITLE);
                    break yield;
            }
        }        
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != State.OL_PRESENTS);
        setVisible(game.getState() == State.OL_PRESENTS);
        if (game.getState() == State.OL_PRESENTS) {
            instructionPointer = 0;
        }
    }    
        
}
