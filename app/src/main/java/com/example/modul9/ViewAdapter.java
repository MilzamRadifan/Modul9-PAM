package com.example.modul9;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final Context context;
  private final List<Note> listNote;
  private DatabaseReference databaseReference;
  private FirebaseAuth mAuth;



  public ViewAdapter (Context context, List<Note> listNote) {
    this.context = context;
    this.listNote = listNote;
    mAuth = FirebaseAuth.getInstance();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder{
    TextView tvTitle, tvDesc;
    Button btEdit, btDelete;

    public ViewHolder(@NonNull View itemView){
      super(itemView);

      tvTitle = itemView.findViewById(R.id.tvTitle);
      tvDesc = itemView.findViewById(R.id.tvDesc);
      btEdit = itemView.findViewById(R.id.btEdit);
      btDelete = itemView.findViewById(R.id.btDelete);
    }
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View vh = LayoutInflater.from(this.context).inflate(R.layout.row_note, parent, false);
    return new ViewHolder(vh);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    Note n = listNote.get(position);
    ViewHolder vh = (ViewHolder) holder;
    vh.tvTitle.setText(n.getTitle());
    vh.tvDesc.setText(n.getDescription());
    vh.btDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        deleteNote(holder.getAdapterPosition());
      }
    });

    vh.btEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateNote(holder.getAdapterPosition());
      }
    });

  }

  @Override
  public int getItemCount() {
    return listNote.size();
  }

  private void updateNote(int position) {
    Note note = listNote.get(position);

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    LayoutInflater inflater = LayoutInflater.from(context);
    View view = inflater.inflate(R.layout.dialog_update_note, null);

    EditText etTitle = view.findViewById(R.id.etTitle);
    EditText etDesc = view.findViewById(R.id.etDesc);

    etTitle.setText(note.getTitle());
    etDesc.setText(note.getDescription());

    builder.setView(view)
            .setTitle("Update Note")
            .setPositiveButton("Update", (dialog, which) -> {
              String title = etTitle.getText().toString();
              String description = etDesc.getText().toString();

              if (!title.isEmpty() && !description.isEmpty()) {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("title", title);
                updateMap.put("description", description);

                databaseReference = FirebaseDatabase.getInstance().getReference("notes")
                        .child(mAuth.getCurrentUser().getUid()).child(note.getKey());

                databaseReference.updateChildren(updateMap).addOnSuccessListener(aVoid -> {
                  note.setTitle(title);
                  note.setDescription(description);
                  notifyItemChanged(position);
                }).addOnFailureListener(e -> {
                  Toast.makeText(context, "Failed to update note", Toast.LENGTH_SHORT).show();
                });
              } else {
                Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
              }
            })
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .create()
            .show();
  }

  private void deleteNote(int position) {
    if (position < 0 || position >= listNote.size()) {
      Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show();
      return;
    }

    Note note = listNote.get(position);
    databaseReference = FirebaseDatabase.getInstance().getReference("notes")
            .child(mAuth.getCurrentUser().getUid()).child(note.getKey());

    databaseReference.removeValue().addOnSuccessListener(aVoid -> {
      if (position < listNote.size()) {
        listNote.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listNote.size());
      }
    }).addOnFailureListener(e -> {
      Toast.makeText(context, "Failed to delete note", Toast.LENGTH_SHORT).show();
    });
  }
}
