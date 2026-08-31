package com.myplatform.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<NoteEntity, String> {

  Optional<NoteEntity> findByIdAndUserId(String id, String userId);

  @Query("""
      select n from NoteEntity n
      where n.userId = :userId
        and (
          :keyword is null
          or :keyword = ''
          or lower(n.title) like lower(concat('%', :keyword, '%'))
        )
      """)
  Page<NoteEntity> pageByUserIdAndKeyword(
      @Param("userId") String userId,
      @Param("keyword") String keyword,
      Pageable pageable);
}
