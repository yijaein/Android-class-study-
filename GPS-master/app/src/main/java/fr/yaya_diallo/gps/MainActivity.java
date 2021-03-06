package fr.yaya_diallo.gps;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private final String TAG = MainActivity.class.getName();
    public static LocationManager locationManager;
    public static Location location;
    GridView gridView;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static String bestProvider;
    public static String mapTitle = "니 위치 ";
    double distance;
    String meter;



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로딩화면
        Intent intent1 = new Intent(this,Splash_Activity.class);
        startActivity(intent1);
//   =================================================

        gridView = (GridView) findViewById(R.id.grid);
        gridView.setAdapter(new CustomAndroidGridViewAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(location == null)
                {
                    mapTitle = "니 위치";

                    location = locationManager.getLastKnownLocation(bestProvider);// 맵 자동 갱신


//++++++++++++++++++++++++++++++++++++++++++++++++++++++==================================================================



                    Toast.makeText(MainActivity.this,"니 위치 "+location.getLatitude()+location.getLongitude(),Toast.LENGTH_LONG).show();



                }





                if (position == 0) {
                    if (location == null) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                        adb.setTitle("죄송합니다. 위치를 찾을 수 없습니다.");
                        adb.setPositiveButton("Ok", null);
                        adb.show();
                    } else {
                        Intent map = new Intent(MainActivity.this, MapsActivity.class);
                        MainActivity.this.startActivity(map);
                    }
                }else if (position == 1) {
                    if (location == null) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                        adb.setTitle("죄송합니다. 위치를 찾을 수 없습니다.");
                        adb.setPositiveButton("Ok", null);
                        adb.show();
                    } else {
                        Intent sendSMS = new Intent(MainActivity.this, SendGpsActivity.class);
                        MainActivity.this.startActivity(sendSMS);
                    }
                }else if (position == 2) {
                    Intent receiveGps = new Intent(MainActivity.this, ReceiveGpsActivity.class);
                    MainActivity.this.startActivity(receiveGps);
                }else if (position == 3) {
                    Intent lieux = new Intent(MainActivity.this, LieuxImportantsActivity.class);
                    MainActivity.this.startActivity(lieux);
                }
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        int hasGPSPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasGPSPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Utils.showMessageOKCancel(MainActivity.this, "GPS를 켜주세요",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }


        /*Criteria
        GPS의 RPOVIDER, 기기 상황에 따른 옵션을 설정할 수 있음.
        해상도가 우수하거나 또는 그렇지 않는 기기에 따른 GPS옵션 설정.
        Ex) 정확도, 전원소비량 , 고도 사용여부 , 방위데이터, 속도 , 금전적비용

         */

        Criteria critere = new Criteria();

        critere.setAccuracy(Criteria.ACCURACY_FINE);// 정밀한 위치 정밀도 요구를 나타냄

        critere.setCostAllowed(false);

        critere.setPowerRequirement(Criteria.POWER_LOW);//저전력 조건

        critere.setSpeedRequired(true);
        bestProvider = locationManager.getBestProvider(critere,true);

        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, pending);
        locationManager.requestLocationUpdates(bestProvider, 6000, 150, this);




    }




    @Override
    public void onLocationChanged(Location p_location) {
        mapTitle = "당신의 위치";
       location= p_location;

        distance = p_location.distanceTo(getPointLocation());
        Log.i("알림1","거리"+distance);
        Toast.makeText(MainActivity.this, "거리:"+distance, Toast.LENGTH_LONG).show();

        if(distance<50)
        {
            PendingIntent activity =null;
            Intent intent = null;
            intent = new Intent(MainActivity.this.getApplicationContext(),Information.class);

            activity = PendingIntent.getActivity(MainActivity.this.getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationManager nm = (NotificationManager)MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT < 16) {
                Notification n  = new Notification.Builder(this)
                        .setContentTitle("New mail from " + "test@gmail.com")
                        .setContentText("Subject")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(activity)
                        .setAutoCancel(true).getNotification();

                n.defaults = Notification.DEFAULT_SOUND;

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, n);
            } else {
                Notification n  = new Notification.Builder(this)
                        .setContentTitle("주변에 식당이 있어용 " + "눌러서 정보확인")
                        .setContentText("눌러봐")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(activity)
                        .setAutoCancel(true).build();
                n.defaults = Notification.DEFAULT_SOUND;
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, n);
                Toast.makeText(MainActivity.this, "뜸?", Toast.LENGTH_LONG).show();
            }

        }


        ////////=================================================================================================================


//==========================================================================


    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        /*if(LocationManager.GPS_PROVIDER.equalsIgnoreCase(provider)){
            location = null;
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.aide_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contact:
                Intent contact = new Intent(MainActivity.this, ContactActivity.class);
                MainActivity.this.startActivity(contact);
                return true;
            case R.id.menu_help:
                Intent aide = new Intent(MainActivity.this, AideActivity.class);
                MainActivity.this.startActivity(aide);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //위치 정보
    private Location getPointLocation(){
        Location point = new Location("point");
        point.setLatitude(36.544902);
        point.setLongitude(128.793711);
        return point;

    }







}
