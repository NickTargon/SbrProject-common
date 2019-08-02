package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.TaskComment;

import java.time.LocalDateTime;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    TaskComment findTaskCommentById(long id);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE TaskComment comment "
            + "SET comment.text=:newText, "
            + "comment.edited=true, comment.createDate=:createDate "
            + "WHERE comment.id=:id"
    )
    void updateTaskComment(
            @Param("id") long idComment,
            @Param("newText") String newText,
            @Param("createDate")LocalDateTime createDate
            );
}
