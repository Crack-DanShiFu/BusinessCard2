package com.card.businesscard2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Setinfomation extends AppCompatActivity {
    private EditText setName;
    private EditText setphoneNumber;
    private EditText setEmail;
    private EditText setaddress;
    private EditText setspecialty;
    private EditText setmotto;
    private Button submit_btn;
    public static final String REGEX_MOBILE = "^[1][3578][0-9]{9}$";
    SharedPreferences userSettings;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.setinfomation);
        init();
        addListener();
        setdata();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String name = userSettings.getString("setname","0");
        String email = userSettings.getString("setemail","0");
        String phone = userSettings.getString("setphone","0");
        String address = userSettings.getString("setaddress","0");
        String specialty = userSettings.getString("setspecialty","0");
        String motto = userSettings.getString("setmotto","0");
        if(name!="0"){
            setName.setText(name);
            setEmail.setText(email);
            setphoneNumber.setText(phone);
            setaddress.setText(address);
            setspecialty.setText(specialty);
            setmotto.setText(motto);
        }
    }
    private void setdata(){
        Intent intent = getIntent();
        //从intent对象中把封装好的数据取出来
        Bundle bundle = intent.getExtras();
        String name = bundle.getString("name");
        String phonenumber = bundle.getString("phonenumber");
        String email = bundle.getString("email");
        String address = bundle.getString("address");
        String specialty = bundle.getString("specialty");
        String motto = bundle.getString("motto");

        setName.setText(name);
        setphoneNumber.setText(phonenumber);
        setEmail.setText(email);
        setaddress.setText(address);
        setmotto.setText(motto);
        setspecialty.setText(specialty);
    }
    private void init(){
        setName =findViewById(R.id.setName);
        setphoneNumber=findViewById(R.id.setphoneNumber);
        setEmail=findViewById(R.id.setEmail);
        setaddress=findViewById(R.id.setaddress);
        setspecialty=findViewById(R.id.setspecialty);
        setmotto=findViewById(R.id.setmotto);
        submit_btn=findViewById(R.id.submit_btn);
        userSettings= getSharedPreferences("setting", 0);

    }
    private void addListener(){
        setphoneNumber.setOnFocusChangeListener(new setphoneNumberChargeListener());
        setEmail.setOnFocusChangeListener(new setEmailChargeListener());
        submit_btn.setOnClickListener(new submitBtnOnClickListener());
    }
    class submitBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (setName.getText().toString().equals("")){
                Toast.makeText(getApplicationContext(),"名字格式错误",Toast.LENGTH_SHORT).show();
            }else if(!isChinaPhoneLegal(setphoneNumber.getText().toString())){
                Toast.makeText(getApplicationContext(),"手机格式错误",Toast.LENGTH_SHORT).show();
            }else if (!isChinaEmailLegal(setEmail.getText().toString())){
                Toast.makeText(getApplicationContext(),"邮箱格式错误",Toast.LENGTH_SHORT).show();
            }
            else {
                SharedPreferences.Editor editor = userSettings.edit();
                editor.putString("setname",setName.getText().toString());
                editor.putString("setemail",setEmail.getText().toString());
                editor.putString("setphone",setphoneNumber.getText().toString());
                editor.putString("setaddress",setaddress.getText().toString());
                editor.putString("setspecialty",setspecialty.getText().toString());
                editor.putString("setmotto",setmotto.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                finish();

            }
        }
    }

    class setphoneNumberChargeListener implements RadioGroup.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!isChinaPhoneLegal(setphoneNumber.getText().toString())){
                setphoneNumber.setError("手机格式错误");

            }
        }
    }
    class setEmailChargeListener implements RadioGroup.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!isChinaEmailLegal(setEmail.getText().toString())){
                setEmail.setError("邮箱格式错误");
            }
        }
    }

    public static boolean isChinaPhoneLegal(String str)
            throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147,145))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }
    public static boolean isChinaEmailLegal(String str)
            throws PatternSyntaxException {
        String regExp = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }


}
