package com.ostermann.sapinoscope;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
 
 
 
public class DataBaseHelper extends SQLiteOpenHelper 
{
      
    private static String DB_NAME = "Sapin_DB";
    
    private static int DB_VERSION = 13;
     
    private SQLiteDatabase myDataBase;
      
    private final Context myContext;
    
    private static final String PRAGMA =
    		"pragma foreign_keys=on";
    
    private static final String CREATE_TABLE_EQUA =
      "CREATE TABLE IF NOT EXISTS EQUA"
	+ "(EQUA_ID INTEGER PRIMARY KEY AUTOINCREMENT ,"
	+ "EQUA_X DECIMAL(10,2) NOT NULL,"
	+ "EQUA_Y DECIMAL(10,2) NULL  ,"
	+ "EQUA_Z DECIMAL(10,2) NULL  ,"
	+ "EQUA_L DECIMAL(10,2) NULL  ,"
    + "EQUA_M DECIMAL(10,2) NULL  )";
    
    private static final String CREATE_TABLE_ANNEE =
	  "CREATE TABLE IF NOT EXISTS ANNEE"
	+ "(ANN_ID DATE NOT NULL,"  
    + "PRIMARY KEY (ANN_ID));";
    
    private static final String CREATE_TABLE_PARCELLE =
	  "CREATE TABLE IF NOT EXISTS PARCELLE"
	+ "( PARC_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
	+ "PARC_N CHAR(32) NOT NULL  ,"
	+ "PARC_DESC CHAR(32) NULL  ,"
	+ "PARC_COEF DECIMAL(10,2) NOT NULL );";
	
	private static final String CREATE_TABLE_SECTEUR =
	  "CREATE TABLE IF NOT EXISTS SECTEUR"
	+ "(SEC_ID INTEGER PRIMARY KEY AUTOINCREMENT ,"
	+ "PARC_ID BIGINT(4) NOT NULL  ,"
	+ "SEC_N CHAR(32) NOT NULL  ,"
	+ "SEC_ANGLE DECIMAL(10,2) NULL  ,"
	+ "SEC_CROIS DECIMAL(10,2) NULL  ," 
	+ "SEC_ZIGZAG INTEGER NULL,"  // 0:FALSE - 1:TRUE
	+ "FOREIGN KEY (PARC_ID) "
	+ "REFERENCES PARCELLE (PARC_ID)) ;";
	
	private static final String CREATE_TABLE_VARIETE =
	  "CREATE TABLE IF NOT EXISTS VARIETE"
	+ "(VAR_ID INTEGER PRIMARY KEY AUTOINCREMENT ,"
	+ "VAR_NOM CHAR(32) NOT NULL  ,"
	+ "VAR_POUSSE DECIMAL(10,2) NOT NULL ) " ;   

	 
	private static final String CREATE_TABLE_COORDONNEE =
	  "CREATE TABLE IF NOT EXISTS COORDONNEE"
	+ "(COORD_ID INTEGER PRIMARY KEY AUTOINCREMENT ,"
	+ "COORD_LAT DECIMAL(20,10) NOT NULL  ,"
	+ "COORD_LON DECIMAL(20,10) NOT NULL )"  ;

	
	private static final String CREATE_TABLE_INFO_SECTEUR =
	  "CREATE TABLE IF NOT EXISTS INFO_SECTEUR"
	+ "(INF_SEC_ID INTEGER PRIMARY KEY AUTOINCREMENT  ,"
	+ "SEC_ID BIGINT(4) NOT NULL  ,"
	+ "ANN_ID DATE NOT NULL  ,"
	+ "INF_SEC_COEF_GEL DECIMAL(10,2) NULL," 
	+ "FOREIGN KEY (SEC_ID)"
	+ "REFERENCES SECTEUR (SEC_ID) ,"
	+ "FOREIGN KEY (ANN_ID)"
	+ "REFERENCES ANNEE (ANN_ID)) ;";	  
	
	private static final String CREATE_TABLE_SAPIN =
	  "CREATE TABLE IF NOT EXISTS SAPIN"
    + "(SAP_ID INTEGER PRIMARY KEY AUTOINCREMENT  ,"
    + "VAR_ID BIGINT(2) NOT NULL  ,"
    + "SEC_ID BIGINT(4) NOT NULL  ,"
	+ "SAP_LIG BIGINT(4) NOT NULL  ,"
	+ "SAP_COL BIGINT(4) NOT NULL  ,"
	+ "SAP_PHO LONGBLOB NULL  ,"
	+ "COORD_ID INTEGER,"
	+ "FOREIGN KEY (VAR_ID)"
	+ "REFERENCES VARIETE (VAR_ID) ,"
	+ "FOREIGN KEY (SEC_ID)"
	+ "REFERENCES SECTEUR (SEC_ID)) ;"
	+ "FOREIGN KEY (COORD_ID)"
	+ "REFERENCES SECTEUR (COORD_ID)) ;";
	
