package org.techtown.diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class Fragment2 extends Fragment {

    Context context;
    OnTabItemSelectedListener listener;
    OnRequestListener requestListener;
    ImageView pictureImageView;
    boolean isPhotoCaptured;
    boolean isPhtoFileSaved;
    int selectedPhotoMenu;
    File file;

    @Override
    public void onAttach(Context context) {//프레그먼트가 실행될때 자동실행
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }
        if (context instanceof OnRequestListener) {
            requestListener = (OnRequestListener) context;
        }
    }

    @Override
    public void onDetach() {//프레그먼트가 종료될때 지동실행
        super.onDetach();
        if (context != null) {
            context = null;
            listener = null;
            requestListener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment2, container, false);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        pictureImageView = rootView.findViewById(R.id.pictureImageView);
        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPhotoCaptured || isPhtoFileSaved) {
                    Toast.makeText(getContext(),"사진수정", Toast.LENGTH_LONG).show();
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                } else {
                    Toast.makeText(getContext(),"신규등록", Toast.LENGTH_LONG).show();
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        return rootView;
    }

    public void showDialog(int contentPhoto) {
        AlertDialog.Builder builder = null;
        switch (contentPhoto) {//신규사진찍기 CONTENT_PHOTO, 앨범에서 선택 CONTENT_PHOTO_EX
            case AppConstants.CONTENT_PHOTO:
                Toast.makeText(getContext(),"사진등록메뉴", Toast.LENGTH_LONG).show();
                builder = new AlertDialog.Builder(context);
                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPhotoMenu = which;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedPhotoMenu == 0) {
                            showPhotoCaptureActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                break;
            default:
                break;
        }
        AlertDialog dialog = builder.create();//팝업 객체생성
        dialog.show();//팝업 렌더링
    }

    public void showPhotoCaptureActivity() { //사진찍기 선택
        if(file == null) {
            file = createFile();
        }
        Uri fileUri = FileProvider.getUriForFile(context, "org.techtown.diary.fileprovider", file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        if(intent.resolveActivity(context.getPackageManager()) != null){
            startActivityForResult(intent, AppConstants.REQ_PHOTO_CAPTURE);
            //위 사진을 찍은 값을 처리 하려면, onActivityResult 매서드 생성해야 함.
        }
    }

    public File createFile() { //사진 찍기시 신규파일 생성
        String filename = "capture.jpg";
        File storageDir = Environment.getExternalStorageDirectory();
        File outFile = new File(storageDir, filename);
        return outFile;
    }

}