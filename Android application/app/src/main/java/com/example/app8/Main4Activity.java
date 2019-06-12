package com.example.app8;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main4Activity extends AppCompatActivity {

    DBHelper dbHelper = new DBHelper(this, "Resident", null, 1);;
//    DBHelper dbHelper = new DBHelper(getApplicationContext(), "Resident", null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        System.out.println("check");
        System.out.println(dbHelper.getResult());
        TextView result = (TextView) findViewById(R.id.result);
        result.setText(dbHelper.getResult());
    }
}