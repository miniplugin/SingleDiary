package org.techtown.diary;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;

public class MainActivity extends AppCompatActivity implements OnTabItemSelectedListener {
    //디버그용 태그 추가
    private static final String TAG = "태그MainActivity";
    //멤버 변수선언
    BottomNavigationView bottomNavigationView;
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    //데이터베이스 객체 생성 이후 쿼리가 가능합니다.(아래)
    public static NoteDatabase mDatabase = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);//변수값 할당
        fragment1 = new Fragment1();//변수값 할당(객체화)
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        //초기 선택된 탭1 fragment1 불러오기(아래)
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment1).commit();
        //현재 안드로이드 스튜디오 작업은 거의 반자동 입니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tab1:
                        Toast.makeText(getApplicationContext(), "첫번째 탭 선택함", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment1).commit();
                        return true;
                    case R.id.tab2:
                        Toast.makeText(getApplicationContext(), "두번째 탭 선택함", Toast.LENGTH_SHORT).show();
                        fragment2 = new Fragment2();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment2).commit();
                        return true;
                    case R.id.tab3:
                        Toast.makeText(getApplicationContext(), "세번째 탭 선택함", Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment3).commit();
                        return true;
                }
                return false;
            }
        });
        // 데이터베이스 열기
        openDatabase();
        // DB에 사진을 저장할 폴더 값지정
        setPicturePath();
    }

    //사진을 저장할 폴더 지정 메서드
    public void setPicturePath() {
        String folderPath = getFilesDir().getAbsolutePath();
        AppConstants.FOLDER_PHOTO = folderPath + File.separator + "photo";
        File photoFolder = new File(AppConstants.FOLDER_PHOTO);
        if (!photoFolder.exists()) {
            photoFolder.mkdirs();
        }
    }

    //데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
    public void openDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");
        } else {
            Log.d(TAG, "Note database is not open.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    @Override
    public void onTabSelected(int position) { //네비게이션 메뉴가 아닌 곳에서 [오늘작성]버튼으로 탭메뉴호출시 구현
        if(position == 0) {
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }else if(position == 1) {
            fragment2 = new Fragment2();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment2).commit();
        }else if(position == 2) {
            bottomNavigationView.setSelectedItemId(R.id.tab3);
        }
    }

    @Override
    public void showFragment2(Note item) {
        fragment2 = new Fragment2();
        fragment2.setItem(item);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment2).commit();
    }
}