package com.myplatform.note.vo;

public class NoteListItemVo {
  private String noteId;
  private String title;
  private Long updatedAt;

  public NoteListItemVo() {
  }

  public NoteListItemVo(String noteId, String title, Long updatedAt) {
    this.noteId = noteId;
    this.title = title;
    this.updatedAt = updatedAt;
  }

  public String getNoteId() {
    return noteId;
  }

  public void setNoteId(String noteId) {
    this.noteId = noteId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Long updatedAt) {
    this.updatedAt = updatedAt;
  }
}
