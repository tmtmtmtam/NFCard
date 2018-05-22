package hk.ust.cse.comp4521.nfcard.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import hk.ust.cse.comp4521.nfcard.Action.Action;
import hk.ust.cse.comp4521.nfcard.Action.BluetoothAction;
import hk.ust.cse.comp4521.nfcard.Action.OpenLinkAction;
import hk.ust.cse.comp4521.nfcard.Action.VibrateAction;
import hk.ust.cse.comp4521.nfcard.Action.WifiAction;
import hk.ust.cse.comp4521.nfcard.CardObj.Card;
import hk.ust.cse.comp4521.nfcard.CardObj.CardHolder;
import hk.ust.cse.comp4521.nfcard.R;


public class StartPage extends AppCompatActivity {
    private ListView listView;
    private NfcAdapter nfcAdapter;
    private FloatingActionButton menuFAB, addCardFAB, storageFAB;
    private Animation openFAB, closeFAB, clockwiseFAB, anticlockwiseFAB;
    boolean isOpen = false;
    public static CustomAdapter customAdapter;
    private ImageView noCardImage;
    public static WifiManager wifiManager;
    public static AudioManager audioManager;
    public static CardHolder cardHolder;
    public static Intent openLinkIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Start Page");
        setSupportActionBar(toolbar);

