package com.LP50.sapinoscope;

import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Graham_graphe extends Activity implements SurfaceHolder.Callback {

	private int positionX;
	private int positionY;
	
	private Vector<Location> registredLocations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.LP50.sapinoscope.R.layout.activity_ajout_sapin3);
		
		positionX = 0;
		positionY = 0;
		
		registredLocations = new Vector<Location>();
		
		Sapinoscope.getLocationHelper().startRecherche();
		
		SurfaceView surfaceView = (SurfaceView) findViewById(com.LP50.sapinoscope.R.id.surfaceView1);
		surfaceView.getHolder().addCallback(this);
		
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		tryDrawing(holder);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		tryDrawing(holder);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
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

    @SuppressWarnings("null")
	private void drawMyStuff(final Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		canvas.drawColor(Color.BLACK);
		Random random = new Random();
		
		int N = 25;
		Vector<Point2D> points = null;
		
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
	
}