package com.ostermann.sapinoscope;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

abstract class GoodLocationListener {
    abstract public void goodLocationFound(Location goodLocation);
};

public class Location_helper {

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private Location currentBestLocation;

	private LocationListener locationListener;

	private LocationManager locationManager; 

	private int currentStatus;
	
	private boolean rechercheEnCours;
	
	public Location_helper() 
	{
		currentBestLocation = null;
		
		rechercheEnCours = false;
		
		// Acquire a reference to the system Location Manager
		Context context = Sapinoscope.getAppContext();
		locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		
		// Define a listener that responds to location updates
		locationListener = new LocationListener() 
		{
			public void onLocationChanged(Location location) 
			{
				Log.i("Location", "Nouveau point trouve !");
				// Called when a new location is found by the network location provider.
				if ( isBetterLocation(location, currentBestLocation) )
				{
					currentBestLocation = location;
					Log.i("Location", "Nouveau point trouve et valide !");
				}
			}

			@Override
			public synchronized void onProviderDisabled(String provider) {
				Log.w("Location", "Impossible d'optenir la position, GPS desactive ?");

			}

			@Override
			public synchronized void onProviderEnabled(String provider) {
				Log.i("Location", "GPS active !");

			}

			@Override
			public synchronized void onStatusChanged(String provider, int status, Bundle extras) 
			{
				switch(status)
				{
				case LocationProvider.OUT_OF_SERVICE:
					Log.e("Location", "Service de localisation indisponible");
					break;
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					Log.w("Location", "Service de localisation actuelement indisponible");
					break;
				case LocationProvider.AVAILABLE:
					Log.i("Location", "Service de localisation disponible!");
					break;
				}
				currentStatus = status;
			}
		};

	}

	/** Determines whether one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	private synchronized boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private synchronized boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public synchronized boolean startRecherche()
	{		
		Log.i("GPS", "Lancement de la recherche...");
		// Register the listener with the Location Manager to receive location updates
		if(!rechercheEnCours)
		{
			boolean launched = false;
			try{
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
				launched = true;
			}catch(Exception e)
			{
				Log.i("GPS", "NETWORK_PROVIDER non disponible");
			}
			try{
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
				launched = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);;
			}catch(Exception e)
			{
				Log.i("GPS", "GPS_PROVIDER non disponible");
			}
			rechercheEnCours = launched;
		}
		return rechercheEnCours;
	}
	
	public synchronized void stopRecherche()
	{
		Log.i("GPS", "Arret de la recherche...");
		// Register the listener with the Location Manager to receive location updates
		locationManager.removeUpdates(locationListener);
		
		rechercheEnCours = false;
	}
	
	public synchronized Point2D getLocation()
	{
		return new Point2D(currentBestLocation.getLatitude(), 
				currentBestLocation.getLongitude());
	}
	/*
	public synchronized void setGoodLocationListerner(int metresTolere, GoodLocationListener listener)
	{
		
	}
	
	public synchronized Location waitGoodLocation( final int metresTolere)
	{
		boolean rechercheDoitEtreArrete = false;
		if( !rechercheEnCours )
		{
			startRecherche();
			rechercheDoitEtreArrete = true; 
		}
		Thread thread = new Thread(
					new Runnable() {
						public void run() {
							while(currentBestLocation == null || currentBestLocation.getAccuracy() > metresTolere)
							{
								try {
									wait(250);
								} catch (InterruptedException e) {
									Log.e("Location", "Interuption pendant l'attente de position du gps");
									e.printStackTrace();
								}
							}
						}
					}
				);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			Log.e("Location", "Interuption pendant l'attente de position du gps");
			e.printStackTrace();
		}
		
		if(rechercheDoitEtreArrete)
			stopRecherche();
		return currentBestLocation;
	}
	*/
}
