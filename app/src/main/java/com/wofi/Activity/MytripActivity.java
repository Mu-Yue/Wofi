package com.wofi.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.wofi.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MytripActivity extends AppCompatActivity implements AMapLocationListener {

    private ImageView ivbtn1 = null;
    private ImageView ivbtn2 = null;
    private String imagePath;
    private MapView mMapView;
    private AMap aMap;
    public AMapLocationClient client;
    public AMapLocationClientOption aMapLocationClientOption;
    private List<LatLng> list;
    Polyline polyline;
    Marker marker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mytrip);
        super.onCreate(savedInstanceState);
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){actionBar.hide();}*/
        mMapView = (MapView) findViewById(R.id.my_trip);
        aMap=mMapView.getMap();
        list=new ArrayList<>();

        ivbtn1 = (ImageView)findViewById(R.id.imagebtn1);
        ivbtn2 = (ImageView)findViewById(R.id.imagebtn2);
        ivbtn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ivbtn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                screenshot(MytripActivity.this);

            }
        });
        LatLng []latLng={new LatLng(34.2106010000,117.1411380000), new LatLng(34.2145580000,117.1415030000),new LatLng(34.2151620000,117.1433050000),
                new LatLng(34.2193850000,117.1473180000), new LatLng(34.2187110000,117.1514380000),new LatLng(34.2181960000,117.1518670000),
                new LatLng(34.2164930000,117.1516740000), new LatLng(34.2142390000,117.1497430000),new LatLng(34.2133870000,117.1484550000),
                new LatLng(34.2124650000,117.1476830000), new LatLng(34.2109210000,117.1460520000),new LatLng(34.2106550000,117.1441000000)
        };

        for(int i=0;i<latLng.length;i++)
        {
            list.add(latLng[i]);
        }
        client = new AMapLocationClient(getApplicationContext());
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMapLocationClientOption = new AMapLocationClientOption(); // 声明定位参数
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        aMapLocationClientOption.setNeedAddress(true); //设置是否返回地址信息（默认返回地址信息）
        aMapLocationClientOption.setOnceLocation(false);
        //设置是否只定位一次,默认为false
        aMapLocationClientOption.setWifiActiveScan(true); //设置是否强制刷新WIFI，默认为强制刷新
        aMapLocationClientOption.setMockEnable(true); //设置是否允许模拟位置,默认为false，不允许模拟位置
        aMapLocationClientOption.setInterval(200000); //设置定位间隔,单位毫秒,默认为2000ms
        client.setLocationOption(aMapLocationClientOption); //给定位客户端对象设置定位参数
        client.startLocation(); // 开始定位
        client.setLocationListener(this);

    }  @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        // 检测一下你当前是否正在定位
        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()); // 获取你当前的位置信息
        if (aMapLocation.getLatitude() == 0.0 && aMapLocation.getLongitude() == 0.0) {
            Toast.makeText(MytripActivity.this, "请检查网络", Toast.LENGTH_SHORT).show(); // 当网络有问题或者刚开始定位的时候会出现经纬度为0的情况，这里进行过滤
        }
        polyline = aMap.addPolyline(new PolylineOptions().color(Color.GREEN));
        polyline.setPoints(list); //
        // 绘制轨迹
        if (marker != null)
        { marker.remove(); }
        marker = aMap.addMarker(new MarkerOptions().position(list.get(0)) .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.stratpoint)));
        // 在你的当前位置绘制一个红点，标出你所在的位置，并且会随着你位置的移动而移动
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(list.get(0), 15);
        aMap.animateCamera(update); // 以你当前的位置为中心，并且地图大小级别为15
        marker = aMap.addMarker(new MarkerOptions().position(list.get(list.size()-1)) .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.endpoint)));
    }

    //截取屏幕的方法
    private void screenshot(Activity activity) {
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if(null == bitmap){
                    return;
                }
                try {
                    // 获取内置SD卡路径
                    String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                    // 图片文件路径
                    imagePath = sdCardPath + File.separator + "screenshot.png";
                    File file = new File(imagePath);
                    FileOutputStream fos = new FileOutputStream(file);
                    boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    try {
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuffer buffer = new StringBuffer();
                    if (b)
                        buffer.append("截屏成功 ");
                    else {
                        buffer.append("截屏失败 ");
                    }
                    if (i != 0)
                        buffer.append("地图渲染完成，截屏无网格");
                    else {
                        buffer.append( "地图未渲染完成，截屏有网格");
                    }
                   /* Toast.makeText(getApplicationContext(), buffer.toString(),Toast.LENGTH_LONG).show();*/

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (imagePath != null){
                    Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
                    File file = new File(imagePath);
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));// 分享的内容
                    intent.setType("image/*");// 分享发送的数据类型
                    Intent chooser = Intent.createChooser(intent, "Share screen shot");
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivity(chooser);
                    }
                } else {
                    Toast.makeText(MytripActivity.this, "先截屏，再分享", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}

