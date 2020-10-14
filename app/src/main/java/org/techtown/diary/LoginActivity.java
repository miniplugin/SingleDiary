package org.techtown.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    Button btLogin;//(멤버)변수선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);//뷰단을 화면에 렌더링(메모리적재)

        btLogin = (Button) findViewById(R.id.btLogin);//변수값 할당(오브젝트생성)
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);//인텐트 메세지 객채를 신규 액티비티로 화면을 출력한다.
                finish();//현재 로그인 액티비티 종료
            }
        });
    }
}