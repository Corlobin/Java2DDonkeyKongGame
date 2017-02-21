package br.ol.donkey.actor;

import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import br.ol.donkey.infra.Actor;
import br.ol.donkey.infra.Time;
import br.ol.g2d.Animation;
import br.ol.g2d.AnimationObjectFrame;
import br.ol.g2d.AnimationObjectInterface;
import br.ol.g2d.AnimationSpriteObject;
import br.ol.g2d.TextBitmapScreen;
import java.awt.Graphics2D;

/**
 * HUD actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class HUD extends Actor<DonkeyGame> {
    
    private Animation hud;
    
    private Animation hudLives;
    private boolean gameStarted;
    
    private AnimationSpriteObject level;
    private boolean levelVisible;
    
    private AnimationSpriteObject bonus;
    private boolean bonusVisible;
    
    private TextBitmapScreen bitmapTextScreen;
    
    public HUD(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        hud = game.getG2D().getAnimations().getCopy("hud");
        hudLives = game.getG2D().getAnimations().getCopy("hud_lives");
        level = getAnimationSpriteObject("level");
        bonus = getAnimationSpriteObject("bonus");
        bitmapTextScreen = game.getG2D().getTextScreens().get("hud");
        
        hud.pause();
        setVisible(true);
        setAnimationSpriteObjectVisible(false, level);
        setAnimationSpriteObjectVisible(false, bonus);
    }
    
    private AnimationSpriteObject getAnimationSpriteObject(String name) {
        for (AnimationObjectInterface animationObject : hud.objects) {
            if (animationObject instanceof AnimationSpriteObject) {
                AnimationSpriteObject asp = (AnimationSpriteObject) animationObject;
                if (asp.getName().equals(name)) {
                    return asp;
                }
            }
        }
        return null;
    }

    private void setAnimationSpriteObjectVisible(boolean visible, AnimationSpriteObject aso) {
        for (AnimationObjectFrame frame : aso.keyframes.values()) {
            frame.alpha = visible ? 1 : 0;
        }
    }

    @Override
    public void update() {
        hudLives.currentFrameIndex = game.getLives();
        bitmapTextScreen.print(1, 1, game.getScore());
        bitmapTextScreen.print(11, 1, game.getHiScore());
        
        if (levelVisible) {
            bitmapTextScreen.print(23, 3, game.getCurrentLevelStr());
        }
        else {
            bitmapTextScreen.print(23, 3, "  ");
        }

        if (bonusVisible) {
            bitmapTextScreen.print(22, 6, game.getBonusStr());
        }
        else {
            bitmapTextScreen.print(22, 6, "    ");
        }
        
        hud.update(Time.delta);
    }
    
    @Override
    protected void draw(Graphics2D g) {
        hud.draw(g);
        bitmapTextScreen.draw(g);
        if (gameStarted) {
            hudLives.draw(g);
        }
    }
    
    public void stateChanged() {
        if (game.getState() == State.INTRO) {
            gameStarted = true;
            hud.play();
            setAnimationSpriteObjectVisible(true, level);
            levelVisible = true;
        }
        else if (game.getState() == State.PLAYING) {
            setAnimationSpriteObjectVisible(true, bonus);
            bonusVisible = true;
        }
        else if (game.getState() == State.GAME_OVER) { 
            gameStarted = false;
            hud.pause();
            hud.currentFrameIndex = 0;
            setAnimationSpriteObjectVisible(false, level);
            setAnimationSpriteObjectVisible(false, bonus);
            levelVisible = false;
            bonusVisible = false;
        }
    }

    @Override
    public void hideAll() {
    }
    
}
