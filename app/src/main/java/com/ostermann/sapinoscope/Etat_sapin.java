package com.ostermann.sapinoscope;

import java.text.SimpleDateFormat;
import java.util.Vector;

import com.ostermann.sapinoscope.Object_sapin.Status_sapin;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Etat_sapin{
	public Object_sapin sapin;
	public Object_infoSapin infoSapin;
	public Object_variete variete;
	
	Etat_sapin(Object_sapin s, Object_infoSapin is, Object_variete v)
	{
		sapin =s;
		infoSapin = is;
		variete =v; 
	}
	
	// retourne la liste de toutes les infos connues pour ce point donne de ce secteur
	public static Vector<Etat_sapin> createListOfInfoSapinFromXY(int secteurID, int x, int y)
	{
		Vector<Etat_sapin> liste = new Vector<Etat_sapin>();
		
		String requette = "SELECT * FROM SAPIN INNER JOIN INFO_SAPIN USING(SAP_ID)"
								+ " INNER JOIN VARIETE USING(VAR_ID)"
								+ " WHERE SAPIN.SEC_ID="+secteurID
								+ " AND SAPIN.SAP_LIG="+x
								+ " AND SAPIN.SAP_COL="+y
								+ " ORDER BY INF_SAP_DATE DESC";
	
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		
		Cursor c = db.rawQuery(requette, null);
		if(c.getCount() >0)
		{
			c.moveToFirst();
			
			do{
				Object_sapin tempsSapin = new Object_sapin(c);
				Object_infoSapin tempInfo = new Object_infoSapin(c);
				Object_variete tempVariete = new Object_variete(c);
            	liste.add(new Etat_sapin(tempsSapin, tempInfo, tempVariete));
            }while(c.moveToNext());
			
			return liste;
		}
		return null;
	}
	
	public String toString()
	{
		if ( infoSapin.status== Status_sapin.TOC)
		{
			return infoSapin.toString();
		}
		else
		{
			return infoSapin.toString() + " " + variete.toString();
		}
	}
}
