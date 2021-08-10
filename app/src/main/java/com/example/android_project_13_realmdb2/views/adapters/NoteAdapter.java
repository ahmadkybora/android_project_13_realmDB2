package com.example.android_project_13_realmdb2.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_project_13_realmdb2.models.Note;
import com.example.android_project_13_realmdb2.views.viewholders.NoteViewHolder;
import com.example.android_project_13_realmdb2.R;

import io.realm.RealmResults;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private RealmResults<Note> notes;
    private Context context;
    private NoteListener noteListener;

    public interface NoteListener {
        void onClickNote(int position, Note note);
    }

    public void setNoteListener(NoteListener noteListener) {
        this.noteListener = noteListener;
    }

    public NoteAdapter(Context context, RealmResults<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adaptor_note, null);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, final int position) {
        if (notes == null) {
            return;
        }

        final Note note = notes.get(position);
        holder.txt_title.setText(note.getTitle());
        holder.txt_note.setText(note.getNote());
        holder.txt_date_time.setText(note.getDate() + " _ " + note.getTime());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (noteListener != null) {
                    noteListener.onClickNote(position, note);
                    return;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (notes == null ? 0 : notes.size());
    }
}
