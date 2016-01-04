package com.LP50.sapinoscope;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Parcelle_modification extends Activity {

	private Context contexte = this;
	private String log_name_activity = "ParcelleModification";
	private int parcelle_id=0;
	private Object_parcelle parcelle;
	
	private TextView txt_parcelle;
	private Spinner spin_parc_coef;
	private EditText ed_parc_desc;
	private EditText ed_parc_name;

	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.LP50.sapinoscope.R.layout.activity_parcelle_modification);
		Log.i(log_name_activity, "NOUVELLE ACTIVITE START");
		
		txt_parcelle = (TextView) findViewById(com.LP50.sapinoscope.R.id.txt_parcelle_modif_title);
		spin_parc_coef = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_parcelle_modif);
		ed_parc_desc = (EditText) findViewById(com.LP50.sapinoscope.R.id.editText_parcelle_modif_desc);
		ed_parc_name = (EditText) findViewById(com.LP50.sapinoscope.R.id.editText_parcelle_modif_nom);

		
		// Reception INTENT
		Intent intent_parc_modif = getIntent();
		parcelle_id = intent_parc_modif.getIntExtra("id", 1);
		final int parcelle_add = intent_parc_modif.getIntExtra("add", 1); 
		
		// on recupere le texte de l'activite si c'est une nouvelle parcelle
		if ( parcelle_add == 1)
		{
			Log.i(log_name_activity,"->INSERT Parcelle");
			String parcelle_name = intent_parc_modif.getStringExtra("name");
			parcelle =  new Object_parcelle(0,parcelle_name , "", 1);
		}
		// on recupere le nom si c'est un UPDATE
		else 
		{
			Log.i(log_name_activity,"->UPDATE Parcelle");
			parcelle = new Object_parcelle(parcelle_id);
		}
		
		// INITIALISATION
		initialisation();
		
		// INSERT UPDATE PARCELLE -- ON CLIC
		Button bt_add_parcelle = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_parcelle_modif_add);
		bt_add_parcelle.setOnClickListener(new OnClickListener() 
		{
			
			public void onClick(View v) 
			{
				if ( ed_parc_name.getText().length() > 0)
				{
					SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
					try
					{
						String req ="";
						float spin = Float.parseFloat(spin_parc_coef.getSelectedItem().toString());
						
						if ( parcelle_add == 0 ) // Update
						{
							req = "UPDATE PARCELLE SET PARC_N='"+ed_parc_name.getText().toString()+"',PARC_DESC='"+ed_parc_desc.getText().toString()+"',PARC_COEF="+spin+"  WHERE PARC_ID="+parcelle_id;
							Log.i("DB-UPT-Parcelle", req);
						}
						else // INSERT
						{
							req = "INSERT INTO PARCELLE ('PARC_N','PARC_DESC','PARC_COEF') VALUES ('"+ed_parc_name.getText().toString()+"','"+ed_parc_desc.getText().toString()+"',"+spin+")";
							Log.i("DB-ADD-Parcelle", req);
						}
						db.execSQL(req);
						Log.i("DB-New-Parcelle", "test insert sans errors");
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
					ed_parc_desc.setText("");
					ed_parc_name.setText("");
					Intent intent_parcelle_liste = new Intent(contexte, Parcelle_listView.class);
					startActivity(intent_parcelle_liste);
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Saisissez un nom", Toast.LENGTH_SHORT).show();
				}
		}
		});
	}

	//*************************************************************************/
	public void initialisation()
	{
		ed_parc_name.setText(parcelle.getName());
		ed_parc_desc.setText(parcelle.getDescription());
		txt_parcelle.setText("Parcelle : "+parcelle.getName());
		
		List<String> list_coef = new ArrayList<String>();
		if ( parcelle.getCoef() != 0 )
		{
			list_coef.add(""+parcelle.getCoef());
		}
		double i=2;
		while ( i > 0.1 )
		{
			if (i != parcelle.getCoef())
			{
				list_coef.add(""+i);
			}
			i = (double)Math.round((i - 0.1)*100)/100 ;			
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_coef);
		//Le layout par défaut est android.R.layout.simple_spinner_dropdown_item
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_parc_coef.setAdapter(adapter);
	}
}
