package com.example.shopist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ProductClass> list;

    public ItemListAdapter(Context context, ArrayList<ProductClass> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(R.layout.row_productlist, parent,false );

        ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
        TextView name = (TextView) convertView.findViewById(R.id.item_name);

        icon.setImageResource(list.get(position).getImage());
        name.setText(list.get(position).getName());
        return convertView;
    }
}
