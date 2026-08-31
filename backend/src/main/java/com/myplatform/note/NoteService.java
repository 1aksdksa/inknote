package com.myplatform.note;

import com.myplatform.common.BusinessException;
import com.myplatform.common.ErrorCodes;
import com.myplatform.note.param.NoteDeleteParam;
import com.myplatform.note.param.NoteDetailParam;
import com.myplatform.note.param.NotePageParam;
import com.myplatform.note.param.NoteSaveParam;
import com.myplatform.note.vo.NoteDetailVo;
import com.myplatform.note.vo.NoteListItemVo;
import com.myplatform.note.vo.NotePageVo;
import com.myplatform.security.AuthContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class NoteService {

  private final NoteRepository noteRepository;

  public NoteService(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  @Transactional(readOnly = true)
  public NotePageVo page(NotePageParam param) {
    String userId = AuthContext.requireUserId();
    int pageIndex = Math.toIntExact(param.getCurrent() - 1);
    int size = Math.toIntExact(param.getSize());
    Page<NoteEntity> page = noteRepository.pageByUserIdAndKeyword(
        userId,
        param.getKeyword(),
        PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "updatedAt")));
    List<NoteListItemVo> records = page.getContent().stream()
        .map(note -> new NoteListItemVo(
            note.getId(),
            note.getTitle(),
            note.getUpdatedAt().toEpochMilli()))
        .toList();
    return new NotePageVo(param.getCurrent(), param.getSize(), page.getTotalElements(), records);
  }

  @Transactional(readOnly = true)
  public NoteDetailVo detail(NoteDetailParam param) {
    String userId = AuthContext.requireUserId();
    NoteEntity note = noteRepository.findByIdAndUserId(param.getNoteId(), userId)
        .orElseThrow(() -> new BusinessException(ErrorCodes.NOTE_NOT_FOUND, "笔记不存在"));
    return toDetailVo(note);
  }

  @Transactional
  public NoteDetailVo save(NoteSaveParam param) {
    String userId = AuthContext.requireUserId();
    Instant now = Instant.now();
    NoteEntity note;
    if (StringUtils.hasText(param.getNoteId())) {
      note = noteRepository.findByIdAndUserId(param.getNoteId(), userId)
          .orElseThrow(() -> new BusinessException(ErrorCodes.NOTE_NOT_FOUND, "笔记不存在"));
    } else {
      note = new NoteEntity();
      note.setId(UUID.randomUUID().toString());
      note.setUserId(userId);
      note.setCreatedAt(now);
    }
    note.setTitle(param.getTitle().trim());
    note.setContentMd(param.getContentMd() == null ? "" : param.getContentMd());
    note.setUpdatedAt(now);
    noteRepository.save(note);
    return toDetailVo(note);
  }

  @Transactional
  public void delete(NoteDeleteParam param) {
    String userId = AuthContext.requireUserId();
    NoteEntity note = noteRepository.findByIdAndUserId(param.getNoteId(), userId)
        .orElseThrow(() -> new BusinessException(ErrorCodes.NOTE_NOT_FOUND, "笔记不存在"));
    noteRepository.delete(note);
  }

  private NoteDetailVo toDetailVo(NoteEntity note) {
    return new NoteDetailVo(
        note.getId(),
        note.getTitle(),
        note.getContentMd(),
        note.getCreatedAt().toEpochMilli(),
        note.getUpdatedAt().toEpochMilli());
  }
}