	private static final String CREATE_TABLE_INFO_SAPIN =
	  "CREATE TABLE IF NOT EXISTS INFO_SAPIN"
	+ "(INF_SAP_ID INTEGER PRIMARY KEY AUTOINCREMENT  ,"
	+ "SAP_ID BIGINT(5) NOT NULL  ,"
	+ "INF_SAP_DATE DATETIME NOT NULL  ,"
	+ "INF_SAP_TAIL DECIMAL(10,2) NOT NULL  ," 
	+ "INF_SAP_STATUS BIGINT(1) NOT NULL  ,"
	+ "FOREIGN KEY (SAP_ID)"
	+ "REFERENCES SAPIN (SAP_ID)) ;";
	
	private static final String CREATE_TABLE_CROISSANCE =
	  "CREATE TABLE IF NOT EXISTS CROISSANCE"
	+ "(CROIS_ID INTEGER PRIMARY KEY AUTOINCREMENT  ,"
	+ "CROIS_B_INF DECIMAL(10,2) NOT NULL  ,"
	+ "CROIS_B_SUP DECIMAL(10,2) NOT NULL ) " ; 
	
	private static final String CREATE_TABLE_SEC_COORDONNEE =
	  "CREATE TABLE IF NOT EXISTS SEC_COORD"
	+ "(COORD_ID BIGINT(5) NOT NULL  ,"
	+ "SEC_ID BIGINT(5) NOT NULL  ," 
	+ "PRIMARY KEY (COORD_ID,SEC_ID) ,"
	+ "FOREIGN KEY (COORD_ID) "
	+ "REFERENCES COORDONNEE (COORD_ID) ,"
	+ "FOREIGN KEY (SEC_ID)"
	+ "REFERENCES SECTEUR (SEC_ID)) ;";
	
	private static final String ALTER_TABLE=
	  "ALTER TABLE INFO_SECTEUR "
    + 	"ADD FOREIGN KEY FK_INFO_SECTEUR_SECTEUR (SEC_ID)"
    + 	"REFERENCES SECTEUR (SEC_ID) ON DELETE CASCADE ;"
    + "ALTER TABLE INFO_SECTEUR "
    +	"ADD FOREIGN KEY FK_INFO_SECTEUR_ANNEE (ANN_ID)"
    +	"REFERENCES ANNEE (ANN_ID)  ;"
    + "ALTER TABLE SECTEUR "
    + 	"ADD FOREIGN KEY FK_SECTEUR_PARCELLE (PARC_ID)"
    + 	"REFERENCES PARCELLE (PARC_ID) ON DELETE CASCADE;"
    + "ALTER TABLE INFO_SAPIN "
    + 	"ADD FOREIGN KEY FK_INFO_SAPIN_ANNEE (ANN_ID)"
    + 	"REFERENCES ANNEE (ANN_ID) ;"
    + "ALTER TABLE INFO_SAPIN" 
    + 	"ADD FOREIGN KEY FK_INFO_SAPIN_SAPIN (SAP_ID)"
    +	"REFERENCES SAPIN (SAP_ID) ON DELETE CASCADE;"
    + "ALTER TABLE SAPIN "
    + 	"ADD FOREIGN KEY FK_SAPIN_VARIETE (VAR_ID)"
    +   "REFERENCES VARIETE (VAR_ID) ;"
    + "ALTER TABLE SAPIN "
    + 	"ADD FOREIGN KEY FK_SAPIN_SECTEUR (SEC_ID)"
    +	"REFERENCES SECTEUR (SEC_ID) ON DELETE CASCADE;"
    + "ALTER TABLE SEC_COORD "
    + 	"ADD FOREIGN KEY FK_SEC_COORD_COORDONNEE (COORD_ID)"
    + 	"REFERENCES COORDONNEE (COORD_ID) ON DELETE CASCADE;"
    + "ALTER TABLE SEC_COORD "
    + 	"ADD FOREIGN KEY FK_SEC_COORD_SECTEUR (SEC_ID)"
    +   "REFERENCES SECTEUR (SEC_ID) ON DELETE CASCADE;";
    
