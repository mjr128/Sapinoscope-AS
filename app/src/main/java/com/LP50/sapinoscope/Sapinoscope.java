package com.LP50.sapinoscope;

import android.app.Application;
import android.content.Context;

public class Sapinoscope extends Application{

    private static Context context;
    private static DataBaseHelper dbHelper;
    private static Location_helper locationHelper; 

    public void onCreate(){
        super.onCreate();
        Sapinoscope.context = getApplicationContext();
        dbHelper = new DataBaseHelper(context);
        locationHelper = new Location_helper();
    }

    public static Context getAppContext() {
        return Sapinoscope.context;
    }
    
    public static DataBaseHelper getDataBaseHelper()
    {
    	return dbHelper;
    }
    
    public static Location_helper getLocationHelper()
    {
    	return locationHelper;
    }
}