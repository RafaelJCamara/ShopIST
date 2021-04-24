package com.example.shopist.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shopist.Activities.ui.cart.CartViewModel;
import com.example.shopist.R;
import com.example.shopist.Server.ServerResponses.CartServerData;
import com.example.shopist.Server.ServerResponses.ListServerData;
import com.example.shopist.Server.ServerResponses.ServerProduct;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartListManager extends ListManager{

    private CartViewModel viewModel;

    public CartListManager(Context context, View view, int listID, LayoutInflater layoutInflater, CartViewModel viewModel) {
        super(context, view, listID, layoutInflater);
        this.viewModel = viewModel;
    }

    public static CartListManager createCartListManager(View view, CartViewModel viewModel) {
        return new CartListManager(view.getContext(), view,
                R.id.cartList, (LayoutInflater) view.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE ), viewModel);
    }

    //depende do tipo de lista
    public void retrieveList(){
        String url = "/cart/"+ /* replace with shoppingListId */ 1;
        Call<CartServerData> call = retrofitManager.accessRetrofitInterface().getCart(url);
        call.enqueue(new Callback<CartServerData>() {
            @Override
            public void onResponse(Call<CartServerData> call, Response<CartServerData> response) {
                if(response.code()==200){
                    //list retrieved by the server
                    CartServerData cart = response.body();
                    //render list in front-end
                    renderCart(cart);
                }
            }

            @Override
            public void onFailure(Call<CartServerData> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderCart(CartServerData cart){
        String cartName = cart.getListName();
        for(ServerProduct product : cart.getProducts()) {
            String finalListInfo = product.getName() + " | " + product.getDescription() + " | " + product.getPrice() + "€ | x" + product.getQuantity();
            listContent.add(finalListInfo);
        }
        this.viewModel.setTotal(cart.getTotal());
        listSettings();
        Toast.makeText(context, "List added with success!", Toast.LENGTH_LONG).show();
    }

    //depende do tipo de lista
    public void createList(){
        // Nothing to do here
    }

    public void handleCheckoutCartLogic() {
        String url = "/cart/" + /* replace with shoppingListId */ 1;
        Call<Void> call = retrofitManager.accessRetrofitInterface().checkoutCart(url);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==200) {
                    listContent.clear();
                    listSettings();
                    viewModel.setTotal(-1);
                    Toast.makeText(context, "Cart checked out!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "SERVER ERROR! Please try again later.", Toast.LENGTH_LONG).show();
            }
        });

    }
}
