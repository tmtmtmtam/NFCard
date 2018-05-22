package hk.ust.cse.comp4521.nfcard.CardObj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.nfcard.Action.Action;
import hk.ust.cse.comp4521.nfcard.Action.OpenLinkAction;

public class Card implements Serializable {
    private String cardName, cardTag;
    private String uid;
    private String image;
    private List<Action> actions;
    private String imagePath;

    public Card(){
        actions = new ArrayList<>();
    }

    public void setCardName(String cardName){
        this.cardName = cardName;
    }

    public void setCardTag(String cardTag) {
        this.cardTag = cardTag;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImage(Bitmap image) {
        String encodedImage;
        final int COMPRESSION_QUALITY = 100;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        this.image = encodedImage;
    }

    public void setImage(String image){
        this.image = image;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void addAction(Action a){
        actions.add(a);
    }

    public void turnOn(){
        for(Action a : actions)
            a.turnOn();
    }

    public String getCardName() {
        return cardName;
    }

    public String getCardTag() {
        return cardTag;
    }

    public Bitmap getImage() {
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public String getStringImage(){
        return image;
    }

    public String getUid() {
        return uid;
    }

    public List<Action> getActions(){
        return actions;
    }

    public String getImagePath() {
        return imagePath;
    }
}
