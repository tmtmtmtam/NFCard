package hk.ust.cse.comp4521.nfcard.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import hk.ust.cse.comp4521.nfcard.R;

public class StoragePage extends AppCompatActivity {
    private Button btImport, btExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Data Import and Export");
        setSupportActionBar(toolbar);
        btExport = findViewById(R.id.exportBT);
        btImport = findViewById(R.id.importBT);
    }

}
