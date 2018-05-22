package hk.ust.cse.comp4521.nfcard.Action;

import android.content.Intent;
import android.net.Uri;

public class OpenLinkAction extends Action {
    private String url;

    public OpenLinkAction(boolean startAction, String url){
        super(startAction, "OPEN");
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        this.url = url;
    }

    @Override
    public void turnOn() {  }

    public String getUrl() {
        return url;
    }
}
