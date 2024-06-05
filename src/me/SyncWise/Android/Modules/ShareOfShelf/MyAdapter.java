		package me.SyncWise.Android.Modules.ShareOfShelf;
		
		import java.util.ArrayList;
		import java.util.TreeSet;
		
		import me.SyncWise.Android.R;
		
		import android.content.Context;
		import android.view.LayoutInflater;
		import android.view.MotionEvent;
		import android.view.View;
		import android.view.ViewGroup;
		import android.widget.BaseAdapter;
		import android.widget.EditText;
		import android.widget.TextView;
		
	public class  MyAdapter extends BaseAdapter {
		public static class ViewHolder {
			public TextView textView;
			public EditText editText;
			}
		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
		
		private ArrayList<String> mData = new ArrayList<String>();
		private LayoutInflater mInflater;
		
		private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
		
		public MyAdapter( Context context  ) {
			mInflater = LayoutInflater.from ( context );
		}
		
		public void addItem(final String item) {
		mData.add(item);
		notifyDataSetChanged();
		}
		
		public void addSeparatorItem(final String item) {
		mData.add(item);
		// save separator position
		mSeparatorsSet.add(mData.size() - 1);
		notifyDataSetChanged();
		}
		
		@Override
		public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
		}
		
		@Override
		public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
		}
		
		public int getCount() {
		return mData.size();
		}
		
		public String getItem(int position) {
		return mData.get(position);
		}
		
		public long getItemId(int position) {
		return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
		holder = new ViewHolder();
		switch (type) {
		case TYPE_ITEM:
		convertView = mInflater.inflate(R.layout.section1, parent,false);
		holder.textView = (TextView)convertView.findViewById(R.id.text);
		holder.editText=(EditText)convertView.findViewById(R.id.edit_text_notes1);
		break;
		case TYPE_SEPARATOR:
		convertView = mInflater.inflate(R.layout.section2,   parent,false);
		holder.textView = (TextView)convertView.findViewById(R.id.textSeparator);
		//holder.editText=(EditText)convertView.findViewById(R.id.edit_text_notes2);
		break;
		}
		convertView.setTag(holder);
		} else {
		holder = (ViewHolder)convertView.getTag();
		}
		holder.textView.setText(mData.get(position));
		return convertView;
		}
		
		 
		
		}
		

		 
		 