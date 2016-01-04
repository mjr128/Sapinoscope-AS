package com.LP50.sapinoscope;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Parcelle_listView extends Activity {

	private Context contexte = this;
	private AlertDialog.Builder dialogBuilder;
	private ListView liste_parcelle;
	private Vector<Object_parcelle> tab_parcelle = null;
	private int item_listview_select = 0;
	private String log_name_activity = "Parcelle_Listview";
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(com.LP50.sapinoscope.R.layout.activity_parcelle_listview);
		liste_parcelle = (ListView) findViewById(com.LP50.sapinoscope.R.id.listView_parcelle);
		Log.i(log_name_activity, "----NOUVELLE ACTIVITE----");
		
		registerForContextMenu(liste_parcelle);
		//Initialisation
		parcelle_listview(liste_parcelle);		
		parcelle_ClickCallBack(liste_parcelle);
		
		// Bouton ajout parcelle
		Button add_parcelle = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_add_parcelle);
		add_parcelle.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				EditText edt_add_parcelle = (EditText) findViewById(com.LP50.sapinoscope.R.id.editText_add_parcelle);
				String name_parcelle = edt_add_parcelle.getText().toString();
				edt_add_parcelle.setText(""); 
				// AJOUT D UNE NOUVELLE PARCELLE
				start_activity_parcelle_modification(0,name_parcelle,1); 
			}
		});
	}
	
	
	//**************************************************************************//
	private void parcelle_listview(ListView liste_parcelle) 
	{
		tab_parcelle =  Object_parcelle.createListOfAllParcelle();
		ArrayAdapter<Object_parcelle> adapter = new ArrayAdapter<Object_parcelle>(this, com.LP50.sapinoscope.R.layout.parcelle_texte,tab_parcelle);
		liste_parcelle.setAdapter(adapter);
	}

	
	//**************************************************************************//
	// Gestion du clic
	private void parcelle_ClickCallBack(ListView liste_parcelle) 
	{	
		liste_parcelle.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) 
			{
				Intent intent_secteur = new Intent(contexte, Secteur_activity.class);
				intent_secteur.putExtra("id", tab_parcelle.get(position).getId());
				intent_secteur.putExtra("name", tab_parcelle.get(position).getName());
				startActivity(intent_secteur);
			}
		});
	}
	
	
	//**************************************************************************//
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		 if (v.getId() == com.LP50.sapinoscope.R.id.listView_parcelle)
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
		   if (item.getTitle() == "Edition") {
			   start_activity_parcelle_modification(tab_parcelle.get(item_listview_select).getId(),tab_parcelle.get(item_listview_select).getName(),0); //ACION UPDATE
		   }
		   else if (item.getTitle() == "Supprimer") {
			   parcelle_delete(tab_parcelle.get(item_listview_select).getId(),tab_parcelle.get(item_listview_select).getName());
		   }
		   else {
		      return false;
		   }
		   return true;
	}
	
	
	//**************************************************************************//
	private void parcelle_delete(final int id,final String name)
	{
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("ATTENTION");
		dialogBuilder.setMessage("Voulez vous supprimer toutes les donnees de "+name+"?");

		// Bouton positif
		dialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
				try
				{
					db.execSQL("DELETE FROM PARCELLE WHERE PARC_ID="+id);
					Toast.makeText(getApplicationContext(), "Suppression de "+name, Toast.LENGTH_SHORT).show();
					parcelle_listview(liste_parcelle);
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
			}
		});
		// Bouton negatif
		dialogBuilder.setNegativeButton("Non", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
			}
		});
		
		// Output
		AlertDialog popup_parcelle = dialogBuilder.create();
		popup_parcelle.show();
	}
	
	
	//**************************************************************************//
	public void start_activity_parcelle_modification(int id, String name, int add_or_modify)
	{
		//  add_or_modify
		//  1 : ADD  
		//  0 : UPDATE
		
		Intent intent_parc_add = new Intent(contexte, Parcelle_modification.class);
		intent_parc_add.putExtra("id", id);
		intent_parc_add.putExtra("name", name);
		intent_parc_add.putExtra("add", add_or_modify);
		startActivity(intent_parc_add);
		finish();
	}
	
}