        init();
        // check
        if (nfcAdapter == null) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage("This device does not support AR NFC.")
                    .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAndRemoveTask();
                        }
                    }).show();
        } else if (!nfcAdapter.isEnabled()) {
            new AlertDialog.Builder(this).setTitle("Error").setMessage("NFC is disable, Pls turn on.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAndRemoveTask();
                        }
                    })
                    .setPositiveButton("Go to Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            else
                                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    }).show();
        }
        updateListView();

        listView.setAdapter(customAdapter);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        customAdapter.update();
        saveData();
    }

    public void updateListView(){
        if(cardHolder.getCardsSize() == 0){
            noCardImage.setVisibility(View.VISIBLE);
        }
        else{
            noCardImage.setVisibility(View.INVISIBLE);
       }
    }

    private void fabClose(){
        addCardFAB.startAnimation(closeFAB);
        storageFAB.startAnimation(closeFAB);
        menuFAB.startAnimation(anticlockwiseFAB);
        addCardFAB.setClickable(false);
        storageFAB.setClickable(false);
        isOpen = false;
    }


    public class CustomAdapter extends BaseAdapter {
        private void loadImageFromStorage(String path, ImageView imageView, String name )
        {

            try {
                File f = new File(path, name + ".jpg");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imageView.setImageBitmap(b);
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }

        }
        @Override
        public int getCount() {
            return cardHolder.getCardsSize();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_listview, null);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            TextView cardName = (TextView) convertView.findViewById(R.id.cardName);
            TextView tag = (TextView) convertView.findViewById(R.id.tag);

            loadImageFromStorage(cardHolder.getCard(i).getImagePath(), imageView, cardHolder.getCard(i).getCardName());
            cardName.setText(cardHolder.getCard(i).getCardName());
            tag.setText(cardHolder.getCard(i).getCardTag());

            return convertView;
        }

        public void update() {
            updateListView();
            this.notifyDataSetChanged();
        }
    }

    private void init(){
        noCardImage = findViewById(R.id.noCardImage);
        menuFAB = findViewById(R.id.showMoreFAB);
        addCardFAB = findViewById(R.id.addCardFAB);
        storageFAB = findViewById(R.id.saveAndLoadFAB);

        openFAB = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        closeFAB = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        clockwiseFAB = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise);

        anticlockwiseFAB = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anticlockwise);

        menuFAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isOpen){
                    fabClose();
                }
                else{
                    addCardFAB.startAnimation(openFAB);
                    storageFAB.startAnimation(openFAB);
                    menuFAB.startAnimation(clockwiseFAB);
                    addCardFAB.setClickable(true);
                    storageFAB.setClickable(true);
                    isOpen = true;
                }
            }
        });

        storageFAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), StoragePage.class);
                fabClose();
                startActivity(intent);
            }
        } );


        addCardFAB.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ReadCardPage.class);
                fabClose();
                startActivity(intent);
            }
        } );

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        cardHolder = new CardHolder();

        listView = findViewById(R.id.ListView);

        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        openLinkIntent = new Intent(Intent.ACTION_VIEW);
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();

    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("my data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cardHolder.getCardsSize());
        editor.putString("card size", json);
        editor.commit();
        for(int i = 0; i < cardHolder.getCardsSize(); i++){
            json = gson.toJson(cardHolder.getCard(i).getCardName());
            editor.putString("card " + i + " name", json);
            editor.commit();
            json = gson.toJson(cardHolder.getCard(i).getCardTag());
            editor.putString("card " + i + " tag", json);
            editor.commit();

            json = gson.toJson(cardHolder.getCard(i).getStringImage());
            editor.putString("card " + i + " image", json);
            editor.commit();


            json = gson.toJson(cardHolder.getCard(i).getImagePath());
            editor.putString("card " + i + " image path", json);
            editor.commit();

            json = gson.toJson(cardHolder.getCard(i).getUid());
            editor.putString("card " + i + " uid", json);
            editor.commit();

            json = gson.toJson(cardHolder.getCard(i).getActions().size());
            editor.putString("card " + i + " action size", json);
            editor.commit();

            for(int a = 0; a < cardHolder.getCard(i).getActions().size(); a++){
                json = gson.toJson(cardHolder.getCard(i).getActions().get(a).getModeName());

                editor.putString("card " + i + " action " + a + "mode", json);
                editor.commit();
                if(cardHolder.getCard(i).getActions().get(a) instanceof OpenLinkAction){
                    json = gson.toJson(((OpenLinkAction)cardHolder.getCard(i).getActions().get(a)).getUrl());
                    editor.putString("card " + i + " action " + a + "url", json);
                    editor.commit();
                }

                json = gson.toJson(cardHolder.getCard(i).getActions().get(a).isStartAction());
                editor.putString("card " + i + " action " + a + " start action", json);
                editor.commit();
            }
        }
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("my data", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("card size", null);
        if(json != null) {
            if(Integer.parseInt(json) > 0) {
                for(int i = 0; i < Integer.parseInt(json); i++) {
                    Card temp = new Card();
                    temp.setCardName(removeFirstAndLastChar(sharedPreferences.getString("card " + i + " name", null)));
                    temp.setCardTag(removeFirstAndLastChar(sharedPreferences.getString("card " + i + " tag", null)));
                    temp.setUid(removeFirstAndLastChar(sharedPreferences.getString("card " + i + " uid", null)));
                    temp.setImagePath(removeFirstAndLastChar(sharedPreferences.getString("card " + i + " image path", null)));

                    String str = sharedPreferences.getString("card " + i + " action size", null);
                    if (str != null) {
                        if(Integer.parseInt(str) > 0 && Integer.parseInt(str) <= 4){
                            for(int a = 0; a < Integer.parseInt(str); a++){
                                Action action;
                                String mode = removeFirstAndLastChar(sharedPreferences.getString("card " + i + " action " + a + "mode", null));
                                String startAction = sharedPreferences.getString("card " + i + " action " + a + " start action", null);
                                if(mode.equals("WIFI")){
                                    if(startAction.equals("true")){
                                        action = new WifiAction(true);
                                    }
                                    else{
                                        action = new WifiAction(false);
                                    }
                                }
                                else if (mode.equals("VIBRATE")) {
                                    if(startAction.equals("true")){
                                        action = new VibrateAction(true);
                                    }
                                    else{
                                        action = new VibrateAction(false);
                                    }
                                }
                                else if (mode.equals("BLUETOOTH")) {
                                    if(startAction.equals("true")){
                                        action = new BluetoothAction(true);
                                    }
                                    else{
                                        action = new BluetoothAction(false);
                                    }
                                }
                                else if (mode.equals("OPEN")) {
                                    String url = removeFirstAndLastChar(sharedPreferences.getString("card " + i + " action " + a + "url", null));

                                    action = new OpenLinkAction(true, url);
                                }
                                else {
                                    action = new BluetoothAction(true);
                                }
                                if(action != null)
                                    temp.addAction(action);
                            }
                        }
                    }
                    cardHolder.addCard(temp);
                }
            }
        }
    }

    public String removeFirstAndLastChar(String string){
        int i = string.length();
        return string.substring(1, i - 1);

    }
}
