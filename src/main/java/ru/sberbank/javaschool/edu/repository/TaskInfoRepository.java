package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.Task;
import ru.sberbank.javaschool.edu.domain.TaskInfo;

import java.time.LocalDateTime;

public interface TaskInfoRepository extends JpaRepository<TaskInfo, Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE TaskInfo taskInfo "
            + "SET taskInfo.task = :task"
            + "  WHERE taskInfo.id=:id"
    )
    void setTaskIdById(
            @Param("id") long idTaskInfo,
            @Param("task") Task task
    );

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE edu_task_info ti "
            + "SET ti.compltime = :complTime, ti.maxmark = :maxMark"
            + "  WHERE ti.task_id=:taskID",
            nativeQuery = true
    )
    void updateTaskInfoByTaskId(
            @Param("taskID") long idTask,
            @Param("complTime") LocalDateTime complTime,
            @Param("maxMark") long maxMark
    );
}
