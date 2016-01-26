package com.jack.domoscrum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MobileList extends BaseAdapter {

	private Context context;
	//private final String[] mobileValues={};
	private final ArrayList<HashMap<String, String>> mobilItem;
	private static final String TAG_DESCRIPCION = "descripcion";
	private static final String TAG_PRECIO = "precio";
	private static final String TAG_IMAGEN = "imagen";
	

	public MobileList(Context context, ArrayList<HashMap<String, String>> itemList) {
		this.context = context;
		this.mobilItem = itemList;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {
			gridView = new View(context);
			gridView = inflater.inflate(R.layout.activity_mobile_list, null);
			setValues(mobilItem,gridView,position);
		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}
	
	
	private void setValues(ArrayList<HashMap<String, String>> mobilItem2, View gridView, int i)
	{
		Iterator it = mobilItem2.get(i).entrySet().iterator();		
			
		while (it.hasNext())
		{
			Map.Entry e = (Map.Entry) it.next();
				
			if (e.getValue() != null)
			{
				if (e.getKey().toString().equals(TAG_DESCRIPCION))
				{
					TextView textView = (TextView) gridView.findViewById(R.id.descripcion);
					textView.setText(e.getValue().toString());
				}
				else 
					if(e.getKey().toString().equals(TAG_PRECIO))
					{
						TextView textView = (TextView) gridView.findViewById(R.id.precioItem);
						textView.setText(e.getValue().toString());
					}
					else
						if(e.getKey().toString().equals(TAG_IMAGEN))
						{
							if (e.getValue().equals("marcadora"))
							{
								ImageView imageView = (ImageView) gridView.findViewById(R.id.image);
								imageView.setImageResource(R.drawable.marcadora);
							} else 
								if (e.getValue().equals("marcadorapro"))
								{
									ImageView imageView = (ImageView) gridView.findViewById(R.id.image);
									imageView.setImageResource(R.drawable.marcadorapro);
								} else 
									if (e.getValue().equals("balashome"))
									{
										ImageView imageView = (ImageView) gridView.findViewById(R.id.image);
										imageView.setImageResource(R.drawable.balas_home);
									} else 
										if  (e.getValue().equals("balasimpact"))
										{
											ImageView imageView = (ImageView) gridView.findViewById(R.id.image);
											imageView.setImageResource(R.drawable.balas_impact);
										}else 
										{
											ImageView imageView = (ImageView) gridView.findViewById(R.id.image);
											imageView.setImageResource(R.drawable.balas_predator);
										}
						}
			}
		}
	}
	

	@Override
	public int getCount() {
		return mobilItem.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
