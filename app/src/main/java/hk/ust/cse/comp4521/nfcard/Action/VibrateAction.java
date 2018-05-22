package hk.ust.cse.comp4521.nfcard.Action;

import android.media.AudioManager;

import static hk.ust.cse.comp4521.nfcard.Activity.StartPage.audioManager;

public class VibrateAction extends Action {

    public VibrateAction(boolean startAction) {
        super(startAction, "VIBRATE");
    }

    @Override
    public void turnOn() {

        if(isStartAction()) {

            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
        else {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        }
    }
}
