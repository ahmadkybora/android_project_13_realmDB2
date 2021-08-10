package com.example.android_project_13_realmdb2.controller;

import com.example.android_project_13_realmdb2.models.Note;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class NoteController {

    private static NoteController instance;
    private Realm realm;

    public interface onLoadNotes {
        void onLoadComplete(RealmResults<Note> notes);
    }

    private NoteController() {
        realm = Realm.getDefaultInstance();
    }

    public static NoteController getInstance() {
        if (instance == null) {
            instance = new NoteController();
        }
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public void refresh() {
        realm.refresh();
    }

    public void insertOrUpdateNote(Note note) {
        realm.copyToRealmOrUpdate(note);
        realm.commitTransaction();
    }

    public void getNotesAsync(final onLoadNotes onLoadNotes) {
        realm.where(Note.class).sort("noteId", Sort.DESCENDING).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<Note>>() {
            @Override
            public void onChange(RealmResults<Note> notes) {
                if (onLoadNotes != null) {
                    onLoadNotes.onLoadComplete(notes);
                    return;
                }
            }
        });
    }

    public Note getNote(int noteId) {
        return realm.where(Note.class).equalTo("noteId", noteId).findFirst();
    }

    public void deleteAll() {
        realm.beginTransaction();
        realm.delete(Note.class);
        realm.commitTransaction();
    }

    public void deleteNote(Note note) {
        realm.beginTransaction();
        note.deleteFromRealm();
        realm.commitTransaction();
    }
}
