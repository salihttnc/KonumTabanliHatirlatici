package com.salihttnc.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotListeleme1 extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerViewAdapter;
    ArrayList<Not> mNotes;
    Not not, not1;
    private Date tarih ;
    private SimpleDateFormat bugun;
    private ArrayList<String> keys;
    private DatabaseReference mDatabaseRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_listeleme);
  keys=new ArrayList<>();

        bugun = new SimpleDateFormat("dd/MM/yyyy");
        final String currentDateandTime = bugun.format(new Date());
            mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Notes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notlar");
        mNotes = new ArrayList<>();
          getKeys();
        progressDialog = new ProgressDialog(NotListeleme1.this);
        progressDialog.setMessage("Lütfen Bekleyiniz..");
        progressDialog.show();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notlar");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNotes.clear();
                not = new Not();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        not = ds.getValue(Not.class);
                        if (not.getDate() != null){
                            if (not.getDate().equals(currentDateandTime)) {
                                mNotes.add(not);
                            }
                    }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //not=new Not("as","asa","asa","asa","sas","asa");
        //not1=new Not("as","asa","asa","asa","sas","asa");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addAdapter();
            }
        }, 3000);

    }

    public void addAdapter(){

        recyclerViewAdapter = findViewById(R.id.liste);
        RecyclerViewAdapter1 myAdapter = new RecyclerViewAdapter1(this, mNotes,keys);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewAdapter.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter.setAdapter(myAdapter);
        progressDialog.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, MapsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);

    }
    private void getKeys() {
        Log.d("tag", "getKeys: EmergencyCall ");


        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keys.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    keys.add(ds.getKey());


                }
                Log.d("tag", "onDataChange Get Keys: EmergencyCall " + keys.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotListeleme1.this, "Veri tabanı hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
