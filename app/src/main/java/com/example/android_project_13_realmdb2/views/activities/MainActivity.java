package com.example.android_project_13_realmdb2.views.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android_project_13_realmdb2.R;
import com.example.android_project_13_realmdb2.controller.NoteController;
import com.example.android_project_13_realmdb2.models.Note;
import com.example.android_project_13_realmdb2.views.adapters.NoteAdapter;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NoteAdapter.NoteListener {

    private TextView txt_no_items;
    private RecyclerView rcl_note;
    private ProgressBar prg_loading;
    private FloatingActionButton fab_insert_note;
    private Handler handler;
    private ViewGroup btn_edit, btn_delete;
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        initViews();
        addNote();
        loadNotes();
    }

    public void initViews() {
        txt_no_items = findViewById(R.id.txt_no_items);
        fab_insert_note = findViewById(R.id.fab_insert_note);
        rcl_note = findViewById(R.id.rcl_note);
        prg_loading = findViewById(R.id.prg_loading);
        fab_insert_note.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_insert_note:
                insertOrUpdateNoteDialog(0, null);
                break;
        }
    }

    public void loadNotes() {
        showLoading();
        NoteController.getInstance().getNotesAsync(new NoteController.onLoadNotes() {
            @Override
            public void onLoadComplete(RealmResults<Note> notes) {
                hideLoading();
                setupRecyclerView(notes);
            }
        });
    }

    public void setupRecyclerView(RealmResults<Note> notes) {
        rcl_note.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(this, notes);
        noteAdapter.setNoteListener(this);
        rcl_note.setAdapter(noteAdapter);
    }

    private void showLoading() {
        prg_loading.setVisibility(View.VISIBLE);
        txt_no_items.setVisibility(View.GONE);
        rcl_note.setVisibility(View.GONE);
    }

    private void hideLoading() {
        prg_loading.setVisibility(View.GONE);
        txt_no_items.setVisibility(View.GONE);
        rcl_note.setVisibility(View.VISIBLE);
    }

    private void noItems() {
        prg_loading.setVisibility(View.GONE);
        txt_no_items.setVisibility(View.VISIBLE);
        rcl_note.setVisibility(View.GONE);
    }

    public void addNote() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                showLoading();
                for (int i = 0; i < 1000; i++) {
                    Note note = new Note();
                    note.setNoteId(i);
                    note.setTitle("title : " + i);
                    note.setNote("note : " + i);
                    note.setDate("date : " + i);
                    note.setTime("time : " + i);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                hideLoading();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                hideLoading();
            }
        });
    }

    public void showBottomSheedDialog(int position, Note note) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_bottom_sheet_dialog, null);
        btn_edit = view.findViewById(R.id.btn_edit);
        btn_delete = view.findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                needShowAlertDialog(position, note);
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                insertOrUpdateNoteDialog(position, note);
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();
    }

    public void needShowAlertDialog(int position, Note note) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Note");
        alertDialog.setMessage("Are you sure");

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (noteAdapter != null) {
                    NoteController.getInstance().deleteNote(note);
                    noteAdapter.notifyDataSetChanged();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void insertOrUpdateNoteDialog(final int position, final Note note) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("ok");
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_insert_update, null);

        Button btn_confirm = view.findViewById(R.id.btn_confirm);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        EditText edt_note = view.findViewById(R.id.edt_note);
        EditText edt_title = view.findViewById(R.id.edt_title);
        EditText edt_note_id = view.findViewById(R.id.edt_note_id);

        if (note != null) {
            edt_note.setText(note.getNote());
            edt_title.setText(note.getTitle());
            edt_note_id.setVisibility(View.GONE);
        } else {
            edt_note.setVisibility(View.VISIBLE);
        }
        final AlertDialog dialog = alertDialog.create();

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                NoteController.getInstance().getRealm().beginTransaction();
                if (note != null) {
                    NoteController.getInstance().getRealm().beginTransaction();
                    note.setTitle(edt_title.getText().toString());
                    note.setNote(edt_title.getText().toString());
                    NoteController.getInstance().insertOrUpdateNote(note);
                    noteAdapter.notifyDataSetChanged();
                    return;
                }
                Note _note = new Note();
                _note.setTitle(edt_title.getText().toString());
                _note.setNote(edt_title.getText().toString());
                _note.setNoteId(Integer.parseInt(edt_note_id.getText().toString().trim().replace(" ", "")));
                _note.setTime("12 : 56");
                _note.setDate("Apr 23");
                _note.setNoteId(100);
                NoteController.getInstance().insertOrUpdateNote(_note);
                noteAdapter.notifyDataSetChanged();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    @Override
    public void onClickNote(int position, Note note) {
        showBottomSheedDialog(position, note);
    }
}