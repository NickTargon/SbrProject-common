package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.Task;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.domain.UserTask;

import java.util.List;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    UserTask findUserTaskByUserAndTask(User user, Task task);

    List<UserTask> findUserTaskByTask(Task task);

    @Query(
            value = "SELECT u.* FROM edu_user_task u WHERE u.user = :user_id ",
            nativeQuery = true
    )
    List<UserTask> getUserTasksByUserId(@Param("user_id") long idUser);
}
