package com.example.shopist.Utils.Other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopist.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<String> shopList;
    private ArrayList<String> selectedShopping;


    public Adapter(List<String> shopList){
        this.shopList = shopList;
        selectedShopping = new ArrayList<>();
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
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public ArrayList<String> getSelectedShopping(){
        return this.selectedShopping;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.shopStore);
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
