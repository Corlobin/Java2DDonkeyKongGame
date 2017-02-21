package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;

/**
 * ScorePoint actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class ScorePoint extends DonkeyActor {
    
    private long showStartTime;
    
    public ScorePoint(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setAffectedByGravity(false);
        setAnimation("score_point");
    }
    
    @Override
    public void update() {
        if (System.currentTimeMillis() - showStartTime < 3000) {
            return;
        }
        setVisible(false);
        setDestroyed(true);
    }
    
    public void show(int x, int y, int scorePoint) {
        set(x, y);
        setVisible(true);
        setDestroyed(false);
        getAnimation().currentFrameIndex = scorePoint / 100;
        showStartTime = System.currentTimeMillis();
    }
    
    @Override
    public void stateChanged() {
        setVisible(game.getState() == DonkeyGame.State.PLAYING 
                || game.getState() == DonkeyGame.State.PLAYING_DESTROYING_ENEMY 
                || game.getState() == DonkeyGame.State.PLAYING_MARIO_START_DYING);
    }
    
}
