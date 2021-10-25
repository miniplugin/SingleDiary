package org.techtown.diary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 메모 데이터베이스
 */
public class NoteDatabase {
    private static final String TAG = "NoteDatabase";//Log.d(검색태그명으로 사용)
    private static NoteDatabase database;//싱글톤 객체(인스턴스): 즉, 객체를 1번만 생성하겠다고 명시
    public static String TABLE_NOTE = "NOTE";//테이블 이름
    public static int DATABASE_VERSION = 1;//버전
    private DatabaseHelper dbHelper;//DB헬퍼 클래스 객체사용
    private SQLiteDatabase db;//SQLiteDatabase 객체사용
    private Context context;//컨텍스트 객체

    private NoteDatabase(Context context) {//생성자
        this.context = context;
    }

    //인스턴스 가져오기: 싱글톤 객체 만드는 고유한 방식(아래)
    public static NoteDatabase getInstance(Context context) {
        if (database == null) {
            database = new NoteDatabase(context);
        }
        return database;
    }

    //데이터베이스 파일 열기
    public boolean open() {
        Log.d(TAG,"opening database [" + AppConstants.DATABASE_NAME + "].");
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return true;
    }

    //데이터베이스 객체 닫기
    public void close() {
        Log.d(TAG,"closing database [" + AppConstants.DATABASE_NAME + "].");
        db.close();
        database = null;
    }

    /**
     * raw 레코드 데이터(커서) 생성 매서드
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
        Log.d(TAG,"executeQuery called.");
        Cursor c1 = null;
        try {
            c1 = db.rawQuery(SQL, null);
            Log.d(TAG,"cursor count : " + c1.getCount());
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
        }
        return c1;
    }

    public boolean execSQL(String SQL) {
        Log.d(TAG,"\nexecute called.\n");
        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch(Exception ex) {
            Log.e(TAG, "Exception in executeQuery", ex);
            return false;
        }
        return true;
    }

    //SQLiteOpenHelper 객체 사용
    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, AppConstants.DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "creating database [" + AppConstants.DATABASE_NAME + "].");
            // 테이블 생성 로직 시작
            Log.d(TAG, "creating table [" + TABLE_NOTE + "].");
            String DROP_SQL = "drop table if exists " + TABLE_NOTE;
            try {
                db.execSQL(DROP_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);
            }
            //테이블 생성
            String CREATE_SQL = "create table " + TABLE_NOTE + "("
                    + "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + "  WEATHER TEXT DEFAULT '', "
                    + "  ADDRESS TEXT DEFAULT '', "
                    + "  LOCATION_X TEXT DEFAULT '', "
                    + "  LOCATION_Y TEXT DEFAULT '', "
                    + "  CONTENTS TEXT DEFAULT '', "
                    + "  MOOD TEXT, "
                    + "  PICTURE TEXT DEFAULT '', "
                    + "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "  MODIFY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_SQL", ex);
            }

            //인덱스 생성
            String CREATE_INDEX_SQL = "create index " + TABLE_NOTE + "_IDX ON " + TABLE_NOTE + "("
                    + "CREATE_DATE"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
                Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
            }
        }

        public void onOpen(SQLiteDatabase db) {
            Log.d(TAG,"opened database [" + AppConstants.DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG,"Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }

    private void println(String msg) {
        Log.d(TAG, msg);
    }

}