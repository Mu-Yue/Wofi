package com.wofi.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.wofi.Bluetooth.ControlCarActivity;
import com.wofi.Lock.Unlock;
import com.wofi.R;
import com.wofi.utils.BicycleXY;
import com.wofi.utils.Interaction;

import static com.wofi.application.MyApplication.bicycleId;
import static com.wofi.application.MyApplication.bicycleXYlist;
import static com.wofi.application.MyApplication.startTime;

public class DialogActivity extends Activity {

    private PayPwdEditText payPwdEditText;
    private Button btn_clear = null;
    private Button btn_sure = null;
    private static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    private boolean isfull = false;//判断输入编号完整性
    private int isUse = 0;//判断使用状态

    private static int isborrow=0;

    public static int getIsborrow() {
        return isborrow;
    }

    public static void setIsborrow(int isborrow) {
        DialogActivity.isborrow = isborrow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dialog);
        AlertDialog dialog=new AlertDialog.Builder(DialogActivity.this).create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        payPwdEditText = (PayPwdEditText) findViewById(R.id.ppe_pwd);
        btn_clear = (Button)findViewById(R.id.clear);
        btn_sure = (Button)findViewById(R.id.sure);
        payPwdEditText.initStyle(R.drawable.edit_num_bg, 6, 0.33f,R.color.black_1,R.color.black_1, 20);
        payPwdEditText.setOnTextFinishListener(new PayPwdEditText.OnTextFinishListener(){
            @Override
            public void onFinish(String str) {
                MainActivity.setNumber(str);
                isfull = true;
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                SharedPreferences sp=getSharedPreferences("Login", Context.MODE_PRIVATE);
                String username=sp.getString("Username","").trim();
                Log.e("Username",username);
                //状态
                if (!isfull){
                    new AlertDialog.Builder(DialogActivity.this).setTitle("提示")
                            .setMessage("请输入完整编号")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                }
                            }).create().show();
                } else if(bicycleXYlist.size()!=0) {
                    for (BicycleXY bicycleXY:bicycleXYlist) {
                        if (bicycleXY.getBicycleId().equals(String.valueOf(Integer.parseInt(MainActivity.getNumber())))) {
                            if (bicycleXY.getBicycleStatement()==-1) {
                                isUse = 1;
                                new android.app.AlertDialog.Builder(DialogActivity.this).setTitle("该车编号：" + Integer.parseInt(String.valueOf(MainActivity.getNumber())))
                                        .setMessage("该车可能已损坏，请寻找其他可用车！")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                            }
                                        }).create().show();
                            } else if (bicycleXY.getBicycleStatement()==0) {
                                isUse = 1;
                                new android.app.AlertDialog.Builder(DialogActivity.this).setTitle("该车编号：" + Integer.parseInt(String.valueOf(MainActivity.getNumber())))
                                        .setMessage("该车正在使用中，请寻找其他可用车！")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //finish();
                                            }
                                        }).create().show();
                            } else {
                                DialogActivity.setIsborrow(1);
                                isUse = 1;
                                Interaction.borrowBicycle(String.valueOf(Integer.parseInt(MainActivity.getNumber())),username);
                                startTime=System.currentTimeMillis();
                                bicycleId=bicycleXY.getBicycleId();
                                Log.e("startTime", String.valueOf(startTime));
                                //悬浮窗权限判断
                                if(Build.VERSION.SDK_INT>=23){
                                    if (!Settings.canDrawOverlays(DialogActivity.this)) {
                                        //Toast.makeText(DialogActivity.this, "当前无权限，请授权！", Toast.LENGTH_SHORT).show();
                                        new android.app.AlertDialog.Builder(DialogActivity.this).setTitle("注意" )
                                                .setMessage("悬浮窗权限未授权！")
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        try{
                                                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:" + getPackageName()));
                                                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                                                        }catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).create().show();
                                        } else {
                                        new android.app.AlertDialog.Builder(DialogActivity.this).setTitle("借车成功  编号为：" + Integer.parseInt(String.valueOf(MainActivity.getNumber())))
                                                .setMessage("如果自动开锁失败，请使用蓝牙开锁！")
                                                .setNeutralButton("确认",new DialogInterface.OnClickListener(){
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        finish();
                                                    }
                                                })
                                                .setPositiveButton("蓝牙开锁", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(DialogActivity.this, ControlCarActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }).create().show();
                                        Intent inser = new Intent(DialogActivity.this,Unlock.class);
                                        Intent int_over = new Intent();
                                        int_over.putExtra("over_fin",1);
                                        int_over.setAction("com.wofi.Activity.DialogActivity");
                                        sendBroadcast(int_over);
                                        startService(inser);
                                    }
                                }else{
                                    new android.app.AlertDialog.Builder(DialogActivity.this).setTitle("借车成功 编号为：" + Integer.parseInt(String.valueOf(MainActivity.getNumber())))
                                            .setMessage("如果自动开锁失败，请使用蓝牙开锁！")
                                            .setNeutralButton("确认",new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    finish();
                                                }
                                            })
                                            .setPositiveButton("蓝牙开锁", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(DialogActivity.this, ControlCarActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).create().show();
                                    Intent inser = new Intent(DialogActivity.this,Unlock.class);
                                    Intent int_over = new Intent();
                                    int_over.putExtra("over_fin",1);
                                    int_over.setAction("com.wofi.Activity.DialogActivity");
                                    sendBroadcast(int_over);
                                    startService(inser);
                                }
                            }
                            break;
                        }
                    }
                    if (isUse == 0){
                        new AlertDialog.Builder(DialogActivity.this).setTitle("提示")
                                .setMessage("该车不在附近区域,请重新输入")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //finish();
                                    }
                                }).create().show();
                    }
                } else {
                    new AlertDialog.Builder(DialogActivity.this).setTitle("提示")
                            .setMessage("该车不在附近区域,请重新输入")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //finish();
                                }
                            }).create().show();
                }
            }
        });
    }

    /**
     * 用户返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if(Build.VERSION.SDK_INT>=23){
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(DialogActivity.this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                } else {
                    new android.app.AlertDialog.Builder(DialogActivity.this).setTitle("借车成功 编号为：" + Integer.parseInt(String.valueOf(MainActivity.getNumber())))
                            .setMessage("如果自动开锁失败，请使用蓝牙开锁！")
                            .setNeutralButton("确认",new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setPositiveButton("蓝牙开锁", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(DialogActivity.this, ControlCarActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).create().show();
                    Intent inser = new Intent(DialogActivity.this,Unlock.class);
                    Intent int_over = new Intent();
                    int_over.putExtra("over_fin",1);
                    int_over.setAction("com.wofi.Activity.DialogActivity");
                    sendBroadcast(int_over);
                    startService(inser);
                }
            }
        }
    }
}
