package hk.ust.cse.comp4521.nfcard.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import hk.ust.cse.comp4521.nfcard.R;

public class RunCardPage extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent mPendingIntent;
    private byte[] uid;

    StringBuilder string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_card_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        uid = tag.getId();
        string = new StringBuilder();
        for (int i = uid.length - 1; i >= 0; i--) {
            int b = uid[i] & 0xff;
            if (b < 0x10)
                string.append('0');
            string.append(Integer.toHexString(b));
            if (i > 0) {
                string.append(" ");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    public void onPause(){
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }
}
