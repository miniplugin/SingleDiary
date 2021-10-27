package org.techtown.diary;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lib.kingja.switchbutton.SwitchMultiButton;

/**
 * A fragment representing a list of Items.
 */
public class Fragment1 extends Fragment {
    //디버그용 태그 추가
    private static final String TAG = "태그Fragment1";
    //클래스에서 사용할 멤버변수 선언(아래)
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    Context context;
    OnTabItemSelectedListener listener;//하단 탭메뉴 인터페이스 리스너 변수
    SimpleDateFormat todayDateFormat;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if(context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(context != null){
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);
        initUI(view);//인플레이트된 화면의 비지니스 로직 구현.
        loadNoteListData();//NOTE 테이블의 저장된 데이터 불러와서 화면에 뿌리기
        return view;
    }
    //리스트 데이터 로딩
    private int loadNoteListData() {
        int recordCount = -1;
        String sql = "select _id, WEATHER, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, MOOD, PICTURE, CREATE_DATE, MODIFY_DATE from " + NoteDatabase.TABLE_NOTE + " order by CREATE_DATE desc";
        NoteDatabase database = NoteDatabase.getInstance(context);
        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);
            recordCount = outCursor.getCount();
            Log.d(TAG,"record count : " + recordCount + "\n");
            ArrayList<Note> items = new ArrayList<Note>();
            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();
                int _id = outCursor.getInt(0);
                String weather = outCursor.getString(1);
                String address = outCursor.getString(2);
                String locationX = outCursor.getString(3);
                String locationY = outCursor.getString(4);
                String contents = outCursor.getString(5);
                String mood = outCursor.getString(6);
                String picture = outCursor.getString(7);
                String dateStr = outCursor.getString(8);
                String createDateStr = null;
                if (dateStr != null && dateStr.length() > 10) {
                    try {
                        Date inDate = AppConstants.dateFormat4.parse(dateStr);
                        if (todayDateFormat == null) {
                            todayDateFormat = new SimpleDateFormat(getResources().getString(R.string.today_date_format));
                        }
                        createDateStr = todayDateFormat.format(inDate);
                        Log.d(TAG,"currentDateString : " + createDateStr);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    createDateStr = "";
                }

                Log.d(TAG,"#" + i + " -> " + _id + ", " + weather + ", " +
                        address + ", " + locationX + ", " + locationY + ", " + contents + ", " +
                        mood + ", " + picture + ", " + createDateStr);
                items.add(new Note(_id, weather, address, locationX, locationY, contents, mood, picture, createDateStr));
            }
            outCursor.close();
            noteAdapter.setItems(items);
            noteAdapter.notifyDataSetChanged();
        }
        return recordCount;
    }

    private void initUI(View view) {
        //오늘작성 버튼 클릭시 액션(아래)
        Button todayWriteButton = view.findViewById(R.id.todayWriteButton);
        todayWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onTabSelected(1);
                    //Toast.makeText(getContext(), "리스너 후 디버그용", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //스위버튼 클릭시 액션(아래)
        SwitchMultiButton switchMultiButton = view.findViewById(R.id.switchButton);
        switchMultiButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                Toast.makeText(getContext(), tabText, Toast.LENGTH_SHORT).show();
                noteAdapter.switchLayout(position);//layout1, layout2 해당레이아웃 불러오기
                noteAdapter.notifyDataSetChanged();//화면 재생
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        //리사이클러 뷰는 레이아웃 매니저에서 변수 선언 하면서 값 할당
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        noteAdapter = new NoteAdapter();
        noteAdapter.addItem(new Note(0, "0", "강남구 삼성동", "", "", "오늘 너무 행복해!", "4", "capture1.jpg", "2월 10일"));
        noteAdapter.addItem(new Note(1, "1", "강남구 삼성동", "", "", "친구와 재미있게 놀았어", "1", "capture1.jpg", "2월 11일"));
        noteAdapter.addItem(new Note(2, "0", "강남구 역삼동", "", "", "집에 왔는데 너무 피곤해 ㅠㅠ", "0", "capture1.jpg", "2월 13일"));

        recyclerView.setAdapter(noteAdapter);

        noteAdapter.setOnItemClickListener(new OnNoteItemClickListener() {
            @Override
            public void onItemClick(NoteAdapter.ViewHolder holder, View view, int position) {
                Note item = noteAdapter.getItem(position);
                Log.d(TAG, "아이템 선택됨 : " + item.get_id());
                //Toast.makeText(getContext(), "아이템 선택됨 : " + item.getContents(), Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.showFragment2(item);
                }
            }
        });

    }
}