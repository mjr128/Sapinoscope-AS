package com.ostermann.sapinoscope;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class Message_alerte_activity extends Activity 
{

	
	private String log_name_activity ="MessageAlerte";
	
	Context contexte;
	
	private int secteurID;
	private int parcelleID;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Log.i(log_name_activity+"/onCreate", "----NOUVELLE ACTIVITE START----");
		setContentView(R.layout.message_alerte);
		
		contexte = this;
		
		Intent intent_alerte = getIntent();
		secteurID = intent_alerte.getIntExtra("id", -1);
		if(secteurID == -1 || parcelleID == -1)
			Log.e("alerteActivity","Impossible de recuperer les informations, etat indetermine...");
		
		// Init
		Object_secteur secteur = new Object_secteur(secteurID);
		Object_parcelle parcelle= new Object_parcelle(secteur.getId_parc());
		
		TextView parcelle_txt = (TextView) findViewById(R.id.txt_messageAlerte_parcelleName_txt);
		parcelle_txt.setText("Parcelle : "+parcelle.getName());

		TextView secteur_txt = (TextView) findViewById(R.id.txt_messageAlerte_secteurName_txt);
		secteur_txt.setText("Secteur :"+secteur.getName());
		
		Button okButton = (Button) findViewById(R.id.bt_messageAlerte_ok);
		okButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intentAjoutSapin = new Intent(contexte, Ajout_sapin.class);
				intentAjoutSapin.putExtra("sect_id", secteurID);
				intentAjoutSapin.putExtra("new_secteur", 1);
				intentAjoutSapin.putExtra("x", 0);
				intentAjoutSapin.putExtra("y", 0);
				startActivity(intentAjoutSapin);
				finish();
			}
		});
		
	}
}
