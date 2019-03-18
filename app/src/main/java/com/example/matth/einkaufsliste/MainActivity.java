package com.example.matth.einkaufsliste;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ArrayList<Store> stores;
ArrayList<Article> articles;
Spinner sp;
ListView lv;
Store curent;
final int REQUEST_WRITE=1,REQUEST_READ=2;
final String FILENAME="einkauf.csv";
public String fcsv="";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv=findViewById(R.id.lv);
        articles=new ArrayList<>();
        ArrayAdapter a=new ArrayAdapter(this,android.R.layout.simple_list_item_1,articles);
        lv.setAdapter(a);
        sp=findViewById(R.id.sp);
        stores=new ArrayList<>();
        ArrayAdapter s=new ArrayAdapter(this,android.R.layout.simple_list_item_1,stores);
        sp.setAdapter(s);
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ);
        }else{
            readSD();
        }
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                curent=stores.get(position);
                articles.clear();
                articles.addAll(curent.articles);
                lv.setAdapter(lv.getAdapter());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                curent=stores.get(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu,menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.add){
            final View v=getLayoutInflater().inflate(R.layout.adddialog,null);
            AlertDialog.Builder b=new AlertDialog.Builder(this);
            b.setTitle("Neuen Artikel hinzufügen")
            .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText name=v.findViewById(R.id.addName);
                    EditText amount=v.findViewById(R.id.addAmount);
                    Article a=new Article(name.getText().toString(),Integer.parseInt(amount.getText().toString()));
                    curent.articles.add(a);
                    articles.clear();
                    articles.addAll(curent.articles);
                    lv.setAdapter(lv.getAdapter());


                }
            })
            .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setView(v)
            .create().show();
        }else if(item.getItemId()==R.id.newStore){
            final View v=getLayoutInflater().inflate(R.layout.storedialog,null);
            AlertDialog.Builder b=new AlertDialog.Builder(this);
            b.setTitle("Neues Geschäft hinzufügen")
                    .setPositiveButton("Hinzufügen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText name=v.findViewById(R.id.storeName);
                            Store s=new Store(name.getText().toString());
                            stores.add(s);
                            sp.setAdapter(sp.getAdapter());
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setView(v)
                    .create().show();
        }else if(item.getItemId()==R.id.save){
            String csv="";
            for (Store st:stores) {
                csv+=st.name+";"+st.toCsv()+"\n";
            }
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                fcsv=csv;
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE);
            }else{

                writeSD(csv);
            }
        }


return true;    }


public void writeSD(String csv){
    String state = Environment.getExternalStorageState();
    if (!state.equals(Environment.MEDIA_MOUNTED)) return;
    File outFile = Environment.getExternalStorageDirectory();
    String path = outFile.getAbsolutePath();
    String fullPath = path + File.separator + FILENAME;

    try {
        PrintWriter pr=new PrintWriter(new OutputStreamWriter(new FileOutputStream(fullPath)));
        pr.write(csv);
        pr.flush();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    }
}
public void readSD(){
    String state = Environment.getExternalStorageState();
    if (!state.equals(Environment.MEDIA_MOUNTED)) return;
    File outFile = Environment.getExternalStorageDirectory();
    String path = outFile.getAbsolutePath();
    String fullPath = path + File.separator + FILENAME;

    try {
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fullPath)));
        String str=null;
        while((str=br.readLine())!=null){
            String[] st=str.split(";");
            Store store=new Store(st[0]);
            ArrayList<Article> art=new ArrayList<>();
            st=st[1].split(",");
            for (String s:st) {
                String[] arts=s.split("#");
                Article a=new Article(arts[0],Integer.parseInt(arts[1]));
                art.add(a);
            }
            store.articles=art;
            stores.add(store);
        }
        sp.setAdapter(sp.getAdapter());
        lv.setAdapter(lv.getAdapter());
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        if(requestCode==REQUEST_READ&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            readSD();
        }else if(requestCode==REQUEST_WRITE&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            writeSD(fcsv);
        }
    }
}
