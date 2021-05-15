package com.example.shopist.Utils.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.example.shopist.Activities.PantryActivity;
import com.example.shopist.Product.CartProduct;
import com.example.shopist.Product.PantryProduct;
import com.example.shopist.R;
import com.example.shopist.Product.Product;

import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends BaseAdapter {

    private final AdapterView.OnItemClickListener itemClickListener;
    Context context;
    List<? extends Product> list;

    SimpleCallback itemDeleteCallback;

    public ItemListAdapter(Context context, List<? extends Product> list, AdapterView.OnItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    public ItemListAdapter(Context context, List<? extends Product> list, AdapterView.OnItemClickListener itemClickListener, SimpleCallback itemDeleteCallback) {
        this(context,list,itemClickListener);
        this.itemDeleteCallback = itemDeleteCallback;
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
        name.setText(product.getName());

        convertView.setOnClickListener(v -> {
            itemClickListener.onItemClick(null, v, position, 0);
        });

        fillDetails(convertView, product, clazz);

        return convertView;
    }

    private void fillDetails(View convertView, Product product, Class<?> clazz) {

        if(clazz == PantryProduct.class) {

            PantryProduct pProduct = (PantryProduct) product;

            TextView name = (TextView) convertView.findViewById(R.id.item_name);
            name.setText(pProduct.getName() + ": " + pProduct.getDescription() + "; Needed: " + pProduct.getNeeded() + " Stock: " + pProduct.getStock());

        } else if (clazz == CartProduct.class) {

            SwipeLayout swipeLayout =  (SwipeLayout) convertView.findViewById(R.id.swipe_left);
            //set show mode.
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            CartProduct cProduct = (CartProduct) product;

            Button delete = swipeLayout.findViewById(R.id.delete_button);
            delete.setOnClickListener(v -> {
                this.itemDeleteCallback.callback(cProduct);
            });

            TextView price = (TextView) convertView.findViewById(R.id.item_price);
            TextView qty = (TextView) convertView.findViewById(R.id.item_qty);

            price.setText(String.format("%.2f€", cProduct.getPrice()));
            qty.setText(String.format("x%d", cProduct.getQuantity()));

        }

    }

    private View getConvertView(Class<?> clazz, ViewGroup parent) {

        if(clazz == PantryProduct.class) {
            return LayoutInflater.from(context).inflate(R.layout.row_productlist, parent,false );
        } else if(clazz == CartProduct.class) {
            return LayoutInflater.from(context).inflate(R.layout.row_cart, parent, false);
        }
        return null;

    }
}



