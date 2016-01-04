package com.LP50.sapinoscope;

import java.util.Date;
import java.util.Vector;

import com.LP50.sapinoscope.Object_sapin.Status_sapin;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Object_variete {

	private int var_id;
	private String var_nom;
	private float var_coef;
	private static String log_name_activity ="Object_Variété";
	
	public Object_variete()
	{
		var_id=0;
		var_nom="";
		var_coef=0;
	}
	
	public Object_variete(int var_id,String var_nom, float var_coef)
	{
		this.var_id = var_id;
		this.var_nom = var_nom;
		this.var_coef = var_coef;
	}
	public Object_variete(Cursor c) 
	{
		var_id = c.getInt(c.getColumnIndex("VAR_ID"));
		var_nom = c.getString(c.getColumnIndex("VAR_NOM"));
		var_coef = c.getFloat(c.getColumnIndex("VAR_POUSSE"));
	}

	public int getVar_id() {
		return var_id;
	}

	public void setVar_id(int var_id) {
		this.var_id = var_id;
	}

	public String getVar_nom() {
		return var_nom;
	}

	public void setVar_nom(String var_nom) {
		this.var_nom = var_nom;
	}

	public float getVar_coef() {
		return var_coef;
	}

	public void setVar_coef(float var_coef) {
		this.var_coef = var_coef;
	}
	
	public String toString()
	{
		return var_nom;
	}
	
	public boolean equals(Object_variete other)
	{
		if(var_nom.toLowerCase().equals(other.var_nom.toLowerCase()))
			return true;
		return false;
	}
	
	public static String getVarieteName(int varieteID)
	{
		String selectQuery = "SELECT VAR_NOM FROM VARIETE WHERE VAR_ID="+varieteID;
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if(c.moveToFirst())
		{
			return c.getString(0);
		}
		Log.e(log_name_activity, "Impossible de trouver le nom de la variete :+varieteID");
		return new String();
	}
	
	public static Vector<Object_variete> createListOfAllVariete()
	{
		Vector<Object_variete> liste = new Vector<Object_variete>();

		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery = "SELECT * FROM VARIETE";
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			if(c.moveToFirst() && nb_row>=0)
			{
	            do
	            {
	            	Object_variete variete= new Object_variete();
	            	variete.setVar_id(c.getInt(c.getColumnIndex("VAR_ID")));
	            	variete.setVar_nom(c.getString(c.getColumnIndex("VAR_NOM")));
	            	variete.setVar_coef(c.getFloat(c.getColumnIndex("VAR_POUSSE")));
	            	liste.add(variete);
	                //Log.i(log_name_activity,"ID:"+parcelle.getId()+" nom:"+parcelle.getName()+" desc:"+parcelle.getDescription()+" coef:"+parcelle.getCoef());
	            }while(c.moveToNext());
	        }
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			Log.e(log_name_activity, "Sortie en erreur");
		}
		return liste;
	}
	
	
}
