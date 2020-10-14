package org.techtown.diary;

import android.view.View;

public interface OnNoteItemClickListener {
    void onItemClick(NoteAdapter.ViewHolder holder, View view, int position);
}
