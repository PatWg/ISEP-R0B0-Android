package com.google.blockly.android.demo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import java.io.FileNotFoundException;
import android.widget.Toast;
import java.util.Scanner;
import java.io.*;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * In this Activity the Python code gets converted into hes code and saved in the specifiled location.
 */

public class CodeActivity extends AppCompatActivity
{
  public  String script;
  private static final int MAX_SIZE = 8192;
  public String result;
  public Button save;
  public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
  public EditText editText;
  /**
   * permissions request code
   */
  private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

  /**
   * Permissions that need to be explicitly requested from end user.
   */
  private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
          Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    checkPermissions();
    setContentView(R.layout.activity_code);
    Intent intent = getIntent();
    TextView textView = (TextView)findViewById(R.id.code_text_view);
    editText = (EditText) findViewById(R.id.editText);
    save = (Button)findViewById(R.id.save);
    String code = intent.getStringExtra("generated_code");
    textView.setText(code);
    script = code;
    File dir=new File(path);
    dir.mkdirs();
    result=sendValue(script);
  }
  public String sendValue(String myscript) {
    String result=null;
    try {
      result = produceHex(myscript);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
  public String produceHex(String script) throws Exception {
    String script1 = hexlify(script);
    Scanner scanner = new Scanner(getResources().openRawResource(R.raw.runtime));
    StringBuilder runtime = new StringBuilder();
    while (scanner.hasNext()) {
      runtime.append(scanner.nextLine())
              .append("\n");
    }

    String[] run = runtime.toString().split("\n");
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < run.length - 2; i++) {
      result.append(run[i])
              .append("\n");
    }
    result.append(script1);
    result.append(run[run.length-2])
            .append("\n")
            .append(run[run.length-1]);
    return result.toString() ;
  }
  /**
   * This method is the one taken from https://github.com/bbcmicrobit/PythonEditor/blob/1df10c07a271d9597eac318aef2e5dc1259af24a/python-main.js#L68
   * All the logic is what is necessary to produce a .hex file with a Python script
   * @return The Python script as a .hex file
   * @throws Exception
   */
  public String hexlify(String script) throws Exception {
    byte[] data = new byte[4 + script.length() + (16 - (4 + script.length()) % 16)];
    data[0] = 77;
    data[1] = 80;
    data[2] = (byte) (script.length() & 0xff);
    data[3] = (byte) ((script.length() >> 8) & 0xff);
    for (int i = 0; i < script.length(); ++i) {
      data[4 + i] = (byte) script.charAt(i);
    }

    if (data.length > MAX_SIZE) {
      throw new Exception("Script is too long");
    }
    int addr = 0x3e000;
    byte[] chunk = new byte[5 + 16];

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(":020000040003F7")
            .append("\n");
    for (int i = 0; i < data.length; i += 16) {
      chunk[0] = 16;
      chunk[1] = (byte) ((addr >> 8) & 0xff);
      chunk[2] = (byte) (addr & 0xff);
      chunk[3] = 0;
      for (int j = 0; j < 16; j++) {
        chunk[4 + j] = data[i + j];
      }
      byte checksum = 0;
      for (int j = 0; j < 4 + 16; j++) {
        checksum += chunk[j];
      }
      chunk[4 + 16] = (byte) ((-checksum) & 0xff);
      stringBuilder.append(':')
              .append(hexlify(chunk).toUpperCase())
              .append("\n");
      addr += 16;
    }
    return stringBuilder.toString();
  }

  public String hexlify(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte aByte : bytes) {
      result.append(String.format("%02X", aByte));
    }
    return result.toString();
  }
  public void buttonSave (View view)
  {
    String  fname = String.valueOf(editText.getText());
    File file = new File (path + "/"+fname+".hex");

    String  saveText = result;

    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();

    Save (file, saveText);
  }

  /**
   * Checks the dynamically-controlled permissions and requests missing permissions from end user.
   */
  protected void checkPermissions() {
    final List<String> missingPermissions = new ArrayList<String>();
    // check all required dynamic permissions
    for (final String permission : REQUIRED_SDK_PERMISSIONS) {
      final int result = ContextCompat.checkSelfPermission(this, permission);
      if (result != PackageManager.PERMISSION_GRANTED) {
        missingPermissions.add(permission);
      }
    }
    if (!missingPermissions.isEmpty()) {
      // request all missing permissions
      final String[] permissions = missingPermissions
              .toArray(new String[missingPermissions.size()]);
      ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
    } else {
      final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
      Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
      onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
              grantResults);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                         @NonNull int[] grantResults) {
    switch (requestCode) {
      case REQUEST_CODE_ASK_PERMISSIONS:
        for (int index = permissions.length - 1; index >= 0; --index) {
          if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
            // exit the app if one permission is not granted
            Toast.makeText(this, "Required permission '" + permissions[index]
                    + "' not granted, exiting", Toast.LENGTH_LONG).show();
            finish();
            return;
          }
        }
        // all permissions were granted
       // initialize();
        break;
    }
  }

  private void Save(File file, String data)
  {
    FileOutputStream fos = null;
    try
    {
      fos = new FileOutputStream(file);
    }
    catch (FileNotFoundException e) {e.printStackTrace();}
    try
    {
      try
      {
        fos.write(data.getBytes());
      }
    catch (IOException e) {e.printStackTrace();}
    }
    finally
    {
      try
      {
       fos.close();
      }
      catch (IOException e) {e.printStackTrace();}
     }
  }
}