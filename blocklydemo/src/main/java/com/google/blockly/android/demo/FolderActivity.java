package com.google.blockly.android.demo;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * In this Activity a Button is created and onclick it makes to show all the files.
 */
public class FolderActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        Intent intent = getIntent();
        TextView textView = (TextView)findViewById(R.id.text_view);
        String title = intent.getStringExtra("title");
        textView.setText(title);
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        Toast.makeText(FolderActivity.this, "CLICK: " + root, Toast.LENGTH_SHORT).show();
    }
    public void button (View view)
    {
        Intent  intent = new Intent(this, ShowFolderActivity.class);
        startActivity(intent);
    }
}
