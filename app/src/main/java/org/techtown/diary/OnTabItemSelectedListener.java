package org.techtown.diary;
//모든 액티비티에서 실행되기 때문에 MainActivity.java 에 implements 한다.
public interface OnTabItemSelectedListener {
    public void onTabSelected(int position);//모든 액티비티에서 하단 메뉴 클랙시 실행
    public void showFragment2(Note item);//Fragment1 에서 리스트 객체 선택시 실행
}
