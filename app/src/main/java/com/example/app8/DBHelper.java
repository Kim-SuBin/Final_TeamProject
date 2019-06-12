package com.example.app8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

//    private static volatile DBHelper dbHelper;
//
//    public static DBHelper getInstance(Context context) {
//        if (dbHelper == null) {
//            synchronized (DBHelper.class) {
//                if (dbHelper == null) {
//                    dbHelper = new DBHelper(context, "Resident", null, 1);
//                }
//            }
//        }
//        return dbHelper;
//    }

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성할 때 호출되는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블 생성
        /* 이름은 Resident이고, 자동으로 값이 증가하는 _id 정수형 기본키 컬럼과
        item 문자열 컬럼, price 정수형 컬럼, create_at 문자열 컬럼으로 구성된 테이블을 생성. */
        db.execSQL("CREATE TABLE Resident (_id INTEGER PRIMARY KEY AUTOINCREMENT, ho TEXT, name TEXT, phone TEXT, ide TEXT, uuid TEXT);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void DBClear() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DROP TABLE Resident");
        onCreate(db);
    }

    public void insert(String ho, String name, String phone, String ide, String uuid) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO Resident VALUES(null, '" + ho + "', '" + name + "', '" + phone + "', '" + ide + "', '" + uuid + "');");
//        db.execSQL("INSERT INTO Resident (ho, name, phone, ide, uuid) Values ('" + ho + "', '" + name + "', '" + phone + "', '" + ide + "', '" + uuid +"');");
        db.close();
    }

    public void update(String ho, String name, String phone, String ide, String uuid) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 가격 정보 수정
        db.execSQL("UPDATE Resident SET ho='" + ho + "', phone='" + phone + "', ide ='" + ide + "', uuid ='" + uuid + " WHERE name='" + name + "';");
        db.close();
    }


    public void delete(String name) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM Resident WHERE name='" + name + "';");
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("Delete from Resident");
        db.close();
    }

    public String getUuid() {
        //uuid 조회
        SQLiteDatabase db = getReadableDatabase();
        String result ="";
        //DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 uuid 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Resident", null);
        while (cursor.moveToNext()) {
            result = cursor.getString(5);
        }
        return result;

    }
    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Resident", null);
        while (cursor.moveToNext()) {
            result += cursor.getString(0)
                    + ". "
                    + cursor.getString(1)
                    + "호 - 이름 : "
                    + cursor.getString(2)
                    + ", 전화번호 : "
                    + cursor.getString(3)
                    + ", 주민등록번호 : "
                    + cursor.getString(4)
                    + ". UUID : "
                    + cursor.getString(5)
                    + "\n";
        }


        return result;
    }
        public String openDoor() {
            // 읽기가 가능하게 DB 열기
            SQLiteDatabase db = getReadableDatabase();
            String result = "";

            // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
            Cursor cursor = db.rawQuery("SELECT * FROM Resident", null);
            while (cursor.moveToNext()) {
                result += cursor.getString(1)
                        + "/"
                        + cursor.getString(2)
                        + "/"
                        + cursor.getString(3)
                        + "/"
                        + cursor.getString(4)
                        + "&";
            }


            return result;
    }

}
