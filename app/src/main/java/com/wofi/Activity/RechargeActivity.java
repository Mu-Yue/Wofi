package com.wofi.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wofi.R;
import com.wofi.utils.DemoAdapter;
import com.wofi.utils.Interaction;
import com.wofi.utils.ItemModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class RechargeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DemoAdapter adapter;
    private TextView tvPay;
    private TextView tv_recharge_money;
    private String a = null;
    private RadioGroup radioGroup;
    private RadioButton RdBtn1 = null;
    private RadioButton RdBtn2 = null;
    private int ispay=0;
    private Toolbar tool_rechange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        tvPay = (TextView) findViewById(R.id.tvPay);
        tool_rechange = (Toolbar) findViewById( R.id.tool_rechange);
        setSupportActionBar(tool_rechange);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_recharge_money = (TextView) findViewById(R.id.tv_recharge_money);
        radioGroup=(RadioGroup)findViewById(R.id.Pay1);
        RdBtn1 = (RadioButton)findViewById(R.id.weixin);
        RdBtn2 = (RadioButton)findViewById(R.id.alipay);
        // RecyclerView 的Item宽或者高不会变(提升性能)。
        recyclerView.setHasFixedSize(true);
        //recycleview设置布局方式，GridView (一行三列)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter = new DemoAdapter());
        adapter.replaceAll(getData(),this);
        EventBus.getDefault().register(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(i==R.id.weixin)
                { ispay=0;}
                else{ispay=1;

                }
            }
        });
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp=getSharedPreferences("Login",MODE_PRIVATE);
                Interaction.recharge(a,sp.getString("Username",""));
                if (a!=null){
                    if(ispay==0) { Intent intent=new Intent(RechargeActivity.this,PaySuccess2.class);
                        String whether_pay= Integer.toString(ispay);
                        intent.putExtra("ispay",whether_pay);
                        intent.putExtra("mypay",a);
                        startActivity(intent);
                    } else {
                        Intent intent=new Intent(RechargeActivity.this,PaySuccess2.class);
                        String whether_pay= Integer.toString(ispay);
                        intent.putExtra("ispay",whether_pay);
                        intent.putExtra("mypay",a);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Toast.makeText(RechargeActivity.this,"请先选择充值金额！",Toast.LENGTH_SHORT).show();
                }
                //finish();
            }

        });

    }

    public ArrayList<ItemModel> getData() {
        String data = "5,10,20,30,50,100";
        // isDiscount ：1、有角标 2、无角标
        String isDiscount = "2";
        String dataArr[] = data.split(",");
        ArrayList<ItemModel> list = new ArrayList<>();
        for (int i = 0; i < dataArr.length; i++) {
            String count = dataArr[i] + "元";
            if (isDiscount.equals("1") && i == 0) {
                list.add(new ItemModel(ItemModel.ONE, count, true));
            } else {
                list.add(new ItemModel(ItemModel.TWO, count, false));
            }
        }

        return list;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAdapterClickInfo(ItemModel model) {
        String money = model.data.toString().replace("元", "");
        tv_recharge_money.setText(money);
        a=money;
        Log.e("充值",money);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
}

