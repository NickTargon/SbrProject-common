package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.MaterialComment;

import java.time.LocalDateTime;

public interface MaterialCommentRepository extends JpaRepository<MaterialComment, Long> {
    MaterialComment findMaterialCommentById(Long id);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE MaterialComment comment "
                    + "SET comment.text=:newText, "
                    + "comment.edited=true, comment.createdate=:createDate "
                    + "WHERE comment.id=:id"
    )
    void updateMaterialComment(
            @Param("id") long idComment,
            @Param("newText") String newText,
            @Param("createDate") LocalDateTime createDate
    );
}
