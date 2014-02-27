package com.gxq.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyAdapter extends BaseAdapter {

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

//	private static final String TAG = "MyAdapter";
//	private Context mContext;
//
//	public MyListData mMyListData;
//
//	public MyAdapter(Context context) {
//		this.mContext = context;
//		mMyListData = new MyListData();
//	}
//
//	@Override
//	public int getCount() {
//		return mMyListData.getSize();
//	}
//
//	@Override
//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		// 优化ListView
////		if (convertView == null) {
////
////			convertView = LayoutInflater.from(mContext).inflate(
////					R.layout.mylistitem_xml, null);
////
////			ItemViewCache viewCache = new ItemViewCache();
////			viewCache.mTextView1 = (TextView) convertView
////					.findViewById(R.id.textView1);
////			viewCache.mTextView2 = (TextView) convertView
////					.findViewById(R.id.textView2);
////			convertView.setTag(viewCache);
////		}
////
////		ItemViewCache cache = (ItemViewCache) convertView.getTag();
////		// 设置文本和图片，然后返回这个View，用于ListView的Item的展示
////		String text1 = mMyListData.mArrayList.get(position).get("firstName").toString();
////		String text2 = mMyListData.mArrayList.get(position).get("lastName").toString();
////		cache.mTextView1.setText(text1);
////		cache.mTextView2.setText(text2);
//		return convertView;
//	}
//
//	// 元素的缓冲类,用于优化ListView
//	private static class ItemViewCache {
//		public TextView mTextView1;
//		public TextView mTextView2;
//	}

}
