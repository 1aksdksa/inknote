package com.myplatform.note;

import com.myplatform.common.ApiResult;
import com.myplatform.note.param.NoteDeleteParam;
import com.myplatform.note.param.NoteDetailParam;
import com.myplatform.note.param.NotePageParam;
import com.myplatform.note.param.NoteSaveParam;
import com.myplatform.note.vo.NoteDetailVo;
import com.myplatform.note.vo.NotePageVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/note")
public class NoteController {

  private final NoteService noteService;

  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @PostMapping("/page")
  public ApiResult<NotePageVo> page(@Valid @RequestBody NotePageParam param) {
    return ApiResult.ok(noteService.page(param));
  }

  @PostMapping("/detail")
  public ApiResult<NoteDetailVo> detail(@Valid @RequestBody NoteDetailParam param) {
    return ApiResult.ok(noteService.detail(param));
  }

  @PostMapping("/save")
  public ApiResult<NoteDetailVo> save(@Valid @RequestBody NoteSaveParam param) {
    return ApiResult.ok(noteService.save(param));
  }

  @PostMapping("/delete")
  public ApiResult<Void> delete(@Valid @RequestBody NoteDeleteParam param) {
    noteService.delete(param);
    return ApiResult.ok();
  }
}
