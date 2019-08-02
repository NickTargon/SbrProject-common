package ru.sberbank.javaschool.edu.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.javaschool.edu.domain.Task;
import ru.sberbank.javaschool.edu.domain.TaskInfo;
import ru.sberbank.javaschool.edu.repository.TaskInfoRepository;


@Service
public class TaskInfoService {

    private final TaskInfoRepository taskInfoRepository;

    public TaskInfoService(TaskInfoRepository taskInfoRepository) {
        this.taskInfoRepository = taskInfoRepository;
    }

    public TaskInfo addTaskInfo(TaskInfo taskInfo) {
        taskInfoRepository.save(taskInfo);

        return taskInfo;
    }

    @Transactional
    public void setTask(@NonNull Task task, TaskInfo taskInfo) {

        taskInfoRepository.setTaskIdById(taskInfo.getId(), task);
    }

    @Transactional
    public void updateTaskInfo(long idTask, TaskInfo taskInfo) {

        taskInfoRepository.updateTaskInfoByTaskId(idTask, taskInfo.getCompleteTime(), taskInfo.getMaxMark());
    }

}
