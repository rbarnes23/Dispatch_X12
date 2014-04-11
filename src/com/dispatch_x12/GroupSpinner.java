package com.dispatch_x12;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class GroupSpinner extends Spinner {
	String id = "";
	Context mContext;
	int mSelectedPosition;
	private GroupAdapter mSpinnerAdapter;
	String[] strings = { "CoderzHeaven", "Google", "Microsoft", "Apple",
			"Yahoo", "Samsung" };

	String[] subs = { "Heaven of all working codes ", "Google sub",
			"Microsoft sub", "Apple sub", "Yahoo sub", "Samsung sub" };
	// references to our images
	int[] arr_images = { R.drawable.appicon36, R.drawable.colorline,
			R.drawable.brush, R.drawable.clearme, R.drawable.drawerasericon,
			R.drawable.eyedropper, };

	/*
	 * int arr_images[] = { R.drawable.arrow_left, R.drawable.arrowleft_fancy,
	 * R.drawable.bg_striped_img, R.drawable.eyedropper2, R.drawable.protractor,
	 * R.drawable.rounded_edges_red};
	 */
	public GroupSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mSpinnerAdapter = new GroupAdapter(context, R.layout.galleryitem2,
				strings);
		this.setAdapter(mSpinnerAdapter);
	}

	public String getSelectionId() {
		return id;
	}

	public class GroupAdapter extends ArrayAdapter<String> {
		private ViewHolder holder;

		public GroupAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			return getCustomView(position, convertView, parent);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getCustomView(position, convertView, parent);
		}

		public View getCustomView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = mInflater.inflate(R.layout.galleryitem2, null);
				holder = new ViewHolder(view);
				view.setTag(holder);

			} else {
				holder = (ViewHolder) view.getTag();
			}

			/*
			 * TextView label=(TextView)view.findViewById(R.id.company);
			 * label.setText(strings[position]);
			 * 
			 * TextView sub=(TextView)view.findViewById(R.id.sub);
			 * sub.setText(subs[position]);
			 */
			//view.setTag(position);
			holder.company.setText(strings[position]);
			holder.sub.setText(subs[position]);
			holder.icon.setImageResource(arr_images[position]);
			final int pos =position; 
			// icon.setTag(position);
			 view.setOnClickListener(new OnClickListener() {
			
			 public void onClick(View v) {
			 mSelectedPosition = pos;
			
			 switch(mSelectedPosition){
			 case 0:
				 //MainActivity.setToast(Integer.toString(pos),0);
			 break;

			 case 1:
				 //MainActivity.setToast(Integer.toString(pos),0);
				 
			 break;
			 case 2:
				 //MainActivity.setToast(Integer.toString(pos),0);	 
			 break;
			 case 4:
				 //MainActivity.setToast(Integer.toString(pos),0);
			 break;

			 default:
				 //MainActivity.setToast(Integer.toString(pos),0);
			 break;
			 }
			 
			
			 }
			 });

			return view;
		}
	}

	class ViewHolder {
		public TextView company;
		public TextView sub;
		public ImageView icon;


		public ViewHolder(View view) {
			company = (TextView) view.findViewById(R.id.company);
			sub = (TextView) view.findViewById(R.id.sub);
			icon = (ImageView) view.findViewById(R.id.thumbImage2);
			icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

		}
	}
}