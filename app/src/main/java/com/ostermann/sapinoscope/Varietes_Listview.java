package com.ostermann.sapinoscope;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Varietes_Listview extends Activity {

	private Context contexte = this;
	private AlertDialog.Builder dialogBuilder;
	private Vector<Object_variete> tab_variete = null;
	private ListView liste_variete;
	private String log_name_activity = "Variete_Listview";
	private int item_listview_selected = 0;
	private EditText edittext_nom_var=null;
	private Spinner spin_var_pousse=null;
	private int id_variete_selectionnee = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_varietes_listview);
		liste_variete = (ListView) findViewById(R.id.listview_varietes);
		Log.i(log_name_activity, "----NOUVELLE ACTIVITE----");
		registerForContextMenu(liste_variete);	//CA PLANTE ICI BORDEL DE MERDE ...
		
		//On cree l'edittext et le spinner
		edittext_nom_var = (EditText) findViewById(R.id.txt_variete_nom);
		spin_var_pousse = (Spinner) findViewById(R.id.spinner_pousse);
		
		//Initialisation
		init_spinner_coeff_pousse();
		Select_spinner_pousse(1);
		varietes_listview(liste_variete);	
		variete_ClickCallBack(liste_variete);
	
		liste_variete.requestFocus();


		// Bouton modif infos variete
		Button btn_modif_infos = (Button) findViewById(R.id.button_modif_variete);
		btn_modif_infos.setEnabled(false);
		btn_modif_infos.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				//On recuppere les nouvelles valeurs pour la variete selectionnee
				String nouv_nom = edittext_nom_var.getText().toString();
				float nouv_coeff = Float.parseFloat(spin_var_pousse.getSelectedItem().toString());
		
				//On fait l'update dans la BDD
				UpdateVariete(nouv_nom, nouv_coeff, id_variete_selectionnee);
				
				//On reactive le bouton ajout et on grise celui de modification
				Button btn_modif_infos = (Button) findViewById(R.id.button_modif_variete);
				Button btn_ajouter_variete = (Button) findViewById(R.id.btn_variete_nouveau);
				btn_modif_infos.setEnabled(false);
				btn_ajouter_variete.setEnabled(true);
				
				//On reinitialise les champs
				edittext_nom_var.setText("");
				Select_spinner_pousse(1);
			}
		});
		
		
		// Bouton ajouter variete
		Button btn_ajouter_variete = (Button) findViewById(R.id.btn_variete_nouveau);
		btn_ajouter_variete.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{	
				//On recupere le nom et le coeff
				String nom_variete = edittext_nom_var.getText().toString();
				float coeff_pousse = Float.parseFloat(spin_var_pousse.getSelectedItem().toString());
	
				//On ajoute une ligne a la BDD
				CreerVariete(nom_variete,coeff_pousse);
			}
		});
		
	}

	
	//**************************************************************************//
	public void init_spinner_coeff_pousse()
	{
		// Creation du spinner coef_gel
		List<String> list_coef_pousse = new ArrayList<String>();

		//Remplit la liste de 1.5 a 0.5
		double i=1.5;	
		while ( i >= 0.5 )
		{
			list_coef_pousse.add(""+i);
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef_pousse);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		//ca plante la
		spin_var_pousse.setAdapter(adapter3);
		
	}
	
	
	//**************************************************************************//
	public void CreerVariete(String nom_variete, float coef_pousse)
	{
		//On cree une nouvelle ligne dans la table INFO_SECTEUR de la BDD avec un coeff gel par defaut
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
		try
		{
			String req_variete = "INSERT INTO VARIETE ('VAR_NOM','VAR_POUSSE') VALUES ('"+nom_variete+"','"+coef_pousse+"');";
			db.execSQL(req_variete);
			Log.i(log_name_activity, req_variete);
			varietes_listview(liste_variete);
			
			//On reinitialise les champs
			edittext_nom_var.setText("");
			Select_spinner_pousse(1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	//**************************************************************************//
	public void UpdateVariete(String nom_variete, float coef_pousse, int ID_variete)
	{
		//On cree une nouvelle ligne dans la table INFO_SECTEUR de la BDD avec un coeff gel par defaut
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
		try
		{
			String req_variete = "UPDATE VARIETE SET VAR_NOM='"+nom_variete+"', VAR_POUSSE='"+coef_pousse+"' WHERE VAR_ID="+ID_variete+";";
			db.execSQL(req_variete);
			Log.i(log_name_activity, req_variete);
			varietes_listview(liste_variete);
			
			//On reinitialise les champs
			edittext_nom_var.setText("");
			Select_spinner_pousse(1);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	
	//**************************************************************************//
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) 
	{
		 if (v.getId() == R.id.listview_varietes) 
	     {
	    	    AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
	    	    item_listview_selected = acmi.position;
	    	    menu.add("Supprimer");
    	}
	}
	
	
	//**************************************************************************//
	private void varietes_listview(ListView liste_variete) 
	{
		tab_variete =  Object_variete.createListOfAllVariete();
		ArrayAdapter<Object_variete> adapter = new ArrayAdapter<Object_variete>(this,R.layout.parcelle_texte,tab_variete); 
		liste_variete.setAdapter(adapter);
	}
	
	
	//**************************************************************************//	
	public boolean onContextItemSelected(MenuItem item) 
	{
		   if (item.getTitle() == "Supprimer") 
		   {
			   variete_delete(tab_variete.get(item_listview_selected).getVar_id(),tab_variete.get(item_listview_selected).getVar_nom());
		   }
		   else {
		      return false;
		   }
		   return true;
	}
	
	
	//**************************************************************************//	
	private void variete_delete(final int id,final String name)
	{
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("ATTENTION");
		dialogBuilder.setMessage("Voulez vous supprimer toutes les donneees de "+name+"?");

		// Bouton positif
		dialogBuilder.setPositiveButton("Oui", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
				try
				{
					db.execSQL("DELETE FROM VARIETE WHERE VAR_ID="+id);
					Toast.makeText(getApplicationContext(), "Suppression de "+name, Toast.LENGTH_SHORT).show();
					varietes_listview(liste_variete);
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
				//On ne fait rien du tout
			}
		});
		
		// Output
		AlertDialog popup_variete = dialogBuilder.create();
		popup_variete.show();
	}
	
	
	//*********************************************************************************
	public void Select_spinner_pousse(float coeff_pousse)
	{
		for (int i=0;i<spin_var_pousse.getCount();i++)
		{
			//On check chaque item de la liste
			float coeff_en_cours = Float.parseFloat(spin_var_pousse.getItemAtPosition(i).toString());
			if (coeff_en_cours == coeff_pousse)
			{
				//On selectionne le bon item dans la liste deroulante annee
				spin_var_pousse.setSelection(i);
				break;
			}
		}
	}
	
	
	//**************************************************************************//
	// Gestion du clic dans la listview
	private void variete_ClickCallBack(ListView liste_variete) {
		liste_variete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
				id_variete_selectionnee = tab_variete.get(position).getVar_id();

				//On affiche les infos dans les zone en bas de page
				edittext_nom_var.setText(tab_variete.get(position).getVar_nom());
				Select_spinner_pousse(tab_variete.get(position).getVar_coef());

				//On desactive certain boutons
				Button btn_modif_infos = (Button) findViewById(R.id.button_modif_variete);
				Button btn_ajouter_variete = (Button) findViewById(R.id.btn_variete_nouveau);
				btn_modif_infos.setEnabled(true);
				btn_ajouter_variete.setEnabled(false);
			}
		});
	}
}
