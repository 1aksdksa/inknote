package com.myplatform.note.vo;

import java.util.List;

public class NotePageVo {
  private Long current;
  private Long size;
  private Long total;
  private List<NoteListItemVo> records;

  public NotePageVo() {
  }

  public NotePageVo(Long current, Long size, Long total, List<NoteListItemVo> records) {
    this.current = current;
    this.size = size;
    this.total = total;
    this.records = records;
  }

  public Long getCurrent() {
    return current;
  }

  public void setCurrent(Long current) {
    this.current = current;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public List<NoteListItemVo> getRecords() {
    return records;
  }

  public void setRecords(List<NoteListItemVo> records) {
    this.records = records;
  }
}
