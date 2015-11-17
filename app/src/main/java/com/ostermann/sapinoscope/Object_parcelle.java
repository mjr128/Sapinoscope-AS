package com.ostermann.sapinoscope;

import java.util.Vector;

import com.ostermann.sapinoscope.Object_sapin.Status_sapin;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Object_parcelle {

	private int id;
	private String name;
	private String description;
	private float coef;

	private static String log_name_activity = "Object_Parcelle";

	public Object_parcelle() {
		id=0;
		name="vide";
		description="vide";
		coef=1;
	}

	public Object_parcelle(int id, String name,String description,int coef) {
		this.id=id;
		this.name=name;
		this.description=description;
		this.coef=coef;
	}

	public Object_parcelle(int id)
	{
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM PARCELLE WHERE PARC_ID="+id;
			Log.i(log_name_activity+"/Object_parcelle(id,Source)",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				this.setId(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
				this.setName(c.getString(c.getColumnIndex("PARC_N")));
				this.setDescription(c.getString(c.getColumnIndex("PARC_DESC")));
				this.setCoef(Float.parseFloat(c.getString(c.getColumnIndex("PARC_COEF"))));
				Log.i(log_name_activity+"/Object_parcelle(id,Source)","ID:"+this.getId()+" nom:"+this.getName()+" desc:"+this.getDescription()+" coef:"+this.getCoef());
			}
			Log.i(log_name_activity+"/Object_parcelle(id,Source)", "Create : "+selectQuery);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/Object_parcelle(id,Source)", "Sortie en erreur");
		}

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getCoef() {
		return coef;
	}

	public void setCoef(float coef) {
		this.coef = coef;
	}

	public String toString() {
		return this.name + " , " + this.description + "\n\tSecteurs\t: "+getCountSecteur()+"\n\tSapins\t\t\t: "+getCountSapin(this.id);
	}
	
	
	public int getCountSecteur()
	{
		int value=0;
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT COUNT(*) AS A FROM SECTEUR WHERE PARC_ID="+this.id;
			Log.i("requette",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				value=c.getInt(0);
				Log.i("DB requette","ID:"+this.id);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return value;
			
	}
	
	public int getCountSapin(int parc_id)
	{
		int value=0;
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			//String selectQuery = 	"SELECT COUNT(*) FROM SAPIN SA INNER JOIN SECTEUR SE ON SE.SEC_ID=SA.SEC_ID WHERE SE.PARC_ID="+parc_id;
			
			String selectQuery =	 " SELECT COUNT(*) "
					+" FROM ( "
					+" SELECT * "
					+" FROM "
					+"	SAPIN SAP "
					+"	INNER JOIN INFO_SAPIN INF "
					+" 	USING(SAP_ID) "
					+"  INNER JOIN SECTEUR SEC "
					+"  USING(SEC_ID) "
					+"	INNER JOIN PARCELLE PAR "
					+"	USING (PARC_ID) "
					+" WHERE "
					+"	PAR.PARC_ID="+parc_id
					+" GROUP BY "
					+"	SAP.SAP_LIG, SAP.SAP_COL, SEC.SEC_ID "
					+" ORDER BY "
					+"	SAP.SAP_LIG ASC , INF.INF_SAP_DATE DESC)"
					+" WHERE "
					+"	INF_SAP_STATUS !="+Status_sapin.TOC;
			
			
			
			
			
			
			Log.i("requette",selectQuery);
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				value=c.getInt(0);
				Log.i("DB requette","ID:"+this.id);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return value;
			
	}
	


	public static Vector<Object_parcelle> createListOfAllParcelle()
	{
		Vector<Object_parcelle> liste = new Vector<Object_parcelle>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM PARCELLE";
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>=0)
			{
				do
				{
					Object_parcelle parcelle= new Object_parcelle();
					parcelle.setId(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
					parcelle.setName(c.getString(c.getColumnIndex("PARC_N")));
					parcelle.setDescription(c.getString(c.getColumnIndex("PARC_DESC")));
					parcelle.setCoef(Float.parseFloat(c.getString(c.getColumnIndex("PARC_COEF"))));
					liste.add(parcelle);
					Log.i(log_name_activity+"/createListOfAllParcelle","ID:"+parcelle.getId()+" nom:"+parcelle.getName()+" desc:"+parcelle.getDescription()+" coef:"+parcelle.getCoef());
				}while(c.moveToNext());
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/createListOfAllParcelle", "Sortie en erreur");
		}
		return liste;
	}

	

}
