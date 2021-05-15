package com.example.shopist.Utils.Other;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopist.Activities.ui.cart.CartActivity;
import com.example.shopist.Product.Product;
import com.example.shopist.R;

import java.util.ArrayList;
import java.util.List;

public class DistributeProductsAtCartAdapter extends RecyclerView.Adapter<DistributeProductsAtCartAdapter.MyViewHolderCart> {
    private List<String> shopList;
//    public static ArrayList<PantryInCartContent> selectedShopping;
    private String productInfo;

    public DistributeProductsAtCartAdapter(List<String> shopList, String productInfo){
        Log.i("Beginning","*****adapter");
        this.shopList = shopList;
        this.productInfo=productInfo;
//        selectedShopping = new ArrayList<PantryInCartContent>();
        for(String s : shopList){
            Log.i("Beginning",s);
        }
    }

    @NonNull
    @Override
    public DistributeProductsAtCartAdapter.MyViewHolderCart onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pantry_cart, parent, false);
        return new DistributeProductsAtCartAdapter.MyViewHolderCart(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull DistributeProductsAtCartAdapter.MyViewHolderCart holder, int position) {
        String s = shopList.get(position);
        holder.textView.setText(s);
        Log.i("Message", "ShopList:" +s);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public class MyViewHolderCart extends RecyclerView.ViewHolder{

        TextView textView;
        EditText editText;

        public MyViewHolderCart(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.pCartName);
            editText = (EditText) itemView.findViewById(R.id.amountToGive);
//            editText.setTag(textView.getText().toString()+"-number");
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String pantryListInfo = textView.getText().toString();
                    String inputedValue = editText.getText().toString();
                    String productName = productInfo.split(" | ")[0];
                    CartActivity.recordChange(inputedValue, productName, pantryListInfo.split("->")[1]);
                }
            });
        }
    }

}
