package com.ostermann.sapinoscope;

import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Secteur_activity extends Activity {
	
	private Context contexte = this;
	private AlertDialog.Builder dialogBuilder;
	private ListView liste_secteur;
	private Vector<Object_secteur> tab_secteur;
	private int item_listview_select = 0;
	private String log_name_activity ="SecteurListview";
	private Object_parcelle parcelle_recep;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_secteur);
		liste_secteur = (ListView) findViewById(R.id.listView_secteur);
		Log.i(log_name_activity+"/onCreate", "----NOUVELLE ACTIVITE----");
		
		// Reception INTENT
		Intent intent_secteur = getIntent();
		int parcelle_id = intent_secteur.getIntExtra("id", -1);
		
		Log.i(log_name_activity+"/onCreate","INTENT GET : PARC_ID:"+parcelle_id);

		if ( parcelle_id == -1)
		{
			Log.e(log_name_activity+"/onCreate","Impossible de récupéré le parcelle ID");
		}
		
		parcelle_recep= new Object_parcelle(parcelle_id);
	

		
		TextView txt_parcelle = (TextView) findViewById(R.id.txt_secteur_select_parcelle_titre);
		txt_parcelle.setText("Parcelle : "+parcelle_recep.getName());

		// initialisation
		registerForContextMenu(liste_secteur);
		secteur_listview(liste_secteur, parcelle_recep.getId());
		secteur_ClickCallBack(liste_secteur);
		
		// bouton ajout secteur
		Button add_secteur = (Button) findViewById(R.id.bt_add_secteur);
		add_secteur.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Log.i(log_name_activity+"/onCreate/onClick", "clic boutton ajout secteur");
				EditText edt_add_secteur = (EditText) findViewById(R.id.editText_add_secteur);
				String name_secteur = edt_add_secteur.getText().toString();
				edt_add_secteur.setText(""); //vide le champ de saisie
				Object_secteur secteur = new Object_secteur(1, parcelle_recep.getId(), name_secteur, 0, 1);
				start_activity_secteur_modification(secteur, 1);
			}
		});
	}
	
	//**************************************************************************//	
	private void secteur_listview(ListView liste_secteur,int parcelle_id) 
	{
		tab_secteur =  Object_secteur.createListOfSecteur(parcelle_id);
		ArrayAdapter<Object_secteur> adapter_secteur = new ArrayAdapter<Object_secteur>(this,R.layout.secteur_texte,tab_secteur); 
		liste_secteur.setAdapter(adapter_secteur);	
	}

	
	//**************************************************************************//
	// Gestion du clic
	private void secteur_ClickCallBack(ListView liste_secteur) 
	{	
		liste_secteur.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) 
			{
				// Selection d'un SECTEUR -- GO to ADD SAPIN / put parc_id,sect_id
				Intent intent_addsapin = new Intent(contexte, Choix_depart_sapin.class);
				intent_addsapin.putExtra("id", tab_secteur.get(position).getId());
				Log.i(log_name_activity+"/secteur_ClickCallBack","INTENT SET : PARC_ID:"+tab_secteur.get(position).getId_parc()+" SECT_N:"+tab_secteur.get(position).getId());
				startActivity(intent_addsapin);
			}
		});
	}
	
	//**************************************************************************//
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		 if (v.getId() == R.id.listView_secteur) 
	     {
	    	    AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
	    	    item_listview_select = acmi.position;
	    	    menu.add("Edition");
	    	    menu.add("Supprimer");
    	}
			     
	}

	
	//**************************************************************************//	
		public boolean onContextItemSelected(MenuItem item) 
		{
			   if (item.getTitle() == "Edition") 
			   {
				   start_activity_secteur_modification(tab_secteur.get(item_listview_select), 0);
			   }
			   else if (item.getTitle() == "Supprimer") 
			   {
				   secteur_delete(tab_secteur.get(item_listview_select));
			   }
			   else 
			   {
			      return false;
			   }
			   return true;
		}
		
		

	//**************************************************************************//
		private void secteur_delete(final Object_secteur secteur)
		{
			dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("ATTENTION");
			dialogBuilder.setMessage("Voulez vous supprimer toutes les données de "+secteur.getName()+"?");

			// Bouton positif
			dialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						String req = "DELETE FROM SECTEUR WHERE SEC_ID="+secteur.getId();
						db.execSQL(req);
						Toast.makeText(getApplicationContext(), "Suppression de "+secteur.getName(), Toast.LENGTH_SHORT).show();
						secteur_listview(liste_secteur,secteur.getId_parc());
						Log.i(log_name_activity+"/secteur_delete",req);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
				}
			});
			// Bouton négatif
			dialogBuilder.setNegativeButton("Non", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					// ne rien faire
				}
			});
			
			// Output
			AlertDialog popup_parcelle = dialogBuilder.create();
			popup_parcelle.show();
		}
		
		//**************************************************************************//
	
	
	
	//**************************************************************************//
	public void start_activity_secteur_modification(Object_secteur secteur, int add_or_modify)
	{
		//  add_or_modify
		//  1 : ADD  
		//  0 : UPDATE
		Log.i(log_name_activity+"/start_activity_secteur_modification", "start_activity_secteur_modification");
		Intent intent_sect_add = new Intent(contexte, Secteur_modification.class);
		intent_sect_add.putExtra("sec_id", secteur.getId());
		intent_sect_add.putExtra("parc_id", secteur.getId_parc());
		intent_sect_add.putExtra("name", secteur.getName());
		intent_sect_add.putExtra("add", add_or_modify);
		Log.i(log_name_activity+"/start_activity_secteur_modification","INTENT SET : PARC_ID:"+secteur.getId_parc()+" SECT_N:"+secteur.getName()+"SECT_ID:"+secteur.getId());
		startActivity(intent_sect_add);
		finish();
	}
	
}
