package org.techtown.diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    Context context;//현재 화면 가리키는 화면변수
    OnTabItemSelectedListener listener;//하단 탭메뉴를 클릭 대기변수
    OnRequestListener requestListener;//카메라앱 클릭 대기변수
    ImageView pictureImageView;//카메라 이미지변수
    boolean isPhotoCaptured;//카메라로 찍은 이미지 확인변수
    boolean isPhtoFileSaved;//카메라로 찍은 이미지 저장된 파일변수
    int selectedPhotoMenu;//카메라 대화상자에서 카메라앱실행,앨범 선택변수
    File file;//저장소 저장된 파일변수
    Bitmap resultPhotoBitmap;//사진찍은 이미지를 화면에 불러올때 이미지변수

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
                    Toast.makeText(getContext(),"사진수정", Toast.LENGTH_SHORT).show();
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                } else {
                    Toast.makeText(getContext(),"신규등록", Toast.LENGTH_SHORT).show();
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });

        //현재 위치 확인
        if(requestListener != null) {
            requestListener.onRequest("getCurrentLocation");
        }

        return rootView;
    }

    public void showDialog(int contentPhoto) {
        AlertDialog.Builder builder = null;
        switch (contentPhoto) {//신규사진찍기 CONTENT_PHOTO, 앨범에서 선택 CONTENT_PHOTO_EX
            case AppConstants.CONTENT_PHOTO:
                Toast.makeText(getContext(),"사진등록메뉴", Toast.LENGTH_SHORT).show();
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
        //아래 org.techtown.diary.fileprovider 는 manifest.xml 시스템에 등록한 이름과 같아야 합니다.
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

    /**
     * 사진찍기 앱 같은 다른 액티비티로 부터 응답받은 내용 처리
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(intent != null) {
            Toast.makeText(getContext(),"응답메세지"+ requestCode, Toast.LENGTH_LONG).show();
            switch (requestCode)  {
                case  AppConstants.REQ_PHOTO_CAPTURE://사진 찍는 경우
                    resultPhotoBitmap = decodeSampledBitmapFromResource(file, pictureImageView.getWidth(), pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);
                    break;
            }
        }
    }

    public Bitmap decodeSampledBitmapFromResource(File file, int width, int height) {

        // 먼저 inJustDecodeBounds = true로 디코딩하여 치수 확인
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(),options);

        // inSampleSize 계산
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(file.getAbsolutePath(),options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        // 이미지의 Raw데이터(원시데이터) 높이 및 너비
        final int height2 = options.outHeight;
        final int width2 = options.outWidth;
        int inSampleSize = 1;

        if (height2 > height || width2 > width) {

            final int halfHeight = height;
            final int halfWidth = width;

            // 가장 큰 inSampleSize 값을 계산하고 둘 다 유지합니다
            // 요청 된 높이와 너비보다 큰 높이와 너비.
            while ((halfHeight / inSampleSize) >= height
                    && (halfWidth / inSampleSize) >= width) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}