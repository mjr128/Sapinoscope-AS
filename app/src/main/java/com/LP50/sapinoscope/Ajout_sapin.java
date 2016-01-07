package com.LP50.sapinoscope;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.LP50.sapinoscope.Object_sapin.Status_sapin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class Ajout_sapin extends Activity {

	private int positionX;
	private int positionY;
		
	private Object_secteur secteurActuel;
	private Object_parcelle parcelleActuel;
	private Object_variete varieteActuel;
	private float tailleActuel;
	private int nbIdentiqueActuel;
	private int nbSapinLigne=0;
	
	boolean zigZag=true;
	
	private TextView textViewParcelle ;
	private TextView textViewSecteur ;
	private Spinner varieteSpinner ;
	private Spinner tailleSpinner ;
	private Spinner nbIdentiqueSpinner ;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageButton imageBtn;
    private Bitmap imageBitmap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ajout_sapin3);
		
		Log.i("ajoutSapinAct","Initialisation de l'ajout de sapin...");
		
		 textViewParcelle = (TextView) findViewById(com.LP50.sapinoscope.R.id.txt_addsap_parcelle_titre);
		 textViewSecteur = (TextView) findViewById(com.LP50.sapinoscope.R.id.txt_addsap_secteur_titre);
		 varieteSpinner = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_addsap_variete);
		 tailleSpinner = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_addsap_taille);
		 nbIdentiqueSpinner = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_addsap_sap_identique);
		 imageBtn = (ImageButton) findViewById(com.LP50.sapinoscope.R.id.imageButton);
		
		Intent intentAjoutSapin = getIntent();
		int secteurID  = intentAjoutSapin.getIntExtra("sect_id", -1);
		if(secteurID == -1 )
			Log.e("ajoutSapinAct","Impossible de recuperer les informations de l'intent, etat indetermine...");
		
		secteurActuel = new Object_secteur(secteurID);
		parcelleActuel = new Object_parcelle(secteurActuel.getId_parc());
		zigZag=secteurActuel.getZigzag();
		
		Log.i("ajoutSapinAct","Ajout prevue pour le secteur "+ secteurID +" de la parcelle "+secteurActuel.getId_parc());
		
		Etat_sapin etatsapin_actuel = null;
		
		int newSecteur = intentAjoutSapin.getIntExtra("new_secteur", -1);
		
		// Secteur Existant
		if(newSecteur == 0)
		{
			int xDepart = intentAjoutSapin.getIntExtra("x", -1);
			int yDepart = intentAjoutSapin.getIntExtra("y", -1);
			
			Log.i("ajoutSapinAct","Reprise de l'enregistrement a partir de x="+xDepart +" et y="+yDepart);
			
			int xMoins1 = getPreviousStepX( xDepart, 	yDepart, secteurActuel.getZigzag() );
			int xMoins2 = getPreviousStepX( xMoins1, 	yDepart, secteurActuel.getZigzag() );
			int xPlus1 =  getNextStepX(xDepart, yDepart, secteurActuel.getZigzag());
			int xPlus2 =  getNextStepX( 	xPlus1, 	yDepart, secteurActuel.getZigzag() );
			
			Vector<Etat_sapin> etatSapinMoins2 = Etat_sapin.createListOfInfoSapinFromXY(secteurID, xMoins2, yDepart);
			Vector<Etat_sapin> etatSapinMoins1 = Etat_sapin.createListOfInfoSapinFromXY(secteurID, xMoins1, yDepart);
			Vector<Etat_sapin> etatSapinDepart = Etat_sapin.createListOfInfoSapinFromXY(secteurID, xDepart, yDepart);
			Vector<Etat_sapin> etatSapinPlus1 = Etat_sapin.createListOfInfoSapinFromXY(secteurID, xPlus1, yDepart);
			Vector<Etat_sapin> etatSapinPlus2 = Etat_sapin.createListOfInfoSapinFromXY(secteurID, xPlus2, yDepart);
			if( etatSapinDepart != null )
				etatsapin_actuel = etatSapinDepart.get(0);
			else
				etatsapin_actuel = null;

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons
			builder.setPositiveButton(com.LP50.sapinoscope.R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User clicked OK button
			           }
			});
			
			CharSequence[] infoAffiche;
			if(etatSapinDepart != null && etatSapinMoins1 != null && etatSapinPlus1 != null)
			{
				infoAffiche = new CharSequence[3];
				infoAffiche[0] = "Precedent : "+(etatSapinMoins1.get(0).toString() + "\n"
						+new SimpleDateFormat("MM/yyyy").format(etatSapinMoins1.get(0).infoSapin.date));
				infoAffiche[1] = "Depart : "+(etatSapinDepart.get(0).toString() + "\n"
						+new SimpleDateFormat("MM/yyyy").format(etatSapinDepart.get(0).infoSapin.date));
				infoAffiche[2] = "Suivant : "+(etatSapinPlus1.get(0).toString() + "\n"
						+new SimpleDateFormat("MM/yyyy").format(etatSapinDepart.get(0).infoSapin.date));
			}
			else if(etatSapinPlus1 == null && etatSapinMoins1 != null && etatSapinMoins2 != null )
			{
				infoAffiche = new CharSequence[3];
				infoAffiche[0] = "Precedent 2: "+(etatSapinMoins2.get(0).toString() + "\n"
						+etatSapinMoins2.get(0).infoSapin.getFormatedDate("MM/yyyy"));
				infoAffiche[1] = "Precedent 1: "+(etatSapinMoins1.get(0).toString() + "\n"
						+etatSapinMoins1.get(0).infoSapin.getFormatedDate("MM/yyyy"));
				infoAffiche[2] = "Depart: "+(etatSapinDepart.get(0).toString() + "\n"
						+etatSapinDepart.get(0).infoSapin.getFormatedDate("MM/yyyy"));
			}
			else if(etatSapinMoins1 == null && etatSapinPlus1 != null && etatSapinPlus2 != null)
			{
				infoAffiche = new CharSequence[3];
				infoAffiche[0] = "Depart: "+(etatSapinDepart.get(0).toString() + "\n"
						+etatSapinDepart.get(0).infoSapin.getFormatedDate("MM/yyyy"));
				infoAffiche[1] = "Suivant 1: "+(etatSapinPlus1.get(0).toString() + "\n"
						+etatSapinPlus1.get(0).infoSapin.getFormatedDate("MM/yyyy"));
				infoAffiche[2] = "Suivant 2: "+(etatSapinPlus2.get(0).toString() + "\n"
						+etatSapinPlus2.get(0).infoSapin.getFormatedDate("MM/yyyy"));
			}
			else
			{
				infoAffiche = new CharSequence[1];
				infoAffiche[0] = "Depart: "+(etatSapinDepart.get(0).toString() + "\n"
					+etatSapinDepart.get(0).infoSapin.getFormatedDate("MM/yyyy"));
			}
			builder.setItems(infoAffiche, null);

			// Create the AlertDialog
			AlertDialog dialog = builder.create();
			dialog.show();
			setAndShowActuelPositionX(xDepart);
			setAndShowActuelPositionY(yDepart);
			
			nbSapinLigne = getNbSapinOnY(secteurID,xDepart,yDepart);
			setTextView_NbSapin_ligne(nbSapinLigne);
		}
		else
			getMaxXYsapinPosFromDB(secteurID);
		
		
		//Init interface
		fillGui();
		set_spinner_value(etatsapin_actuel);
		
		Sapinoscope.getLocationHelper().startRecherche();
		
		//Listener de spinner
		Spinner varieteSpin 	= (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_addsap_variete);
		varieteSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,int position, long id) {
				varieteActuel = (Object_variete) adapter.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		Spinner tailleSpin = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_addsap_taille);
		tailleSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,int position, long id) {
				//transformation de la string "1,2m" en 1.2 lisible par un float :
				tailleActuel = stringTailleToFloat((String) adapter.getItemAtPosition(position));
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		Spinner nbItentiqueSpin = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_addsap_sap_identique);
		nbItentiqueSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
				nbIdentiqueActuel = Integer.parseInt((String) adapter.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		//Listener de bouton
		Button addSapinBtn = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_addsap_sapin_existant);
		addSapinBtn.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				addSapinAtCurrentPosInDb(Status_sapin.OK, nbIdentiqueActuel);
				goToNextPositionX();
			}
		});
		
		Button newLineBtn = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_addsap_nouvelle_ligne);
		newLineBtn.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				goToNextPositionY();
			}
		});
		
		Button newSapinBtn = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_addsap_nouveau_sapin);
		newSapinBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				addSapinAtCurrentPosInDb(Status_sapin.NOUVEAU, nbIdentiqueActuel);
				goToNextPositionX();
			}
		});
		
		Button newSoucheBtn = (Button) findViewById(com.LP50.sapinoscope.R.id.bt_addsap_souche);
		newSoucheBtn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) {
				addSapinAtCurrentPosInDb(Status_sapin.TOC, nbIdentiqueActuel);
				goToNextPositionX();
			}
		});


		imageBtn.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
	}

    //Fonction qui récupère la miniature
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            //imageView = (ImageView) findViewById(R.id.mImageView);
			imageBtn.setImageBitmap(imageBitmap);
        }
    }

    //static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

	protected void onPause()
	{
		super.onPause();
		Sapinoscope.getLocationHelper().stopRecherche();
	}
	
	protected void onStop()
	{
		super.onStop();
		Sapinoscope.getLocationHelper().stopRecherche();
		//setPositionsSecteur();
	}
	
	protected void onResume()
	{
		super.onResume();

		// On remet a jour la liste des variete disponible car cette methode est appelle
		// apres la modification des varietes
		int currentSelection = varieteSpinner.getSelectedItemPosition();
		Vector<Object_variete> varietes = Object_variete.createListOfAllVariete();
		ArrayAdapter<Object_variete> adapterVariete = new ArrayAdapter<Object_variete>(this, com.LP50.sapinoscope.R.layout.secteur_texte,varietes);
		varieteSpinner.setAdapter(adapterVariete);
		varieteSpinner.setSelection(currentSelection);

		Sapinoscope.getLocationHelper().startRecherche();
	}
	
	protected void onStart()
	{
		super.onStart();
		Sapinoscope.getLocationHelper().startRecherche();
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		Sapinoscope.getLocationHelper().stopRecherche();
		//setPositionsSecteur();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.LP50.sapinoscope.R.menu.settings_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == com.LP50.sapinoscope.R.id.action_settings) {
			Intent intent = new Intent(this, Varietes_Listview.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void tryDrawing(SurfaceHolder holder) {
        Log.i("Draw", "Trying to draw...");

        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            Log.e("Draw", "Cannot draw onto the canvas as it's null");
        } else {
            drawMyStuff(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawMyStuff(final Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		canvas.drawColor(Color.BLACK);
		Random random = new Random();
		
		int N = 50;
		Vector<Point2D> points = new Vector<Point2D>(N);
		
		for(int i=0;i<N;i++)
		{
			int x = random.nextInt(canvas.getWidth());
			int y = random.nextInt(canvas.getHeight());
			points.add(new Point2D(x, y));
			canvas.drawCircle(x, y, 10, paint);
		}
		GrahamScan graham = new GrahamScan(points);
		
		Point2D start = null;
		Point2D stop = null;
		Point2D firstPoint = null;
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(5);
		for (Point2D p : graham.hull())
		{
			if(start == null)
			{
				firstPoint=p;
				start=p;
			}
			else
			{		
				stop=p;	
				canvas.drawLine((float)start.x(), (float)start.y(), (float)stop.x(), (float)stop.y(), paint);
				start=p;
			}
		}
		canvas.drawLine((float)start.x(), (float)start.y(), (float)firstPoint.x(), (float)firstPoint.y(), paint);
    }

    // return l'id de la coordonne insere dans la base 
    private int insertCoord(Point2D position) throws Exception
    {
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	
    	String reqSelectID = "SELECT COORD_ID FROM COORDONNEE WHERE(COORD_LAT= "+position.x()+" AND COORD_LON ="+position.y()+")";
    	Cursor c = db.rawQuery(reqSelectID, null);
    	if (c.getCount() != 0)
    	{
    		c.moveToFirst();
    		return c.getInt(c.getColumnIndex("COORD_ID"));
    	}
    	
    	String reqInsert = "INSERT INTO COORDONNEE ('COORD_LAT','COORD_LON') VALUES ("+position.x()+","+position.y()+")";
		Log.i("testDB", reqInsert);
		db.execSQL(reqInsert);
		
		c = db.rawQuery(reqSelectID, null);
		if (c.getCount() == 0)
		{
			throw new Exception("Erreur lors de l'insertion des coordonnees :"+position.x()+", "+position.y());
		}
		else
		{
			c.moveToFirst();
    		return c.getInt(c.getColumnIndex("COORD_ID"));
		}
    }
    
    private int getNbSapinOnY(int idSecteur, int x, int y)
    {
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
    	String a;
    	if( zigZag && y%2==1 )
    		a="MAX";
    	else
    		a="MIN";
    	
    	String reqMinSelectX = "SELECT "+a+"(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
    	Cursor cursorX = db.rawQuery(reqMinSelectX, null);
    	if( !cursorX.moveToFirst() )
    		return 0;
    	int minX = cursorX.getInt(0);
    	
    	return x-minX+1;
    }
    
    private boolean getMaxXYsapinPosFromDB(final int idSecteur)
    {
    	//On commence par recuperer Y pour savoir s'il faut prendre le maximum ou le minimum de X
    	
    	String reqSelectMaxY = "SELECT MAX(SAP_COL) FROM SAPIN WHERE SEC_ID='"+idSecteur+"'";
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getReadableDatabase();
    	
    	Cursor cursorY = db.rawQuery(reqSelectMaxY, null);
    	cursorY.moveToFirst();
    	positionY = cursorY.getInt(0);
    	
    	String reqMinSelectX = "SELECT MIN(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
    	Cursor cursorX = db.rawQuery(reqMinSelectX, null);
    	cursorX.moveToFirst();
    	int minX = cursorX.getInt(0);
    	
    	String reqMaxSelectX = "SELECT MAX(SAP_LIG) FROM SAPIN WHERE SEC_ID='"+idSecteur+"' AND SAP_COL='"+positionY+"'";
    	cursorX = db.rawQuery(reqMaxSelectX, null);
    	cursorX.moveToFirst();
    	int maxX = cursorX.getInt(0);
    	
    	if(zigZag && positionY%2 == 1) // Y est impaire et on est en mode zigzag, donc on decremente X 
    	{
	    	positionX = (minX==0) ? minX : minX-1;
    		
    	}else // Y est paire ou on est pas en mode zigzag, donc on incremente X
    	{
	    	positionX = (maxX==0) ? maxX : maxX+1;
    	}
    	
    	nbSapinLigne = maxX - minX;
    	setTextView_NbSapin_ligne(nbSapinLigne);
    	
    	if(positionX == 0 && positionY == 0)
    	{
    		Log.i("ajoutSapinAct","pas encore de sapin enregistre pour cette parcelle, debut a 0,0");
    		return false;
    	}
    	else
			Log.i("ajoutSapinAct","Sapin trouve pour ce secteur, debut a "+ positionX +","+positionY);
    	
    	return true;
    }
    
    private void addSapinAtCurrentPosInDb(Object_sapin.Status_sapin status, int nbIdentique)
    {
    	for(int i=0; i<nbIdentique; ++i)
    	{
	    	// sapin qui contient les infos que l'utilisateur souhaite enregistrer
	    	Object_sapin sapin = new Object_sapin();
	    	sapin.sec_id = secteurActuel.getId();
	    	sapin.var_id = varieteActuel.getVar_id();
	    	if(zigZag && positionY%2 == 1)//Les colonnes impaires sont decremente, les colonnes paires sont incrementees
	    		sapin.xLigne = positionX - i;
	    	else
	    		sapin.xLigne = positionX + i;
	    	sapin.yColonne = positionY;
	    	
	    	// infoSapin qui contient les infos que l'utilisateur souhaite enregistrer
	    	Object_infoSapin infoSapin = new Object_infoSapin(Calendar.getInstance().getTime());
	    	infoSapin.status= status;
            infoSapin.photo = imageBitmap;
	    	switch (status)
	    	{
	    		case NOUVEAU:
		    		infoSapin.taille= 0.4f;
		    		break;
	    		
	    		case OK:
	    			infoSapin.taille= tailleActuel;
	    			break;
	    			
	    		case TOC:
	    		case VIDE:
	    		case INDEFINI:
	    			infoSapin.taille = 0;
	    			break;
	    	}
	    	
	    	
	    	Etat_sapin etatActuel = new Etat_sapin(sapin, infoSapin, varieteActuel);
	    	
	    	//On recupere les infos enregistrees dans la base sur cet emplacement :
	    	Vector<Etat_sapin> precedentEregitrements = Etat_sapin.createListOfInfoSapinFromXY(secteurActuel.getId(), sapin.xLigne, sapin.yColonne);
	    	
	    	boolean informationsTrouve = false;
	    	
	    	//On prend le dernier etat sapin enregistre :
	    	if(precedentEregitrements != null && precedentEregitrements.size()>0)
	    	{
		    	Etat_sapin dernierEtatSapinEregistre = precedentEregitrements.get(0);
		    	
		    	if(estUneEvolutionProbable(dernierEtatSapinEregistre, etatActuel))
		    	{
		    		// Le sapin que l'utilisateur souhaite enregistre est le meme que celui qui est deja en place, 
		    		// on va donc reutiliser ce sapin, et n'ajouter que l'infoSapin dans la base
		    		
		    		infoSapin.sap_id = dernierEtatSapinEregistre.sapin.getSapId();
		    		informationsTrouve =true;
		    		
		    		Log.i("ajoutSapinAct", "Utilisation d'un ancien sapin(id="+dernierEtatSapinEregistre.sapin.getSapId()+") a la position : "+sapin.xLigne+","+sapin.yColonne);
		    	}
	    	}
	    	if (!informationsTrouve) 
	    	{
	    		// Il n'y a aucune information sur cet emplacement, ou
	    		// Le sapin que l'utilisateur souhaite enregistrer n'est pas le meme que celui qui est deja en place,
	    		// on va donc enregistrer un nouveau sapin et son premier infoSapin dans la base. 
	    		// Ainsi que sa position GPS
	    		
	    		boolean positionTrouve=false;
		    	try 
		    	{
		    		int idCoord = insertCoord(Sapinoscope.getLocationHelper().getLocation());
		    		sapin.coord_id=idCoord;
		    		positionTrouve=true;
				} catch (Exception e) 
		    	{
					Log.w("ajoutSapinAct", "Impossible de sauvegarder la position, sauvegarde du sapin sans position...");
				}
		    	sapin.saveInDb(positionTrouve);// L'enregistrement dans la base du sapin, met a jour son id.
		    	Log.i("ajoutSapinAct", "Ajout d'un nouveau sapin(id="+sapin.getSapId()+") a la position : "+sapin.xLigne+","+sapin.yColonne);
		    	infoSapin.sap_id = sapin.getSapId();
	    	}
	    	infoSapin.saveInDb();
    	}
    	nbSapinLigne += nbIdentique;
    	Spinner nbIdentiqueSpinner = (Spinner) findViewById(com.LP50.sapinoscope.R.id.spin_addsap_sap_identique);
    	nbIdentiqueSpinner.setSelection(0);


		//imageBtn.setImageResource(0);
		//imageBtn.setImageDrawable(null);
		imageBtn.setImageBitmap(null);
    }
    
    // Donne quel serait le point suivant a entrer : 
    public static int getNextStepX(int x, int y, boolean _zigzag)
    {
    	if(_zigzag && y%2 == 1)
			return x-1;
		else
			return x+1;
    }
    
    public static int getPreviousStepX(int x, int y, boolean _zigzag)
    {
    	if(_zigzag && y%2 == 1)
			return x+1;
		else
			return x-1;
    }
    
    private void goToNextPositionX()
    {
    	if(zigZag && positionY%2 == 1)
			setAndShowActuelPositionX(positionX-=nbIdentiqueActuel);
		else
			setAndShowActuelPositionX(positionX+=nbIdentiqueActuel);
    	
    	Vector<Etat_sapin> list_etatsapins = Etat_sapin.createListOfInfoSapinFromXY(secteurActuel.getId(), positionX, positionY);
    	
    	if(list_etatsapins != null)
    		set_spinner_value(list_etatsapins.get(0));
    }
    
    private void goToNextPositionY()
    {
        nbSapinLigne=0;
    	setAndShowActuelPositionY(positionY + 1);
    	if(zigZag)
    	{
    		if( positionY%2 == 0)
    			setAndShowActuelPositionX(positionX+1);
    		else
    			setAndShowActuelPositionX(positionX-1);
    	}else
    		setAndShowActuelPositionX(0);
    }
    
    private void setAndShowActuelPositionX(int x)
    {
    	 positionX = x;
    	 setTextView_NbSapin_ligne(nbSapinLigne);
        
    }
    
    private void setAndShowActuelPositionY(int y)
    {
        // Affiche 0 lors d une nouvelle ligne
        setTextView_NbSapin_ligne(nbSapinLigne);
    	positionY = y;
   	 	TextView txtView_getY = (TextView) findViewById(com.LP50.sapinoscope.R.id.txt_addsapin_getY);
   	 	txtView_getY.setText("Ligne :" + (y + 1));
    }

    //ici je dois récupérer et afficher ma photo dans imageView
    private void fillGui()
    {
    	textViewParcelle.setText("Parcelle : " + parcelleActuel.getName());
    	textViewSecteur.setText("Secteur : " + secteurActuel.getName());
		
		String[] tailles = getResources().getStringArray(com.LP50.sapinoscope.R.array.taille_predefini);
		ArrayAdapter<String> adapterTaille = new ArrayAdapter<String>(this, com.LP50.sapinoscope.R.layout.secteur_texte,tailles);
		tailleSpinner.setAdapter(adapterTaille);
		
		String[] nbIdentiques = getResources().getStringArray(com.LP50.sapinoscope.R.array.nbIdentiques);
		ArrayAdapter<String> nbIdentiquesAdapter = new ArrayAdapter<String>(this, com.LP50.sapinoscope.R.layout.secteur_texte,nbIdentiques);
		nbIdentiqueSpinner.setAdapter(nbIdentiquesAdapter);

		Vector<Object_variete> varietes = Object_variete.createListOfAllVariete();
		ArrayAdapter<Object_variete> adapterVariete = new ArrayAdapter<Object_variete>(this, com.LP50.sapinoscope.R.layout.secteur_texte,varietes);
		varieteSpinner.setAdapter(adapterVariete);

        int idSapin = returnIdInfoSapin();
        if (idSapin != -1)
        {
            try
            {
                Bitmap bmp = Object_infoSapin.getLastInfoSapin(idSapin).photo;
				imageBtn.setImageBitmap(bmp);
            }
            catch(Exception ex)
            {
				Log.w("Ajout sapin", "Sapin sans photo !");
			}

        }
    }

    // return l'id de l'infosapin
    private int returnIdInfoSapin()
    {
        SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();

        String reqSelectID = "SELECT SAP_ID FROM SAPIN WHERE SAP_LIG = "+positionX+" AND SAP_COL = "+positionY+
                                                            " AND SEC_ID = "+secteurActuel.getId()+";";
        Cursor c = db.rawQuery(reqSelectID, null);
        if (c.getCount() != 0)
        {
            c.moveToFirst();
            return c.getInt(c.getColumnIndex("SAP_ID"));
        }
        return -1;
    }

    private void set_spinner_value(Etat_sapin etatsapin_actuel)
    {
		if(etatsapin_actuel != null)
		{
			ArrayAdapter<String> tailles = (ArrayAdapter<String>) tailleSpinner.getAdapter();
			ArrayAdapter<Object_variete> adapterVariete = (ArrayAdapter<Object_variete>) varieteSpinner.getAdapter();
			
	    	for (int i = 0; i< tailles.getCount() ; i++) 
			{
	    		float tailleDansLaListe = stringTailleToFloat(tailles.getItem(i));
				if(tailleDansLaListe == etatsapin_actuel.infoSapin.taille)
				{
					tailleSpinner.setSelection(i);
					break;
				}
			}
	    	
	    	for (int i = 0; i<adapterVariete.getCount() ; i++) 
			{
				if(etatsapin_actuel.variete.equals(adapterVariete.getItem(i)))
				{
					varieteSpinner.setSelection(i);
					break;
				}
			}
		}
    }
    
    private float stringTailleToFloat(String s)
    {
    	Pattern p = Pattern.compile("(\\d+[,.]?\\d*)");
		Matcher m = p.matcher(s);
		if(!m.find())
			Log.e("ajoutSapinAct", "impossible de lire le spinner de taille!");
		return Float.parseFloat(m.group(1).replace(",", "."));
		
    }

    private void removeAllCoordForSecteur(int secteurID)
    {
    	String requete = "DELETE FROM SEC_COORD WHERE SEC_ID="+secteurID;
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	db.execSQL(requete);
    }
    
    private void insertPointSecteur(Point2D p, int secteurID)
    {
    	String requete = "SELECT COORD_ID FROM COORDONNEE WHERE COORD_LAT="+p.x()+" AND COORD_LON="+p.y();
    	
    	SQLiteDatabase db = Sapinoscope.getDataBaseHelper().getWritableDatabase();
    	
    	Cursor c = db.rawQuery(requete, null);
    	
    	if(c.getCount()==0)
    	{
    		Log.e("ajoutSapinAct","Impossible de retrouver le point : ("+p.x()+" "+p.y()+") dans la base!");
    		return;
    	}
    	
    	if(c.getCount()>1)
    		Log.w("ajoutSapinAct","Le point : ("+p.x()+" "+p.y()+") est present 2 fois dans la base!");
    	
    	c.moveToFirst();
    	
    	int coordID = c.getInt(0);
    	
    	requete = "INSERT INTO SEC_COORD (	'SEC_ID',			'COORD_ID')"
			   				+"VALUES(	'"+secteurID +"',	'"+coordID+"')";
    	
    	db.execSQL(requete);
    }
    
    private void setPositionsSecteur()
    {
    	Vector<Object_sapin> sapinList = Object_sapin.createListOfSapin(secteurActuel.getId());
    	Vector<Point2D> positionsSapinConnus = new Vector<Point2D>();
    	    	
    	for(int i=0; i<sapinList.size(); ++i)
    	{
    		if(sapinList.get(i).coordonne != null)
    			positionsSapinConnus.add(sapinList.get(i).coordonne);
    	}
    	
    	if(positionsSapinConnus.size() <3)
    	{
    		Log.e("ajoutSapinAct","Pas assez de points de sapin pour definir un secteur, le secteur n'est pas defini.");
    		return;
    	}
    		
    	removeAllCoordForSecteur(secteurActuel.getId());
    	
    	GrahamScan graham = new GrahamScan(positionsSapinConnus);
    	for(Point2D p : graham.hull())
    	{
    		insertPointSecteur(p,secteurActuel.getId());
    	}
    }

    private void setTextView_NbSapin_ligne(int x)
    {
   	 TextView txtView_getX = (TextView) findViewById(com.LP50.sapinoscope.R.id.txt_addsapin_getX);
   	 txtView_getX.setText("Sapin N° :"+x);

    }
    
    // Renvoie true si le sapin evolution pourrait etre la source apres quelques temps, utilise toutes les infos disponibles, sauf l'id
    private boolean estUneEvolutionProbable(Etat_sapin source, Etat_sapin evolution)
    {
    	if(!source.variete.equals(evolution.variete))
    		return false;
    	if(source.sapin.xLigne != evolution.sapin.xLigne)
    		return false;
    	if(source.sapin.yColonne != evolution.sapin.yColonne)
    		return false;
    	
    	if(source.infoSapin.status == Status_sapin.NOUVEAU || source.infoSapin.status == Status_sapin.OK)
    		if(evolution.infoSapin.status == Status_sapin.OK || evolution.infoSapin.status == Status_sapin.TOC)
    		{
    			long msEntreDeuxMesures = evolution.infoSapin.date.getTime() - source.infoSapin.date.getTime();
    			if(msEntreDeuxMesures < 0)// evolution doit etre postérieure à la source
    				return false;
    			
    			double jourEntreDeuxMesures = msEntreDeuxMesures/86400000.0f;// 86400000.0f = nb de ms dans une journee
    			
    			double elevationEntreDeuxMesures = evolution.infoSapin.taille - source.infoSapin.taille;
    			
    			// le sapin ne doit pas avoir reduit de plus de 20%
    			if( elevationEntreDeuxMesures < -(evolution.infoSapin.taille*0.20) )
    				return false;
    				
    			
    			// La taille doit etre stocke en cm pour que les calculs marche
    			double elevationMoyenneEntreDeuxMesuresParMParJour = elevationEntreDeuxMesures / jourEntreDeuxMesures;  
    			
    			// Etimation : un sapin prend un maximum de 60cm/an :
    			double elevationMaxParMParAn = 60.0f/100;// 60.0f == 60.0 le f est la pour limiter les erreurs de calcul sur les flotants. google pour plus d'info
    			double elevationMaxParMParJour = elevationMaxParMParAn / 365.0f ;
    			
    			// Le sapin ne doit pas avoir grandi de plus de 60cm/an pour etre valide
    			if( elevationMoyenneEntreDeuxMesuresParMParJour > elevationMaxParMParJour)
    				return false;
    			
    			// Si aucun if jusqu'ici n'a retourne false, c'est que l'evolution est probablement celle de la source
    			return true;
    		}
    	
    	return false;
    }
}
