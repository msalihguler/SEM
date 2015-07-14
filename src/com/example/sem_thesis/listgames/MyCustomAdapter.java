package com.example.sem_thesis.listgames;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sem_thesis.R;

public class MyCustomAdapter extends ArrayAdapter<HashMap<String,String>>{
	ArrayList<HashMap<String,String>> arraytoList= new ArrayList<HashMap<String,String>>() ;
    private LayoutInflater inflater;

	@SuppressWarnings("unchecked")
	public MyCustomAdapter(Context context, int resource,
			ArrayList<HashMap<String,String>> arrays) {
		super(context, resource, arrays);
		inflater=LayoutInflater.from(context);
		arraytoList=(ArrayList<HashMap<String, String>>) arrays.clone();
		// TODO Auto-generated constructor stub
	}
	
	 
	private class ViewHolder{
		TextView name,description;
		ImageView rate;
	}
	  ViewHolder viewHolder;
	  
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	   
	   if(convertView==null)
	   {  
		   convertView=inflater.inflate(R.layout.list_item, null);
		    viewHolder=new ViewHolder();
		     
		    viewHolder.rate=(ImageView) convertView.findViewById(R.id.rate);
		    viewHolder.name=(TextView) convertView.findViewById(R.id.name);
		    viewHolder.description=(TextView) convertView.findViewById(R.id.description);
		     
	
		    convertView.setTag(viewHolder);
	   }
	   else
		    viewHolder=(ViewHolder) convertView.getTag();
	   		String rate_number = arraytoList.get(position).get("rate");
	   		int rateInt=Integer.parseInt(rate_number);
	   		
	   		if(rateInt==0){
	   			viewHolder.rate.setImageResource(R.drawable.zerostar);
	   		}else if(rateInt==1){
	   			viewHolder.rate.setImageResource(R.drawable.onestar);

	   		}else if(rateInt==2){
	   			viewHolder.rate.setImageResource(R.drawable.twostar);

	   		}else if(rateInt==3){
	   			viewHolder.rate.setImageResource(R.drawable.threestar);

	   		}else if(rateInt==4){
	   			viewHolder.rate.setImageResource(R.drawable.fourstar);

	   		}else if(rateInt==5){
	   			viewHolder.rate.setImageResource(R.drawable.fivestar);
	   		}
	   		viewHolder.name.setText(arraytoList.get(position).get("name").toString());
	   	   viewHolder.description.setText(arraytoList.get(position).get("description").toString());
	return convertView;
	  }    
}
