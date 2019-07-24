package com.salihttnc.myapplication;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<Not> mNote;
    private ImageView mDeleteBtn,mEditBtn;
    private DatabaseReference mDatabaseRef;
    private String userId;
    private Integer global_postion;
    private ProgressDialog dialog;
    private ArrayList<String> keys;
    private Intent editPerson,editPerson1,editPerson2;
    private TextView note_tittle;
    private ImageView button,button1;

    public RecyclerViewAdapter(Context mContext, List<Not> mNote,ArrayList<String> keys) {
        this.mContext = mContext;
        this.mNote = mNote;
        this.keys=keys;
        if(mNote.size()==0)
            Toast.makeText(mContext, "Listelenecek  veri yok ", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view;
        LayoutInflater mInflater=LayoutInflater.from(mContext);
        view=mInflater.inflate(R.layout.adapterlayout,parent,false );

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
          Not notOrnek=new Not();
          notOrnek=mNote.get(position);
          holder.setData(notOrnek,position);
          holder.setData2(notOrnek,position);
          holder.setData3(notOrnek,position);
          holder.setData4(notOrnek,position);
    }
    public void deleteItem(int position)
    {
        this.global_postion=position;
        dialog=new ProgressDialog(mContext);
        dialog.setMessage("Veri Siliniyor");
        dialog.show();
        removeFromDatabase();


    }
    public void removeFromDatabase() {
           userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef=FirebaseDatabase.getInstance().getReference()
                .child("Notes").child(userId).child("Notlar");
        mDatabaseRef.child(keys.get(global_postion)).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful())
                                               {
                                                   notifyItemRemoved(global_postion);// listeyi güncelliyor
                                                   notifyItemRangeChanged(global_postion, mNote.size()); // listeyi güncellemekle berebaer aralığıda değiştiriyor
                                                   mNote.remove(global_postion);
                                                   dialog.dismiss();

                                                   //notifyDataSetChanged(); Yukarıdaki iki satır kodun yerine kullanılabilir ama daha masraflıdır ve animasyon yok

                                               }
                                               else
                                                   Toast.makeText(mContext, "İşlem Başarısız", Toast.LENGTH_SHORT).show();
                                               dialog.dismiss();
                                           }
                                       }
                );
        Log.d("tag", "removedFromDatabase: ");

    }
    @Override
    public int getItemCount() {
        return mNote.size();
    }

     class MyViewHolder extends  RecyclerView.ViewHolder
    {
        TextView note,txtView;
        int position=0;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtView=itemView.findViewById(R.id.txtView);
            mDeleteBtn=itemView.findViewById(R.id.ic_deletePerson);
            mEditBtn=itemView.findViewById(R.id.ic_editPerson);
            note=itemView.findViewById(R.id.note_tittle);
            button=itemView.findViewById(R.id.button);
            button1=itemView.findViewById(R.id.button1);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPerson2=new Intent(mContext,HavaDurumu.class);
                    editPerson2.putExtra("key",keys.get(position));
                    mContext.startActivity(editPerson2);
                }
            });

            mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   deleteItem(position);
                }
            });
 mEditBtn.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         editPerson=new Intent(mContext,EditMapsActivity.class);
         editPerson.putExtra("key",keys.get(position));
               mContext.startActivity(editPerson);

     }
 });
 button.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         editPerson1=new Intent(mContext,MapsActivity2.class);
         editPerson1.putExtra("key",keys.get(position));
         mContext.startActivity(editPerson1);
     }
 });


        }

        public void setData(Not not, int position)
        {
            this.txtView.setText("Notunuz: "+not.getNote());
            this.note.setText("Baslık: "+not.getTittle());
            this.position=position;



        }
        public void setData2(Not not, int position)
        {
            this.txtView.setText("Notunuz: "+not.getNote());
            this.note.setText("Baslık: "+not.getTittle());
            this.position=position;



        }
        public void setData3(Not not, int position)
        {
            this.txtView.setText("Notunuz: "+not.getNote());
            this.note.setText("Baslık: "+not.getTittle());
            this.position=position;



        }
        public void setData4(Not not, int position)
        {
            this.txtView.setText("Notunuz: "+not.getNote());
            this.note.setText("Baslık: "+not.getTittle());
            this.position=position;



        }


    }
}