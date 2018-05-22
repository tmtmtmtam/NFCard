package hk.ust.cse.comp4521.nfcard.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import hk.ust.cse.comp4521.nfcard.Action.Action;
import hk.ust.cse.comp4521.nfcard.Action.BluetoothAction;
import hk.ust.cse.comp4521.nfcard.Action.OpenLinkAction;
import hk.ust.cse.comp4521.nfcard.Action.VibrateAction;
import hk.ust.cse.comp4521.nfcard.Action.WifiAction;
import hk.ust.cse.comp4521.nfcard.CardObj.Card;
import hk.ust.cse.comp4521.nfcard.CardObj.CardHolder;
import hk.ust.cse.comp4521.nfcard.R;

import static hk.ust.cse.comp4521.nfcard.Activity.StartPage.cardHolder;
import static hk.ust.cse.comp4521.nfcard.Activity.StartPage.customAdapter;


public class AddCardPage extends AppCompatActivity {
    private ImageView takePhotoImageView;
    private CheckBox wifiCB, vibrateCB, bluetoothCB, openLinkCB;
    private Switch wifiSwitch, vibrateSwitch, bluetoothSwitch;
    private EditText cardNameEdit, cardTagEdit, urlEdit;
    private TextView urlTextView;
    private Bitmap photo, camera;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private Button createBT;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Card Information");
        setSupportActionBar(toolbar);
        takePhotoImageView = findViewById(R.id.takePhotoImageView);

        photo = BitmapFactory.decodeResource(getResources(), R.drawable.camera1);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), photo);
        roundedBitmapDrawable.setCircular(true);
        takePhotoImageView.setImageDrawable(roundedBitmapDrawable);

        takePhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);
                }
                else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }

        });

        wifiCB = findViewById(R.id.wifiCB);
        vibrateCB = findViewById(R.id.vibrateCB);
        bluetoothCB = findViewById(R.id.bluetoothCB);
        openLinkCB = findViewById(R.id.urlCB);
        urlTextView = findViewById(R.id.urlTextView);

        wifiSwitch = findViewById(R.id.wifiSwitch);
        vibrateSwitch = findViewById(R.id.vibrateSwitch);
        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);
        urlEdit = findViewById(R.id.urlEdit);

        urlEdit.setEnabled(false);
        urlTextView.setTextColor(Color.GRAY);
        wifiSwitch.setEnabled(false);
        vibrateSwitch.setEnabled(false);
        bluetoothSwitch.setEnabled(false);
        wifiCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    wifiSwitch.setEnabled(true);
                }
                else{
                    wifiSwitch.setEnabled(false);
                }
            }
        });

        vibrateCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    vibrateSwitch.setEnabled(true);
                }
                else{
                    vibrateSwitch.setEnabled(false);
                }
            }
        });

        bluetoothCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    bluetoothSwitch.setEnabled(true);
                }
                else{
                    bluetoothSwitch.setEnabled(false);
                }
            }
        });

        openLinkCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    urlEdit.setEnabled(true);
                    urlTextView.setTextColor(Color.BLACK);
                }
                else{
                    urlEdit.setEnabled(false);
                    urlTextView.setTextColor(Color.GRAY);

                }
            }
        });

        createBT = findViewById(R.id.createObjBT);
        cardNameEdit = findViewById(R.id.cardNameEdit);
        cardTagEdit = findViewById(R.id.tagEdit);

        createBT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Card c = new Card();
                c.setCardName(cardNameEdit.getText().toString());
                c.setCardTag(cardTagEdit.getText().toString());
                c.setUid(getIntent().getStringExtra("UID"));

                if(camera != null){
                    c.setImagePath(saveToInternalStorage(camera, cardNameEdit.getText().toString()));
                }
                else{
                    photo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
                    c.setImagePath(saveToInternalStorage(photo, cardNameEdit.getText().toString()));
                }

                if(wifiCB.isChecked()){
                    Action a = new WifiAction(wifiSwitch.isChecked());
                    c.addAction(a);
                }
                if(bluetoothCB.isChecked()){
                    Action a = new BluetoothAction(bluetoothSwitch.isChecked());
                    c.addAction(a);
                }
                if(vibrateCB.isChecked()){
                    Action a = new VibrateAction(vibrateSwitch.isChecked());
                    c.addAction(a);
                }
                if(openLinkCB.isChecked()){
                    Action a = new OpenLinkAction(false, urlEdit.getText().toString());
                    c.addAction(a);
                }

                cardHolder.addCard(c);
                customAdapter.update();
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (getParent() == null)
            setResult(Activity.RESULT_OK, intent);
        else
            getParent().setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onActivityResult ( int requestCode2, int resultCode, Intent data){
        if (requestCode2 == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            camera = (Bitmap) data.getExtras().get("data");
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), camera);
            roundedBitmapDrawable.setCircular(true);
            takePhotoImageView.setImageDrawable(roundedBitmapDrawable);
            takePhotoImageView.setImageBitmap(camera);
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory,name + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
