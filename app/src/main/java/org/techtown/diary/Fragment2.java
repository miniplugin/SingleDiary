package org.techtown.diary;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.channguyen.rsv.RangeSliderView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragment2 extends Fragment {
    //디버그용 태그 추가
    private static final String TAG = "태그Fragment2";
    Context context;//현재 화면 가리키는 화면변수
    OnTabItemSelectedListener listener;//하단 탭메뉴를 클릭 대기변수
    ImageView pictureImageView;//카메라 이미지변수
    boolean isPhotoCaptured;//카메라로 찍은 이미지 확인변수
    boolean isPhotoFileSaved;//카메라로 찍은 이미지 저장된 파일변수
    int selectedPhotoMenu = 0;//카메라 대화상자에서 카메라앱실행,앨범 선택변수
    File file;//저장소 저장된 파일변수
    Uri fileUri;//저장소의 파일 경로
    Bitmap resultPhotoBitmap;//사진찍은 이미지를 화면에 불러올때 이미지변수
    int mMode = AppConstants.MODE_INSERT;
    int weatherIndex = 0;//일기신규등록시 날씨 값 초기화
    TextView dateTextView;//일기 등록날찌 객체선언
    TextView locationTextView;//일기 등록시 구글위치 객체선언(현재 구글위치정보는 사용하지 않음)
    RangeSliderView moodSlider;//일기 등록시 일일기분 객체생성
    int moodIndex = 2;//일일 기분 초기값
    Note item;//일기장 전송값 Get/Set 용 클래스
    EditText contentsInput;//일기장 내용 UI객체
    SimpleDateFormat todayDateFormat;//일기장NOTE 에서 가져온 날짜 객체로 사용
    String currentDateString;//일기장 저장날짜 출력포맷 변수

    public void imageAutoRotate(int angle) {
        if(Build.VERSION.SDK_INT >= 24) {
            pictureImageView.setRotation(0);//Glide 로 이미지 자동으로 돌아가는 부분 처리
        } else {
            pictureImageView.setRotation(angle);//Glide 로 이미지 자동으로 돌아가는 부분 처리
        }
    }
    @Override
    public void onAttach(Context context) {//프레그먼트가 실행될때 자동실행
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {//프레그먼트가 종료될때 지동실행
        super.onDetach();
        if (context != null) {
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment2, container, false);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);
        dateTextView = rootView.findViewById(R.id.dateTextView);
        locationTextView = rootView.findViewById(R.id.locationTextView);
        contentsInput = rootView.findViewById(R.id.contentsInput);

        pictureImageView = rootView.findViewById(R.id.pictureImageView);
        pictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPhotoCaptured || isPhotoFileSaved) {
                    Toast.makeText(getContext(),"사진수정", Toast.LENGTH_SHORT).show();
                    showDialog(AppConstants.CONTENT_PHOTO_EX);
                } else {
                    Toast.makeText(getContext(),"신규등록", Toast.LENGTH_SHORT).show();
                    showDialog(AppConstants.CONTENT_PHOTO);
                }
            }
        });
        
        initUI(rootView);//일기저장 메서드 실행
        applyItem();//Fragment1 에서 item 클릭시 적용되는 값
        return rootView;
    }

    private void applyItem() {
        if (item != null) {
            mMode = AppConstants.MODE_MODIFY;
            locationTextView.setText(item.getAddress());
            dateTextView.setText(item.getCreateDateStr());
            contentsInput.setText(item.getContents());
            String picturePath = item.getPicture();
            Log.d(TAG,"picturePath : " + picturePath);
            if (picturePath == null || picturePath.equals("")) {
                pictureImageView.setImageResource(R.drawable.noimagefound);
                imageAutoRotate(0);//Glide 로 이미지 자동으로 돌아가는 부분 처리
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                resultPhotoBitmap = BitmapFactory.decodeFile(picturePath, options);
                pictureImageView.setImageBitmap(resultPhotoBitmap);
                imageAutoRotate(-90);//Glide 로 이미지 자동으로 돌아가는 부분 처리
                /* Glide 로 이미지 자동으로 돌아가는 부분 처리 취소 UI로 처리
                Glide.with(context)
                        .load(new File(filePath))
                        .apply(new RequestOptions().override(resultPhotoBitmap.getWidth(), resultPhotoBitmap.getHeight()))
                        .into(pictureImageView);
                */
            }
            moodIndex = Integer.parseInt(item.getMood());
            moodSlider.setInitialIndex(moodIndex);
        } else {
            mMode = AppConstants.MODE_INSERT;
            locationTextView.setText("");
            Date currentDate = new Date();
            if (todayDateFormat == null) {
                todayDateFormat = new SimpleDateFormat(getResources().getString(R.string.today_date_format));
            }
            currentDateString = todayDateFormat.format(currentDate);
            Log.d(TAG,"currentDateString : " + currentDateString);
            dateTextView.setText(currentDateString);
            contentsInput.setText("");
            pictureImageView.setImageResource(R.drawable.noimagefound);
            imageAutoRotate(0);
            moodSlider.setInitialIndex(2);
        }
    }

    private void initUI(ViewGroup rootView) {
        contentsInput = rootView.findViewById(R.id.contentsInput);
        //일기저장버튼 클릭이벤트
        Button saveButton = rootView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMode == AppConstants.MODE_INSERT) {
                    saveNote();
                } else if(mMode == AppConstants.MODE_MODIFY) {
                    modifyNote();
                }
                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });
        //일기삭제버튼 클릭이벤트
        Button deleteButton = rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });
        //일기닫기버튼 클릭이벤트
        Button closeButton = rootView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTabSelected(0);
                }
            }
        });
        moodSlider = rootView.findViewById(R.id.sliderView);
        final RangeSliderView.OnSlideListener listener = new RangeSliderView.OnSlideListener() {
            @Override
            public void onSlide(int index) {
                Log.d(TAG,"moodIndex changed to " + index);
                moodIndex = index;
            }
        };
        moodSlider.setOnSlideListener(listener);
        moodSlider.setInitialIndex(2);
    }

    //데이터베이스 레코드 추가
    private void saveNote() {
        String address = "구글위치입력 사용안함";
        String contents = contentsInput.getText().toString();
        String picturePath = savePicture();
        String sql = "insert into " + NoteDatabase.TABLE_NOTE +
                "(WEATHER, ADDRESS, LOCATION_X, LOCATION_Y, CONTENTS, MOOD, PICTURE) values(" +
                "'"+ weatherIndex + "', " +
                "'"+ address + "', " +
                "'"+ "" + "', " +
                "'"+ "" + "', " +
                "'"+ contents + "', " +
                "'"+ moodIndex + "', " +
                "'"+ picturePath + "')";
        Log.d(TAG, "sql : " + sql);
        NoteDatabase database = NoteDatabase.getInstance(context);
        database.execSQL(sql);
    }

    //데이터베이스 레코드 수정
    private void modifyNote() {
        if (item != null) {
            String address = "구글위치수정 사용안함";
            String contents = contentsInput.getText().toString();
            String picturePath = savePicture();
            String sql = "update " + NoteDatabase.TABLE_NOTE +
                    " set " +
                    "   WEATHER = '" + weatherIndex + "'" +
                    "   ,ADDRESS = '" + address + "'" +
                    "   ,LOCATION_X = '" + "" + "'" +
                    "   ,LOCATION_Y = '" + "" + "'" +
                    "   ,CONTENTS = '" + contents + "'" +
                    "   ,MOOD = '" + moodIndex + "'" +
                    "   ,PICTURE = '" + picturePath + "'" +
                    " where " +
                    "   _id = " + item._id;
            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
    }
    //캡쳐또는 앨범의 사진을 저장하고,URL 을 DB에 저장할 수 있도록 구하는 메서드
    private String savePicture() {
        if (resultPhotoBitmap == null) {
            Log.d(TAG,"No picture to be saved.");
            return "";
        }
        File photoFolder = new File(AppConstants.FOLDER_PHOTO);
        if(!photoFolder.isDirectory()) {
            Log.d(TAG, "creating photo folder : " + photoFolder);
            photoFolder.mkdirs();
        }
        String photoFilename = createFilename();
        String picturePath = photoFolder + File.separator + photoFilename;
        try {
            FileOutputStream outstream = new FileOutputStream(picturePath);
            resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            outstream.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return picturePath;
    }
    //저장할 파일이 중복되지 않도록 현재 시간을 파일명으로 주기위한 메서드
    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());
        return curDateStr;
    }

    //레코드 삭제
    private void deleteNote() {
        Log.d(TAG,"deleteNote called.");
        if (item != null) {
            String sql = "delete from " + NoteDatabase.TABLE_NOTE +
                    " where " +
                    "   _id = " + item._id;

            Log.d(TAG, "sql : " + sql);
            NoteDatabase database = NoteDatabase.getInstance(context);
            database.execSQL(sql);
        }
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
                        } else if(selectedPhotoMenu == 1) {
                            showPhotoSelectionActivity();
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                break;
            case AppConstants.CONTENT_PHOTO_EX:
                builder = new AlertDialog.Builder(context);
                builder.setTitle("사진 메뉴 선택");
                builder.setSingleChoiceItems(R.array.array_photo_ex, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPhotoMenu = which;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedPhotoMenu == 0) {//사진 찍기
                            showPhotoCaptureActivity();
                        } else if(selectedPhotoMenu == 1) {//앨범에서 선택하기
                            showPhotoSelectionActivity();
                        } else if(selectedPhotoMenu == 2) {//사진 삭제하기
                            isPhotoCaptured = false;//이 변수값으로 사진등록(CONTENT_PHOTO)로 변경됨
                            pictureImageView.setImageResource(R.drawable.imagetoset);
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                break;
            default:
                break;
        }
        AlertDialog dialog = builder.create();//팝업 객체생성
        dialog.show();//팝업 렌더링
    }

    public void showPhotoSelectionActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, AppConstants.REQ_PHOTO_SELECTION);
    }

    public void showPhotoCaptureActivity() { //사진찍기 선택
        try {
            file = createFile();
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();//createFile()에서 파일이 저장되지 않을때, 에러발생하지 않도록 더미파일생성
        } catch(Exception e) {
            e.printStackTrace();
        }
        //아래 org.techtown.diary.fileprovider 는 manifest.xml 시스템에 등록한 이름과 같아야 합니다.
        //Uri fileUri = FileProvider.getUriForFile(context, "org.techtown.diary.fileprovider", file);
        if(Build.VERSION.SDK_INT >= 24) {
            Log.d("search","안드로이드7.0 누가버전부터 FileProvider 사용가능");
            fileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
        } else {
            //build.gradle의 targetSdkVersion을 24미만으로 설정해야 아래경로로 파일이 저장않됨
            //안드로이드 정책임. 참조: https://darksilber.tistory.com/325
            //fileUri = Uri.fromFile(file);
            Uri fileUri = FileProvider.getUriForFile(context, "org.techtown.diary.fileprovider", file);
            Log.d(TAG, "여기2 " + fileUri);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//카메라앱 실행
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//위 카메라앱에서 저장된 값을 가져오는 권한
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, AppConstants.REQ_PHOTO_CAPTURE);
        //위 사진을 찍은 값을 처리 하려면, onActivityResult 매서드 생성해야 함.
    }

    public File createFile() { //사진 찍기시 신규파일 생성
        String filename = createFilename();//"capture.jpg";
        File outFile = new File(context.getFilesDir(), filename);//아래 2줄을 1줄로 처리가능
        //File storageDir = Environment.getExternalStorageDirectory();
        //File outFile = new File(storageDir, filename);
        Log.d(TAG, "여기1 " + outFile.getAbsolutePath());
        return outFile;
    }

    /**
     * 사진찍기 앱 같은 다른 액티비티로 부터 응답받은 내용 처리
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d(TAG,"여기3 "+ requestCode);
        if(intent != null) {
            String filePath = "";
            switch (requestCode)  {
                case  AppConstants.REQ_PHOTO_CAPTURE://사진 찍는 경우
                    /*resultPhotoBitmap = decodeSampledBitmapFromResource(file, pictureImageView.getWidth(), pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);*/
                    Uri selectedImage2 = intent.getData();
                    String[] filePathColumn2 = {MediaStore.Images.Media.DATA};
                    Cursor cursor2 = context.getContentResolver().query(selectedImage2, filePathColumn2, null, null, null);
                    cursor2.moveToFirst();
                    int columnIndex2 = cursor2.getColumnIndex(filePathColumn2[0]);
                    filePath = cursor2.getString(columnIndex2);
                    cursor2.close();
                    //이미지 축소 되어서 resultPhotoBitmap 으로 저장짐(아래)
                    resultPhotoBitmap = decodeSampledBitmapFromResource(new File(filePath), pictureImageView.getWidth(), pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);
                    imageAutoRotate(-90);//Glide 로 이미지 자동으로 돌아가는 부분 처리
                    /* Glide 로 이미지 자동으로 돌아가는 부분 처리 취소 UI로 처리
                    Glide.with(context)
                            .load(new File(filePath))
                            .apply(new RequestOptions().override(pictureImageView.getWidth(), pictureImageView.getHeight()))
                            .into(pictureImageView);
                     */
                    isPhotoFileSaved = true;//이 변수값으로 사진수정(CONTENT_PHOTO_EX)로 변경됨
                    break;
                case AppConstants.REQ_PHOTO_SELECTION://앨범에서 사진을 선택하는 경우
                    Uri selectedImage = intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                    //이미지 축소 되어서 불러와짐(아래)
                    resultPhotoBitmap = decodeSampledBitmapFromResource(new File(filePath), pictureImageView.getWidth(), pictureImageView.getHeight());
                    pictureImageView.setImageBitmap(resultPhotoBitmap);
                    imageAutoRotate(-90);//Glide 로 이미지 자동으로 돌아가는 부분 처리
                    /* Glide 로 이미지 자동으로 돌아가는 부분 처리 취소 UI로 처리
                    Glide.with(context)
                            .load(new File(filePath))
                            .apply(new RequestOptions().override(pictureImageView.getWidth(), pictureImageView.getHeight()))
                            .into(pictureImageView);
                     */
                    isPhotoCaptured = true;//이 변수값으로 사진수정(CONTENT_PHOTO_EX)로 변경됨
                    break;
            }
        }
    }
    //아래는 이미지 사이즈 줄이기 메서드 사용안함, 대신 Glide.apply 로 이미지 리사이즈처리
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

    public void setItem(Note item) {
        this.item = item;
    }
}