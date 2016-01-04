package com.LP50.sapinoscope;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Secteur_modification extends Activity 
{

	private Context contexte = this;

	private String log_name_activity = "SECTEUR_MODIFICATION";
	private Object_secteur secteur;

	private TextView txt_sect;
	private EditText ed_sect_desc = null;
	private Spinner spin_sect_crois=null;
	private Spinner spin_sect_gel=null;
	private Spinner spin_sect_annee=null;
	private CheckBox zigzag;
	private int zigzag_value;
	
	private int sect_add;

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(com.LP50.sapinoscope.R.layout.activity_secteur_modification);
		Log.i(log_name_activity, "----NOUVELLE ACTIVITE START----");

		txt_sect = (TextView) findViewById(com.LP50.sapinoscope.R.id.txt_secteur_modif_title);
		ed_sect_desc = (EditText) findViewById(com.LP50.sapinoscope.R.id.editText_secteur_modif_nom);
		spin_sect_crois = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spinner_coef_croissance);
		spin_sect_gel = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spinner_coef_gel);
		spin_sect_annee = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spinner_annee);
		zigzag = (CheckBox) findViewById(com.LP50.sapinoscope.R.id.chek_secteur_modification_zigzag);
		
		// Reception INTENT
		Intent intent_sect_modif = getIntent();
		int sec_id = 	intent_sect_modif.getIntExtra("sec_id", -1);
		final int parc_id =  	intent_sect_modif.getIntExtra("parc_id", -1);
		String name =  	intent_sect_modif.getStringExtra("name");
		sect_add = 		intent_sect_modif.getIntExtra("add", -1); 		// bool = 1 : ADD  // 0 : update


		// on recupere le texte de l'activite si c'est une nouvelle parcelle
		if ( sect_add == -1)
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else if ( sect_add == 1) // INSERT (si on cree un nouveau secteur)
		{
			if( parc_id == -1)
				Log.e(log_name_activity, "Impossible d'initialiser via parc_id");

			Log.i(log_name_activity,"->Ajout d'un nouveau secteur");
			secteur = new Object_secteur(0, parc_id, name, 0, 0);
		}
		else // sinon SELECT (quand on modifie un secteur existant)
		{
			Log.i(log_name_activity,"->Modification d'un secteur");
			secteur = new Object_secteur(sec_id);
		}


		Log.i(log_name_activity,"INTENT GET : PARC_ID:"+secteur.getId_parc()+"  SECT_N:"+secteur.getName()+"  SECT_ID:"+secteur.getId());


		// INITIALISAION
		txt_sect.setText("Secteur : "+secteur.getName());
		ed_sect_desc.setText(secteur.getName());		
		init_spinner_coef_croissance();
		init_spinner_annee();
		init_spinner_coef_gel();
		Select_spinner_coef_croissance();
		Select_spinner_annee();
		Select_spinner_coef_gel();
		if ( secteur.getZigzag())
			zigzag.setChecked(true);
		else
			zigzag.setChecked(false);

		//---------------------------------------------------------
		// INSERT UPDATE PARCELLE -- ON CLIC     (Bouton "VALIDER")
		//---------------------------------------------------------
		Button bt_add_secteur = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_secteur_modif_add);
		bt_add_secteur.setOnClickListener(new OnClickListener() 
		{
			
			public void onClick(View v) 
			{

				if (ed_sect_desc.getText().length() >0 )
				{
					if ( zigzag.isChecked() )
						zigzag_value=1;
					else
						zigzag_value=0;
					
					Log.i(log_name_activity, "Clic - VALIDER");
					String sect_name = ed_sect_desc.getText().toString();
					String spin_annee = spin_sect_annee.getSelectedItem().toString();
					float spin_crois = Float.parseFloat(spin_sect_crois.getSelectedItem().toString());
					float spin_gel =   Float.parseFloat(spin_sect_gel.getSelectedItem().toString());
	
					if ( sect_add == 0 ) // Update
					{
						// UPDATE SECTEUR
						Log.i(log_name_activity, "UPDATE Secteur");
						SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
						try
						{
							String req_secteur = "UPDATE SECTEUR SET SEC_N='"+sect_name+"' , SEC_CROIS='"+spin_crois+"' , SEC_ZIGZAG="+zigzag_value+" WHERE SEC_ID="+secteur.getId() ;
							db.execSQL(req_secteur);
							Log.i(log_name_activity, "req_secteur");
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						// INSERT SECTEUR
						Log.i(log_name_activity, "INSERT SECTEUR");
						SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
						try
						{
							String req_secteur = "INSERT into SECTEUR (PARC_ID,SEC_N,SEC_ANGLE,SEC_CROIS,SEC_ZIGZAG)  VALUES ( "+secteur.getId_parc()+",'"+sect_name+"', 0 , "+spin_crois+","+zigzag_value+" ) ;" ;
							db.execSQL(req_secteur);
							Log.i(log_name_activity, "req_secteur");
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
	
						// GET LAST ID ADDED
						secteur.setId(select_max_id("SECTEUR", "SEC_ID"));
						if (secteur.getId() != 0)
						{
							// INSERT INFO_SECTEUR
							db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
							try
							{
								String req_info_seq = "INSERT into INFO_SECTEUR (SEC_ID,ANN_ID,INF_SEC_COEF_GEL) VALUES (  "+secteur.getId()+", "+spin_annee+", "+spin_gel+");";
								db.execSQL(req_info_seq);
								Log.i(log_name_activity, req_info_seq);
							}
							catch(SQLException e)
							{
								e.printStackTrace();
							}
						}
					}
					
					Intent intent_addsapin = new Intent(contexte, Message_alerte_activity.class);
					intent_addsapin.putExtra("id", secteur.getId());
					Log.i(log_name_activity+"/onClick","INTENT SET : PARC_ID:"+secteur.getId());
					startActivity(intent_addsapin);
					finish();
					
					/*Intent intent_secteur_liste = new Intent(contexte, Secteur_activity.class);
					intent_secteur_liste.putExtra("id", secteur.getId_parc());
					Log.i(log_name_activity,"INTENT SET : PARC_ID:"+secteur.getId_parc()+" SECT_N:"+sect_name+"SECT_ID:"+secteur.getId());
					startActivity(intent_secteur_liste);
					finish();*/
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Saisissez un nom", Toast.LENGTH_SHORT).show();
				}
			}
		});


		//********************************************* SPINNER ANNEE *********************************************
		spin_sect_annee.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				// TODO Auto-generated method stub
				Log.i("onItemSelected","Entree dans la fonction onItemSelected spinner_annee");
				
				if ( sect_add == -1)	
				{
					Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
				}
				else if (sect_add == 1) //bool = 1 : nouveau secteur
				{				
					//On ne fait rien du tout
				}
				else // bool = 0 : modification d'un secteur
				{
					//on regarde dans INFO_SECTEUR si une ligne existe deja pour l'annee selectionnee
					int annee_en_cours = Integer.parseInt(spin_sect_annee.getSelectedItem().toString());
					boolean resultat = Retourne_si_annee_deja_existante(annee_en_cours);
					
					if(resultat == true)
					{
						Log.i(log_name_activity,"L'annee existe deja dans la BDD");
						//On va recuperer le coeff de gel pour l'annee voulue et on l'affiche.
						Select_spinner_coef_gel();
					}
					else 
					{
						Log.i(log_name_activity,"L'annee n'existe pas encore dans la BDD");
						//On cree une nouvelle ligne dans la table INFO_SECTEUR de la BDD avec un coeff gel par defaut
						SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
						try
						{
							String req_secteur = "INSERT into INFO_SECTEUR (SEC_ID, ANN_ID, INF_SEC_COEF_GEL) ";
							req_secteur += "VALUES (" + secteur.getId() + ",'" + annee_en_cours + "','1.0');";
							db.execSQL(req_secteur);
							Log.i(log_name_activity, req_secteur);
						}
						catch(SQLException e)
						{
							e.printStackTrace();
						}
						
						//On va recuperer le coeff de gel pour l'annee voulue et on l'affiche.
						Select_spinner_coef_gel();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
				Log.i("onNothingSelected","Entree dans la fonction onNothingSelected spinner_annee");
			}
		});
		
		
		//********************************************* SPINNER COEFF GEL *********************************************
		spin_sect_gel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				// TODO Auto-generated method stub
				Log.i("onItemSelected","Entree dans la fonction onItemSelected spinner_gel");

				if ( sect_add == -1)	
				{
					Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
				}
				else if (sect_add == 1) //bool = 1 : nouveau secteur
				{				
					//On ne fait rien du tout
				}
				else // bool = 0 : modification d'un secteur
				{
					//On recupere l'annee en cours pour ensuite UPDATER son coeff gel
					int annee_selectionnee = Integer.parseInt(spin_sect_annee.getSelectedItem().toString());
					float nouveau_coeff_gel = Float.parseFloat(spin_sect_gel.getSelectedItem().toString());
					
					//On fait l'UPDATE
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						//"UPDATE SECTEUR SET SEC_N='"+sect_name+"' , SEC_CROIS='"+spin_crois+"' WHERE SEC_ID="+secteur.getId() ;
						String req_secteur = "UPDATE INFO_SECTEUR SET INF_SEC_COEF_GEL='" + nouveau_coeff_gel + "' ";
						req_secteur += "WHERE ((ANN_ID="+annee_selectionnee +") AND (SEC_ID="+secteur.getId()+"))";
						db.execSQL(req_secteur);
						Log.i(log_name_activity, req_secteur);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}			
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
				Log.i("onNothingSelected","Entree dans la fonction onNothingSelected spinner_gel");
			}
		});

		
		//********************************************* SPINNER COEFF CROISSANCE *********************************************
		spin_sect_crois.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			{
				// TODO Auto-generated method stub
				Log.i("onItemSelected","Entree dans la fonction onItemSelected spinner_croissance");

				if ( sect_add == -1)	
				{
					Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
				}
				else if (sect_add == 1) //bool = 1 : nouveau secteur
				{				
					//On ne fait rien du tout
				}
				else // bool = 0 : modification d'un secteur
				{				
					//On recupere le nouveau coeff de croissance du secteur
					float nouveau_coeff_croissance = Float.parseFloat(spin_sect_crois.getSelectedItem().toString());
					
					//On fait l'UPDATE
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						//"UPDATE SECTEUR SET SEC_N='"+sect_name+"' , SEC_CROIS='"+spin_crois+"' WHERE SEC_ID="+secteur.getId() ;
						String req_secteur = "UPDATE SECTEUR SET SEC_CROIS='" + nouveau_coeff_croissance + "' WHERE SEC_ID="+secteur.getId();
						db.execSQL(req_secteur);
						Log.i(log_name_activity, req_secteur);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}			
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub
				Log.i("onNothingSelected","Entree dans la fonction onNothingSelected spinner_gel");
			}
		});
	}



	//**************************************************************************//	
	public int select_max_id(String table, String colonne)
	{
		int value=0;

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT MAX("+colonne+") FROM "+table;
			Log.i("requete",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				value=c.getInt(0);
				Log.i("DB requete","ID:"+value+" nb_row="+nb_row);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}

	//*********************************************************************************
	//Retourne vrai si la ligne de l'annee selectionnee existe dans la table
	public boolean Retourne_si_annee_deja_existante(int annee_voulue)
			{
				float value=0;

				SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
				try
				{
					String selectQuery = "SELECT INF_SEC_COEF_GEL FROM INFO_SECTEUR WHERE ((ANN_ID="+annee_voulue +") AND (SEC_ID="+secteur.getId()+"))";
					Log.i("requete",selectQuery);
					Cursor c = db.rawQuery(selectQuery, null);
					int nb_row = c.getCount();
					if(nb_row>0)
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				return false;
			}
			
	//*********************************************************************************
	public void init_spinner_coef_croissance()
	{
		// Creation du spinner croissance
		List<String> list_coef_crois = new ArrayList<String>();	
		double i=1.6;
		while ( i > 0.1 )
		{
			list_coef_crois.add(""+i);
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef_crois);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_crois.setAdapter(adapter);
	}

	//*********************************************************************************
	public void init_spinner_annee()
	{
		//String year = (String) android.text.format.DateFormat.format("yyyy", date);
		List<String> list_annee = new ArrayList<String>();

		//On recupere l'annee actuelle
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		int DateActuelle = Integer.parseInt(sdf.format(c.getTime()));	

		if ( sect_add == -1)	
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else if (sect_add == 1) //bool = 1 : nouveau secteur
		{				
			//on n'affiche que l'annee actuelle
			list_annee.add(""+DateActuelle);			
		}
		else // bool = 0 : modification d'un secteur
		{
			//on affiche l'annee actuelle avec 1 avant et 2 apres
			for(int i = DateActuelle -1 ; i<DateActuelle+3 ; i++)
			{
				list_annee.add(""+i);
			}
		}

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_annee);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_annee.setAdapter(adapter2);
	}

	//*********************************************************************************
	public void init_spinner_coef_gel()
	{
		// Creation du spinner coef_gel
		List<String> list_coef_gel = new ArrayList<String>();

		//Remplit la liste de 1 a 0.1
		double i=1.0;	
		while ( i >= 0.1 )
		{
			list_coef_gel.add(""+i);
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef_gel);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_sect_gel.setAdapter(adapter3);
	}

	//*********************************************************************************
	public void Select_spinner_annee()
	{
		if ( sect_add == -1)	
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else //nouveau secteur ou modification existant : meme combat !
		{
			//On recupere l'annee actuelle
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			int DateActuelle = Integer.parseInt(sdf.format(c.getTime()));	

			for (int i=0;i<spin_sect_annee.getCount();i++)
			{
				//On check chaque item de la liste
				int index_annee = Integer.parseInt(spin_sect_annee.getItemAtPosition(i).toString());
				if (index_annee == DateActuelle)
				{
					//On selectionne le bon item dans la liste deroulante annee
					spin_sect_annee.setSelection(i);
					break;
				}
			}
		}
	}

	//*********************************************************************************
	public void Select_spinner_coef_gel()
	{
		if ( sect_add == -1)	
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else if (sect_add == 1) //bool = 1 : nouveau secteur
		{				
			//On cherche le 1.0 par defaut
			for (int i=0;i<spin_sect_gel.getCount();i++)
			{
				//On check chaque item de la liste
				float index_gel = Float.parseFloat(spin_sect_gel.getItemAtPosition(i).toString());
				if (index_gel == 1.0)
				{
					//On selectionne le bon item dans la liste deroulante annee
					spin_sect_gel.setSelection(i);
					break;
				}
			}
		}
		else // bool = 0 : modification d'un secteur
		{
			//On recupere le coef de gel du secteur en fonction de l'annee
			int annee_en_cours = Integer.parseInt(spin_sect_annee.getSelectedItem().toString());
			float coeff_BDD = Get_Gel_Fonction_Annee(annee_en_cours);

			Log.i(log_name_activity, "Annee_en_cours  (Select_spinner_coef_gel) : "+annee_en_cours);
			Log.i(log_name_activity, "Coeff_BDD  (Select_spinner_coef_gel) : "+coeff_BDD);

			for (int i=0;i<spin_sect_gel.getCount();i++)
			{
				//On check chaque item de la liste
				float index_gel = Float.parseFloat(spin_sect_gel.getItemAtPosition(i).toString());
				if (index_gel == coeff_BDD)
				{
					//On selectionne le bon item dans la liste deroulante annee
					spin_sect_gel.setSelection(i);
					break;
				}
			}
		}
	}
	
	//*********************************************************************************
	public void Select_spinner_coef_croissance()
	{

		if ( sect_add == -1)	
		{
			Log.e(log_name_activity, "Impossible d'initialiser, sect_add est introuvable");
		}
		else if (sect_add == 1 )
		{
			for (int i=0;i<spin_sect_crois.getCount();i++)
			{				
				if ( (Float.parseFloat(spin_sect_crois.getItemAtPosition(i).toString())) == 1)
				{
					spin_sect_crois.setSelection(i);
					break;
				}
			}
		}
		else // bool = 0 : modification d'un secteur
		{
			//On recupere le coef de gel du secteur en fonction de l'annee
			float coeff_croiss = secteur.getCoef_croissance();
			
			//si c'est un nouveau secteur, on choisi 1.0 par d�faut
			if(sect_add == -1) 
			{ 
				coeff_croiss=(float) 1.0; 
			} 
			
			Log.i("DEBUG COEF","COEF:"+secteur.getCoef_croissance());

			for (int i=0;i<spin_sect_crois.getCount();i++)
			{				
				//On check chaque item de la liste
				//Log.i(log_name_activity, "Index for i="+i+"   : "+index_croiss+"    coeff_croiss ="+coeff_croiss);

				//String a = spin_sect_crois.getItemAtPosition(i).toString();
				//a = a.substring(0, (a.length()-1));
				//if ( (Float.parseFloat(a)/100) == coeff_croiss)

				if ( (Float.parseFloat(spin_sect_crois.getItemAtPosition(i).toString())) == coeff_croiss)
				{
					spin_sect_crois.setSelection(i);
					break;
				}
			}
		}
	}

	//*********************************************************************************
	public float Get_Gel_Fonction_Annee(int annee_voulue)
	{
		float value=0;

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT INF_SEC_COEF_GEL FROM INFO_SECTEUR WHERE ((ANN_ID="+annee_voulue +") AND (SEC_ID="+secteur.getId()+"))";
			Log.i("requete",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				value=c.getFloat(0);
				Log.i("DB requete","ID:"+value+" nb_row="+nb_row);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}

}
