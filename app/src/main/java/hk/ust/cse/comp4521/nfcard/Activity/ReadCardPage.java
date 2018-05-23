package hk.ust.cse.comp4521.nfcard.Activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import hk.ust.cse.comp4521.nfcard.Action.Action;
import hk.ust.cse.comp4521.nfcard.Action.OpenLinkAction;
import hk.ust.cse.comp4521.nfcard.CardObj.Card;
import hk.ust.cse.comp4521.nfcard.R;

import static hk.ust.cse.comp4521.nfcard.Activity.StartPage.cardHolder;

public class ReadCardPage extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView uidTextView, detectTextView;
    private PendingIntent mPendingIntent;
    private Button nextPageBT;
    private ImageView imageView;
    private byte[] uid;
    private int REQUEST_CODE = 1;
    StringBuilder string;
    private ColorMatrix cm;
    private ColorMatrixColorFilter grayColorFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_card_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Read Card");
        setSupportActionBar(toolbar);

        uidTextView = findViewById(R.id.uidTextView);
        detectTextView = findViewById(R.id.detectTextView);
        imageView = findViewById(R.id.readCardImage);
        imageView.setImageResource(R.drawable.nfcon);

        cm = new ColorMatrix();
        cm.setSaturation(0);
        grayColorFilter = new ColorMatrixColorFilter(cm);
        imageView.setColorFilter(grayColorFilter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nextPageBT = findViewById(R.id.nextPageBT);
        nextPageBT.setText("Next");
        nextPageBT.setEnabled(false);

        nextPageBT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), AddCardPage.class);
                intent.putExtra("UID", string.toString());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


    @Override
    public void onResume() {
        super.onResume();
        nextPageBT.setVisibility(View.VISIBLE);
        nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction())){
            imageView.setColorFilter(null);
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
            if(cardHolder.contentCard(string.toString())){
                Card temp = cardHolder.getCard(string.toString());
                detectTextView.setText("Card " + temp.getCardName() + " turned on");
                uidTextView.setText(temp.getCardTag());
                loadImageFromStorage(temp.getImagePath(), imageView, temp.getCardName());
                nextPageBT.setVisibility(View.INVISIBLE);
                temp.turnOn();
                for(Action a : temp.getActions()){
                    if(a instanceof OpenLinkAction) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(((OpenLinkAction) a).getUrl()));
                        startActivityForResult(i, 9999);
                    }
                }
            }
            else{
                imageView.setColorFilter(null);
                detectTextView.setText("Detect an NFC Tag");
                uidTextView.setText("UID: " + string);
                nextPageBT.setEnabled(true);
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }


    private void loadImageFromStorage(String path, ImageView imageView, String name ){
        try {
            File f = new File(path, name + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    public void show(String s){

        new AlertDialog.Builder(this).setTitle("Error").setMessage(s)
                .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAndRemoveTask();
                    }
                }).show();
    }

}
