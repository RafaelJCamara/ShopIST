package com.example.shopist.Utils.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopist.Product.PantryProduct;
import com.example.shopist.R;
import com.example.shopist.Product.Product;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    Context context;
    ArrayList<? extends Product> list;

    public ItemListAdapter(Context context, ArrayList<? extends Product> list) {
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

        Product product = list.get(position);
        Class<?> clazz = product.getClass();

        convertView = getConvertView(clazz, parent);

        ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
        TextView name = (TextView) convertView.findViewById(R.id.item_name);

        icon.setImageResource(product.getImage());

        //String productInfo = productName+"; "+productDescription+"; Needed: "+needed+" ; "+"Stock: "+stock;
        fillDetails(convertView, product, clazz);

        return convertView;
    }

    private void fillDetails(View convertView, Product product, Class<?> clazz) {

        if(clazz == PantryProduct.class) {

            PantryProduct pProduct = (PantryProduct) product;

            TextView name = (TextView) convertView.findViewById(R.id.item_name);
            name.setText(pProduct.getName() + ": " + pProduct.getDescription() + "; Needed: " + pProduct.getNeeded() + " Stock: " + pProduct.getStock());

        }

    }

    private View getConvertView(Class<?> clazz, ViewGroup parent) {

        if(clazz == PantryProduct.class) {
            return LayoutInflater.from(context).inflate(R.layout.row_productlist, parent,false );
        }
        return null;

    }
}



