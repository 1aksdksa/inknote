package com.myplatform.note.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NoteSaveParam {

  private String noteId;

  @NotBlank(message = "标题不能为空")
  @Size(max = 200, message = "标题最多 200 字")
  private String title;

  private String contentMd;

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
}
