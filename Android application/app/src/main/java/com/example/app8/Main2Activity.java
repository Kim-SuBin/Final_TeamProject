package com.example.app8;


import android.os.Handler;
import android.os.Message;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main2Activity extends AppCompatActivity {
    //    public static final String EXTRA_MESSAGE = "com.example.APP5.PASSWORD"
    Intent intent2;
    //서버 소켓통신 환경설정
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 5001;
    private final String ip = "18.220.63.77";
    private MyHandler myHandler;
    private MyThread myThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        intent2 = new Intent(getApplicationContext(),Main3Activity.class);//비번버튼눌렀을때 페이지 넘기는 intent

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //서버와 통신시작
        try {
            clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socketOut.println("P2");//서버에게 페이지 알림

        myHandler = new MyHandler();
        myThread = new MyThread();
        myThread.start();

        Button btn = (Button) findViewById(R.id.password);//버튼 변수 설정
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //패스워드확인버튼 눌렀을때 : textview의 값받아와서 비밀번호와 확인!
                EditText text_pw = (EditText) findViewById(R.id.pwtext);//안드로이드 위젯(글자입력)을 쓸꺼다
                String pw = text_pw.getText().toString();//pw문자열변수에 위의 text_pw에서 텍스트를 가져와 문자열로 바꿔서 사용
                socketOut.println("?"+pw);
            }
        });
    }
    class MyThread extends Thread {
        boolean stop = false;

        public void stopThread() {
            stop = true;
        }
        @Override
        public void run() {
            while (true) {
                try {
                    // InputStream의 값을 읽어와서 data에 저장
                    String data = socketIn.readLine();
                    // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                    Message msg = myHandler.obtainMessage();
                    msg.obj = data;
                    myHandler.sendMessage(msg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if(msg.obj.toString().equals("confirm")) {//보낸 비번이 인증확인이면,
                startActivity(intent2);//다음페이지로 넘어감
            }
            else{
                new AlertDialog.Builder(Main2Activity.this)
                        .setTitle("오류 : 동 비밀번호가 틀립니다")
                        .setMessage("\n\n 비밀번호를 다시 입력하십시오")
                        .setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        })
                        .show();
            }
        }
    }
    public void buttonback(View v){
        finish();
    }
}