package com.example.app8;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //블루투스 소켓통신 환경설정
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;
    final static int BT_MESSAGE_READ = 2;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //서버 소켓통신 환경설정
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 5555;
    private final String ip = "18.220.63.77";
    private MyHandler myHandler;
    private MyThread myThread;

    // DB를 위한 환경설정
    public static final String DB_name = "Resident.db";
    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(getApplicationContext(), DB_name, null, 1);

        //서버소켓통신 환경설정
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothrasberry();
    }

    //실행코드
    void bluetoothrasberry() {//if문을 while 로 고침
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();
            //라즈베리파이가 있으면 항상 연결
            System.out.println("check1");
            connectSelectedDevice("raspberrypi");
            mThreadConnectedBluetooth.write("hello I'm App");//블루투스 소켓 연결시 처음으로 보내지는 데이터

        }
    }

    //라즈베리파이에서 받아온 데이터값이 DB같으면 실행되는 서버소켓통신코드
    void runserver(){
        //readMessage가 DB의 uuid값과 일치하면
        //서버와 통신시작
        try {
            clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        socketOut.println("DOOR");//서버에게 페이지 알림

        myHandler = new MyHandler();
        myThread = new MyThread();
        myThread.start();
        System.out.println(dbHelper.openDoor());
        socketOut.println(dbHelper.openDoor());
//        socketOut.println("1"+"/"+"Kimsubin"+"/"+"010-1111-1111"+"/"+"111111-111111"+"&");
    }

    //서버소켓통신 Thread
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
                    Message servermsg = myHandler.obtainMessage();
                    servermsg.obj = data;
                    myHandler.sendMessage(servermsg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //서버소켓통신 Handler
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message servermsg) {
            String a = servermsg.obj.toString();
            String reala = a.substring(0,7);
            if(reala.equals("confirm")) {//보낸 비번이 인증확인이면,
                //서버 소켓 닫아주기
                myThread.stopThread();
                mThreadConnectedBluetooth.cancel();
                //               bluetoothrasberry();
                System.out.println(reala);
            }
        }
    }

    //블루투스 연결하는 메소드
    void connectSelectedDevice(String selectedDeviceName) {
        for (BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;
                System.out.println(tempDevice.getName());
                break;
            }
        }//실제로 블루투스 장치와 연결하는 부분이다.
        // 우리가 연결에 필요한 값은 장치의 주소이다.
        // 따라서 for 문으로 페어링된 모든 장치를 검색을 하면서 매개 변수 값과 비교하여 같다면 그 장치의 주소 값을 얻어오는 부분이다.
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mBluetoothHandler = new ConnectedBluetoothHandler();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            Toast.makeText(getApplicationContext(), "라즈베리파이와 연결되었습니다.", Toast.LENGTH_LONG).show();
//            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }//mBluetoothDevice를 통해 createRfcommSocketToServiceRecord(UUID)를 호출하여 mBluetoothSocket을 가져온다. (참고로 여기서 사용된 UUID 값은 시리얼 통신용이다.)
    //그러면 mBluetoothDevice에 연결 될 mBluetoothSocket이 초기화된다. 그 후 connect()를 호출하여 연결을 시작한다.


    //ConnectedBluetoothThread 쓰레드
    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //ConnectedBluetoothThread 쓰레드의 시작이며 이 쓰레드에서 사용할 전역 객체들을 선언하였다.
        // 위에서 사용한 소켓이 이미 메인 액티비티 자체의 소켓이니 그대로 사용해도 되지만 쓰레드 내부 자체에서만 사용할 소켓 객체를 추가하였다.
        //
        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
//                        Message msg = mBluetoothHandler.obtainMessage();
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    //ConnectedBluetoothHandler 핸들러
    private class ConnectedBluetoothHandler extends Handler{
        public void handleMessage( android.os.Message msg) {
            if (msg.what == BT_MESSAGE_READ) {
                String readMessage = null;
                try {
                    readMessage = new String((byte[]) msg.obj, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("블루투스핸들러");
                String receiveuuid = readMessage.substring(0,36);
                if (dbHelper.getUuid().equals(receiveuuid)){

                    runserver();
                }
            }
        }
    }



    //다음창으로 넘어가기 버튼
    public void apart(View v) {
        Intent intent = new Intent(this, Main0Activity.class);
        startActivity(intent);
    }
}