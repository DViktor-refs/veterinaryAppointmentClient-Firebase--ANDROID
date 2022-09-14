package com.example.firebaselogin.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkState  {

    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager!= null){
            NetworkInfo activeNetwork =  connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null){
                if (activeNetwork.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
