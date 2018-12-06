package com.wofi.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.wofi.R;
import com.wofi.constants.Constants;
import com.wofi.utils.HttpUtils;
import com.wofi.utils.Interaction;
import com.wofi.utils.PayPwdEditText;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.wofi.application.MyApplication.cash;
import static com.wofi.application.MyApplication.feedback;

public class RepairActivity extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{
    private PayPwdEditText payPwdEditText;
    private Toolbar toolbar;
    private String BicycleId;
    private Button rep;
    private EditText editText;
    private String Title="车辆问题";
    private String Contenttext="类型";
    private CheckBox[] checkBox= new CheckBox[7];
    private boolean isTF = false;//判断是否输入完整
    private static String input;
    private static String getInput(){
        return input;
    }
    private static void setInput(String input){
        RepairActivity.input = input;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);
        rep= (Button) findViewById(R.id.rep);
        rep.setOnClickListener(this);
        editText= (EditText) findViewById(R.id.beizhu);
        payPwdEditText = (PayPwdEditText) findViewById(R.id.ppe_pwd);
        payPwdEditText.initStyle(R.drawable.edit_num_bg, 6, 0.33f,R.color.color999999,R.color.color999999, 20);
        payPwdEditText.setOnTextFinishListener(new PayPwdEditText.OnTextFinishListener(){
            @Override
            public void onFinish(String str) {
                RepairActivity.setInput(str);
                BicycleId= String.valueOf(Integer.parseInt(payPwdEditText.getPwdText()));
                isTF = true;//完全输入
            }
        });

        checkBox[0]= (CheckBox) findViewById(R.id.checkbox1);
        checkBox[1]= (CheckBox) findViewById(R.id.checkbox2);
        checkBox[2]= (CheckBox) findViewById(R.id.checkbox3);
        checkBox[3]= (CheckBox) findViewById(R.id.checkbox4);
        checkBox[4]= (CheckBox) findViewById(R.id.checkbox5);
        checkBox[5]= (CheckBox) findViewById(R.id.checkbox6);
        checkBox[6]= (CheckBox) findViewById(R.id.checkbox7);
        for(int i=0;i<7;i++){
            checkBox[i].setOnCheckedChangeListener(this);
        }
        toolbar= (Toolbar) findViewById(R.id.repairtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button bn1 = (Button)findViewById(R.id.rep);
        bn1.setOnClickListener(this);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.rep:
                if (isTF){
                SharedPreferences sp=getSharedPreferences("Login",MODE_PRIVATE);
                Interaction.userFeedback(Title,Contenttext,editText.getText().toString(),BicycleId,sp.getString("Username",""));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(feedback.equals("-2")){
                    new AlertDialog.Builder(RepairActivity.this).setTitle("提示")
                            .setMessage("不存在这辆车!")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            }).create().show();
                    isTF=false;
                    payPwdEditText.clearText();
                }
                    if(feedback.equals("1")){
                        new AlertDialog.Builder(RepairActivity.this).setTitle("提示")
                                .setMessage("反馈成功!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                }).create().show();
                        isTF=false;
                        payPwdEditText.clearText();
                    }
                    if(feedback.equals("0")){
                        new AlertDialog.Builder(RepairActivity.this).setTitle("提示")
                                .setMessage("反馈失败!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                }).create().show();
                        isTF=false;
                        payPwdEditText.clearText();
                    }
                    if(feedback.equals("-1")){
                        new AlertDialog.Builder(RepairActivity.this).setTitle("提示")
                                .setMessage("反馈为空")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                }).create().show();
                        isTF=false;
                        payPwdEditText.clearText();
                    }
                    if(feedback.equals("-3")){
                        new AlertDialog.Builder(RepairActivity.this).setTitle("提示")
                                .setMessage("后台错误亲（反馈对象创建失败")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                }).create().show();
                        isTF=false;
                        payPwdEditText.clearText();
                    }
                }
                else
                {
                    new AlertDialog.Builder(RepairActivity.this).setTitle("提示")
                            .setMessage("请输入完整的车辆编号!")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            }).create().show();
                    isTF=false;
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId())
        {
            case R.id.checkbox1:
                if(checkBox[0].isChecked()){
                    Contenttext=Contenttext+","+checkBox[0].getText().toString();
                }
                else{
                    Contenttext=Contenttext.replaceAll(","+checkBox[0].getText().toString(),"");
                }
                break;
            case R.id.checkbox2:
                if(checkBox[1].isChecked()){
                    Contenttext=Contenttext+","+checkBox[1].getText().toString();
                }
                else{
                    Contenttext=Contenttext.replaceAll(","+checkBox[1].getText().toString(),"");
                }
                break;
            case R.id.checkbox3:
                if(checkBox[2].isChecked()){
                    Contenttext=Contenttext+","+checkBox[2].getText().toString();
                }
                else{
                    Contenttext=Contenttext.replaceAll(","+checkBox[2].getText().toString(),"");
                }
                break;
            case R.id.checkbox4:
                if(checkBox[3].isChecked()){
                    Contenttext=Contenttext+","+checkBox[3].getText().toString();
                }
                else{
                    Contenttext=Contenttext.replaceAll(","+checkBox[3].getText().toString(),"");
                }
                break;
            case R.id.checkbox5:
                if(checkBox[4].isChecked()){
                    Contenttext=Contenttext+","+checkBox[4].getText().toString();
                }
                else{
                    Contenttext=Contenttext.replaceAll(","+checkBox[4].getText().toString(),"");
                }
                break;
            case R.id.checkbox6:
                if(checkBox[5].isChecked()){
                    Contenttext=Contenttext+","+checkBox[5].getText().toString();
                }
                else{
                    Contenttext=Contenttext.replaceAll(","+checkBox[5].getText().toString(),"");
                }
                break;
            case R.id.checkbox7:
                if(checkBox[6].isChecked()){
                    Contenttext=Contenttext+","+checkBox[6].getText().toString();
                }
                else{
                    Contenttext=Contenttext.replaceAll(","+checkBox[6].getText().toString(),"");
                }
                break;
        }
    }

}
