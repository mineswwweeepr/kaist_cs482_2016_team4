package kr.ac.kaist.vclab.bubble.models;

import kr.ac.kaist.vclab.bubble.environment.GameEnv;

/**
 * Created by mnswpr on 11/23/2016.
 */

// FIXME SG
public class Item extends BubbleSphere {

    // FIXME HOW TO MAKE IT GLOW?
    // FIXME HAVING TO CHANGE COLOR AS RANDOM

    private float center[];
    private boolean isHitted;
    private int itemType;

    public Item(float[] _center){
        super(GameEnv.getInstance().radiusOfItem, 2);
        center = _center;
        isHitted = false;
        itemType = 1; // 0: BAD, 1: GOOD
    }

    public boolean getHitstatus(){
        return isHitted;
    }
    public void updateHitStatus(){
        isHitted = true;
        makeItInvisible();
    }
    // FIXME RE-IMPLEMENT IT NOT TO DRAW
    private void makeItInvisible(){
        this.setVertices(new float[]{0f,0f,0f});
    }
}
