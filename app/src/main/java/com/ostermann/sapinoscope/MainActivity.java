package com.ostermann.sapinoscope;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private Context contexte = this; 

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{  
		super.onCreate(savedInstanceState);
		Log.i("Activity", "lancement du menu principal");

		setContentView(R.layout.activity_main);

		// Bouton capture de sapin
		Button captureButton = (Button) findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent(contexte, Parcelle_listView.class);
				startActivity(intent);
			}
		});


		
		// Bouton modifier varietes
			Button verietesBoutton = (Button) findViewById(R.id.button_varietes);
			verietesBoutton.setOnClickListener(new OnClickListener() 
			{
				public void onClick(View v) 
				{
					Intent intent = new Intent(contexte, Varietes_Listview.class);
					startActivity(intent);
				}
			});
		

	}


	


}
