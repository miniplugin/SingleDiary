package org.techtown.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.techtown.diary.restapi.AsyncResponse;
import org.techtown.diary.restapi.JsonConverter;
import org.techtown.diary.restapi.MemberVO;
import org.techtown.diary.restapi.PostResponseAsyncTask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;//(멤버)변수선언
    EditText editTextID, editTextPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);//뷰단을 화면에 렌더링(메모리적재)

        btnLogin = (Button) findViewById(R.id.btLogin);//변수값 할당(오브젝트생성)
        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //스프링으로 보낼 데이터를 해시맵 타입을 저장
                HashMap postDataParams = new HashMap();
                postDataParams.put("txtUsername",editTextID.getText().toString());
                postDataParams.put("txtPassword",editTextPassword.getText().toString());
                //스프링앱 주소를 지정
                //String requestUrl = "http://192.168.1.2:8080/android/login";
                String requestUrl = "http://kimilguk.herokuapp.com/android/login";
                //jsp의 Ajax과 같은 역할의 AsyncTask클래스 사용
                PostResponseAsyncTask readTask = new PostResponseAsyncTask(LoginActivity.this, postDataParams, new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {//output는 스프링앱에서 전송받은 로그인 사용자 정보
                        Toast.makeText(LoginActivity.this, output+"디버그", Toast.LENGTH_SHORT).show();
                        Log.d("디버그", "여기 " + output);
                        if(!output.equals("")) {
                            Map<String, Object> jsonMap = new HashMap<String, Object>();
                            jsonMap = JsonConverter.getMapFromJsonObject(output);
                            //자바 Map 데이터를 VO 로 반환(아래)
                            MemberVO memberVO = new MemberVO();
                            for(Map.Entry<String, Object> entrySet: jsonMap.entrySet()) {
                                Field[] fields = MemberVO.class.getDeclaredFields();
                                for(Field field:fields) {
                                    field.setAccessible(true);
                                    String fieldName = field.getName();
                                    boolean isSameName = entrySet.getKey().equals(fieldName);
                                    if(isSameName) {
                                        try {
                                            field.set(memberVO, jsonMap.get(fieldName));
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if (memberVO != null) { //로그인 사용자 정보가 있으면
                                Log.i("디버그", memberVO.toString());
                                //로그인 이후 액티비티를 여기서 띄우기
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();//현재 액티비티 종료
                            } else {
                                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "RestApi서버가 작동하지 않습니다.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                readTask.execute(requestUrl);//한번 작업(백그라운드호출)
            }
        });
    }
}