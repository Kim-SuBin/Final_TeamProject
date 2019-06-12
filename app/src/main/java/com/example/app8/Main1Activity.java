package com.example.app8;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
    }
    public void button101(View v){
        Intent intent1 = new Intent(this, Main2Activity.class);
        startActivity(intent1);
    }
    public void button102(View v){
        Intent intent1 = new Intent(this, Main2Activity.class);
        startActivity(intent1);
    }
    public void button103(View v){
        Intent intent1 = new Intent(this, Main2Activity.class);
        startActivity(intent1);
    }
    //뒤로가기 버튼
    public void buttonback(View v){
        finish();
    }
}