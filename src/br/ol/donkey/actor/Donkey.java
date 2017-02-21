package br.ol.donkey.actor;

import br.ol.donkey.DonkeyActor;
import br.ol.donkey.DonkeyGame;
import br.ol.donkey.DonkeyGame.State;
import br.ol.g2d.AnimationObjectFrame;
import br.ol.g2d.AnimationObjectInterface;
import br.ol.g2d.AnimationSpriteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Donkey actor class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Donkey extends DonkeyActor {

    private final List<String> firstAnimationsName = new ArrayList<String>();
    private AnimationSpriteObject barrelNormal;
    private AnimationSpriteObject barrelBlue;
    private boolean barrelToSpawnIsBlue;

    public Donkey(DonkeyGame game) {
        super(game);
    }

    @Override
    public void init() {
        setKinematic(true);
        setAnimation("donkey_throwing_barrel_normal");
        setFirstAnimationProbabilities();
        getBarrelsAnimationFrames();
    }

    private void setFirstAnimationProbabilities() {
        // donkey_center -> 60%, donkey_left_hand_up -> 20%, donkey_right_hand_up -> 20%
        for (int p = 0; p < 6; p++) {
            firstAnimationsName.add("donkey_center");
        }
        for (int p = 0; p < 2; p++) {
            firstAnimationsName.add("donkey_left_hand_up");
            firstAnimationsName.add("donkey_right_hand_up");
        }
    }
    
    private void getBarrelsAnimationFrames() {
        for (AnimationObjectInterface object : getAnimation().objects){
            AnimationSpriteObject spriteObject = (AnimationSpriteObject) object;
            // System.out.println("--->donkey animation object name: " + spriteObject.name);
            if (spriteObject.name.equals("barrel_normal")) {
                barrelNormal = spriteObject;
            }
            else if (spriteObject.name.equals("barrel_blue")) {
                barrelBlue = spriteObject;
            }
        }
    }
    
    private void setAnimationSpriteObjectVisible(AnimationSpriteObject spriteObject, boolean visible) {
        for (AnimationObjectFrame frame : spriteObject.keyframes.values()) {
            frame.alpha = visible ? 1 : 0;
        }
    }

    private void chooseRandomFirstAnimation() {
        int randomAnimationIndex = (int) (10 * Math.random());
        for (AnimationObjectInterface object : getAnimation().objects){
            AnimationSpriteObject spriteObject = (AnimationSpriteObject) object;
            //System.out.println("donkey animation object name: " + spriteObject.name);
            if (firstAnimationsName.contains(spriteObject.name)) {
                AnimationObjectFrame keyFrame = spriteObject.getKeyFrame(0);
                keyFrame.alpha = 0;
                if (spriteObject.name.equals(firstAnimationsName.get(randomAnimationIndex))) {
                    keyFrame.alpha = 1;
                }
            }
        }
    }
    
    @Override
    public void updatePlaying() {
        yield:
        while (true) {
            switch (instructionPointer) {
                case 0:
                    setAnimationSpriteObjectVisible(barrelNormal, false);
                    setAnimationSpriteObjectVisible(barrelBlue, true);
                    getAnimation().stop();
                    getAnimation().play();
                    instructionPointer = 1;
                case 1:
                    if (getAnimation().currentFrameIndex >= 22) {
                        game.spawnFirstBlueBarrel();
                        getAnimation().currentFrameIndex = 0;
                        instructionPointer = 2;
                    }
                    break yield;
                case 2:
                    barrelToSpawnIsBlue = Math.random() < 0.1 && game.getFireEnemiesCount() < 3;
                    if (barrelToSpawnIsBlue) { // blue
                        setAnimationSpriteObjectVisible(barrelNormal, false);
                        setAnimationSpriteObjectVisible(barrelBlue, true);
                    }
                    else { // normal
                        setAnimationSpriteObjectVisible(barrelNormal, true);
                        setAnimationSpriteObjectVisible(barrelBlue, false);
                    }
                    instructionPointer = 3;
                case 3:
                    if (getAnimation().currentFrameIndex >= 40) {
                        game.spawnBarrel(barrelToSpawnIsBlue);
                        instructionPointer = 4;
                    }
                case 4:
                    if (getAnimation().currentFrameIndex == getAnimation().lastFrameIndex) {
                        chooseRandomFirstAnimation();
                        waitTime = System.currentTimeMillis();
                        getAnimation().pause();
                        getAnimation().currentFrameIndex = 0;
                        instructionPointer = 5;
                    }
                    break yield;
                case 5:
                    if (System.currentTimeMillis() - waitTime < (6 - game.getCurrentLevel()) * 100) {
                        break yield;
                    }
                    getAnimation().play();
                    instructionPointer = 2;
                    break yield;
            }
        }
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
        
        if (game.getState() == State.INTRO_HOW_HIGH) {
            instructionPointer = 0;
        }
    }

}
