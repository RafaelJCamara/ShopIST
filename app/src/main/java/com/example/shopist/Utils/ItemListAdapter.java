package com.example.shopist.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopist.R;
import com.example.shopist.Product.Product;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> list;

    public ItemListAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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

        //String productInfo = productName+"; "+productDescription+"; Needed: "+needed+" ; "+"Stock: "+stock;

        name.setText(list.get(position).getName()+": +"+list.get(position).getDescription()+"; Needed: "+ list.get(position).getNeeded()+" Stock: "+list.get(position).getStock());
        return convertView;
    }
}



