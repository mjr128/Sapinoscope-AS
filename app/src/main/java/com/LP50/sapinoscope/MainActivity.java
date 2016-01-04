package com.LP50.sapinoscope;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity 
{
	private Context contexte = this; 

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{  
		super.onCreate(savedInstanceState);
		Log.i("Activity", "lancement du menu principal");

		setContentView(com.LP50.sapinoscope.R.layout.activity_main);

		// Bouton capture de sapin
		Button captureButton = (Button) findViewById(com.LP50.sapinoscope.R.id.button_capture);
		captureButton.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent(contexte, Parcelle_listView.class);
				startActivity(intent);
			}
		});


		
		// Bouton modifier varietes
			Button verietesBoutton = (Button) findViewById(com.LP50.sapinoscope.R.id.button_varietes);
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
