package com.ostermann.sapinoscope;

import java.util.Vector;

import com.ostermann.sapinoscope.Object_sapin.Status_sapin;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class Object_sapinDetails {

	
	int id;
	int ligne;
	int colonne;
	Status_sapin status;
	String variete;
	Float taille;
	int numero;
	
	
	public Object_sapinDetails()
	{
		id=0;
		ligne=0;
		colonne=0;
		status=Status_sapin.INDEFINI;
		variete="null";
		taille= null;
		numero=0;
	}


	public int getNumero() {
		return numero;
	}


	public void setNumero(int numero) {
		this.numero = numero;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getLigne() {
		return ligne;
	}


	public void setLigne(int ligne) {
		this.ligne = ligne;
	}


	public int getColonne() {
		return colonne;
	}


	public void setColonne(int colonne) {
		this.colonne = colonne;
	}


	public Status_sapin getStatus() {
		return status;
	}


	public void setStatus(Status_sapin status) {
		this.status = status;
	}


	public String getVariete() {
		return variete;
	}


	public void setVariete(String variete) {
		this.variete = variete;
	}


	public float getTaille() {
		return taille;
	}


	public void setTaille(float taille) {
		this.taille = taille;
	}


	@Override
	public String toString() {
		
		float hight=taille;
		String unite="m";

		String s = "";
		if( status == Status_sapin.INDEFINI || status == Status_sapin.VIDE)
		{
			s = numero+": Erreur "+status+" l:"+ligne+" c;"+colonne;
		}
		else if ( status == Status_sapin.NOUVEAU)
		{
			s = numero+".  Jeune plant - "+variete;
		}
		else if ( status == Status_sapin.OK)
		{
			if( taille < 1)
			{
				hight = taille * 100  ;
				int a = (int) hight ;
				unite="cm";
				s = numero+".  "+a+unite+" - "+variete;
			}
			else
			{
				s = numero+".  "+hight+" "+unite+" "+variete;
			}
			
		}
		else if (status ==Status_sapin.TOC)
		{
			s = numero+".  Souche";
		}
		
		return s;
	}
	 
	 
	public static Vector<Object_sapinDetails> createListOfSapin_Y(int secteur_id, int y, int limit)
	{
		Vector<Object_sapinDetails> L_sapinD = new Vector<Object_sapinDetails>();
		SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
		try
		{
			String selectQuery =  " SELECT						"
								+ "   S.SAP_ID 			AS ID 	"
								+ "	 ,S.SAP_LIG 		AS X 	"
								+ "  ,S.SAP_COL  		AS Y 	"
								+ "	 ,I.INF_SAP_STATUS 	AS ST	"
								+ "	 ,V.VAR_NOM 		AS VAR 	"
								+ "	 ,I.INF_SAP_TAIL AS TAILLE	"				
								+ "	FROM						"
								+ "		SAPIN S 				"
								+ "		INNER JOIN INFO_SAPIN I "
								+ "			USING(SAP_ID)		"
								+ "		INNER JOIN VARIETE V 	"
								+ " 		USING(VAR_ID)		"
								+ " WHERE 						"
								+ "		S.SEC_ID="+secteur_id
								+ "	AND							"
								+ "		S.SAP_COL="+y
								+ " GROUP BY 					"
								+ "		S.SAP_LIG				"
								+ "	ORDER BY					"
								+ "		S.SAP_LIG ASC			"
								+"		,I.INF_SAP_DATE DESC";
			if (limit!=0)
				selectQuery = selectQuery+ "LIMIT "+limit;
			
			Cursor c = db.rawQuery(selectQuery, null);
			int nb_row = c.getCount();
			int i=1;
			if(c.moveToFirst() && nb_row>=0)
			{
				do
				{
					Object_sapinDetails sapin= new Object_sapinDetails();
					sapin.setId(c.getInt(c.getColumnIndex("ID")));
					sapin.setLigne(c.getInt(c.getColumnIndex("X")));
					sapin.setColonne(c.getInt(c.getColumnIndex("Y")));
					sapin.setStatus(Status_sapin.fromInt(c.getInt(c.getColumnIndex("ST"))));
					sapin.setVariete(c.getString(c.getColumnIndex("VAR")));
					sapin.setTaille(c.getFloat(c.getColumnIndex("TAILLE")));
					sapin.setNumero(i);
					L_sapinD.add(sapin);
					i++;
					Log.i("Obj_sapinDetails/createListOfAllParcelle","ID:"+sapin.getId()+" X:"+sapin.getLigne()+" Y:"
							+sapin.getColonne()+" Status:"+sapin.getStatus()+" Variété:"+sapin.getVariete()+" Taille:"+sapin.getTaille());
				}while(c.moveToNext());
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		return L_sapinD;
	}

	
	
}
