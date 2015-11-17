package com.ostermann.sapinoscope;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ostermann.sapinoscope.Object_sapin.Status_sapin;

public class Object_infoSapin {
	private int inf_sap_id;
	public int sap_id;
	public Date date;
	public float taille;
	public Status_sapin status;
	
	public int getInfoSapinID()
	{
		return inf_sap_id;
	}
	
	public Object_infoSapin(Date d)
	{
		inf_sap_id=-1;
		date=d;
		sap_id=-1;
		taille=-1;
		status = status.INDEFINI;
	}
	
	public Object_infoSapin(int id_sapin, Date d,int e)
	{
		String requetteSelect = "SELECT * FROM INFO_SAPIN WHERE SAP_ID="+id_sapin+" AND INF_SAP_DATE="+d.getTime();
		
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		Cursor c = db.rawQuery(requetteSelect, null);
		
		if(c.getCount()>1)
			Log.w("Object_infoSapin", "multiple entrees trouvees dans la table info sapin pour les valeurs : sapinID:"+id_sapin+" date:"+d.getTime());
		
		if(c.getCount()!=0)
		{
			c.moveToFirst();
			inf_sap_id = c.getInt(c.getColumnIndex("INF_SAP_ID"));
			date = d;
			sap_id = id_sapin;
			taille = c.getFloat(c.getColumnIndex("INF_SAP_TAIL"));
			status = Status_sapin.fromInt(c.getInt(c.getColumnIndex("INF_SAP_STATUS")));
		}
		else// Nouvel object, on lui donne les valeurs par default
		{
			inf_sap_id=-1;
			date=d;
			sap_id=id_sapin;
			taille=-1;
			status = status.INDEFINI;
		}
	}
	
	public Object_infoSapin(Cursor c) 
	{
		inf_sap_id = c.getInt(c.getColumnIndex("INF_SAP_ID"));
		date = new Date(c.getLong(c.getColumnIndex("INF_SAP_DATE")));
		sap_id = c.getInt(c.getColumnIndex("SAP_ID"));;
		taille = c.getFloat(c.getColumnIndex("INF_SAP_TAIL"));
		status = Status_sapin.fromInt(c.getInt(c.getColumnIndex("INF_SAP_STATUS")));
	}

	
	public String toString() {
		
		float height=taille;
		String s = "";
		String unite="m";
		if ( status == Status_sapin.NOUVEAU)
		{
			s = "Jeune plant";
		}
		else if ( status == Status_sapin.OK)
		{
			if( taille < 1)
			{
				height = taille * 100  ;
				int a = (int) height ;
				unite="cm";
				s = a+unite;
			}
			else
			{
				s = taille+unite;
			}
			
		}
		else if (status ==Status_sapin.TOC)
		{
			s = "Souche";
		}
		
		return s;
	}
	
	public String getFormatedDate(String format)
	{
		return new SimpleDateFormat(format).format(date);
	}
	
	public void saveInDb()
	{
		if(date == null || sap_id == -1 || taille == -1 || status == Status_sapin.INDEFINI)
			Log.w("Object_infoSapin", "Enregistrement d'un infoSapin contenant des erreurs dans la base : date=="+date+" sap_id=="+sap_id+" taille=="+taille+" status=="+status);
		
		String requette;
		
		if(inf_sap_id==-1)//infoSapin inconnu dans la base, ajout
		{
    		requette = "INSERT INTO INFO_SAPIN ('INF_SAP_DATE',			'SAP_ID',			'INF_SAP_TAIL',		'INF_SAP_STATUS')"
    						+"VALUES(	   		'"+date.getTime() +"',	'"+sap_id+"',		'"+taille+"',		'"+status+"');";
	    
		}else//l' infoSapin vient de la base, il faut donc mettre a jour ses infos
		{
			requette = 			  "UPDATE INFO_SAPIN SET "
								+ "INF_SAP_DATE=	'"+ date.getTime()+"',"
								+ "SAP_ID=			'"+ sap_id  +"',"
								+ "INF_SAP_TAIL=	'"+ taille  +"',"
								+ "INF_SAP_STATUS=	'"+ status 	+"'"
								+ "WHERE INF_SAP_ID="+inf_sap_id;
		}
		
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	db.execSQL(requette);
    	
    	if(inf_sap_id==-1)// L'infoSapin vient d'etre ajoute dans la base. On va maintenant recupere son id
    	{
	    		requette= "SELECT SAP_ID FROM INFO_SAPIN WHERE "
	    				+ "INF_SAP_DATE=	'"+ date.getTime()+"' AND "
						+ "SAP_ID=			'"+ sap_id  +"' AND "
						+ "INF_SAP_TAIL=	'"+ taille  +"' AND "
						+ "INF_SAP_STATUS=	'"+ status 	+"'";

			Cursor c = db.rawQuery(requette, null);
			c.moveToFirst();
			sap_id = c.getInt(0);
    	}
	}
}
