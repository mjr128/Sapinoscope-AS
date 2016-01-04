package com.LP50.sapinoscope;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class Choix_depart_sapin extends Activity {

	
	private String log_name_activity ="ChoixDepartSapin";
	private Object_secteur secteur = null;
	private Context contexte = this;
	private Spinner spin_colonneY;
	private Spinner spin_ligneX;
	private Vector<Object_sapinDetails> L_sapinD;
	private Object_sapinDetails sapin;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(com.LP50.sapinoscope.R.layout.activity_choix_depart_sapin);
		Log.i(log_name_activity+"/onCreate", "----NOUVELLE ACTIVITE----");

		spin_colonneY = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_choixSapin_colonneY);
		spin_ligneX = (Spinner)   findViewById(com.LP50.sapinoscope.R.id.spin_choixSapin_ligne);
		
		// Reception INTENT
		Intent intent_depart = getIntent();
		int secteur_id = intent_depart.getIntExtra("id", -1);
		Log.i(log_name_activity+"/onCreate", "INTENT GET"+secteur_id);
		
		if ( secteur_id == -1)
			Log.e(log_name_activity+"/onCreate","Impossible de récupéré secteur ID");
		
		// Initialisation
		initialisation(secteur_id);
		
		spin_colonneY.setOnItemSelectedListener(new OnItemSelectedListener() 
		{

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				init_spinner_ligneX(spin_colonneY.getSelectedItemPosition());
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		spin_ligneX.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			public void onItemSelected(AdapterView<?> adapter, View view, int position, long arg3) 
			{
				sapin = (Object_sapinDetails) adapter.getItemAtPosition(position);
				Log.i("ChoixDepartSapin", sapin.toString());
			}

			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		
		// Validation du choix
		Button button_ok = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_choixSapin_ok);
		button_ok.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("LongLogTag")
			public void onClick(View v) {
				Intent intent = new Intent(contexte, Ajout_sapin.class);
				if(sapin == null) {
					intent.putExtra("sect_id", secteur.getId());
					intent.putExtra("x", 0);
					intent.putExtra("y", 0);
					intent.putExtra("new_secteur", 0); // 0 : secteur existant
				}else {
					intent.putExtra("sect_id", secteur.getId());
					intent.putExtra("x", sapin.getLigne());
					intent.putExtra("y", sapin.getColonne());
					intent.putExtra("new_secteur", 0); // 0 : secteur existant
					Log.i("ChoixDepartSapin/buttonOK", "INTENT SEND SEC_ID:" + secteur.getId() +
							" x:" + (sapin.getLigne()) +
							" y:" + (sapin.getColonne()));
				}

				startActivity(intent);
				finish();
				
			}
		});
		
	}

	public void initialisation(int secteur_id)
	{
		secteur = new Object_secteur(secteur_id);
		//sapin = Object_sapin.createListOfSapin(secteur_id); 
		int max_y = Object_sapin.selectMaxNbSapins("SAP_COL", "SEC_ID", secteur.getId());
		Log.i(log_name_activity+"/initialisation", "selectMaxNbSapins:"+max_y);
		init_spinner_colonneY(max_y);
		init_spinner_ligneX(max_y);
	}
	
	public void init_spinner_colonneY(int max_y)
	{
		
		List<String> liste_colonneY = new ArrayList<String>();
		for (int j = 0; j <= max_y; j++) 
		{
			/*L_sapinD = Object_sapinDetails.createListOfSapin_Y(secteur.getId(), j, 0);
			L_sapinD.size();*/
			liste_colonneY.add("Ligne "+(j+1)+"   : "+Object_sapin.countNbSapin("SAP_COL", j,"SEC_ID",secteur.getId())+" sapins");
			Log.i(log_name_activity+"/initialisation", "liste_colonneY add:"+(j+1));
		}	

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, liste_colonneY);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_colonneY.setAdapter(adapter3);
		spin_colonneY.setSelection(max_y);
		Log.i(log_name_activity+"/initialisation", "init_spinner_colonneY OK");
	}
	
	public void init_spinner_ligneX(int y)
	{
		L_sapinD = Object_sapinDetails.createListOfSapin_Y(secteur.getId(), y, 0);
		
		ArrayAdapter<Object_sapinDetails> adapterX = new ArrayAdapter<Object_sapinDetails>(this, com.LP50.sapinoscope.R.layout.parcelle_texte,L_sapinD);
		spin_ligneX.setAdapter(adapterX);
		spin_ligneX.setSelection((L_sapinD.size()-1));
		Log.i(log_name_activity+"/initialisation", "init_spinner_colonneX OK");
	}
	
	
	
}
