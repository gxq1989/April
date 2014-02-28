package com.amapv2.apis.overlay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amapv2.apis.R;
import com.amapv2.apis.util.Constants;

/**
 * AMapV2地图中简单介绍一些GroundOverlay的用法.
 */
public class GroundOverlayActivity extends Activity {

	private AMap amap;
	private MapView mapview;
	private GroundOverlay groundoverlay;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groundoverlay_activity);
		mapview = (MapView) findViewById(R.id.map);
		mapview.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (amap == null) {
			amap = mapview.getMap();
			addOverlayToMap();
		}
	}

	private void addOverlayToMap() {
		amap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.936713,
				116.386475), 18));// 设置当前地图显示为北京市恭王府
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(new LatLng(39.935029, 116.384377))
				.include(new LatLng(39.939577, 116.388331)).build();
		groundoverlay = amap.addGroundOverlay(new GroundOverlayOptions()
				.anchor(0.5f, 0.5f)
				.transparency(0.1f)
				.image(BitmapDescriptorFactory
						.fromResource(R.drawable.groundoverlay))
				.positionFromBounds(bounds));
	}

	/**
	 * 方法必须重写
	 */
	protected void onResume() {
		super.onResume();
		mapview.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapview.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
	}

	public View getView(String title, String text) {
		View view = getLayoutInflater().inflate(R.layout.marker, null);
		TextView text_title = (TextView) view.findViewById(R.id.marker_title);
		TextView text_text = (TextView) view.findViewById(R.id.marker_text);
		text_title.setText(title);
		text_text.setText(text);
		return view;
	}
}
