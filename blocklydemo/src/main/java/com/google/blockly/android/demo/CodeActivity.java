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
public class CodeActivity extends AppCompatActivity
{
  public  String script;
  private static final int MAX_SIZE = 8192;
  public String result;
  public Button save;
  public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/block";
  public EditText editText;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
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
    return result.toString();
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
  public static void Save(File file, String data)
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