package com.google.blockly.android.demo;

import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.widget.ListView;
import android.widget.Toast;
import android.net.Uri;

import android.content.Intent;
import android.content.IntentFilter;

import junit.framework.Test;

public class ShowFolderActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    private List<String> fileList = new ArrayList<String>();
    private FilenameFilter textFilter = new FilenameFilter() {
        @Override
        public boolean accept(File file, String s) {
            return s.toLowerCase().endsWith(".hex");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_folder);
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/folder");
        lv = getListView();
        ListDir(root);
        ListView listView = getListView();
        // Setup listerner for clicks
        listView.setOnItemClickListener(this);

    }
    @Override
    public void onItemClick(AdapterView<?> adapter, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        String item = adapter.getItemAtPosition(position).toString();
        Toast.makeText(ShowFolderActivity.this, "CLICK: " + item, Toast.LENGTH_SHORT).show();
        sendfile(item);
    }

    void ListDir(File f) {
        File[] files = f.listFiles(textFilter);
        fileList.clear();
        for (File file : files) {
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        setListAdapter(directoryList);
    }

    public void sendfile(String filename) {
        // TODO Auto-generated method stub
        File file = new File(filename);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.setPackage("com.android.bluetooth");
        Uri outputFileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
        intent.putExtra(Intent.EXTRA_STREAM,outputFileUri);
        startActivity(intent);
    }
}
