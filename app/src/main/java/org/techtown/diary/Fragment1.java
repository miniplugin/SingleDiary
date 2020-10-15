package org.techtown.diary;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import lib.kingja.switchbutton.SwitchMultiButton;

/**
 * A fragment representing a list of Items.
 */
public class Fragment1 extends Fragment {
    //클래스에서 사용할 멤버변수 선언(아래)
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    Context context;
    OnTabItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, container, false);

        initUI(view);

        return view;
    }

    private void initUI(View view) {
        //오늘작성 버튼 클릭시 액션(아래)
        Button todayWriteButton = view.findViewById(R.id.todayWriteButton);
        todayWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onTabSelected(1);
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
                Toast.makeText(getContext(), "아이템 선택됨 : " + item.getContents(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}