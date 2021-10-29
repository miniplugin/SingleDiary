package org.techtown.diary.restapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 이 클래스틑 RestAPI에서 받은 Json 값은 MemberVO클래스 매핑하면서, memberList 입력하는 기능
 * @param <T> 의미는 타입이 정해져 있지 않는 매개변수
 */
public class JsonConverter<T> {
    //Json 데이터를 리스트형으로 반환
    public ArrayList<T> toArrayList(String jsonString, Class<T> clazz) {
        jsonString = jsonString.substring(jsonString.indexOf('['),jsonString.indexOf(']')+1);
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        Gson gson = builder.create();//객체 생성
        Type type = new ListParameterizedType(clazz);
        ArrayList<T> list = gson.fromJson(jsonString, type);
        return list;
    }
    //중첩 클래스 생성
    private static class ListParameterizedType implements ParameterizedType {
        private Type type;

        public ListParameterizedType(Type type) {
            this.type = type;
        }

        @NonNull
        @Override
        public Type[] getActualTypeArguments() {
            return new Type[] {type}; // 기본값을 변경
        }

        @NonNull
        @Override
        public Type getRawType() {
            return ArrayList.class; // 기본값을 변경
        }

        @Nullable
        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}