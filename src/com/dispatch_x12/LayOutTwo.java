package com.dispatch_x12;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.FlickrPOIProvider;
import org.osmdroid.bonuspack.location.PicasaPOIProvider;
import org.osmdroid.bonuspack.overlays.ExtendedOverlayItem;
import org.osmdroid.bonuspack.overlays.ItemizedOverlayWithBubble;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.routing.GoogleRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.NetworkLocationIgnorer;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.DirectedLocationOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import com.dispatch_x12.AppSettings;
import com.dispatch_x12.MainActivity;
import com.dispatch_x12.R;
import com.dispatch_x12.adapters.StopListAdapter;
import com.dispatch_x12.map.POIInfoWindow;
import com.dispatch_x12.map.ViaPointInfoWindow;
import com.dispatch_x12.utilities.CalendarUtils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.dispatch_x12.utilities.Constant;

public class LayOutTwo extends Fragment implements MapEventsReceiver,
		LocationListener, SensorEventListener {
	ViewGroup root;
	private static ListView mStopList;

	private StopListAdapter mStopListAdapter;
	private ArrayList<HashMap<String, String>> mJsonStopList = new ArrayList<HashMap<String, String>>();

	private Context mContext;

	protected MapView mMap;
	protected GeoPoint mStartPoint, mDestinationPoint;
	protected ArrayList<GeoPoint> mWayPoints = new ArrayList<GeoPoint>(2);

	protected static int START_INDEX = -2, DEST_INDEX = -1;
	protected ItemizedOverlayWithBubble<ExtendedOverlayItem> itineraryMarkers;
	// for departure, destination and mWayPoints
	protected ExtendedOverlayItem markerStart, markerDestination;
	DirectedLocationOverlay myLocationOverlay;
	// MyLocationNewOverlay myLocationNewOverlay;
	protected LocationManager locationManager;
	protected SensorManager mSensorManager;
	protected Sensor mOrientation;

	protected boolean mTrackingMode = false;
	Button mTrackingModeButton;
	float mAzimuthAngleSpeed = 0.0f;

	protected PathOverlay polygonOverlay; // enclosing polygon of destination
											// location - experimental
	protected Road mRoad;
	protected PathOverlay roadOverlay;
	protected ItemizedOverlayWithBubble<ExtendedOverlayItem> roadNodeMarkers;
	protected static final int ROUTE_REQUEST = 1;

	ArrayList<POI> mPOIs;
	ItemizedOverlayWithBubble<ExtendedOverlayItem> poiMarkers;
	AutoCompleteTextView poiTagText;
	protected static final int POIS_REQUEST = 2;

	private int mRouteProvider = Integer.parseInt(AppSettings
			.getRouteProvider());
	private int mZoomLevel = Integer.parseInt(AppSettings.getZoomLevel());
	private int mMapProvider = Integer.parseInt(AppSettings.getMapProvider());
	private JSONArray mPayList = new JSONArray();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		root = (ViewGroup) inflater.inflate(R.layout.layout_two, null);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStopList = (ListView) root.findViewById(R.id.listview);

		mStopListAdapter = new StopListAdapter(mContext, mJsonStopList,
				R.layout.stopsrowlist, new String[] { "stopInformation",
						"message", "_id", "ïdentificationNo", "lon", "lat" },
				new int[] { R.id.stopNo, R.id.stopInfo, R.id._id,
						R.id._identificationNo, R.id.lon, R.id.lat });
		mStopList.setAdapter(mStopListAdapter);

		mMap = (MapView) root.findViewById(R.id.map);
		setmMapProvider(mMapProvider);

		mMap.setBuiltInZoomControls(true);
		mMap.setMultiTouchControls(true);
		MapController mapController = mMap.getController();

		// To use MapEventsReceiver methods, we add a MapEventsOverlay:
		MapEventsOverlay overlay = new MapEventsOverlay(mContext, this);
		mMap.getOverlays().add(overlay);

		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		myLocationOverlay = new DirectedLocationOverlay(mContext);
		mMap.getOverlays().add(myLocationOverlay);

		if (savedInstanceState == null) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null)
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			if (location != null) {
				// location known:
				onLocationChanged(location);
				mapController.setZoom(mZoomLevel);
				mapController.setCenter(new GeoPoint(location));
				mStartPoint = new GeoPoint(location);
			} else {
				// no location known, we put a hard-coded map position:
				myLocationOverlay.setEnabled(false);
				mapController.setZoom(mZoomLevel);
				mapController.setCenter(new GeoPoint(27.9, -82.5));
			}
			mStartPoint = null;
			mDestinationPoint = null;
			mWayPoints = new ArrayList<GeoPoint>();
			// GeoPoint mStartPoint = new GeoPoint(48.13, -1.63);
			// mWayPoints.add(new GeoPoint(48.4, -1.9)); //end point

		} else {
			myLocationOverlay.setLocation((GeoPoint) savedInstanceState
					.getParcelable("location"));
			// TODO: restore other aspects of myLocationOverlay...
			// mStartPoint = savedInstanceState.getParcelable("start");
			// mDestinationPoint =
			// savedInstanceState.getParcelable("destination");
			// mWayPoints = savedInstanceState
			// .getParcelableArrayList("mWayPoints");
			// mapController.setZoom(savedInstanceState.getInt("zoom_level"));
			// mapController.setCenter((GeoPoint) savedInstanceState
			// .getParcelable("map_center"));
		}

		ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mContext);
		mMap.getOverlays().add(scaleBarOverlay);

		final ArrayList<ExtendedOverlayItem> roadItems = new ArrayList<ExtendedOverlayItem>();
		roadNodeMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
				mContext, roadItems, mMap);
		mMap.getOverlays().add(roadNodeMarkers);

		// POI markers:
		final ArrayList<ExtendedOverlayItem> poiItems = new ArrayList<ExtendedOverlayItem>();
		poiMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
				mContext, poiItems, mMap, new POIInfoWindow(mMap));
		mMap.getOverlays().add(poiMarkers);

		final ArrayList<ExtendedOverlayItem> waypointsItems = new ArrayList<ExtendedOverlayItem>();

		itineraryMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
				mContext, waypointsItems, mMap, new ViaPointInfoWindow(
						R.layout.itinerary_bubble, mMap));

		mMap.getOverlays().add(itineraryMarkers);

		// Itinerary markers:
		// final ArrayList<ExtendedOverlayItem> waypointsItems = new
		// ArrayList<ExtendedOverlayItem>();
		// ArrayList<ExtendedOverlayItem> waypointsItems = new
		// ArrayList<ExtendedOverlayItem>();
		//
		// itineraryMarkers = new
		// ItemizedOverlayWithBubble<ExtendedOverlayItem>(
		// mContext, waypointsItems, mMap, new ViaPointInfoWindow(
		// R.layout.itinerary_bubble, mMap));
		//
		// mMap.getOverlays().add(itineraryMarkers);
		// updateUIWithItineraryMarkers();

		// Tracking system:
		mTrackingModeButton = (Button) root
				.findViewById(R.id.buttonTrackingMode);
		mTrackingModeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mTrackingMode = !mTrackingMode;
				updateUIWithTrackingMode();
			}
		});
		if (savedInstanceState != null) {
			mTrackingMode = savedInstanceState.getBoolean("tracking_mode");
			updateUIWithTrackingMode();
		}

		// getRoadAsync();
		// AutoCompleteOnPreferences departureText = (AutoCompleteOnPreferences)
		// findViewById(R.id.editDeparture);
		// departureText.setPrefKeys(SHARED_PREFS_APPKEY, PREF_LOCATIONS_KEY);

		init(root);

		return root;
	}

	// OnButtonPressListener buttonListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// try {
		// buttonListener = (OnButtonPressListener) getActivity();
		// } catch (ClassCastException e) {
		// throw new ClassCastException(activity.toString()
		// + " must implement onButtonPressed");
		// }
	}

	void init(ViewGroup root) {

		// Button but = (Button) root.findViewById(R.id.button2);
		// but.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Message msg = new Message();
		// msg.what = 2;
		// msg. = "Message From Second Fragment";
		// buttonListener.onButtonPressed(msg);
		// }
		// });

	}

	private void setmMapProvider(int mMapProvider) {
		OnlineTileSourceBase tileSource = null;

		switch (mMapProvider) {
		case 1:// :
			tileSource = TileSourceFactory.MAPNIK;
			break;
		case 2:// :
			tileSource = TileSourceFactory.MAPQUESTOSM;
			break;
		case 3:// :
			tileSource = TileSourceFactory.MAPQUESTAERIAL;
			break;
		case 4:// :
			tileSource = TileSourceFactory.ROADS_OVERLAY_NL;
			break;
		case 5:// :
			tileSource = TileSourceFactory.CYCLEMAP;
			break;

		default:
			tileSource = TileSourceFactory.MAPQUESTOSM;
		}

		mMap.setTileSource(tileSource);
	}

	public void setMessage(Message msg) {
		Bundle bundle = msg.getData();
		if (msg.what == Constant.PAYDRIVER) {
			// double len = mRoad.mLength;
			try {
				mPayList = new JSONArray(bundle.getString("message"));

				String title = "DriverSelected";

				String place = "552 W Davis Blvd,Tampa,FL 33606";
				int status = 1;
				long startDate = Calendar.getInstance().getTimeInMillis();
				String addInfo = Calendar.getInstance().getTime()
						.toLocaleString();
				boolean isRemind = true;
				boolean isMailService = true;
				CalendarUtils.addEventToCalendar(mContext, title, addInfo,
						place, status, startDate, isRemind, isMailService);
				// Intent intent =
				// CalendarUtilsIntent.addCalendarEventbyIntent();
				// startActivity(intent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mRoad != null) {
				calculateTripProfit(mRoad.mLength, mRoad.mDuration, mPayList);
			}
			// String test = mPayList.toString();// bundle.getString("message");
			// MainActivity.setToast(test, Toast.LENGTH_SHORT);

		} else if (msg.what == Constant.STOPS) {
			// Bundle bundle = msg.getData();
			ArrayList<HashMap<String, String>> jsonStopList = (ArrayList<HashMap<String, String>>) bundle
					.get("message");
			String rates = bundle.getString("rates");

			if (jsonStopList == null || jsonStopList.size() == 0) {
				return;
			}
			createWayPoints(jsonStopList);
		}
	}

	// void setMessage(ArrayList<HashMap<String, String>> msg) {
	// // TextView txt = (TextView) root.findViewById(R.id.textView1);
	// // txt.setText(msg.obj.toString());
	// // msg.obj.toString()
	//
	// MessageAdapter messageAdapter = new MessageAdapter(mContext, msg,
	// R.layout.messagerowlist,
	// new String[] { "fullname", "message" }, new int[] {
	// R.id.fullname, R.id.message });
	//
	//

	// // intervalAdapter = new IntervalAdapter(mContext, msg,
	// // R.layout.messagerowlist, new String[] { "intervalno",
	// // "pacetext" }, new int[] { R.id.fullname, R.id.message });
	// mStopList.setAdapter(messageAdapter);
	// messageAdapter.notifyDataSetChanged();
	// }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// MainActivity.setToast("LAND", 1);
		} else {
			// MainActivity.setToast("PORT", 1);
		}
	}

	public void createWayPointsOld(
			ArrayList<HashMap<String, String>> jsonStopList) {
		mWayPoints.clear();// Need to clear mWayPoints so we don't get dups
		mJsonStopList.clear();// Clear the jsonStopList
		mJsonStopList.addAll(jsonStopList); // Add stops to use with adapter
		int size = jsonStopList.size();
		for (int stops = 0; stops < size; stops++) {
			HashMap<String, String> stop = jsonStopList.get(stops);
			int lon = Integer.parseInt(stop.get("lon"));
			int lat = Integer.parseInt(stop.get("lat"));
			String identificationNo = stop.get("identificationNo");// Not
																	// currently
																	// used
																	// but
																	// will
																	// in
																	// future
			GeoPoint point = new GeoPoint(lat, lon);
			if (point != null) {
				mDestinationPoint = point;
			}
			if (stops == 0) {
				// if (mStartPoint == null) {
				mStartPoint = point;
				// }
				MapController mapController = mMap.getController();
				mapController.setCenter(mStartPoint);
				mapController.setZoom(mZoomLevel);
				// } else if (stops == size) {
				// if (point != null) {
				// mDestinationPoint = point;
				// }
			} else {
				mWayPoints.add(point);
			}
		}
		// Probably dont want to do this here until had chance to add
		// distances...possibly after getRoadAsync so can get distances between
		// legs after route is run
		// mStopListAdapter.notifyDataSetChanged();
		// Route and Directions
		final ArrayList<ExtendedOverlayItem> roadItems = new ArrayList<ExtendedOverlayItem>();
		roadNodeMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
				mContext, roadItems, mMap);
		mMap.getOverlays().add(roadNodeMarkers);

		// POI markers:
		final ArrayList<ExtendedOverlayItem> poiItems = new ArrayList<ExtendedOverlayItem>();
		poiMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
				mContext, poiItems, mMap, new POIInfoWindow(mMap));
		mMap.getOverlays().add(poiMarkers);

		getRoadAsync();
		mStopListAdapter.notifyDataSetChanged();
	}

	/*
	 * createWayPoints -
	 */
	public void createWayPoints(ArrayList<HashMap<String, String>> jsonStopList) {
		mWayPoints.clear();// Need to clear mWayPoints so we don't get dups
		mJsonStopList.clear();// Clear the jsonStopList
		mJsonStopList.addAll(jsonStopList); // Add stops to use with adapter
		int size = jsonStopList.size();
		for (int stops = 0; stops < size; stops++) {
			HashMap<String, String> stop = jsonStopList.get(stops);
			int lon = Integer.parseInt(stop.get("lon"));
			int lat = Integer.parseInt(stop.get("lat"));

			String identificationNo = stop.get("identificationNo");// Not
																	// currently
																	// used
																	// but
																	// will
																	// in
																	// future
			GeoPoint point = new GeoPoint(lat, lon);
			if (point != null) {
				mDestinationPoint = point;
			}

			if (stops == 0) {
				mStartPoint = point;
				MapController mapController = mMap.getController();
				mapController.setCenter(mStartPoint);
				mapController.setZoom(mZoomLevel);
			}
			mWayPoints.add(point);
		}
		// Probably dont want to do this here until had chance to add
		// distances...possibly after getRoadAsync so can get distances between
		// legs after route is run
		// mStopListAdapter.notifyDataSetChanged();
		// Route and Directions
		List<Overlay> mapOverlays = mMap.getOverlays();
		if (roadOverlay != null) {
			mapOverlays.remove(roadOverlay);
		}

		if (roadNodeMarkers != null) {
			roadNodeMarkers.removeAllItems();
		}

		if (itineraryMarkers != null) {
			itineraryMarkers.removeAllItems();
		}

		// final ArrayList<ExtendedOverlayItem> roadItems = new
		// ArrayList<ExtendedOverlayItem>();
		// roadNodeMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
		// mContext, roadItems, mMap);
		// mMap.getOverlays().add(roadNodeMarkers);
		//
		// // POI markers:
		// final ArrayList<ExtendedOverlayItem> poiItems = new
		// ArrayList<ExtendedOverlayItem>();
		// poiMarkers = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(
		// mContext, poiItems, mMap, new POIInfoWindow(mMap));
		// mMap.getOverlays().add(poiMarkers);
		//
		// final ArrayList<ExtendedOverlayItem> waypointsItems = new
		// ArrayList<ExtendedOverlayItem>();
		//
		// itineraryMarkers = new
		// ItemizedOverlayWithBubble<ExtendedOverlayItem>(
		// mContext, waypointsItems, mMap, new ViaPointInfoWindow(
		// R.layout.itinerary_bubble, mMap));
		//
		// mMap.getOverlays().add(itineraryMarkers);
		// updateUIWithItineraryMarkers();

		getRoadAsync();
		mStopListAdapter.notifyDataSetChanged();
	}

	public void getRoadAsync() {
		mRoad = null;
		new UpdateRoadTask().execute(mWayPoints);
	}

	public void getRoadAsyncOld() {
		mRoad = null;
		GeoPoint roadmStartPoint = null;
		if (mStartPoint != null) {
			roadmStartPoint = mStartPoint;
		} else if (myLocationOverlay.isEnabled()
				&& myLocationOverlay.getLocation() != null) {
			// use my current location as itinerary start point:
			roadmStartPoint = myLocationOverlay.getLocation();
		}
		if (roadmStartPoint == null || mDestinationPoint == null) {
			updateUIWithRoad(mRoad);
			return;
		}
		ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>(2);
		waypoints.add(roadmStartPoint);
		// add intermediate via points:
		for (GeoPoint p : mWayPoints) {
			waypoints.add(p);
		}
		waypoints.add(mDestinationPoint);
		new UpdateRoadTask().execute(waypoints);
	}

	public void calculateTripProfit(double length, double duration,
			JSONArray payList) {
		// Set route info in the text view:
		TextView routeInfo = (TextView) root.findViewById(R.id.routeInfo);
		TextView tripInfo = (TextView) root.findViewById(R.id.tripInfo);
		routeInfo.setText("");
		tripInfo.setText("");
		// Get the Unit of Measurement
		String uom = AppSettings.getUom().contentEquals("1") ? "mi" : "km";
		length = uom.contentEquals("mi") ? length * Constant.KMTOMILES : length;
		int roadLength;
		if (length >= 100.0) {
			roadLength = (int) (length);
		} else if (length >= 1.0) {
			roadLength = (int) (Math.round(length * 10) / 10.0);
		} else {
			roadLength = (int) (length * 1000);
		}

		double tripCost = 0;
		double tripRevenue = calculateTripRevenue(roadLength, duration, uom);
		try {
			tripCost = calculateTripCost(roadLength, duration, uom, payList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Show profit/loss info
		NumberFormat format = NumberFormat.getCurrencyInstance();
		double profit = tripRevenue - tripCost;
		if (profit > 0) {
			tripInfo.setTextColor(getResources().getColor(R.color.green));
		} else {
			tripInfo.setTextColor(getResources().getColor(R.color.red));
		}
		;
		tripInfo.setText("Profit=" + format.format(tripRevenue) + " - "
				+ format.format(tripCost) + "=" + format.format(profit));

		// routeInfo.setText(mRoad.getLengthDurationText(-1));
		routeInfo.setText(getLengthDurationText(roadLength, duration, uom));
		// routeInfo.setText(Double.toString(mRoad.mLegs.get(1).mLength)+":"+Double.toString(mRoad.mLegs.get(1).mDuration));

	}

	/**
	 * @param length
	 *            in km
	 * @param duration
	 *            in sec
	 * @return a human-readable length&duration text.
	 */
	public String getLengthDurationText(int length, double duration, String uom) {
		String result;

		result = length + uom + ", ";

		int totalSeconds = (int) duration;
		int hours = totalSeconds / 3600;
		int minutes = (totalSeconds / 60) - (hours * 60);
		int seconds = (totalSeconds % 60);
		if (hours != 0) {
			result += hours + "h ";
		}
		if (minutes != 0) {
			result += minutes + "min ";
		}
		if (hours == 0 && minutes == 0) {
			result += seconds + "sec";
		}
		return result;
	}

	public double calculateTripRevenue(int length, double time, String uom) {
		double revenue = 0;
		// int roadLength = 0;
		int agreedUponMiles = 0;
		double ratePerMile = 1.95;
		double stopCharge = 65.00;
		double accessorialCharge = 5.00;
		double fuelSurCharge = .25;// fsc per mile
		boolean includeFirstStop = true;
		if (agreedUponMiles < 1) {
			agreedUponMiles = length;
		}
		// Calculate no of stops
		int noOfStops = (includeFirstStop) ? mJsonStopList.size()
				: mJsonStopList.size() - 1;
		revenue = (agreedUponMiles * ratePerMile)
				+ (agreedUponMiles * fuelSurCharge) + (noOfStops * stopCharge);

		return revenue;
	}

	public double calculateTripCost(int length, double time, String uom,
			JSONArray driverPay) throws JSONException {
		// int roadLength=length;
		double costPerTrip;

		int size = mPayList.length();
		double ratePerUom = 0;
		int numberOfStops = mJsonStopList.size();
		double ratePerStop = 0;
		double fuelCostPerUom = .67;
		for (int i = 0; i < size; i++) {
			JSONObject pay = (JSONObject) mPayList.get(i);
			if (pay.getString("type").contentEquals("pm")) {
				ratePerUom = pay.getDouble("amount");
			}
			if (pay.getString("type").contentEquals("stop")) {
				ratePerStop = pay.getDouble("amount");
			}

		}
		costPerTrip = (length * ratePerUom) + (numberOfStops * ratePerStop)
				+ (length * fuelCostPerUom);
		return costPerTrip;
	}

	void updateUIWithRoad(Road road) {
		roadNodeMarkers.removeAllItems();
		// TextView routeInfo = (TextView) root.findViewById(R.id.routeInfo);
		// TextView tripInfo = (TextView) root.findViewById(R.id.tripInfo);
		// routeInfo.setText("");
		// tripInfo.setText("");
		List<Overlay> mapOverlays = mMap.getOverlays();
		if (roadOverlay != null) {
			mapOverlays.remove(roadOverlay);
		}
		if (road == null)
			return;
		if (road.mStatus == Road.STATUS_DEFAULT)
			MainActivity.setToast("Route Not Available", Toast.LENGTH_SHORT);
		roadOverlay = RoadManager.buildRoadOverlay(road, mMap.getContext());
		Overlay removedOverlay = mapOverlays.set(1, roadOverlay);
		// we set the road overlay at the "bottom", just above the
		// MapEventsOverlay,
		// to avoid covering the other overlays.
		mapOverlays.add(removedOverlay);
		putRoadNodes(road);
		mMap.invalidate();
		calculateTripProfit(road.mLength, road.mDuration, mPayList);
		// // Set route info in the text view:
		// double tripCost = 0;
		// double tripRevenue = calculateTripRevenue(road.mLength,
		// road.mDuration);
		// try {
		// tripCost = calculateTripCost(road.mLength, road.mDuration, mPayList);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// // Show profit/loss info
		// NumberFormat format = NumberFormat.getCurrencyInstance();
		// double profit = tripRevenue - tripCost;
		// if (profit > 0) {
		// tripInfo.setTextColor(getResources().getColor(R.color.green));
		// } else {
		// tripInfo.setTextColor(getResources().getColor(R.color.red));
		// }
		// ;
		// tripInfo.setText("Profit=" + format.format(tripRevenue) + " - "
		// + format.format(tripCost) + "=" + format.format(profit));
		//
		// routeInfo.setText(road.getLengthDurationText(-1));
	}

	// Async task to reverse-geocode the marker position in a separate thread:
	private class GeocodingTask extends AsyncTask<Object, Void, String> {
		ExtendedOverlayItem marker;

		@Override
		protected String doInBackground(Object... params) {
			marker = (ExtendedOverlayItem) params[0];
			return getAddress(marker.getPoint());
		}

		@Override
		protected void onPostExecute(String result) {
			marker.setDescription(result);
			itineraryMarkers.showBubbleOnItem(marker, mMap, false); // open
																	// bubble on
																	// the item
		}
	}

	void updateUIWithTrackingMode() {
		if (mTrackingMode) {
			mTrackingModeButton
					.setBackgroundResource(R.drawable.btn_tracking_on);
			if (myLocationOverlay.isEnabled()
					&& myLocationOverlay.getLocation() != null) {
				mMap.getController().animateTo(myLocationOverlay.getLocation());
			}
			mMap.setMapOrientation(-mAzimuthAngleSpeed);
			mTrackingModeButton.setKeepScreenOn(true);
		} else {
			mTrackingModeButton
					.setBackgroundResource(R.drawable.btn_tracking_off);
			mMap.setMapOrientation(0.0f);
			mTrackingModeButton.setKeepScreenOn(false);
		}
	}

	GeocoderNominatim geocoder = new GeocoderNominatim(mContext);

	// ------------- Geocoding and Reverse Geocoding

	/**
	 * Reverse Geocoding
	 */
	public String getAddress(GeoPoint p) {
		String theAddress;
		try {
			double dLatitude = p.getLatitudeE6() * 1E-6;
			double dLongitude = p.getLongitudeE6() * 1E-6;
			List<Address> addresses = geocoder.getFromLocation(dLatitude,
					dLongitude, 1);
			StringBuilder sb = new StringBuilder();
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				int n = address.getMaxAddressLineIndex();
				for (int i = 0; i <= n; i++) {
					if (i != 0)
						sb.append(", ");
					sb.append(address.getAddressLine(i));
				}
				theAddress = new String(sb.toString());
			} else {
				theAddress = null;
			}
		} catch (IOException e) {
			theAddress = null;
		}
		if (theAddress != null) {
			return theAddress;
		} else {
			return "";
		}
	}

	// ------------ Itinerary markers

	/**
	 * add (or replace) an item in markerOverlays. p position.
	 */
	public ExtendedOverlayItem putMarkerItem(ExtendedOverlayItem item,
			GeoPoint p, int index, int titleResId, int markerResId,
			int iconResId) {
		if (item != null) {
			itineraryMarkers.removeItem(item);
		}
		Drawable marker = getResources().getDrawable(markerResId);
		String title = getResources().getString(titleResId);
		ExtendedOverlayItem overlayItem = new ExtendedOverlayItem(title, "", p,
				mContext);
		overlayItem.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
		overlayItem.setMarker(marker);
		if (iconResId != -1)
			overlayItem.setImage(getResources().getDrawable(iconResId));
		overlayItem.setRelatedObject(index);
		itineraryMarkers.addItem(overlayItem);
		mMap.invalidate();
		// Start geocoding task to update the description of the marker with its
		// address:
		new GeocodingTask().execute(overlayItem);
		return overlayItem;
	}

	public void updateUIWithItineraryMarkers() {
		itineraryMarkers.removeAllItems();
		// Start marker:
		if (mStartPoint != null) {
			markerStart = putMarkerItem(null, mStartPoint, START_INDEX,
					R.string.departure, R.drawable.marker_departure, -1);
		}
		// Via-points markers if any:
		for (int index = 0; index < mWayPoints.size() - 1; index++) {
			putMarkerItem(null, mWayPoints.get(index), index,
					R.string.viapoint, R.drawable.marker_via, -1);
		}
		// Destination marker if any:
		if (mDestinationPoint != null) {
			markerDestination = putMarkerItem(null, mDestinationPoint,
					DEST_INDEX, R.string.destination,
					R.drawable.marker_destination, -1);
		}
	}

	// ------------ Route and Directions

	private void putRoadNodes(Road road) {
		roadNodeMarkers.removeAllItems();
		Drawable marker = getResources().getDrawable(R.drawable.marker_node);
		int n = road.mNodes.size();
		TypedArray iconIds = getResources().obtainTypedArray(
				R.array.direction_icons);
		for (int i = 0; i < n; i++) {
			RoadNode node = road.mNodes.get(i);
			String instructions = (node.mInstructions == null ? ""
					: node.mInstructions);

			ExtendedOverlayItem nodeMarker = new ExtendedOverlayItem("Step "
					+ (i + 1), instructions, node.mLocation, mContext);
			nodeMarker.setSubDescription(road.getLengthDurationText(
					node.mLength, node.mDuration));
			nodeMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
			nodeMarker.setMarker(marker);
			int iconId = iconIds.getResourceId(node.mManeuverType,
					R.drawable.ic_empty);
			if (iconId != R.drawable.ic_empty) {
				Drawable icon = getResources().getDrawable(iconId);
				nodeMarker.setImage(icon);
			}
			roadNodeMarkers.addItem(nodeMarker);
		}
	}

	/**
	 * Async task to get the road in a separate thread.
	 */
	private class UpdateRoadTask extends AsyncTask<Object, Void, Road> {
		@Override
		protected Road doInBackground(Object... params) {
			@SuppressWarnings("unchecked")
			ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
			RoadManager roadManager = null;
			Locale locale = Locale.getDefault();
			switch (mRouteProvider) {
			case 0:// OSRM:
				roadManager = new OSRMRoadManager();
				break;
			case 1:// MAPQUEST_FASTEST:
				roadManager = new MapQuestRoadManager();
				roadManager.addRequestOption("locale=" + locale.getLanguage()
						+ "_" + locale.getCountry());
				break;
			case 2:// MAPQUEST_BICYCLE:
				roadManager = new MapQuestRoadManager();
				roadManager.addRequestOption("locale=" + locale.getLanguage()
						+ "_" + locale.getCountry());
				roadManager.addRequestOption("routeType=bicycle");
				break;
			case 3:// MAPQUEST_PEDESTRIAN:
				roadManager = new MapQuestRoadManager();
				roadManager.addRequestOption("locale=" + locale.getLanguage()
						+ "_" + locale.getCountry());
				roadManager.addRequestOption("routeType=pedestrian");
				break;
			case 4:// GOOGLE_FASTEST:
				roadManager = new GoogleRoadManager();
				// roadManager.addRequestOption("mode=driving"); //default
				break;
			default:
				return null;
			}
			return roadManager.getRoad(waypoints);
		}

		@Override
		protected void onPostExecute(Road result) {
			mRoad = result;
			updateUIWithRoad(result);
			updateUIWithItineraryMarkers();
			// mStopListAdapter.notifyDataSetChanged();
			// reb getPOIAsync(poiTagText.getText().toString());
		}
	}

	void getPOIAsync(String tag) {
		poiMarkers.removeAllItems();
		new POITask().execute(tag);
	}

	private class POITask extends AsyncTask<Object, Void, ArrayList<POI>> {
		String mTag;

		@Override
		protected ArrayList<POI> doInBackground(Object... params) {
			mTag = (String) params[0];

			if (mTag == null || mTag.equals("")) {
				return null;
			} else if (mTag.equals("wikipedia")) {
				GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider(
						"mkergall");
				// ArrayList<POI> pois = poiProvider.getPOICloseTo(point, 30,
				// 20.0);
				// Get POI inside the bounding box of the current map view:
				BoundingBoxE6 bb = mMap.getBoundingBox();
				ArrayList<POI> pois = poiProvider.getPOIInside(bb, 30);
				return pois;
			} else if (mTag.equals("flickr")) {
				FlickrPOIProvider poiProvider = new FlickrPOIProvider(
						"c39be46304a6c6efda8bc066c185cd7e");
				BoundingBoxE6 bb = mMap.getBoundingBox();
				ArrayList<POI> pois = poiProvider.getPOIInside(bb, 30);
				return pois;
			} else if (mTag.startsWith("picasa")) {
				PicasaPOIProvider poiProvider = new PicasaPOIProvider(null);
				BoundingBoxE6 bb = mMap.getBoundingBox();
				// allow to search for keywords among picasa photos:
				String q = mTag.substring("picasa".length());
				ArrayList<POI> pois = poiProvider.getPOIInside(bb, 30, q);
				return pois;
			} else {
				NominatimPOIProvider poiProvider = new NominatimPOIProvider();
				// poiProvider.setService(NominatimPOIProvider.MAPQUEST_POI_SERVICE);
				ArrayList<POI> pois;
				if (mRoad == null) {
					BoundingBoxE6 bb = mMap.getBoundingBox();
					pois = poiProvider.getPOIInside(bb, mTag, 100);
				} else {
					pois = poiProvider.getPOIAlong(mRoad.getRouteLow(), mTag,
							100, 2.0);
				}
				return pois;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<POI> pois) {
			mPOIs = pois;
			if (mTag.equals("")) {
				// no search, no message
			} else if (mPOIs == null) {

				MainActivity.setToast("Technical issue when getting " + mTag
						+ " POI.", Toast.LENGTH_LONG);
			} else {
				MainActivity.setToast("" + mPOIs.size() + " " + mTag
						+ " entries found", Toast.LENGTH_LONG);
				if (mTag.equals("flickr") || mTag.startsWith("picasa")
						|| mTag.equals("wikipedia"))
					startAsyncThumbnailsLoading(mPOIs);
			}
			updateUIWithPOI(mPOIs);
		}
	}

	// ----------------- POIs

	void updateUIWithPOI(ArrayList<POI> pois) {
		if (pois != null) {
			for (POI poi : pois) {
				ExtendedOverlayItem poiMarker = new ExtendedOverlayItem(
						poi.mType, poi.mDescription, poi.mLocation, mContext);
				Drawable marker = null;
				if (poi.mServiceId == POI.POI_SERVICE_NOMINATIM) {
					marker = getResources().getDrawable(
							R.drawable.marker_poi_default);
				} else if (poi.mServiceId == POI.POI_SERVICE_GEONAMES_WIKIPEDIA) {
					if (poi.mRank < 90)
						marker = getResources().getDrawable(
								R.drawable.marker_poi_wikipedia_16);
					else
						marker = getResources().getDrawable(
								R.drawable.marker_poi_wikipedia_32);
				} else if (poi.mServiceId == POI.POI_SERVICE_FLICKR) {
					marker = getResources().getDrawable(
							R.drawable.marker_poi_flickr);
				} else if (poi.mServiceId == POI.POI_SERVICE_PICASA) {
					marker = getResources().getDrawable(
							R.drawable.marker_poi_picasa_24);
					poiMarker.setSubDescription(poi.mCategory);
				}
				poiMarker.setMarker(marker);
				poiMarker.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
				// thumbnail loading moved in POIInfoWindow.onOpen for better
				// performances.
				poiMarker.setRelatedObject(poi);
				poiMarkers.addItem(poiMarker);
			}
		}
		mMap.invalidate();
	}

	ExecutorService mThreadPool = Executors.newFixedThreadPool(5);

	/** Loads all thumbnails in background */
	void startAsyncThumbnailsLoading(ArrayList<POI> pois) {
		/*
		 * Try to stop existing threads: not sure it has any effect... if
		 * (mThreadPool != null){ //Stop threads if any:
		 * mThreadPool.shutdownNow(); } mThreadPool =
		 * Executors.newFixedThreadPool(5);
		 */
		for (int i = 0; i < pois.size(); i++) {
			final int index = i;
			final POI poi = pois.get(index);
			mThreadPool.submit(new Runnable() {
				@Override
				public void run() {
					Bitmap b = poi.getThumbnail();
					if (b != null) {
						/*
						 * //Change POI marker: ExtendedOverlayItem item =
						 * poiMarkers.getItem(index); b =
						 * Bitmap.createScaledBitmap(b, 48, 48, true);
						 * BitmapDrawable bd = new
						 * BitmapDrawable(getResources(), b);
						 * item.setMarker(bd);
						 */
					}
				}
			});
		}
	}

	// ------------ SensorEventListener implementation
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		myLocationOverlay.setAccuracy(accuracy);
		mMap.invalidate();
	}

	static float mAzimuthOrientation = 0.0f;

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ORIENTATION:
			if (mSpeed < 0.1) {
				/*
				 * TODO Filter to implement... float azimuth = event.values[0];
				 * if (Math.abs(azimuth-mAzimuthOrientation)>2.0f){
				 * mAzimuthOrientation = azimuth;
				 * myLocationOverlay.setBearing(mAzimuthOrientation); if
				 * (mTrackingMode) map.setMapOrientation(-mAzimuthOrientation);
				 * else map.invalidate(); }
				 */
			}
			// at higher speed, we use speed vector, not phone orientation.
			break;
		default:
			break;
		}
	}

	// ------------ LocationListener implementation
	private final NetworkLocationIgnorer mIgnorer = new NetworkLocationIgnorer();
	long mLastTime = 0; // milliseconds
	double mSpeed = 0.0; // km/h

	@Override
	public void onLocationChanged(final Location pLoc) {
		long currentTime = System.currentTimeMillis();
		if (mIgnorer.shouldIgnore(pLoc.getProvider(), currentTime))
			return;
		double dT = currentTime - mLastTime;
		if (dT < 100.0) {
			// Toast.makeText(this, pLoc.getProvider()+" dT="+dT,
			// Toast.LENGTH_SHORT).show();
			return;
		}
		mLastTime = currentTime;

		GeoPoint newLocation = new GeoPoint(pLoc);
		if (!myLocationOverlay.isEnabled()) {
			// we get the location for the first time:
			myLocationOverlay.setEnabled(true);
			mMap.getController().animateTo(newLocation);
		}

		GeoPoint prevLocation = myLocationOverlay.getLocation();
		myLocationOverlay.setLocation(newLocation);
		myLocationOverlay.setAccuracy((int) pLoc.getAccuracy());

		if (prevLocation != null
				&& pLoc.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			/*
			 * double d = prevLocation.distanceTo(newLocation); mSpeed =
			 * d/dT*1000.0; // m/s mSpeed = mSpeed * 3.6; //km/h
			 */
			mSpeed = pLoc.getSpeed() * 3.6;
			long speedInt = Math.round(mSpeed);
			TextView speedTxt = (TextView) root.findViewById(R.id.speed);
			speedTxt.setText(speedInt + " km/h");

			// TODO: check if speed is not too small
			if (mSpeed >= 0.1) {
				// mAzimuthAngleSpeed =
				// (float)prevLocation.bearingTo(newLocation);
				mAzimuthAngleSpeed = pLoc.getBearing();
				myLocationOverlay.setBearing(mAzimuthAngleSpeed);
			}
		}

		if (mTrackingMode) {
			// keep the map view centered on current location:
			mMap.getController().animateTo(newLocation);
			mMap.setMapOrientation(-mAzimuthAngleSpeed);
		} else {
			// just redraw the location overlay:
			mMap.invalidate();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean longPressHelper(IGeoPoint arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean singleTapUpHelper(IGeoPoint arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}