package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.Material;
import ru.sberbank.javaschool.edu.domain.Task;
import ru.sberbank.javaschool.edu.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Task getTaskByCourseAndAuthorAndTitle(Course course, User author, String title);

    Task getTaskById(long id);

    List<Task> getTaskByCourseOrderById(Course course);

    List<Task> getTaskByCourse(Course course);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Task task "
            + "SET task.title=:newTitle, task.text=:newText,"
            + " task.edited=true, task.createDate=:editDate"
            + "  WHERE task.id=:id"
    )
    void updateTask(
            @Param("id") long idMaterial,
            @Param("newTitle") String newTitle,
            @Param("newText") String newText,
            @Param("editDate") LocalDateTime editDate
    );
}
