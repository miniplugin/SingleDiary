package org.techtown.diary;

import java.text.SimpleDateFormat;

//한줄 일기장에서 사용하는 공통 상수변수 정의
public class AppConstants {
    public static final int REQ_LOCATION_BY_ADDRESS = 101;
    public static final int REQ_WEATHER_BY_GRID = 102;

    public static final int REQ_PHOTO_CAPTURE = 103;
    public static final int REQ_PHOTO_SELECTION = 104;

    public static final int CONTENT_PHOTO = 105;
    public static final int CONTENT_PHOTO_EX = 106;
    public static final int MODE_INSERT = 1;//일기저장모드 명시
    public static final int MODE_MODIFY = 2;//일기수정모드 명시

    public static String FOLDER_PHOTO;
    public static String DATABASE_NAME = "note.db";

    public static SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
