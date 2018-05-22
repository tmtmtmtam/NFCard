package hk.ust.cse.comp4521.nfcard.Action;

import android.bluetooth.BluetoothAdapter;

public class BluetoothAction extends Action {

    public BluetoothAction(boolean startAction) {
        super(startAction, "BLUETOOTH");
    }

    @Override
    public void turnOn() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(isStartAction()) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }
        else{
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }
}