    /**
      * Constructor
      * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
      * @param context
      */
    public DataBaseHelper(Context context) {
    	super(context, DB_NAME, null, DB_VERSION);
    	this.myContext = context;
    }  
         
     
     @Override
     public synchronized void close(){
          
         if(myDataBase !=null)
             myDataBase.close();
         super.close();
     }
     
      
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        // cette fonction est appelle lorsque la base de donnees est cree pour la premiere fois, 
    	// on va donc lancer le script de creation de base :
    	Log.i("DB","onCreate fonction");
    	try 
    	{
    		db.execSQL(PRAGMA);
    		db.execSQL(CREATE_TABLE_EQUA);
    		db.execSQL(CREATE_TABLE_ANNEE);
    		db.execSQL(CREATE_TABLE_PARCELLE);
    		db.execSQL(CREATE_TABLE_SECTEUR);
    		db.execSQL(CREATE_TABLE_VARIETE);
    		db.execSQL(CREATE_TABLE_COORDONNEE);
    		db.execSQL(CREATE_TABLE_INFO_SECTEUR);
    		db.execSQL(CREATE_TABLE_SAPIN);
    		db.execSQL(CREATE_TABLE_INFO_SAPIN);
    		db.execSQL(CREATE_TABLE_CROISSANCE);
    		db.execSQL(CREATE_TABLE_SEC_COORDONNEE); 
    		db.execSQL(CREATE_TABLE_ANNEE);
    		//db.execSQL(ALTER_TABLE);
    		Log.i("DB", "Creation de la base sans erreurs!");
    	}
    	catch(NotFoundException e)
    	{
    		Log.e("DB", "Impossible de charger le script de generation de base!");
    		e.printStackTrace();
		}
    	catch (SQLException e) {
    		Log.e("DB", "Error lors de la creation de la base!");
    		e.printStackTrace();
    		if(myContext.deleteDatabase(DB_NAME))
    			Log.w("DB", "Suppression de la base de donnee reussi");
    		else
    			Log.e("DB", "Echec de la suppression de la base de donnee, etat indefini!");
    		
		}
    	init_db_value(db);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	try{
		Log.i("DB", "Mise a jours de la base de donnee de la version "+oldVersion+" vers la version "+newVersion);
    	db.execSQL("DROP TABLE IF EXISTS EQUA");
		db.execSQL("DROP TABLE IF EXISTS ANNEE");
		db.execSQL("DROP TABLE IF EXISTS PARCELLE");
		db.execSQL("DROP TABLE IF EXISTS SECTEUR");
		db.execSQL("DROP TABLE IF EXISTS VARIETE");
		db.execSQL("DROP TABLE IF EXISTS COORDONNEE");
		db.execSQL("DROP TABLE IF EXISTS INFO_SECTEUR");
		db.execSQL("DROP TABLE IF EXISTS SAPIN");
		db.execSQL("DROP TABLE IF EXISTS INFO_SAPIN");
		db.execSQL("DROP TABLE IF EXISTS CROISSANCE");
		db.execSQL("DROP TABLE IF EXISTS SEC_COORDONNEE"); 
		db.execSQL("DROP TABLE IF EXISTS ANNEE");
		db.execSQL("DROP TABLE IF EXISTS SEC_COORDONNEE");
		
    	}
    	catch(SQLException e)
    	{
    		Log.e("DB-INIT", "Echec de la mise a jours");
    		e.printStackTrace();
    	}
    	onCreate(db);
    } 
    
    public void init_db_value(SQLiteDatabase db)
    {
    	try{
    		Log.i("DB-INIT", "Init des valeurs par defauts");
        	db.execSQL("INSERT INTO VARIETE ('VAR_NOM','VAR_POUSSE') VALUES ('Nordmann',1.0)  ");
        	db.execSQL("INSERT INTO VARIETE ('VAR_NOM','VAR_POUSSE') VALUES ('Nobilis',0.8)  ");
        	db.execSQL("INSERT INTO VARIETE ('VAR_NOM','VAR_POUSSE') VALUES ('Epicea',1.3)  ");
        	db.execSQL("INSERT INTO VARIETE ('VAR_NOM','VAR_POUSSE') VALUES ('Grandis',1.2)  ");
        	db.execSQL("INSERT INTO VARIETE ('VAR_NOM','VAR_POUSSE') VALUES ('Sapin Bleu',1.0)  ");
        	for (int i = 2015; i<30; i++) 
        	{
        		db.execSQL("INSERT INTO ANNEE ('ANN_ID') VALUES ("+i+")  ");
        		Log.i("DB-INIT", "add "+i);
			}
    	}
    	catch(SQLException e)
    	{
    		Log.e("DB-INIT", "ECHEC de l'init");
    		e.printStackTrace();
    	}
    }

    
    
}