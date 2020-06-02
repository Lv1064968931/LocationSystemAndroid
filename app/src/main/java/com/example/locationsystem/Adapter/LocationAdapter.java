package com.example.locationsystem.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.locationsystem.Entity.Location;
import com.example.locationsystem.R;

import java.util.List;

public class LocationAdapter extends BaseAdapter {

    private List<Location> locationList = null;
    private LayoutInflater inflater;
    public LocationAdapter(){}
    public LocationAdapter(List<Location> locationList, Context context){
            this.locationList = locationList;
            this.inflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return locationList==null?0:locationList.size();
    }

    @Override
    public Object getItem(int position) {
        return locationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_display, null);
            viewHolder.beaconNameTV = (TextView)convertView
                    .findViewById(R.id.beaconNameTV);
            viewHolder.xTV = (TextView)convertView
                    .findViewById(R.id.xTV);
            viewHolder.yTV = (TextView)convertView
                    .findViewById(R.id.yTV);
            viewHolder.locationTV = (TextView)convertView
                    .findViewById(R.id.loactionTV);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Location location = locationList.get(position);
        viewHolder.beaconNameTV.setText(location.getBeaconName());
        viewHolder.xTV.setText(String.valueOf(location.getX()));
        viewHolder.yTV.setText(String.valueOf(location.getY()));
        viewHolder.locationTV.setText(location.getLocation());
        return convertView;
    }

    private class ViewHolder{
        TextView beaconNameTV,xTV,yTV,locationTV;
    }

}
