package com.bulentsiyah.gesturerecognitionwithandroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.Date;
        import java.util.List;
        import java.util.Random;
        import java.util.Timer;
        import java.util.TimerTask;
        import java.lang.reflect.Type;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Message;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    int nnn=0;
    Double Korelasyon_Belirlenmis_Esik = 0.55;
    KorelasyonFinalsinifi finalist_korelasyonFinalsinifi = new KorelasyonFinalsinifi();
    AsyncTask_GetRegister asyncTask_GetRegister = new AsyncTask_GetRegister(
            null, "", "");

    public static File folder = new File(Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/_gesturerecognition");

    private SensorManager mSensorManager = null;
    static String logyaz = "sira;Pitch;Roll;Yaw;";
    Ad_Tumgyrolar_Sinifi anlik_gyrosinifiList = new Ad_Tumgyrolar_Sinifi();

    public static Json_Ad_Tumgyrolar_Sinifi json_tumu = new Json_Ad_Tumgyrolar_Sinifi();

    private float[] gyro_now = new float[3];
    private static int sira = 1;

    public Handler mHandler;
    private Timer fuseTimer = new Timer();
    public static final int TIME_CONSTANT = 20;

    DecimalFormat d = new DecimalFormat("#.####");
    DecimalFormat doubleformat = new DecimalFormat("##.####");

    ListView listViewGorev;
    List<RowItemGorev> rowItemsGorev;
    CustomBaseAdapterGorev adapterGorev = null;
    String FileName = "";
    EditText editTextFileName;
    TextView textViewKayitSuresi;
    Button buttonhareketebasladurdur;
    Button buttontestebasladurdur;

    long kayitVarmiVarsaSuresi = 0;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    String[] PERMISSIONS= {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonhareketebasladurdur = (Button) findViewById(R.id.buttonhareketebasladurdur);
        buttontestebasladurdur = (Button) findViewById(R.id.buttontestebasladurdur);

        textViewKayitSuresi = (TextView) findViewById(R.id.textViewKayitSuresi);
        listViewGorev = (ListView) findViewById(R.id.listView1);
        ColorDrawable divcolor = new ColorDrawable(Color.GRAY);
        listViewGorev.setDivider(divcolor);
        listViewGorev.setDividerHeight(1);
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        initListeners();
        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
                1000, TIME_CONSTANT);
        mHandler = new Handler();

        try {
            String listeType = read_TumListeLoglari();
            Json_Ad_Tumgyrolar_Sinifi temp = new Gson().fromJson(listeType,
                    Json_Ad_Tumgyrolar_Sinifi.class);

            if (temp != null) {

                json_tumu.array_Ad_Tumgyrolar_Sinifi = temp.array_Ad_Tumgyrolar_Sinifi;
            }

        } catch (Exception exp) {

        }

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initListeners();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_hareketlistesi) {
            try {

                Intent i = new Intent(MainActivity.this, MotionsList.class);
                // Intent i = new Intent(
                // MainActivity.this,TamamlandiOnayisteActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            } catch (Exception exp) {
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void initListeners() {
        try {
            // mSensorManager.registerListener(this,
            // mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            // SensorManager.SENSOR_DELAY_GAME);

            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                    SensorManager.SENSOR_DELAY_GAME);

            // mSensorManager
            // .registerListener(this, mSensorManager
            // .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            // SensorManager.SENSOR_DELAY_GAME);
        } catch (Exception exp) {
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    // private float[] gravity = new float[3];

    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:

                    break;

                case Sensor.TYPE_GYROSCOPE:
                    // System.arraycopy(gyro_now, 0, gyro_before, 0, 3);

                    if (event.values[0] != 0 && event.values[1] != 0
                            && event.values[2] != 0) {
                        float[] returnbb = kalibre(event.values);
                        System.arraycopy(returnbb, 0, gyro_now, 0, 3);

                        try {
                            // if(kayitVarmiVarsaSuresi!=0){
                            //
                            // String yazi=String.format("%d;%.7f;%.7f;%.7f;",
                            // sira,
                            // returnbb[0],
                            // returnbb[1],
                            // returnbb[2]
                            // );
                            // if(logyaz.length()==0){
                            // logyaz=yazi;
                            // }else{
                            // logyaz=logyaz+"\n"+yazi;
                            // }
                            //
                            // sira+=1;
                            // }

                            mHandler.post(updateOreintationDisplayTask);
                        } catch (Exception exp) {
                        }
                    }

                    // gyroFunction(event);
                    break;

                // case Sensor.TYPE_MAGNETIC_FIELD:
                // // copy new magnetometer data into magnet array
                // System.arraycopy(event.values, 0, magnet, 0, 3);
                // break;

            }

        } catch (Exception exp) {
        }
    }

    float[] valuesOncekiler = new float[3];
    float katsayi = (float) 0.95;

    private float[] kalibre(float[] values) {

        try {
            // katsayi = (float)
            // (Float.parseFloat(editText1.getText().toString()) * 0.1);
        } catch (Exception exp) {
            exp.toString();
        }

        values[0] = (valuesOncekiler[0] * katsayi)
                + (values[0] * (1 - katsayi));
        values[1] = (valuesOncekiler[1] * katsayi)
                + (values[1] * (1 - katsayi));
        values[2] = (valuesOncekiler[2] * katsayi)
                + (values[2] * (1 - katsayi));
        // values[0]=(float) (Math.round(values[0]*10000.0)/10000.0);
        // values[1]=(float) (Math.round(values[1]*10000.0)/10000.0);
        // values[2]=(float) (Math.round(values[2]*10000.0)/10000.0);
        valuesOncekiler[0] = values[0];
        valuesOncekiler[1] = values[1];
        valuesOncekiler[2] = values[2];

        return values;
    }

    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            try {
                if (kayitVarmiVarsaSuresi != 0) {
                    String x = String.format("%.7f", gyro_now[0]);
                    String y = String.format("%.7f", gyro_now[1]);
                    String z = String.format("%.7f", gyro_now[2]);

                    String yazi = String.format("%d;%s;%s;%s;", sira, x, y, z);
                    // if (logyaz.length() == 0) {
                    // logyaz = yazi;
                    // } else {
                    logyaz = logyaz + "\n" + yazi;
                    // }

                    anlik_gyrosinifiList.gyro_x.add(x);
                    anlik_gyrosinifiList.gyro_y.add(y);
                    anlik_gyrosinifiList.gyro_z.add(z);
                    sira += 1;
                }
            } catch (Exception exp) {
            }
        }
    }

    private Runnable updateOreintationDisplayTask = new Runnable() {
        public void run() {
            updateOreintationDisplay();
        }
    };

    public void updateOreintationDisplay() {
        try {
            rowItemsGorev = new ArrayList<RowItemGorev>();
            // RowItemGorev item = new RowItemGorev(R.drawable.ic_launcher,
            // "İvme Ölçer X", String.valueOf(d.format(accel_now[0])
            // +
            // " m/s^2")+"	Fark: "+String.valueOf(d.format(accel_now[0]-accel_before[0])
            // + " m/s^2"));
            // rowItemsGorev.add(item);
            //
            // item = new RowItemGorev(R.drawable.ic_launcher, "İvme Ölçer Y",
            // String.valueOf(d.format(accel_now[1]) +
            // " m/s^2")+"	Fark: "+String.valueOf(d.format(accel_now[1]-accel_before[1])
            // + " m/s^2"));
            // rowItemsGorev.add(item);
            //
            // item = new RowItemGorev(R.drawable.ic_launcher, "İvme Ölçer Z",
            // String.valueOf(d.format(accel_now[2]) +
            // " m/s^2")+"	Fark: "+String.valueOf(d.format(accel_now[2]-accel_before[2])
            // + " m/s^2"));
            // rowItemsGorev.add(item);
            //
            // item = new RowItemGorev(R.drawable.ic_launcher, "", "");
            // rowItemsGorev.add(item);

            RowItemGorev item = new RowItemGorev(R.drawable.ic_launcher,
                    "Jiroskop Pitch", String.valueOf(d.format(gyro_now[0])
                    + " rad/s"));
            // +"	Fark: "+String.valueOf(d.format(gyro_now[0]-gyro_before[0])
            // + " rad/s"));
            rowItemsGorev.add(item);

            item = new RowItemGorev(R.drawable.ic_launcher, "Jiroskop Roll",
                    String.valueOf(d.format(gyro_now[1]) + " rad/s"));
            // +"	Fark: "+String.valueOf(d.format(gyro_now[1]-gyro_before[1])
            // + " rad/s"));
            rowItemsGorev.add(item);

            item = new RowItemGorev(R.drawable.ic_launcher, "Jiroskop Yaw",
                    String.valueOf(d.format(gyro_now[2]) + " rad/s"));
            // +"	Fark: "+String.valueOf(d.format(gyro_now[2]-gyro_before[2])
            // + " rad/s"));

            rowItemsGorev.add(item);

            adapterGorev = new CustomBaseAdapterGorev(getApplicationContext(),
                    rowItemsGorev, true, true, true, true, true);
            listViewGorev.setAdapter(adapterGorev);

            // try{
            if (kayitVarmiVarsaSuresi != 0) {
                //
                // String yazi=sira+";"+String.valueOf(accel_now[0])+";"
                // +String.valueOf(accel_now[1])+";"
                // +String.valueOf(accel_now[2])+";"
                // +String.valueOf(gyro_now[0])+";"
                // +String.valueOf(gyro_now[1])+";"
                // +String.valueOf(gyro_now[2])+";";
                // webserviceLoglari(yazi,FileName);
                textViewKayitSuresi.setVisibility(View.VISIBLE);

                double farkSaniye = (double) (System.currentTimeMillis() - kayitVarmiVarsaSuresi)
                        / (double) 1000;
                String bb = String.format("%.4f", farkSaniye);
                textViewKayitSuresi.setText(bb + " Saniyedir Kayıt Ediliyor..");
                //
            } else {
                // kayitsinifi=new ArrayList<Kayitsinifi>();
                textViewKayitSuresi.setVisibility(View.GONE);

            }
            //
            // } catch (Exception exp) {
            // }
            // mAzimuthView.setText(d.format(gyroOrientation[0] * 180/Math.PI) +
            // '°');
            // mPitchView.setText(d.format(gyroOrientation[1] * 180/Math.PI) +
            // '°');
            // mRollView.setText(d.format(gyroOrientation[2] * 180/Math.PI) +
            // '°');
            //
            // mAzimuthView.setText(d.format(fusedOrientation[0] * 180/Math.PI)
            // + '°');
            // mPitchView.setText(d.format(fusedOrientation[1] * 180/Math.PI) +
            // '°');
            // mRollView.setText(d.format(fusedOrientation[2] * 180/Math.PI) +
            // '°');
        } catch (Exception exp) {
        }
    }

    void ToastBlue(final int detal, String textstr) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.toast_layout_root_blue));

            TextView text = (TextView) layout.findViewById(R.id.text__blue);
            text.setText(textstr);

            Toast toast = new Toast(getApplicationContext());

            toast.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setDuration(detal);
            toast.setView(layout);
            toast.show();
        } catch (Exception ex) {

        }

    }

    @SuppressLint("DefaultLocale")
    public void button_test(View v) {
        try {

            if (json_tumu.array_Ad_Tumgyrolar_Sinifi.size() == 0) {
                ViewDialog alert = new ViewDialog();
                alert.showDialog(
                        MainActivity.this,
                        "Eğitim hareketi Yok",
                        "Eğitim Hareketi Listesinde veri Bulunmadığı için test işlemi yapılamaz."
                                + " Öncelikle en az bir Eğitim Hareketi Kaydetmelisiniz.");

            } else {
                if (buttontestebasladurdur.getText().equals(
                        "Test Hareketi Durdur")) {
                    // webserviceLoglari("", "test");
                    buttonhareketebasladurdur.setVisibility(View.VISIBLE);
                    buttontestebasladurdur.setVisibility(View.VISIBLE);

                    webserviceLoglari(logyaz, "test");
                    try {
                        asyncTask_GetRegister = new AsyncTask_GetRegister(
                                anlik_gyrosinifiList, "", "");
                        asyncTask_GetRegister.execute("");

                    } catch (Exception exp) {

                    }
                    anlik_gyrosinifiList = new Ad_Tumgyrolar_Sinifi();
                    anlik_gyrosinifiList.gyro_x.clear();
                    anlik_gyrosinifiList.gyro_y.clear();
                    anlik_gyrosinifiList.gyro_z.clear();
                    buttontestebasladurdur.setText("Test Hareketine Başla");
                    kayitVarmiVarsaSuresi = 0;
                    sira = 1;
                    logyaz = "sira;Pitch;Roll;Yaw;";
                } else {
                    handler.sendEmptyMessageDelayed(3, 1 * 1000);
                }

            }

        } catch (Exception exp) {
        }
    }

    @SuppressLint("DefaultLocale")
    public void button_register(View v) {
        try {
            if (buttonhareketebasladurdur.getText().equals(
                    "Eğitim Hareketi Kaydını Durdur")) {
                buttonhareketebasladurdur.setVisibility(View.VISIBLE);
                buttontestebasladurdur.setVisibility(View.VISIBLE);


                ToastBlue(10000, "Eğitim Hareketi başarıyla kaydedilmiştir");
                // webserviceLoglari("", FileName);
                webserviceLoglari(logyaz, FileName);

                try {
                    Gson gson = new Gson();

                    Ad_Tumgyrolar_Sinifi newL = new Ad_Tumgyrolar_Sinifi();
                    newL.adi = FileName;
                    newL.gyro_x = anlik_gyrosinifiList.gyro_x;
                    newL.gyro_y = anlik_gyrosinifiList.gyro_y;
                    newL.gyro_z = anlik_gyrosinifiList.gyro_z;
                    json_tumu.array_Ad_Tumgyrolar_Sinifi.add(newL);

                    String durum = gson.toJson(json_tumu,
                            Json_Ad_Tumgyrolar_Sinifi.class);
                    TumListeLoglari(durum);

                } catch (Exception exp) {
                    exp.toString();
                }
                anlik_gyrosinifiList = new Ad_Tumgyrolar_Sinifi();
                anlik_gyrosinifiList.gyro_x.clear();
                anlik_gyrosinifiList.gyro_y.clear();
                anlik_gyrosinifiList.gyro_z.clear();
                buttonhareketebasladurdur.setText("Eğitim Hareketi Kaydet");
                kayitVarmiVarsaSuresi = 0;
                sira = 1;
                logyaz = "sira;Pitch;Roll;Yaw;";
            } else {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.delete_dialog_red);

                editTextFileName = (EditText) dialog
                        .findViewById(R.id.editTextFileName);

                TextView textViewUyariTitle = (TextView) dialog
                        .findViewById(R.id.textViewUyariTitlee_delete);
                textViewUyariTitle.setText("Hareket Kaydını Adınızı Giriniz");
                Button dialogOkeyButton = (Button) dialog
                        .findViewById(R.id.btn_dialog_okey);
                dialogOkeyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editTextFileName.getText().toString().length() != 0) {
                            handler.sendEmptyMessageDelayed(2, 1 * 1000);
                            FileName = editTextFileName.getText().toString();
                            dialog.dismiss();

                        } else {
                            ViewDialog alert = new ViewDialog();
                            alert.showDialog(MainActivity.this, "Boş Alan",
                                    "Boş isim giremezsiniz!!");
                        }

                    }
                });

                Button dialogCancelButton = (Button) dialog
                        .findViewById(R.id.btn_dialog_vazgec);
                dialogCancelButton
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editTextFileName.setText("");
                                dialog.dismiss();
                            }
                        });

                dialog.show();
            }

        } catch (Exception exp) {
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 1) {
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(MainActivity.this, "Hata Oluştu",
                            "İşlemler sırasında hata oluştu!! Lütfen daha sonra tekrar deneyiniz");
                } else if (msg.what == 2) {
                    buttonhareketebasladurdur.setVisibility(View.VISIBLE);
                    buttontestebasladurdur.setVisibility(View.INVISIBLE);


                    buttonhareketebasladurdur
                            .setText("Eğitim Hareketi Kaydını Durdur");
                    kayitVarmiVarsaSuresi = System.currentTimeMillis();

                } else if (msg.what == 3) {
                    buttonhareketebasladurdur.setVisibility(View.INVISIBLE);
                    buttontestebasladurdur.setVisibility(View.VISIBLE);

                    buttontestebasladurdur.setText("Test Hareketi Durdur");
                    kayitVarmiVarsaSuresi = System.currentTimeMillis();

                } else if (msg.what == 4) {

                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(
                            MainActivity.this,
                            "Test Sonucu Başarılı",
                            "Yapılan hareket en çok '"
                                    + finalist_korelasyonFinalsinifi.adi
                                    + "' "
                                    + "isimli eğitim verisine benzemektedir."
                                    + " Eğitim verisi ile test verisi arasındaki korelasyon seviyesi :"
                                    + d.format(finalist_korelasyonFinalsinifi.puanenyuksek)
                                    + " dır. "
                                    + "Her boylam için sırayla korelasyon seviyeleri x: "
                                    + d.format(finalist_korelasyonFinalsinifi.puanx)
                                    + " y: "
                                    + d.format(finalist_korelasyonFinalsinifi.puany)
                                    + " z: "
                                    + d.format(finalist_korelasyonFinalsinifi.puanz));
                } else if (msg.what == 5) {

                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(
                            MainActivity.this,
                            "Test Sonucu Başarısız",
                            "Yapılan hareket belirlenmiş eşik değer altında kaldığı için hiçbir harekete benzemiyor!"
                                    + " Buna rağmen en çok '"
                                    + finalist_korelasyonFinalsinifi.adi
                                    + "' "
                                    + "isimli eğitim verisine benzemektedir."
                                    + " Eğitim verisi ile test verisi arasındaki korelasyon seviyesi :"
                                    + d.format(finalist_korelasyonFinalsinifi.puanenyuksek)
                                    + " dır. "
                                    + "Her boylam için sırayla korelasyon seviyeleri x: "
                                    + d.format(finalist_korelasyonFinalsinifi.puanx)
                                    + " y: "
                                    + d.format(finalist_korelasyonFinalsinifi.puany)
                                    + " z: "
                                    + d.format(finalist_korelasyonFinalsinifi.puanz));
                } else if (msg.what == 6) {
                    ViewDialog alert = new ViewDialog();
                    alert.showDialog(
                            MainActivity.this,
                            "Test Verisi Az",
                            "Analiz için test veya eğitim süresini biraz daha artırınız! Şuan ki veri miktarı: "+nnn);
                }

            } catch (Exception exp) {
            }
        };
    };

    void ToastRed(String textstr) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout_red,
                    (ViewGroup) findViewById(R.id.toast_layout_root_red));

            TextView text = (TextView) layout.findViewById(R.id.text_red);
            text.setText(textstr);

            Toast toast = new Toast(getApplicationContext());

            toast.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setDuration(5000);
            toast.setView(layout);
            toast.show();
        } catch (Exception ex) {

        }

    }


    public static Boolean webserviceLoglari(String text, String FileName) {
        try {

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File fname = new File(folder, FileName + ".csv");

            // String okunanDeger = readHata(folder, fname);
            //
            // if (okunanDeger.length() == 0) {
            // return false;
            // }

            if (fname.exists()) {
                fname.delete();
            }

            String YazilacakNot = text;

            if (folder.canWrite()) {
                FileWriter fwriter = new FileWriter(fname);
                BufferedWriter out = new BufferedWriter(fwriter);
                out.write(YazilacakNot);
                out.close();
                fwriter.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.toString();
            return false;
        }

    }

    // public static String readHata(File folder, File fname) {
    // // Date now = new Date();
    // String ret = "";
    // try {
    //
    // if (!folder.exists()) {
    // folder.mkdirs();
    //
    // }
    //
    // if (!fname.exists()) {
    // ret = "sira;Pitch;Roll;Yaw;\n";
    // return ret;
    // }
    // FileInputStream fIn = new FileInputStream(fname);
    // BufferedReader myReader = new BufferedReader(new InputStreamReader(
    // fIn));
    // String aDataRow = "";
    // StringBuilder aBuffer = new StringBuilder();
    // while ((aDataRow = myReader.readLine()) != null) {
    // aBuffer.append(aDataRow);
    // aBuffer.append("\n");
    // }
    // ret = (aBuffer.toString());
    // myReader.close();
    //
    // } catch (Exception e) {
    // ret = "";
    // }
    // return ret;
    // }

    public static Boolean TumListeLoglari(String text) {
        try {

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File fname = new File(folder, "TumDegerler" + ".txt");

            if (fname.exists()) {
                fname.delete();
            }
            // String okunanDeger = readHata(folder, fname);
            //
            // if (okunanDeger.length() == 0) {
            // return false;
            // }

            String YazilacakNot = text;

            if (folder.canWrite()) {
                FileWriter fwriter = new FileWriter(fname);
                BufferedWriter out = new BufferedWriter(fwriter);
                out.write(YazilacakNot);
                out.close();
                fwriter.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.toString();
            return false;
        }

    }

    public static String read_TumListeLoglari() {
        // Date now = new Date();
        String ret = "";
        try {

            if (!folder.exists()) {
                folder.mkdirs();

            }
            File fname = new File(folder, "TumDegerler" + ".txt");

            FileInputStream fIn = new FileInputStream(fname);
            BufferedReader myReader = new BufferedReader(new InputStreamReader(
                    fIn));
            String aDataRow = "";
            StringBuilder aBuffer = new StringBuilder();

            while ((aDataRow = myReader.readLine()) != null) {
                aBuffer.append(aDataRow);
                aBuffer.append("\n");
            }
            ret = (aBuffer.toString());
            myReader.close();

        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    public static class KorelasyonFinalsinifi implements
            Comparable<KorelasyonFinalsinifi> {
        public String adi;
        public Double puanenyuksek = (double) 0;
        public Double puan = (double) 0;
        public Double puanx = (double) 0;
        public Double puany = (double) 0;
        public Double puanz = (double) 0;

        public static Comparator<KorelasyonFinalsinifi> FruitNameComparator = new Comparator<KorelasyonFinalsinifi>() {

            public int compare(KorelasyonFinalsinifi fruit1,
                               KorelasyonFinalsinifi fruit2) {

                double fruitName1 = fruit1.puan;
                double fruitName2 = fruit2.puan;

                // ascending order
                return Double.compare(fruitName2, fruitName1);

                // descending order
                // return fruitName2.compareTo(fruitName1);
            }

        };

        @Override
        public int compareTo(KorelasyonFinalsinifi arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
    }

    // public class gyrosinifi {
    // public float[] gyro = new float[3];
    // }

    private class AsyncTask_GetRegister extends
            AsyncTask<String, String, String> {

        ProgressDialog dialog;
        Ad_Tumgyrolar_Sinifi test_gyrosinifiList = new Ad_Tumgyrolar_Sinifi();

        // new ProgressDialog(bmainregisteractivity.this);

        public AsyncTask_GetRegister(Ad_Tumgyrolar_Sinifi b, String bb,
                                     String bbb) {
            test_gyrosinifiList = b;
        }

        @Override
        protected void onPreExecute() {
            try {
                dialog = new ProgressDialog(MainActivity.this,
                        R.style.CustomDialog);
                dialog.setMessage("Test verileri ile Eğitim verileri arasında Korelasyon uygulanırken lütfen bekleyiniz...");
                dialog.setCancelable(false);
                dialog.show();

            } catch (Exception exp) {
                exp.toString();
            }
        }

        protected String doInBackground(String... params) {
            try {
                List<KorelasyonFinalsinifi> korelasyonFinalsinifi_List = new ArrayList<MainActivity.KorelasyonFinalsinifi>();
                KorelasyonFinalsinifi korelasyonFinalsinifi;
                for (Ad_Tumgyrolar_Sinifi egitimHareketi : json_tumu.array_Ad_Tumgyrolar_Sinifi) {
                    korelasyonFinalsinifi = new KorelasyonFinalsinifi();
                    korelasyonFinalsinifi.adi = egitimHareketi.adi;

                    for (int xyz = 0; xyz < 3; xyz++) {
                        // hesap başlıyor
                        List<String> test_gyro = new ArrayList<String>();
                        List<String> egitim_gyro = new ArrayList<String>();

                        if (xyz == 0) {
                            test_gyro = test_gyrosinifiList.gyro_x;
                            egitim_gyro = egitimHareketi.gyro_x;
                        } else if (xyz == 1) {
                            test_gyro = test_gyrosinifiList.gyro_y;
                            egitim_gyro = egitimHareketi.gyro_y;
                        } else if (xyz == 2) {
                            test_gyro = test_gyrosinifiList.gyro_z;
                            egitim_gyro = egitimHareketi.gyro_z;
                        }
                        int normalize_farki = test_gyro.size()
                                - egitim_gyro.size();
                        // fazla olan kırpılacak şekilde işlem yapılacak
                        // test fazla
                        if (normalize_farki >= 0) {
                            // örn test 200 eğitim 150
                            // fark 50
                            // aradaki silinme oranı fark/mıktar
                            // 50/200=4 o zaman 4 değerden biri silinecek

                            normalize_farki = Math.abs(normalize_farki);
                            int azaltmaKatsayisi = test_gyro.size()
                                    / normalize_farki;
                            List<String> clone_test_gyro = new ArrayList<String>();
                            List<String> clone2_test_gyro = new ArrayList<String>();

                            for (int iii = 0; iii < test_gyro.size(); iii++) {
                                if (iii % (azaltmaKatsayisi + 1) == 0) {

                                } else {
                                    clone_test_gyro.add(test_gyro.get(iii));
                                }
                            }
                            normalize_farki = clone_test_gyro.size()
                                    - egitim_gyro.size();

                            if (normalize_farki > 0) {
                                for (int iii = 0; iii < clone_test_gyro.size(); iii++) {
                                    if (iii > normalize_farki) {
                                        clone2_test_gyro.add(clone_test_gyro
                                                .get(iii));
                                    }
                                }
                                test_gyro = clone2_test_gyro;
                            } else {
                                test_gyro = clone_test_gyro;
                            }

                        } else {
                            // eğitim fazla
                            normalize_farki = Math.abs(normalize_farki);
                            int azaltmaKatsayisi = egitim_gyro.size()
                                    / normalize_farki;
                            List<String> clone_egitim_gyro = new ArrayList<String>();
                            List<String> clone2_egitim_gyro = new ArrayList<String>();

                            for (int iii = 0; iii < egitim_gyro.size(); iii++) {
                                if (iii % (azaltmaKatsayisi + 1) == 0) {

                                } else {
                                    clone_egitim_gyro.add(egitim_gyro.get(iii));
                                }
                            }
                            normalize_farki = clone_egitim_gyro.size()
                                    - test_gyro.size();

                            if (normalize_farki > 0) {
                                for (int iii = 0; iii < clone_egitim_gyro
                                        .size(); iii++) {
                                    if (iii > normalize_farki) {
                                        clone2_egitim_gyro
                                                .add(clone_egitim_gyro.get(iii));
                                    }
                                }
                                egitim_gyro = clone2_egitim_gyro;
                            } else {
                                egitim_gyro = clone_egitim_gyro;
                            }

                        }

                        int n = test_gyro.size() > egitim_gyro.size() ? egitim_gyro
                                .size() : test_gyro.size();
                        if(n<=50){
                            nnn=n;
                            return "veriaz";
                        }
                        Double Σx = (double) 0;
                        Double Σx2 = (double) 0;
                        Double Σy = (double) 0;
                        Double Σy2 = (double) 0;
                        Double Σxy = (double) 0;
                        // String testVerisi="";
                        // String EgitimVerisi="";

                        for (int n_item = 0; n_item < n; n_item++) {
                            // testVerisi+="\n"+test_gyro.get(n_item).replace(",",
                            // ".");
                            // EgitimVerisi+="\n"+egitim_gyro.get(n_item).replace(",",
                            // ".");
                            Double x = Double.parseDouble(test_gyro.get(n_item)
                                    .replace(",", "."));
                            Double y = Double.parseDouble(egitim_gyro.get(
                                    n_item).replace(",", "."));
                            Σx += x;
                            Σx2 += Math.pow(x, 2);
                            Σy += y;
                            Σy2 += Math.pow(y, 2);
                            Σxy += x * y;
                        }
                        Double ΣxKaresi = Math.pow(Σx, 2);
                        Double ΣyKaresi = Math.pow(Σy, 2);
                        Double Pay = Σxy - (Σx * Σy / n);
                        Double tempPayda = (Σx2 - (ΣxKaresi / n))
                                * (Σy2 - (ΣyKaresi / n));
                        Double Payda = Math.sqrt(tempPayda);
                        Double r = Pay / Payda;
                        if (xyz == 0) {
                            korelasyonFinalsinifi.puanx = r;
                        } else if (xyz == 1) {
                            korelasyonFinalsinifi.puany = r;
                        } else if (xyz == 2) {
                            korelasyonFinalsinifi.puanz = r;
                        }

                        korelasyonFinalsinifi.puan += r;

                        if (r > korelasyonFinalsinifi.puanenyuksek) {
                            korelasyonFinalsinifi.puanenyuksek = r;
                        }
                    }
                    korelasyonFinalsinifi_List.add(korelasyonFinalsinifi);
                }

                Collections.sort(korelasyonFinalsinifi_List,
                        MainActivity.KorelasyonFinalsinifi.FruitNameComparator);
                finalist_korelasyonFinalsinifi = korelasyonFinalsinifi_List
                        .get(0);
                if (finalist_korelasyonFinalsinifi.puanenyuksek >= Korelasyon_Belirlenmis_Esik) {
                    return "Tamam";
                } else {
                    return "TamamDegil";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Hata";
            }

        }

        protected void onPostExecute(String UzunIslemSonucu) {

            try {
                dialog.dismiss();
                if (UzunIslemSonucu.equals("Hata")) {
                    handler.sendEmptyMessage(1);
                } else if (UzunIslemSonucu.equals("Tamam")) {
                    handler.sendEmptyMessage(4);
                } else if (UzunIslemSonucu.equals("TamamDegil")) {
                    handler.sendEmptyMessage(5);
                } else if (UzunIslemSonucu.equals("veriaz")) {
                    handler.sendEmptyMessage(6);
                }

            } catch (Exception exp) {

            }
        }
    }

    public static List<String> cloneList(List<String> list) {
        try {
            List<String> clone = new ArrayList<String>(list.size());
            for (String item : list)
                clone.add(item);
            return clone;
        } catch (Exception exp) {
            return new ArrayList<String>();
        }
    }

    public static Comparator<Ad_Tumgyrolar_Sinifi> FruitNameComparator = new Comparator<Ad_Tumgyrolar_Sinifi>() {

        public int compare(Ad_Tumgyrolar_Sinifi fruit1,
                           Ad_Tumgyrolar_Sinifi fruit2) {

            String fruitName1 = fruit1.adi.toUpperCase();
            String fruitName2 = fruit2.adi.toUpperCase();

            // ascending order
            return fruitName1.compareTo(fruitName2);

            // descending order
            // return fruitName2.compareTo(fruitName1);
        }

    };
}
