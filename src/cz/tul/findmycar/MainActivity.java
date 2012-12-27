package cz.tul.findmycar;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// definice textovych poli
	TextView textWait;
	ProgressBar progress;
	Button setCar;
	Button getCar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		// gps pozice
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener ll = new mylocationlistener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	// listener na GPS
	class mylocationlistener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {

			if (location != null) {
				final int plong = (int) (location.getLongitude() * 1000000);
				final int plat = (int) (location.getLatitude() * 1000000);

				setContentView(R.layout.buttons);

				setCar = (Button) findViewById(R.id.button1);
				getCar = (Button) findViewById(R.id.button2);

				// listener ulozeni pozice
				setCar.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// potvrzujici dialog
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								MainActivity.this);

						// set title
						alertDialogBuilder.setTitle(R.string.save_car_gps);

						// set dialog message
						alertDialogBuilder
								.setMessage(R.string.save_car_gps_dialog)
								.setCancelable(false)
								.setPositiveButton(R.string.yes,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												// ulozeni do localstorage
												SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
												SharedPreferences.Editor editor = sharedPref
														.edit();
												editor.putInt("Lat", plat);
												editor.putInt("Long", plong);
												editor.commit();

												// vypsani toastu
												Context context = getApplicationContext();
												String text = getResources()
														.getString(
																R.string.save_car_saved);
												int duration = Toast.LENGTH_SHORT;
												Toast toast = Toast
														.makeText(context,
																text, duration);
												toast.show();
											}
										})
								.setNegativeButton(R.string.no,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
												dialog.cancel();
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();

					}
				});

				// ziskani ulozene pozice
				getCar.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						// nacteni dat z local storrage
						SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
						int plat = sharedPref.getInt("Lat", 0);
						int plong = sharedPref.getInt("Long", 0);

						// prepnuti do dalsi aktivity s predanim dat
						Intent myIntent = new Intent(v.getContext(), Map.class);
						myIntent.putExtra("plat", (int) plat);
						myIntent.putExtra("plong", (int) plong);
						startActivityForResult(myIntent, 0);
					}
				});
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			setContentView(R.layout.loading);
			textWait = (TextView) findViewById(R.id.textView1);
			textWait.setText(R.string.gps_error);
		}

		@Override
		public void onProviderEnabled(String provider) {
			setContentView(R.layout.loading);
			textWait.setText(R.string.gps_wait);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
