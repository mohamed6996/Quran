package com.listenquran.quran;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by lenovo on 6/28/2017.
 */

public class Singleton {


    //create an instance of this class
    public static Singleton mInstance;

    //create a context variable
    private static Context mctx;

    private RequestQueue requestQueue;

    // create a constructor
    private Singleton(Context context) {
        mctx = context;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());
        }

        //  requestQueue = getRequestQueue();
    }


    //   public RequestQueue getRequestQueue(){
    //     if (requestQueue== null){requestQueue = Volley.newRequestQueue(mctx.getApplicationContext());}

    //    return requestQueue;
    // };


    // method to get an instance of this class
    public static synchronized Singleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Singleton(context);
        }

        return mInstance;
    }


    // method to add the req. to req.qu
    public <T> void addToReq(Request<T> request) {

        requestQueue.add(request);
    }
}

