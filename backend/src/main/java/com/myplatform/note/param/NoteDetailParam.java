package com.myplatform.note.param;

import jakarta.validation.constraints.NotBlank;

public class NoteDetailParam {

  @NotBlank(message = "noteId 不能为空")
  private String noteId;

  public String getNoteId() {
    return noteId;
  }

  public void setNoteId(String noteId) {
    this.noteId = noteId;
  }
}
