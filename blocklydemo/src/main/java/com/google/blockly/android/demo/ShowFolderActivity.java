package com.google.blockly.android.demo;


import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.bluetooth.BluetoothAdapter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.ListActivity;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
/**
 * The Selected files is send to the connected device via bluetooth.
 */
public class ShowFolderActivity extends ListActivity implements AdapterView.OnItemClickListener {
    ListView lv;
    private List<String> fileList = new ArrayList<String>();
    public BluetoothAdapter myBluetooth = null;
    String address = null;
    private BluetoothSocket btSocket = null;
    private static final String TAG = "BluetoothActivity";
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
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
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/folder");
        lv = getListView();
        ListDir(root);
        ListView listView = getListView();
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
       /* Uri path = FileProvider.getUriForFile(this,"com.mydomain.fileprovider",new File(filename));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM ,path);
        intent.setType("image/*");
        intent.setPackage("com.android.bluetooth");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Toast.makeText(ShowFolderActivity.this, "Sending File to ......",Toast.LENGTH_SHORT).show();
        startActivity(intent);*/





 }
}


