package com.example.modul9;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewNoteActivity extends AppCompatActivity {

  private DatabaseReference databaseReference;
  private FirebaseAuth mAuth;
  private FirebaseUser curUser;
  RecyclerView recyclerView;
  List<Note> listNote = new ArrayList<>();
  ViewAdapter adapter;

  FloatingActionButton btAdd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_note);

    recyclerView = findViewById(R.id.rvViewNote);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    mAuth = FirebaseAuth.getInstance();
    curUser = mAuth.getCurrentUser();
    databaseReference = FirebaseDatabase.getInstance().getReference("notes").child(curUser.getUid());

    adapter = new ViewAdapter(this, listNote);
    recyclerView.setAdapter(adapter);

    btAdd = findViewById(R.id.btAdd);
    btAdd.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(ViewNoteActivity.this, InsertNoteActivity.class);
        startActivity(intent);
      }
    });

    readData();

  }

  @Override
  protected void onResume() {
    super.onResume();
    readData();
  }


  private void readData() {
    databaseReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        listNote.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
          Note note = dataSnapshot.getValue(Note.class);
          if (note != null) {
            note.setKey(dataSnapshot.getKey());
            listNote.add(note);
          }
        }
        adapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(ViewNoteActivity.this, "gagal mengambil data",
                Toast.LENGTH_SHORT).show();
      }
    });
  }
}