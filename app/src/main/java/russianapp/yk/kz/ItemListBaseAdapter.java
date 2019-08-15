package russianapp.yk.kz;

import java.io.InputStream;
import java.util.ArrayList;

import russianapp.yk.kz.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListBaseAdapter extends BaseAdapter {
	private static ArrayList<ItemDetails> itemDetailsrrayList;
	
	private LayoutInflater l_Inflater;
	
	//public ImageLoader imageLoader;

	public ItemListBaseAdapter(Context context, ArrayList<ItemDetails> results) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return itemDetailsrrayList.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public String url;
	public InputStream is;

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.item_details_view, null);
			holder = new ViewHolder();
			//holder.txt_itemName = (TextView) convertView.findViewById(R.id.name);
			holder.txt_itemDescription = (TextView) convertView.findViewById(R.id.itemDescription);
			holder.txt_itemPrice = (TextView) convertView.findViewById(R.id.price);
			holder.itemImage = (ImageView) convertView.findViewById(R.id.photo);
			holder.txt_itemPhone = (TextView) convertView.findViewById(R.id.phone);
			holder.txt_itemDate = (TextView) convertView.findViewById(R.id.date);
			
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//holder.txt_itemName.setText(itemDetailsrrayList.get(position).getId());
		holder.txt_itemDescription.setText(itemDetailsrrayList.get(position).getItemDescription());
		holder.txt_itemPrice.setText(itemDetailsrrayList.get(position).getPrice());			
		holder.txt_itemPhone.setText(itemDetailsrrayList.get(position).getPhone());			
		holder.txt_itemDate.setText(itemDetailsrrayList.get(position).getDate());
    	holder.itemImage.setImageDrawable(itemDetailsrrayList.get(position).getImage());
    	
		return convertView;
	}
	
	static class ViewHolder {
		//TextView txt_itemName;
		TextView txt_itemDescription;
		TextView txt_itemPrice;
		TextView txt_itemPhone;
		TextView txt_itemDate;		
		ImageView itemImage;
	}
}
