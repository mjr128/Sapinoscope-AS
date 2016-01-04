package com.LP50.sapinoscope;

import java.util.Vector;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Object_secteur {

	private int id;
	private int id_parc;
	private String name;
	private float angle;
	private float coef_croissance;
	private static String log_name_activity = "Object_Secteur";
	private boolean zigzag;

	public Object_secteur() {
		id=0;
		id_parc=0;
		name="vide";
		angle=0;
		coef_croissance=1;
		zigzag=true;
	}

	public Object_secteur(int id, int id_parc,String name,float angle, float coef_croissance) {
		this.id=id;
		this.id_parc=id_parc;
		this.name=name;
		this.angle=angle;
		this.coef_croissance=coef_croissance;
		zigzag=true;
	}
	
	public Object_secteur(int id)
	{
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{

			String selectQuery = "SELECT * FROM SECTEUR WHERE SEC_ID="+id;
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
				this.setId(c.getInt(c.getColumnIndex("SEC_ID")));
				this.setId_parc(c.getInt(c.getColumnIndex("PARC_ID")));
				this.setName(c.getString(c.getColumnIndex("SEC_N")));
				this.setAngle(c.getFloat(c.getColumnIndex("SEC_ANGLE")));
				this.setCoef_croissance(c.getFloat(c.getColumnIndex("SEC_CROIS")));
				try{
            		setZigzag( c.getInt(c.getColumnIndex("SEC_ZIGZAG"))==0? false :true);
            	}catch(Exception e)
            	{
            		setZigzag(true);
            	}
			}
			else
			{
				Log.e(log_name_activity+"/Object_secteur", "Impossible nb_row:"+nb_row+" ID:"+id);
			}
			Log.i(log_name_activity+"/Object_secteur", "Create : "+selectQuery);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/Object_secteur", "Sortie en erreur");
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_parc() {
		return id_parc;
	}

	public void setId_parc(int id_parc) {
		this.id_parc = id_parc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getCoef_croissance() {
		return coef_croissance;
	}

	public void setCoef_croissance(float coef_croissance) {
		this.coef_croissance = coef_croissance;
	}
	
	public void setZigzag(boolean b)
	{
		zigzag=b;
	}
	
	public boolean getZigzag()
	{
		return zigzag;
	}

	public String toString() {
		return this.name + "   > "+Object_sapin.countNbSapin("SEC_ID", this.id,"1",1)+" sapins";
	}
	
	
	public static Vector<Object_secteur> createListOfSecteur(int parc_id)
	{
		Vector<Object_secteur> liste = new Vector<Object_secteur>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM SECTEUR WHERE PARC_ID="+parc_id;
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>0)
			{
	            do{
	            	Object_secteur secteur= new Object_secteur();
	            	secteur.setId(Integer.parseInt(c.getString(c.getColumnIndex("SEC_ID"))));
	            	secteur.setId_parc(Integer.parseInt(c.getString(c.getColumnIndex("PARC_ID"))));
	            	secteur.setName(c.getString(c.getColumnIndex("SEC_N")));
	            	secteur.setAngle(Float.parseFloat(c.getString(c.getColumnIndex("SEC_ANGLE"))));
	            	secteur.setCoef_croissance(Float.parseFloat(c.getString(c.getColumnIndex("SEC_CROIS"))));
	            	try{
	            		secteur.setZigzag( c.getInt(c.getColumnIndex("SEC_ZIGZAG"))==0? false :true);
	            	}catch(Exception e)
	            	{
	            		secteur.setZigzag(true);
	            	}
	            	Log.i(log_name_activity+"/createListOfSecteur", "ID:"+secteur.getId()+" PARC_ID:"+secteur.getId_parc()+" NAME:"+secteur.getName());
	            	liste.add(secteur);
	            }while(c.moveToNext());
	        }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.i(log_name_activity+"/createListOfSecteur", "Sortie en erreur");
		}
		return liste;
	}

	
}
