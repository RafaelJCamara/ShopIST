package com.example.shopist.Utils.Other;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopist.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<String> shopList;
    private ArrayList<String> selectedShopping;
    public static ArrayList<String> existingShopping = new ArrayList<String>();

    public Adapter(List<String> shopList){
        Log.i("Beginning","*****adapter");
        this.shopList = shopList;
        selectedShopping = new ArrayList<String>();
        for(String s : shopList){
            Log.i("Beginning",s);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lista, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String s = shopList.get(position);
        holder.checkBox.setText(s);
        Log.i("Message", "ShopList:" +s);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public ArrayList<String> getSelectedShopping(){
        return this.selectedShopping;
    }

    public void addShoppingLists(String[] shops){
        for(int i=0;i!=shops.length;i++){
            existingShopping.add(shops[i]);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.shopStore);
//            for(String s : Adapter.existingShopping){
//                if(s.trim().equals(checkBox.getText().toString().split(" -> ")[1].trim())){
//                    checkBox.setEnabled(true);
//                }
//            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        selectedShopping.add(checkBox.getText().toString());
                    }else{
                        selectedShopping.remove(checkBox.getText().toString());
                    }
                }
            });
        }

    }

}
