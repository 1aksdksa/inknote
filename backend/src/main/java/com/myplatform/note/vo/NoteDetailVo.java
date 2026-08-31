package com.myplatform.note.vo;

public class NoteDetailVo {
  private String noteId;
  private String title;
  private String contentMd;
  private Long createdAt;
  private Long updatedAt;

  public NoteDetailVo() {
  }

  public NoteDetailVo(
      String noteId, String title, String contentMd, Long createdAt, Long updatedAt) {
    this.noteId = noteId;
    this.title = title;
    this.contentMd = contentMd;
    this.createdAt = createdAt;
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

  public String getContentMd() {
    return contentMd;
  }

  public void setContentMd(String contentMd) {
    this.contentMd = contentMd;
  }

  public Long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Long createdAt) {
    this.createdAt = createdAt;
  }

  public Long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Long updatedAt) {
    this.updatedAt = updatedAt;
  }
}
