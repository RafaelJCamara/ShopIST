package com.example.shopist.Activities.ui.pantries;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopist.Activities.ui.ListFragment;
import com.example.shopist.R;
import com.example.shopist.Utils.ListManager;
import com.example.shopist.Utils.PantryListManager;

public class PantriesFragment extends ListFragment {

    private PantriesViewModel pantriesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pantriesViewModel =
                new ViewModelProvider(this).get(PantriesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pantries, container, false);

        listManager = PantryListManager.createPantryListManager(root);
        super.onCreateView(inflater, container, savedInstanceState);

        //final TextView textView = root.findViewById(R.id.text_pantries);
        pantriesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }
}