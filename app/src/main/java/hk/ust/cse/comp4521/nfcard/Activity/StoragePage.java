package hk.ust.cse.comp4521.nfcard.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import hk.ust.cse.comp4521.nfcard.CardObj.CardHolder;
import hk.ust.cse.comp4521.nfcard.R;

import static hk.ust.cse.comp4521.nfcard.Activity.StartPage.cardHolder;

public class StoragePage extends AppCompatActivity {
    private Button btImport, btExport;
    private static final int REQUEST_IMPORT = 999;
    private static final int REQUEST_EXPORT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Data Import and Export");
        setSupportActionBar(toolbar);
        btExport = findViewById(R.id.exportBT);
        btImport = findViewById(R.id.importBT);

        btExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, REQUEST_EXPORT);
            }
        });

        btImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new  Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, REQUEST_IMPORT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMPORT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String uri = data.getData().toString();

                new AlertDialog.Builder(this).setTitle("Error").setMessage(uri)
                        .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finishAndRemoveTask();
                            }
                        }).show();
                try {
                    FileOutputStream fos = new FileOutputStream(uri);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(cardHolder);
                    oos.flush();
                    oos.close();
                } catch (Exception e) {

                }
            }
        }
        if(requestCode == REQUEST_EXPORT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String uri = data.getData().toString();
                new AlertDialog.Builder(this).setTitle("Error").setMessage(uri)
                        .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finishAndRemoveTask();
                            }
                        }).show();
                try {
                    FileInputStream fis = new FileInputStream(uri + System.currentTimeMillis() + ".comp4521");
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    cardHolder = (CardHolder) ois.readObject();
                    ois.close();
                }
                catch (Exception e) {

                }
            }
        }
    }
}
