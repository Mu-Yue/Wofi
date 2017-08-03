package com.wofi.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.wofi.CheckNetwork;
import com.wofi.R;
import com.wofi.databinding.ActivityMainBinding;
import com.wofi.navigation.NavigationActivity;
import com.wofi.navigation.WalkRouteCalculateActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,AMapLocationListener,LocationSource,
        BottomNavigationView.OnNavigationItemSelectedListener,View.OnClickListener,AMap.OnMarkerClickListener, AMap.InfoWindowAdapter{
    private MapView mapView;
    private AMap aMap;
    private ImageView menubt;
    private LinearLayout nv_header;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FrameLayout framelayout;

    private ImageButton imageButton;

    private TextView tx1;
    private boolean t=true;

    private ActivityMainBinding mBinding;

    public static final int REQUEST_CODE = 1;

    private double lat;
    private double lon;
    int count=0;

    public double end_lat;
    public double end_lon;

    //定位服务类。此类提供单次定位、持续定位、地理围栏、最后位置相关功能
    private AMapLocationClient aMapLocationClient;
    private OnLocationChangedListener listener;
    private AMapLocationClientOption aMapLocationClientOption;//定位参数设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);  //此方法必须重写

        initMap();
        initId();
        initLayout();

        drawerLayout= (DrawerLayout) findViewById(R.id.id_drawer_layout);
        navigationView= (NavigationView) findViewById(R.id.id_nv_menu);

        ZXingLibrary.initDisplayOpinion(this);

        initButton();
        initToolbar();
        setupDrawerContent(navigationView);
    }

    private void initId() {
        drawerLayout = mBinding.idDrawerLayout;
        navigationView = mBinding.idNvMenu;
        toolbar = mBinding.include.idToolbar;
        framelayout = mBinding.include.llTitleMenu;
        imageButton=mBinding.include.scan;
    }

    private void initLayout() {
        navigationView.inflateHeaderView(R.layout.header_layout);
        View drawview=navigationView.getHeaderView(0);

        nv_header= (LinearLayout) drawview.findViewById(R.id.icon_layout);
        nv_header.setOnClickListener(this);

        tx1= (TextView) drawview.findViewById(R.id.username);

        SharedPreferences sp=getSharedPreferences("Login", Context.MODE_PRIVATE);
        String username=sp.getString("Username","").trim().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        tx1.setText(username);

        imageButton= (ImageButton) findViewById(R.id.scan);
        imageButton.setOnClickListener(this);

    }

    public void initButton() {
        menubt= (ImageView) findViewById(R.id.iv_title_menu);
        menubt.setOnClickListener(this);
    }

    public void initMap()
    {
        Button button1 = (Button) findViewById(R.id.location);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                intent.putExtra("st_lat", Double.toString(lat));
                intent.putExtra("st_lon", Double.toString(lon));
                startActivity(intent);
                //Log.i("mylocation", Double.toString(lat));

            }
        });
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
        }
        aMap = mapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);//设置地图类型
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));//设置地图缩放级别
        if (CheckNetwork.getNetworkState(this)) {
        } else {
            Toast.makeText(this, "当前网络不可用，请检查网络状态", Toast.LENGTH_LONG).show();
        }
        StartLocation();
    }

    public void StartLocation() {
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.strokeColor(android.R.color.transparent);
        locationStyle.radiusFillColor(android.R.color.transparent);
        //locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.start));
        //locationStyle.strokeColor(Color.BLUE);
        locationStyle.strokeWidth(5);
        aMap.setMyLocationStyle(locationStyle);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationEnabled(true);// 设置为true表示系统定位按钮显示并响应点击，false表示隐藏，默认是false
        aMapLocationClient = new AMapLocationClient(getApplicationContext());
        aMapLocationClient.setLocationListener(this);

        aMapLocationClientOption = new AMapLocationClientOption();//初始化定位参数
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        aMapLocationClientOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        aMapLocationClientOption.setOnceLocation(false);//设置是否只定位一次,默认为false
        aMapLocationClientOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        aMapLocationClientOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        aMapLocationClientOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        aMapLocationClient.setLocationOption(aMapLocationClientOption);//给定位客户端对象设置定位参数
        aMapLocationClient.startLocation();//启动定位
    }

    public void initToolbar()
    {
        toolbar= (Toolbar) findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
        //销毁定位客户端
        if(aMapLocationClient!=null){
            aMapLocationClient.onDestroy();
            aMapLocationClient = null;
            aMapLocationClientOption = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(listener!=null && aMapLocation!=null) {
            listener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                if(t)
                {
                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(lat,lon),16,0,0)));//设置地图缩放级别
                    t=false;
                }
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getRoad();//街道信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                lat = aMapLocation.getLatitude();//获取纬度
                lon = aMapLocation.getLongitude();//获取经度
                Marker marker;
                Random random=new Random();
                if(count<10) {
                    for (int i = 0; i < 10; i++) {
                        double la, lo;
                        la = random.nextDouble()/50;
                        lo = random.nextDouble()/50;
                        LatLng latLng = new LatLng(lat, lon);
                        switch(count%4)
                        {
                            case 0:
                                latLng = new LatLng(lat +la,lon+lo);break;
                            case 1:
                                latLng = new LatLng(lat -la,lon+lo);break;
                            case 2:
                                latLng = new LatLng(lat +la,lon-lo);break;
                            case 3:
                                latLng = new LatLng(lat -la,lon-lo);break;
                        }
                        marker = aMap.addMarker(new MarkerOptions().position(latLng).title(null).snippet(null));
                        //BitmapDescriptorFactory.fromResource(R.drawable.bike);
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bike));
                        LatLng end_location = marker.getPosition();
                        end_lat = end_location.latitude;
                        end_lon = end_location.longitude;
                        //Log.i("st_lat", Double.toString(lat));
                        //Log.i("st_lon", Double.toString(lon));
                        //Log.i("end_lat", Double.toString(end_lat));
                        //Log.i("end_lon", Double.toString(end_lon));
                        //final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title("自行车").snippet("Wofi"));
                        count++;
                    }
                }
                AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
                    // marker 对象被点击时回调的接口，返回 true 则表示接口已响应事件，否则返回false
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Intent intent = new Intent(MainActivity.this, WalkRouteCalculateActivity.class);
                        LatLng end_location = marker.getPosition();
                        end_lat = end_location.latitude;
                        end_lon = end_location.longitude;
                        intent.putExtra("st_lat", Double.toString(lat));
                        intent.putExtra("st_lon", Double.toString(lon));
                        intent.putExtra("end_lat", Double.toString(end_lat));
                        intent.putExtra("end_lon", Double.toString(end_lon));
                        //Log.i("st_lat", Double.toString(lat));
                        //Log.i("st_lon", Double.toString(lon));
                        //Log.i("end_lat", Double.toString(end_lat));
                        //Log.i("end_lon", Double.toString(end_lon));
                        startActivity(intent);
                        return false;
                    }
                };
                aMap.setOnMarkerClickListener(markerClickListener);// 绑定 Marker 被点击事件
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("Tomato","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        listener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {

    }

    public void setupDrawerContent(NavigationView navigationView) {
        this.navigationView.setNavigationItemSelectedListener(

                new NavigationView.OnNavigationItemSelectedListener()
                {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        menuItem.setChecked(false);
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId())
                        {
                            case R.id.action_trip:
                                Intent in1=new Intent(MainActivity.this,MyJourneyActivity.class);
                                startActivity(in1);
                                break;
                            case R.id.action_wallet:
                                Intent in2=new Intent(MainActivity.this,MyWalletActivity.class);
                                startActivity(in2);
                                break;
                            case R.id.action_baoxiu:
                                Intent in3=new Intent(MainActivity.this,RepairActivity.class);
                                startActivity(in3);
                                break;
                            case R.id.action_guide:
                                Intent in4=new Intent(MainActivity.this,UserGuideActivity.class);
                                startActivity(in4);
                                break;
                            case R.id.action_about:
                                Intent in5=new Intent(MainActivity.this,AboutActivity.class);
                                startActivity(in5);
                                break;
                            case R.id.action_logout:
                                logoutdialog();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.iv_title_menu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.icon_layout:
                drawerLayout.closeDrawer(GravityCompat.START);
                //Toast.makeText(getApplicationContext(),"个人中心",Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(MainActivity.this,PersonalInformation.class);
                startActivityForResult(intent1,REQUEST_CODE);
                break;
            case R.id.scan:
                Intent intent=new Intent(MainActivity.this,ScanActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        // TODO Auto-generated method stub
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_logout)
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public void logoutdialog(){


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("退出登录");
        builder.setMessage("确定退出登录？");
        DialogInterface.OnClickListener dialogOnclicklistener=new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i)
                {
                    case Dialog.BUTTON_POSITIVE:
                        SharedPreferences sp=getSharedPreferences("Login",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();editor.clear();
                        editor.commit();
                        //Log.e("听说名字长才能找得到",sp.getString("token",""));
                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        break;
                    default:
                        break;
                }
            }
        };
        builder.setPositiveButton("确定",dialogOnclicklistener);
        builder.setNegativeButton("取消",dialogOnclicklistener);
        builder.create().show();
    }
}
