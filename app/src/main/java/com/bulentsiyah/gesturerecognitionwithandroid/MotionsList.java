package com.bulentsiyah.gesturerecognitionwithandroid;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
public class MotionsList extends AppCompatActivity {

    ListView listViewsave;
    List<String> rowItemsGorev;
    //CustomBaseAdapterGorev adapterGorev = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motions_list);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        } catch (Exception ex) {
            ex.toString();
        }

        setTitle("Öğretilen Hareketler Listesi");
        listViewsave = (ListView) findViewById(R.id.listViewsave);
        ColorDrawable divcolor = new ColorDrawable(Color.GRAY);
        listViewsave.setDivider(divcolor);
        listViewsave.setDividerHeight(1);

        listViewsave.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View view,
                                    final int position, long id) {
                try {
                    final Dialog dialog = new Dialog(MotionsList.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.delete_dialog_red);

                    EditText editTextFileName = (EditText) dialog
                            .findViewById(R.id.editTextFileName);
                    editTextFileName.setVisibility(View.GONE);

                    TextView textViewUyariTitle = (TextView) dialog
                            .findViewById(R.id.textViewUyariTitlee_delete);
                    textViewUyariTitle
                            .setText("Seçili Öğeyi Silmek İstiyormusunuz?");
                    Button dialogOkeyButton = (Button) dialog
                            .findViewById(R.id.btn_dialog_okey);
                    dialogOkeyButton.setText("Evet, Sil");

                    dialogOkeyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                dialog.dismiss();
                                //int deger = position;
                                Gson gson = new Gson();
                                String rr = rowItemsGorev.get(position);
                                MainActivity.json_tumu.array_Ad_Tumgyrolar_Sinifi.remove(position);

                                MainActivity.TumListeLoglari(gson.toJson(MainActivity.json_tumu, Json_Ad_Tumgyrolar_Sinifi.class));

//								Gson gson = new Gson();
//								SharedPreferences prefs = getSharedPreferences(
//										MainActivity.SharedPreferencesName,
//										MODE_PRIVATE);
//								SharedPreferences.Editor editor = prefs.edit();
//
//								Type listType = new TypeToken<ArrayList<Listesinifi>>() {
//								}.getType();
//								editor.putString("listesinifiList", gson.toJson(
//										MainActivity.listesinifiList, listType));
//								editor.commit();
                                Boolean result = deleteRecursiveListItem(MainActivity.folder,rr+".csv");
                                ToastBlue(10 * 1000, "Kayıt başarıyla Silindi.");
                                onBackPressed();
                                if (result) {
//									ToastBlue(10 * 1000, "Kayıt başarıyla Silindi.");
//									onBackPressed();

                                } else {
                                    //ToastRed("Kayıt Silinirken hata oluştu!!!");
                                }
                            } catch (Exception ex) {
                                ex.toString();
                                ToastRed("Kayıt Silinirken hata oluştu!!!");
                            }
                        }
                    });

                    Button dialogCancelButton = (Button) dialog
                            .findViewById(R.id.btn_dialog_vazgec);
                    dialogCancelButton.setText("Vazgeç");

                    dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();

                } catch (Exception exp) {
                }

            }
        });

        ListeyiKontrolEt();
    }

    public void button_liste(View v) {
        try {

            final Dialog dialog = new Dialog(MotionsList.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.delete_dialog_red);

            EditText editTextFileName = (EditText) dialog
                    .findViewById(R.id.editTextFileName);
            editTextFileName.setVisibility(View.GONE);

            TextView textViewUyariTitle = (TextView) dialog
                    .findViewById(R.id.textViewUyariTitlee_delete);
            textViewUyariTitle
                    .setText("Eğitim Listesini Temizlemek İstiyormusunuz?");
            Button dialogOkeyButton = (Button) dialog
                    .findViewById(R.id.btn_dialog_okey);
            dialogOkeyButton.setText("Evet, Sil");

            dialogOkeyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        MainActivity.json_tumu.array_Ad_Tumgyrolar_Sinifi=new ArrayList<Ad_Tumgyrolar_Sinifi>();
                        Gson gson = new Gson();

                        MainActivity.TumListeLoglari(gson.toJson(MainActivity.json_tumu, Json_Ad_Tumgyrolar_Sinifi.class));
                        Boolean result = deleteRecursive(MainActivity.folder);
                        if (result) {
                            ToastBlue(10 * 1000, "Liste başarıyla temizlendi.");
                            onBackPressed();
                        } else {
                            ToastRed("Liste temizlenemedi!!!");
                        }

                    } catch (Exception exp) {
                        ToastRed("Liste temizlenemedi!!!");
                    }
                }
            });

            Button dialogCancelButton = (Button) dialog
                    .findViewById(R.id.btn_dialog_vazgec);
            dialogCancelButton.setText("Vazgeç");

            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        } catch (Exception exp) {
        }
    }

    Boolean deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            return fileOrDirectory.delete();
        } catch (Exception exp) {
            return false;
        }

    }

    Boolean deleteRecursiveListItem(File fileOrDirectory,String ItemName) {
        Boolean ReturnState=false;
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles()){
                    if(child.getName().equals(ItemName)){
                        deleteRecursive(child);
                        ReturnState= true;
                        return ReturnState;
                    }
                }
            ReturnState=false;
        } catch (Exception exp) {
            ReturnState=false;
        }
        return ReturnState;
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

    void ToastRed(String textstr) {
        try {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout_red,
                    (ViewGroup) findViewById(R.id.toast_layout_root_red));

            TextView text = (TextView) layout.findViewById(R.id.text_red);
            text.setText(textstr);

            Toast toast = new Toast(getApplicationContext());

            toast.setGravity(Gravity.CENTER | Gravity.FILL_HORIZONTAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } catch (Exception ex) {

        }

    }

    void ListeyiKontrolEt() {
        try {
            rowItemsGorev = new ArrayList<String>();
            if (MainActivity.json_tumu.array_Ad_Tumgyrolar_Sinifi.size()==0) {
                ToastRed("Eğitim Hareketi Listesinde Veri Bulunmamaktadır");
                onBackPressed();
            }else{
                Collections.sort(MainActivity.json_tumu.array_Ad_Tumgyrolar_Sinifi,
                        MainActivity.FruitNameComparator);

                for (Ad_Tumgyrolar_Sinifi listesinifi : MainActivity.json_tumu.array_Ad_Tumgyrolar_Sinifi) {
//					RowItemGorev item = new RowItemGorev(R.drawable.ic_launcher,
//							listesinifi.adi, "");
//					rowItemsGorev.add(item);
                    rowItemsGorev.add(listesinifi.adi);
                }

                ArrayAdapter<String> adapterGorev = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, rowItemsGorev);

//				adapterGorev = new CustomBaseAdapterGorev(getApplicationContext(),
//						rowItemsGorev, true, true, true, true, true);
                listViewsave.setAdapter(adapterGorev);
            }



        } catch (Exception ex) {
            ex.toString();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                try {
                    onBackPressed();
                } catch (Exception exp) {
                    exp.toString();
                }

                return true;
            }

        } catch (Exception ex) {
            ex.toString();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                onBackPressed();
            }
        } catch (Exception ex) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            overridePendingTransition(R.anim.trans_right_in,
                    R.anim.trans_right_out);
        } catch (Exception ex) {
            ex.toString();
        }

    }
    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // Inflate the menu; this adds items to the action bar if it is present.
    // getMenuInflater().inflate(R.menu.motions_list, menu);
    // return true;
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // // Handle action bar item clicks here. The action bar will
    // // automatically handle clicks on the Home/Up button, so long
    // // as you specify a parent activity in AndroidManifest.xml.
    // int id = item.getItemId();
    // if (id == R.id.action_settings) {
    // return true;
    // }
    // return super.onOptionsItemSelected(item);
    // }
}
