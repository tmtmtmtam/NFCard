package hk.ust.cse.comp4521.nfcard.Action;

import android.app.Activity;

import java.io.Serializable;

public abstract class Action extends Activity implements Serializable{
    private boolean startAction;
    protected String modeName;

    public  Action(boolean startAction, String modeName){
        this.modeName = modeName;
        this.startAction = startAction;
    }
    public abstract void turnOn();
    public boolean isStartAction() {
        return startAction;
    }
    public String getModeName(){
        return modeName;
    }
}
