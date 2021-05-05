package com.example.shopist.Utils;

public class PublicInfoManager {
    //this class contains important info for crowdsourcing purposes

    /*
    *  Info for estimating queue waiting times
    * */
    private static String currentStoreId;
    private static int currentNumberItemsInCart;

    public static void setCurrentStoreId(String newStoreId){
        currentStoreId = newStoreId;
    }

    public static String getCurrentStoreId() {
        return currentStoreId;
    }

    public static void setCurrentNumberItemsInCart(int currentNumberItemsInCart) {
        PublicInfoManager.currentNumberItemsInCart = currentNumberItemsInCart;
    }

    public static int getCurrentNumberItemsInCart() {
        return currentNumberItemsInCart;
    }

}
