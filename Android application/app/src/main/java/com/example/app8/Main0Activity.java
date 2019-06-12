package com.example.app8;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Main0Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main0);
    }
    public void apart1(View v){
        Intent intent0 = new Intent(this, Main1Activity.class);
        startActivity(intent0);
    }
    public void buttonback(View v){
        finish();
    }
}