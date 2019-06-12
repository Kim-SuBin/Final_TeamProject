package com.example.app8;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;


public class Main3Activity extends AppCompatActivity {
    Intent intent3;
    //서버 소켓통신 환경설정
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 5001;
    private final String ip = "18.220.63.77";
    private MyHandler myHandler;
    private MyThread myThread;

    // handler에서 사용하기위한 전역변수
    String inf1, inf2, inf3, inf4;

    // DB를 위한 환경설정
    public static final String DB_name = "Resident.db";
    public DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        dbHelper = new DBHelper(getApplicationContext(), DB_name, null, 1);


        intent3 = new Intent(getApplicationContext(),Main4Activity.class);//다음페이지로 넘어가는 intent

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
        socketOut.println("P3");//서버에게 페이지 알림

        myHandler = new Main3Activity.MyHandler();
        myThread = new Main3Activity.MyThread();
        myThread.start();

        Button infbut = (Button) findViewById(R.id.infbutton);//버튼 변수 설정
        infbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //거주자정보확인버튼 눌렀을때 : textview의 값받아와서 비밀번호와 확인!
                EditText text_inf1 = (EditText) findViewById(R.id.inf1);
                inf1 = text_inf1.getText().toString();
                EditText text_inf2 = (EditText) findViewById(R.id.inf2);
                inf2 = text_inf2.getText().toString();
                EditText text_inf3 = (EditText) findViewById(R.id.inf3);
                inf3 = text_inf3.getText().toString();
                EditText text_inf4 = (EditText) findViewById(R.id.inf4);
                inf4 = text_inf4.getText().toString();

                //서버로 보내기
                socketOut.println(inf1+"/"+inf2+"/"+inf3+"/"+inf4+"#");
            }
        });

    }
    class MyThread extends Thread {
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
            String servermsg = msg.obj.toString();//서버에서 준 데이터는 인증결과+UUID값이다.
            String servermsg_confirm=servermsg.substring(0,7);//서버에서 준 데이터중 인증결과만 뜻하는 변수
            String servermsg_uuid = servermsg.substring(8);

            //보낸 거주자 정보가 웹디비랑 인증확인이면,
            if(servermsg_confirm.equals("confirm") && inf1 != null && inf2 != null && inf3 != null && inf4 != null) {
                dbHelper.insert(inf1, inf2, inf3, inf4, servermsg_uuid);
                System.out.println(dbHelper.getResult());
                System.out.println(dbHelper.getUuid());
                System.out.println("dbconfirm");
                startActivity(intent3);//다음페이지로 넘어감

            }
            else{
                new AlertDialog.Builder(Main3Activity.this)
                        .setTitle("오류 : 거주자 정보가 틀립니다")
                        .setMessage("\n\n 거주자 정보를 다시 입력하십시오")
                        .setNeutralButton("닫기", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        })
                        .show();
            }
        }
    }
    public void buttonback2(View v){
        finish();
    }
}