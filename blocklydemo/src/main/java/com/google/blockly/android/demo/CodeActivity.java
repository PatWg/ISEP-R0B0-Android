package com.google.blockly.android.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import com.google.blockly.android.HexCode;

public class CodeActivity extends HexCode
{
  public CodeActivity() {}

  private Button but1;

  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_code);
    Intent intent = getIntent();
    TextView textView = (TextView)findViewById(R.id.code_text_view);

    String code = intent.getStringExtra("generated_code");
    textView.setText(code);


    but1=(Button)findViewById(R.id.but1);
    but1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hexcode();
      }
    });
  }
  public void hexcode(){
    Intent hex= new Intent(this,HexActivity.class);
    startActivity(hex);
  }

  public void produceHex(){


  }
}