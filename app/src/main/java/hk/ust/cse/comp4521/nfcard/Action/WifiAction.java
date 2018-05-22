package hk.ust.cse.comp4521.nfcard.Action;



import static hk.ust.cse.comp4521.nfcard.Activity.StartPage.wifiManager;

public class WifiAction extends Action {

    public WifiAction(boolean startAction) {
        super(startAction, "WIFI");
    }

    @Override
    public void turnOn() {
        if(isStartAction()) {
            if(!wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(true);
        }
        else{
            if(wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(false);
        }
    }

}
