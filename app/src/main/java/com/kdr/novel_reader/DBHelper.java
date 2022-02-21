package com.kdr.novel_reader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.provider.BaseColumns;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;  // 데이터베이스 스키마를 변경하는 경우 데이터베이스 버전을 증가시켜야 합니다.
    public static final String DATABASE_NAME = "userdict.db"; // 데이터베이스 이름

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FeedEntry.SQL_CREATE_ENTRIES); //테이블 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(FeedEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_dict"; //테이블 명
        public static final String COLUMN_NAME_JA = "ja_name"; //컬럼 명
        public static final String COLUMN_NAME_KO = "ko_name"; //컬럼 명

        //테이블 생성 쿼리
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        FeedEntry.COLUMN_NAME_JA + " TEXT," +
                        FeedEntry.COLUMN_NAME_KO + " TEXT)";
        //DROP TABLE 테이블을 삭제 쿼리
        //IF EXISTS 절을 사용하면 삭제하려는 데이터베이스나 테이블이 존재하지 않아서 발생하는 에러를 미리 방지.
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    }

    public static String getExternalSdCardPath() {
        String path = null;

        File sdCardFile = null;
        List<String> sdCardPossiblePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard");

        for (String sdPath : sdCardPossiblePath) {
            File file = new File("/mnt/", sdPath);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                }
                else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
        }
        else {
            sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        return sdCardFile.getAbsolutePath();
    }










}


