package com.myplatform.note.param;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class NotePageParam {

  @NotNull(message = "current 不能为空")
  @Min(value = 1, message = "current 从 1 开始")
  private Long current;

  @NotNull(message = "size 不能为空")
  @Min(value = 1, message = "size 至少为 1")
  @Max(value = 100, message = "size 不能超过 100")
  private Long size;

  private String keyword;

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

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }
}
