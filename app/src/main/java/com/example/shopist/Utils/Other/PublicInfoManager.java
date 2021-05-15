package com.example.shopist.Utils.Other;

public class PublicInfoManager {
    //this class contains important info for crowdsourcing purposes

    /*
    *  Info for estimating queue waiting times
    * */
    public static String currentShopUuid;
    public static int currentNumberItemsInCart;

    public static void setCurrentStoreId(String newStoreId){
        currentShopUuid = newStoreId;
    }

    public static String getCurrentStoreId() {
        return currentShopUuid;
    }

    public static void setCurrentNumberItemsInCart(int currentNumberItemsInCart) {
        PublicInfoManager.currentNumberItemsInCart = currentNumberItemsInCart;
    }

    public static int getCurrentNumberItemsInCart() {
        return currentNumberItemsInCart;
    }

}
