package br.ol.donkey.actor;


import br.ol.donkey.*;
import br.ol.donkey.DonkeyGame.State;

/**
 * LevelCleared actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class LevelCleared extends DonkeyActor {
    
    public LevelCleared(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setCollisionCheckEnabled(false);
        setKinematic(true);
    }
    
    @Override
    public void updateLevelCleared() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    setAnimation("level_cleared");
                    getAnimation().stop();
                    getAnimation().play();
                    instructionPointer = 1;
                case 1:
                    if (getAnimation().playing) {
                        break yield;
                    }
                    game.goToNextLevel();
                    break yield;
            }
        }        
    }
    
    @Override
    public void stateChanged() {
        setPaused(game.getState() != State.LEVEL_CLEARED);
        setVisible(game.getState() == State.LEVEL_CLEARED);
        if (game.getState() == State.LEVEL_CLEARED) {
            instructionPointer = 0;
        }
    }    
        
}
