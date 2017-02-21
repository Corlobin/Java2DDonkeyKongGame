package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;

/**
 * Spark actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Spark extends DonkeyActor {
    
    private int scorePoint;
    
    public Spark(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setAffectedByGravity(false);
        setAnimation("destroying_enemy");
    }

    @Override
    protected void updatePlayingDestroyingEnemy() {
        if (getAnimation().playing) {
            return;
        }
        game.showScorePoint((int) getX(), (int) getY(), scorePoint);
        game.setState(State.PLAYING);
    }
    
    @Override
    public void stateChanged() {
        if (game.getState() == State.PLAYING_DESTROYING_ENEMY) {
            setVisible(true);
            getAnimation().play();
        }
        else {
            setVisible(false);
            getAnimation().stop();
        }
    }
    
    public void show(int x, int y, int scorePoint) {
        this.scorePoint = scorePoint;
        set(x, y);
        game.setState(State.PLAYING_DESTROYING_ENEMY);
    }
    
}
