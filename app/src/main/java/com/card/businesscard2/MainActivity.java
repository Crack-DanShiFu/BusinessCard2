package com.card.businesscard2;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.card.businesscard2.tool.RoundImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE=3;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    private boolean mIsShowBack=false;
    private FrameLayout mFlContainer;
    private LinearLayout front_layout;
    private LinearLayout back_layout;
    private LinearLayout right_side;
    private Uri imageUri;


    private AnimatorSet mRightOutSet;
    private AnimatorSet mLeftInSet;

    private TextView name;
    private TextView phoneNumber;
    private TextView Email;
    private TextView address;
    private TextView specialty;
    private TextView motto;
    private RoundImageView back_head;
    SharedPreferences userSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        init();
        addlistener();
        setAnimators();
        setCameraDistance();
    }

    private void init(){
        front_layout=findViewById(R.id.front_layout);
        back_layout=findViewById(R.id.back_layout);
        mFlContainer=findViewById(R.id.mFlContainer);
        name =findViewById(R.id.name);
        phoneNumber =findViewById(R.id.phoneNumber);
        Email =findViewById(R.id.Email);
        address =findViewById(R.id.address);
        specialty =findViewById(R.id.specialty);
        motto =findViewById(R.id.motto);
        back_head=findViewById(R.id.back_head);
        right_side=findViewById(R.id.right_side);

        userSettings= getSharedPreferences("setting", 0);

    }
    public void addlistener(){
        back_head.setOnLongClickListener(new back_headClickListener());
        right_side.setOnLongClickListener(new right_sideClickListener());

    }
    @Override
    protected void onStart() {
        super.onStart();
        String setname = userSettings.getString("setname","0");
        String setemail = userSettings.getString("setemail","0");
        String setphone = userSettings.getString("setphone","0");
        String setaddress = userSettings.getString("setaddress","0");
        String setspecialty = userSettings.getString("setspecialty","0");
        String setmotto = userSettings.getString("setmotto","0");
        if(setname!="0"){
            name.setText(setname);
            Email.setText(setemail);
            phoneNumber.setText(setphone);
            address.setText(setaddress);
            specialty.setText(setspecialty);
            motto.setText(setmotto);
        }


        String back_head_uri = userSettings.getString("back_head_uri","0");
//        获取权限
        if(!back_head_uri.equals("0")){
            verifyStoragePermissions(this);
            Uri uri =Uri.fromFile(new File(back_head_uri));
            Bitmap bit = null;
            try {
                bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                System.out.println();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(bit!=null){
                back_head.setImageBitmap(bit);
            }
        }


    }
    class back_headClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            choosePhoto();
            return true;
        }
    }
    class right_sideClickListener implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View v) {
            Intent intent=new Intent(MainActivity.this, Setinfomation.class);
            Bundle bundle =new Bundle();
            bundle.putString("name",name.getText().toString());
            bundle.putString("phonenumber",phoneNumber.getText().toString());
            bundle.putString("email",Email.getText().toString());
            bundle.putString("address",address.getText().toString());
            bundle.putString("specialty",specialty.getText().toString());
            bundle.putString("motto",motto.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
    }

    /**
     * 从相册选取图片
     */
    void choosePhoto(){
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }
    public void onActivityResult(int req, int res, Intent data) {
        switch (req) {
            /**
             * 拍照的请求标志
             */
            case CROP_PHOTO:
                if (res==RESULT_OK) {
                    try {
                        /**
                         * 该uri就是照片文件夹对应的uri
                         */
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        back_head.setImageBitmap(bit);

                    } catch (Exception e) {
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.i("tag", "失败");
                }

                break;
            /**
             * 从相册中选取图片的请求标志
             */

            case REQUEST_CODE_PICK_IMAGE:
                if (res == RESULT_OK) {
                    try {
                        /**
                         * 该uri是上一个Activity返回的
                         */
                        Uri uri = data.getData();
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        back_head.setImageBitmap(bit);
                        SharedPreferences.Editor editor = userSettings.edit();

                        System.out.println(getRealPathFromUri(this,uri));
                        editor.putString("back_head_uri",getRealPathFromUri(this,uri));
                        editor.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("tag",e.getMessage());
                        Toast.makeText(this,"程序崩溃",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Log.i("crack", "失败");
                }

                break;

            default:
                break;
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    private void setAnimators() {
        mRightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.anim_out);
        mLeftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.anim_in);

        // 设置点击事件
        mRightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mFlContainer.setClickable(false);
            }
        });
        mLeftInSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFlContainer.setClickable(true);
            }
        });
    }
    // 改变视角距离, 贴近屏幕
    private void setCameraDistance() {
        int distance = 16000;
        float scale = getResources().getDisplayMetrics().density * distance;
        front_layout.setCameraDistance(scale);
        back_layout.setCameraDistance(scale);
    }
    public void flipCard(View view) {
        if (!mIsShowBack) {
            mRightOutSet.setTarget(front_layout);
            mLeftInSet.setTarget(back_layout);
            mRightOutSet.start();
            mLeftInSet.start();
            mIsShowBack = true;
            back_head.setClickable(false);
            right_side.setClickable(true);
        } else { // 背面朝上
            mRightOutSet.setTarget(back_layout);
            mLeftInSet.setTarget(front_layout);
            mRightOutSet.start();
            mLeftInSet.start();
            mIsShowBack = false;
            back_head.setClickable(true);
            right_side.setClickable(false);
        }
    }

}
