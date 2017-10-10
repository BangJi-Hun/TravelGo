package jihun.travelgo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutCustomOverlay;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

public class SightsActivity extends NMapActivity {

    private static final String LOG_TAG = "NMapViewer";
    private static final boolean DEBUG = false;

    private MapContainerView mMapContainerView;

    private NMapView mMapView;
    private NMapController mMapController = null;

    private static final NGeoPoint NMAP_LOCATION_DEFAULT = new NGeoPoint(128.3874624, 36.1323454);
    private static final int NMAP_ZOOMLEVEL_DEFAULT = 11;
    private static final int NMAP_VIEW_MODE_DEFAULT = NMapView.VIEW_MODE_VECTOR;
    private static final boolean NMAP_TRAFFIC_MODE_DEFAULT = false;
    private static final boolean NMAP_BICYCLE_MODE_DEFAULT = false;

    private static final String KEY_ZOOM_LEVEL = "NMapViewer.zoomLevel";
    private static final String KEY_CENTER_LONGITUDE = "NMapViewer.centerLongitudeE6";
    private static final String KEY_CENTER_LATITUDE = "NMapViewer.centerLatitudeE6";
    private static final String KEY_VIEW_MODE = "NMapViewer.viewMode";
    private static final String KEY_TRAFFIC_MODE = "NMapViewer.trafficMode";
    private static final String KEY_BICYCLE_MODE = "NMapViewer.bicycleMode";

    private SharedPreferences mPreferences;

    private NMapOverlayManager mOverlayManager;

    private NMapMyLocationOverlay mMyLocationOverlay;
    private NMapLocationManager mMapLocationManager;
    private NMapCompassManager mMapCompassManager;

    private jihun.travelgo.NMapViewerResourceProvider mMapViewerResourceProvider;

    private NMapPOIdataOverlay mFloatingPOIdataOverlay;
    private NMapPOIitem mFloatingPOIitem;

    //GPS 기능
    private Location lastKnownLocation = null;
    static double nowLat, nowLong;

    // 현재 클릭된 명소
    static String nowSights;

    // 검색 명소
    static String searchSights = null, searchLat = null, searchLo = null;
    static int searchFlag = 0;

    //정복
    private static int conClear=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 레이아웃 초기화
        setContentView(R.layout.activity_sights);
        mMapView = (NMapView) findViewById(R.id.sightsMap);

        // API 키 등록
        mMapView.setApiKey("mlN7zvjpvn3oKAFZ9SWp");

        // initialize map view
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        // register listener for map state changes
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
        mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
        mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

        // use map controller to zoom in/out, pan and set map center, zoom level etc.
        mMapController = mMapView.getMapController();

        // use built in zoom controls
        NMapView.LayoutParams lp = new NMapView.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
        mMapView.setBuiltInZoomControls(true, lp);

        // create resource provider
        mMapViewerResourceProvider = new jihun.travelgo.NMapViewerResourceProvider(this);

        // set data provider listener
        super.setMapDataProviderListener(onDataProviderListener);

        // create overlay manager
        mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
        // register callout overlay listener to customize it.
        mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
        // register callout overlay view listener to customize it.
        mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

        // location manager
        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

