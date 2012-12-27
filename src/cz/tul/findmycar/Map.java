package cz.tul.findmycar;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {

	private MapView mapView;
	private GeoPoint car;
	private LocationManager myLocationManager;
	private LocationListener myLocationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);

		// ziskani dat z prvni aktivity
		int plat = 0;
		int plong = 0;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			plat = extras.getInt("plat");
			plong = extras.getInt("plong");
		}

		// vlastnsti mapy
		this.mapView = (MapView) findViewById(R.id.mapview);
		this.mapView.setBuiltInZoomControls(false);
		this.mapView.setTraffic(true);
		this.mapView.setSatellite(false);

		// vykreslovac (volat pri zmene GPS)
		List<Overlay> mapOverlays = this.mapView.getOverlays();

		// ikony
		Drawable icon1 = this.getResources().getDrawable(R.drawable.car);
		Drawable icon2 = this.getResources().getDrawable(R.drawable.my);

		// korekce stinu ikony
		icon1.setBounds(-icon1.getIntrinsicWidth() / 2,
				-icon1.getIntrinsicHeight(), icon1.getIntrinsicWidth() / 2, 0);
		icon2.setBounds(-icon2.getIntrinsicWidth() / 2,
				-icon2.getIntrinsicHeight(), icon2.getIntrinsicWidth() / 2, 0);

		HelloItemizedOverlay itemizedoverlay = new HelloItemizedOverlay(icon1,
				this);

		// gps bod AUTO
		this.car = new GeoPoint(plat, plong);
		OverlayItem overlayitem = new OverlayItem(car, "Najdi moje auto",
				"Zde se nachází moje auto");
		itemizedoverlay.addOverlay(overlayitem);

		// moje GPS pozice
		myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		myLocationListener = new MyLocationListener();
		myLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, myLocationListener);
		GeoPoint myLocation = new GeoPoint((int) (myLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				.getLatitude() * 1000000), (int) (myLocationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER)
				.getLongitude() * 1000000));
		CenterMap(myLocation);
		OverlayItem overlayitem2 = new OverlayItem(myLocation,
				"Najdi moje auto", "Zde se nyní nacházíte");
		// nastaveni jine ikony
		overlayitem2.setMarker(icon2);
		itemizedoverlay.addOverlay(overlayitem2);

		// vycentrovani mapy
		CenterMap(myLocation);

		// pridani znacek do mapy
		mapOverlays.add(itemizedoverlay);
	}

	private void CenterMap(GeoPoint myLocation) {
		// zoom mapy
		this.mapView.getController()
				.zoomToSpan(
						Math.abs(myLocation.getLatitudeE6()
								- this.car.getLatitudeE6()),
						Math.abs(myLocation.getLongitudeE6()
								- this.car.getLongitudeE6()));

		// stred mapy
		GeoPoint center = new GeoPoint(
				(myLocation.getLatitudeE6() + this.car.getLatitudeE6()) / 2,
				(myLocation.getLongitudeE6() + this.car.getLongitudeE6()) / 2);
		this.mapView.getController().setCenter(center);
	}

	// listener na zmenu GPS pozice
	private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location argLocation) {
			GeoPoint myGeoPoint = new GeoPoint(
					(int) (argLocation.getLatitude() * 1000000),
					(int) (argLocation.getLongitude() * 1000000));

			// vycentrovani mapy
			CenterMap(myGeoPoint);
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.maps_type, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.basic:
			this.mapView.setTraffic(true);
			this.mapView.setSatellite(false);
			return true;
		case R.id.satelite:
			this.mapView.setSatellite(true);
			this.mapView.setTraffic(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
