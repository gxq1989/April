package com.april.whereisapril;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.april.util.AMapUtil;
import com.april.util.ToastUtil;

/**
 * 地理编码与逆地理编码功能介绍
 */
public class WhereIsAprilActivity extends Activity implements OnGeocodeSearchListener, OnClickListener,
        AMapLocationListener, Runnable {
    
    private Context mContext;
    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private AMap aMap;
    private MapView mapView;
    /**
     * by April -- 跟定位功能融合在一起，改为获取当前经纬度
     * 参考LocationNetworkActivity里的定位数据获取
     */
    private LatLonPoint latLonPoint/* = new LatLonPoint(39.90865, 116.39751)*/;
    private Button geoButton;
    private Button regeoButton;

    private LocationManagerProxy mAMapLocManager = null;
    private AMapLocation aMapLocation;// 用于判断定位超时
    private Handler handler = new Handler();

    private Marker geoMarker;
    private Marker regeoMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mContext = this;
        setContentView(R.layout.activity_where_is_april);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

        //start 
        mAMapLocManager = LocationManagerProxy.getInstance(this);
        /*
         * mAMapLocManager.setGpsEnable(false);//
         * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
         * API定位采用GPS和网络混合定位方式
         * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
         */
        mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
        handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
        //end
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        geoButton = (Button) findViewById(R.id.geoButton);
        geoButton.setOnClickListener(this);
        regeoButton = (Button) findViewById(R.id.regeoButton);
        regeoButton.setOnClickListener(this);
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocation();// 停止定位
    }

    /**
     * 销毁定位
     */
    private void stopLocation() {
        if (mAMapLocManager != null) {
            mAMapLocManager.removeUpdates(this);
            mAMapLocManager.destory();
        }
        mAMapLocManager = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        showDialog();

        GeocodeQuery query = new GeocodeQuery(name, "010");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == 0) {
            if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(address.getLatLonPoint()));
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:" + address.getFormatAddress();
                ToastUtil.show(mContext, addressName);
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else if (rCode == 27) {
            ToastUtil.show(mContext, R.string.error_network);
        } else if (rCode == 32) {
            ToastUtil.show(mContext, R.string.error_key);
        } else {
            ToastUtil.show(mContext, R.string.error_other);
        }
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress() + "附近";
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(latLonPoint), 15));
                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
                ToastUtil.show(mContext, addressName);
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else if (rCode == 27) {
            ToastUtil.show(mContext, R.string.error_network);
        } else if (rCode == 32) {
            ToastUtil.show(mContext, R.string.error_key);
        } else {
            ToastUtil.show(mContext, R.string.error_other);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        /**
         * 响应地理编码按钮
         */
        case R.id.geoButton:
            getLatlon("方恒国际中心");
            break;
        /**
         * 响应逆地理编码按钮
         */
        case R.id.regeoButton:
            getAddress(latLonPoint);
            break;
        default:
            break;
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            this.aMapLocation = location;// 判断超时机制
            Double geoLat = location.getLatitude();
            Double geoLng = location.getLongitude();
            String cityCode = "";
            String desc = "";
            Bundle locBundle = location.getExtras();
            if (locBundle != null) {
                cityCode = locBundle.getString("citycode");
                desc = locBundle.getString("desc");
            }

            latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
            regeoButton.setText("ResGeoCoding(" + location.getLatitude() + ", " + location.getLongitude());

            geoButton.setText("GeoCoding(" + desc + ")");
        }

    }

    @Override
    public File getExternalFilesDir(String type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File getExternalCacheDir() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void run() {
        if (aMapLocation == null) {
            Toast.makeText(this, "12秒内还没有定位成功，停止定位", Toast.LENGTH_SHORT).show();
            stopLocation();// 销毁掉定位
        }

    }
}