        // create my location overlay
        mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);

        //명소 마크
        sightOverlay();

        //GPS 정복기능
        Button btnConquest = (Button) findViewById(R.id.btn_conquest);
        btnConquest.setOnClickListener(btnConquestListener);
        btnConquest.setEnabled(false);

        //명소 검색
        Button btnSightsSearch = (Button) findViewById(R.id.btn_sights_search);
        btnSightsSearch.setOnClickListener(btnSishgtsSearchListener);

        Intent intent = getIntent();
        searchSights = intent.getStringExtra("searchSights");
        searchFlag = intent.getIntExtra("searchFlag", 0);
        conClear = intent.getIntExtra("conClear", 0)

        //기본 위치
        mMapController.setMapCenter(new NGeoPoint(128.392750, 36.145837), 14);
        mMapController.animateTo(new NGeoPoint(128.392750, 36.145837));

        //정복 화면
        /*
        if(conClear == 1)
        {
        }
        */
    }

    private View.OnClickListener btnConquestListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(SightsActivity.this, ConquestActivity.class);
            i.putExtra("sightsName", nowSights);
            startActivity(i);
        }
    };

    private View.OnClickListener btnSishgtsSearchListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(SightsActivity.this, SearchActivity.class);
            // 명소 배열
            String[] products = {"금오공대 디지털관", "금오산 명금폭포", "금오랜드", "박정희 대통령 생가", "구미역", "동락공원"};

            i.putExtra("products", products);
            startActivity(i);
            finish();
        }
    };

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            lastKnownLocation = location;
            nowLat = lastKnownLocation.getLatitude();
            nowLong = lastKnownLocation.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };

    // 현재 위치 찾기 시작
    private void startMyLocation() {

        if (mMyLocationOverlay != null) {
            if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                mOverlayManager.addOverlay(mMyLocationOverlay);
            }

            if (mMapLocationManager.isMyLocationEnabled()) {

                if (!mMapView.isAutoRotateEnabled()) {
                    mMyLocationOverlay.setCompassHeadingVisible(true);

                    mMapView.setAutoRotateEnabled(true, false);
                    nowLat = lastKnownLocation.getLatitude();
                    nowLong = lastKnownLocation.getLongitude();
                    mMapContainerView.requestLayout();
                } else {
                    stopMyLocation();
                }

                mMapView.postInvalidate();
            } else {
                boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                if (!isMyLocationEnabled) {
                    Toast.makeText(SightsActivity.this, "설정에서 위치기능을 확인해주세요",
                            Toast.LENGTH_LONG).show();

                    Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(goToSettings);

                    return;
                }
            }
        }
    }

    //현재 위치 찾기 중지
    private void stopMyLocation() {
        if (mMyLocationOverlay != null) {
            mMapLocationManager.disableMyLocation();

            if (mMapView.isAutoRotateEnabled()) {
                mMyLocationOverlay.setCompassHeadingVisible(false);

                mMapCompassManager.disableCompass();

                mMapView.setAutoRotateEnabled(false, false);

                mMapContainerView.requestLayout();
            }
        }
    }


    // 명소 마크
    private void sightOverlay() {
        int markerId = jihun.travelgo.NMapPOIflagType.PIN;

        //POI 아이템 관리 클래스 생성(전체 아이템 수, NMapResourceProvider 상속 클래스)
        NMapPOIdata poiData = new NMapPOIdata(2, mMapViewerResourceProvider);
        poiData.beginPOIdata(2); //추가 시작
        NMapPOIitem item = poiData.addPOIitem(36.1323454, 128.3874624, "금오공대 디지털관", markerId, 0);
        item.setRightAccessory(true, jihun.travelgo.NMapPOIflagType.CLICKABLE_ARROW);
        poiData.addPOIitem(128.392539, 36.145832, "금오공대 디지털관", markerId, 0);
        poiData.addPOIitem(128.296935, 36.102989, "금오산 명금폭포", markerId, 0);
        poiData.addPOIitem(128.316206, 36.113073, "금오랜드", markerId, 0);
        poiData.addPOIitem(128.348731, 36.088558, "박정희 대통령 생가", markerId, 0);
        poiData.addPOIitem(128.330718, 36.128479, "구미역", markerId, 0);
        poiData.addPOIitem(128.401340, 36.098034, "동락공원", markerId, 0);
        poiData.endPOIdata(); //추가 종료

        // POI data overlay 객체 생성(여러 개의 오버레이 아이템을 포함할 수 있는 오버레이 클래스)
        NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);

        // set event listener to the overlay
        poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener);

        // select an item
        poiDataOverlay.selectPOIitem(0, true);

        //모든 POI 데이터를 화면에 표시(zomLevel)
        //poiDataOverlay.showAllPOIdata(0);
    }

    /* NMapDataProvider Listener */
    private final OnDataProviderListener onDataProviderListener = new OnDataProviderListener() {

        @Override
        public void onReverseGeocoderResponse(NMapPlacemark placeMark, NMapError errInfo) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onReverseGeocoderResponse: placeMark="
                        + ((placeMark != null) ? placeMark.toString() : null));
            }

            if (errInfo != null) {
                Log.e(LOG_TAG, "Failed to findPlacemarkAtLocation: error=" + errInfo.toString());

                Toast.makeText(SightsActivity.this, errInfo.toString(), Toast.LENGTH_LONG).show();
                return;
            }

            if (mFloatingPOIitem != null && mFloatingPOIdataOverlay != null) {
                mFloatingPOIdataOverlay.deselectFocusedPOIitem();

                if (placeMark != null) {
                    mFloatingPOIitem.setTitle(placeMark.toString());
                }
                mFloatingPOIdataOverlay.selectPOIitemBy(mFloatingPOIitem.getId(), false);
            }
        }
    };

    /* MyLocation Listener */
    private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

        @Override
        public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {

            nowLat = myLocation.getLatitude();
            nowLong = myLocation.getLongitude();

            if (searchFlag == 1) {
                SightsList sl = new SightsList();
                int sightCount = sl.findSightsArray(searchSights);
                mMapController.animateTo(new NGeoPoint(sl.getLo(sightCount), sl.getLat(sightCount)));
            } else {
                if (mMapController != null) {
                    mMapController.animateTo(myLocation);
                }
            }

            return true;
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

            Toast.makeText(SightsActivity.this, "현재 위치를 찾고 있습니다", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {
            Toast.makeText(SightsActivity.this, "현재 위치를 알 수 없습니다", Toast.LENGTH_LONG).show();
            stopMyLocation();
        }
    };

    /* MapView State Change Listener*/
    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

        @Override
        public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

            if (errorInfo == null) { // success
                SightsList sl = new SightsList();

                if (searchFlag == 1) {
                    int sightCount = sl.findSightsArray(searchSights);
                    mMapController.setMapCenter(new NGeoPoint(sl.getLo(sightCount), sl.getLat(sightCount)), 14);
                    mMapController.animateTo(new NGeoPoint(sl.getLo(sightCount), sl.getLat(sightCount)));
                } else {
                    restoreInstanceState();
                    startMyLocation();
                }

            } else { // fail
                Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

                Toast.makeText(SightsActivity.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
            }
        }

        @Override
        public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
            }
        }

        @Override
        public void onZoomLevelChange(NMapView mapView, int level) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
            }
        }

        @Override
        public void onMapCenterChangeFine(NMapView mapView) {
        }
    };

    private final NMapView.OnMapViewTouchEventListener onMapViewTouchEventListener = new NMapView.OnMapViewTouchEventListener() {

        @Override
        public void onLongPress(NMapView mapView, MotionEvent ev) {
        }

        @Override
        public void onLongPressCanceled(NMapView mapView) {
        }

        @Override
        public void onSingleTapUp(NMapView mapView, MotionEvent ev) {
        }

        @Override
        public void onTouchDown(NMapView mapView, MotionEvent ev) {
        }

        @Override
        public void onScroll(NMapView mapView, MotionEvent e1, MotionEvent e2) {
        }

        @Override
        public void onTouchUp(NMapView mapView, MotionEvent ev) {
        }

    };

    private final NMapView.OnMapViewDelegate onMapViewTouchDelegate = new NMapView.OnMapViewDelegate() {

        @Override
        public boolean isLocationTracking() {
            if (mMapLocationManager != null) {
                if (mMapLocationManager.isMyLocationEnabled()) {
                    return mMapLocationManager.isMyLocationFixed();
                }
            }
            return false;
        }

    };

    /* POI data State Change Listener*/
    private final NMapPOIdataOverlay.OnStateChangeListener onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

        @Override
        public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onCalloutClick: title=" + item.getTitle());
            }

            Button Btn = (Button) findViewById(R.id.btn_conquest);

            // 오버레이 타이틀 클릭 이벤트
            NGeoPoint point = item.getPoint();
            if (nowLat >= point.latitude - 0.001 && nowLat <= point.latitude + 0.001 &&
                    nowLong >= point.longitude - 0.001 && nowLong <= point.longitude + 0.001) {
                Btn.setEnabled(true);
                Btn.setBackgroundResource(R.drawable.ic_conquest);
                nowSights = item.getTitle();
                Toast.makeText(SightsActivity.this, "정복 가능합니다!", Toast.LENGTH_LONG).show();
            } else {
                Btn.setEnabled(false);
                Btn.setBackgroundResource(R.drawable.ic_conoff);
                Toast.makeText(SightsActivity.this, item.getTitle() + "와 더 가까이 가주세요!", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            if (DEBUG) {
                if (item != null) {
                    Log.i(LOG_TAG, "onFocusChanged: " + item.toString());
                } else {
                    Log.i(LOG_TAG, "onFocusChanged: ");
                }
            }
        }
    };

    private final NMapPOIdataOverlay.OnFloatingItemChangeListener onPOIdataFloatingItemChangeListener = new NMapPOIdataOverlay.OnFloatingItemChangeListener() {

        @Override
        public void onPointChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
            NGeoPoint point = item.getPoint();

            if (DEBUG) {
                Log.i(LOG_TAG, "onPointChanged: point=" + point.toString());
            }

            findPlacemarkAtLocation(point.longitude, point.latitude);
            item.setTitle(null);
        }
    };

    private final NMapOverlayManager.OnCalloutOverlayListener onCalloutOverlayListener = new NMapOverlayManager.OnCalloutOverlayListener() {

        @Override
        public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

            // handle overlapped items
            if (itemOverlay instanceof NMapPOIdataOverlay) {
                NMapPOIdataOverlay poiDataOverlay = (NMapPOIdataOverlay) itemOverlay;

                // check if it is selected by touch event
                if (!poiDataOverlay.isFocusedBySelectItem()) {
                    int countOfOverlappedItems = 1;

                    NMapPOIdata poiData = poiDataOverlay.getPOIdata();
                    for (int i = 0; i < poiData.count(); i++) {
                        NMapPOIitem poiItem = poiData.getPOIitem(i);

                        // skip selected item
                        if (poiItem == overlayItem) {
                            continue;
                        }

                        // check if overlapped or not
                        if (Rect.intersects(poiItem.getBoundsInScreen(), overlayItem.getBoundsInScreen())) {
                            countOfOverlappedItems++;
                        }
                    }

                    if (countOfOverlappedItems > 1) {
                        String text = countOfOverlappedItems + " overlapped items for " + overlayItem.getTitle();
                        Toast.makeText(SightsActivity.this, text, Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }

            // use custom old callout overlay
            if (overlayItem instanceof NMapPOIitem) {
                NMapPOIitem poiItem = (NMapPOIitem) overlayItem;

                if (poiItem.showRightButton()) {
                    return new jihun.travelgo.NMapCalloutCustomOldOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);
                }
            }

            // use custom callout overlay
            return new NMapCalloutCustomOverlay(itemOverlay, overlayItem, itemBounds, mMapViewerResourceProvider);

            // set basic callout overlay
            //return new NMapCalloutBasicOverlay(itemOverlay, overlayItem, itemBounds);
        }

    };

    private final NMapOverlayManager.OnCalloutOverlayViewListener onCalloutOverlayViewListener = new NMapOverlayManager.OnCalloutOverlayViewListener() {

        @Override
        public View onCreateCalloutOverlayView(NMapOverlay itemOverlay, NMapOverlayItem overlayItem, Rect itemBounds) {

            if (overlayItem != null) {
                // [TEST] 말풍선 오버레이를 뷰로 설정함
                String title = overlayItem.getTitle();
                if (title != null && title.length() > 5) {
                    return new NMapCalloutCustomOverlayView(SightsActivity.this, itemOverlay, overlayItem, itemBounds);
                }
            }
            // null을 반환하면 말풍선 오버레이를 표시하지 않음
            return null;
        }

    };

    /* Local Functions */
    private static boolean mIsMapEnlared = false;

    private void restoreInstanceState() {
        mPreferences = getPreferences(MODE_PRIVATE);

        int longitudeE6 = mPreferences.getInt(KEY_CENTER_LONGITUDE, NMAP_LOCATION_DEFAULT.getLongitudeE6());
        int latitudeE6 = mPreferences.getInt(KEY_CENTER_LATITUDE, NMAP_LOCATION_DEFAULT.getLatitudeE6());
        int level = mPreferences.getInt(KEY_ZOOM_LEVEL, NMAP_ZOOMLEVEL_DEFAULT);
        int viewMode = mPreferences.getInt(KEY_VIEW_MODE, NMAP_VIEW_MODE_DEFAULT);
        boolean trafficMode = mPreferences.getBoolean(KEY_TRAFFIC_MODE, NMAP_TRAFFIC_MODE_DEFAULT);
        boolean bicycleMode = mPreferences.getBoolean(KEY_BICYCLE_MODE, NMAP_BICYCLE_MODE_DEFAULT);

        mMapController.setMapViewMode(viewMode);
        mMapController.setMapViewTrafficMode(trafficMode);
        mMapController.setMapViewBicycleMode(bicycleMode);
        mMapController.setMapCenter(new NGeoPoint(longitudeE6, latitudeE6), level);

        if (mIsMapEnlared) {
            mMapView.setScalingFactor(2.0F);
        } else {
            mMapView.setScalingFactor(1.0F);
        }
    }

    private void saveInstanceState() {
        if (mPreferences == null) {
            return;
        }

        NGeoPoint center = mMapController.getMapCenter();
        int level = mMapController.getZoomLevel();
        int viewMode = mMapController.getMapViewMode();
        boolean trafficMode = mMapController.getMapViewTrafficMode();
        boolean bicycleMode = mMapController.getMapViewBicycleMode();

        SharedPreferences.Editor edit = mPreferences.edit();

        edit.putInt(KEY_CENTER_LONGITUDE, center.getLongitudeE6());
        edit.putInt(KEY_CENTER_LATITUDE, center.getLatitudeE6());
        edit.putInt(KEY_ZOOM_LEVEL, level);
        edit.putInt(KEY_VIEW_MODE, viewMode);
        edit.putBoolean(KEY_TRAFFIC_MODE, trafficMode);
        edit.putBoolean(KEY_BICYCLE_MODE, bicycleMode);

        edit.commit();
    }

    //Container view class to rotate map view.
    private class MapContainerView extends ViewGroup {

        public MapContainerView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int width = getWidth();
            final int height = getHeight();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View view = getChildAt(i);
                final int childWidth = view.getMeasuredWidth();
                final int childHeight = view.getMeasuredHeight();
                final int childLeft = (width - childWidth) / 2;
                final int childTop = (height - childHeight) / 2;
                view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            }

            if (changed) {
                mOverlayManager.onSizeChanged(width, height);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SightsActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {

        stopMyLocation();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveInstanceState();
        super.onDestroy();
    }
}
