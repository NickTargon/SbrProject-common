package ru.sberbank.javaschool.edu.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.repository.CourseRepository;
import ru.sberbank.javaschool.edu.repository.TaskInfoRepository;
import ru.sberbank.javaschool.edu.repository.TaskRepository;
import ru.sberbank.javaschool.edu.repository.UserTaskRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest("TaskService")
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private CourseRepository courseRepository;
    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private UserTaskRepository userTaskRepository;
    @MockBean
    private TaskInfoRepository taskInfoRepository;
    private final long ID = 1L;

    @Test
    public void createTask() {
        Task task = new Task();
        User user = new User();
        Course course = new Course();
        TaskInfo taskInfo = new TaskInfo();

        task.setTitle("test");
        user.setId(ID);
        course.setId(ID);
        course.setCourseUsers(new HashSet<>(Collections.singletonList(new CourseUser(course, user, Role.TEACHER))));

        Mockito.doReturn(course)
                .when(courseRepository)
                .findCourseById(ID);


        boolean isCreated = taskService.createTask(course, user, task, taskInfo);

        Assert.assertTrue(isCreated);

        Mockito.verify(taskRepository, Mockito.times(1))
                .getTaskByCourseAndAuthorAndTitle(course, user, "test");
        Mockito.verify(taskRepository, Mockito.times(1)).save(task);
        Mockito.verify(taskInfoRepository, Mockito.times(1)).save(taskInfo);
    }

//    @Test
//    public void editTask() {
//        Task taskOld = new Task();
//        Task taskNew = new Task();
//        TaskInfo taskInfo = new TaskInfo();
//        User user = new User();
//        Course course = new Course();
//        LocalDateTime dateTime = LocalDateTime.of(2019,8,15,20,0);
//
//        course.setId(ID);
//        course.setCourseUsers(new HashSet<>(Collections.singletonList(new CourseUser(course, user, Role.TEACHER))));
//
//        taskOld.setId(ID);
//        taskOld.setCourse(course);
//
//        taskNew.setTitle("new");
//        taskNew.setText("newText");
//
//        taskInfo.setMaxMark(100L);
//        taskInfo.setCompleteTime(dateTime);
//
//        Mockito.doReturn(taskOld)
//                .when(taskRepository)
//                .getTaskById(ID);
//
//        Mockito.doReturn(course)
//                .when(courseRepository)
//                .findCourseById(ID);
//
//
//        taskService.editTask(ID, taskNew, taskInfo, user);
//
//        Mockito.verify(taskRepository, Mockito.times(1))
//                .updateTask(ID, "new", "newText");
//        Mockito.verify(taskInfoRepository, Mockito.times(1))
//                .updateTaskInfoByTaskId(ID, dateTime, 100L);
//    }

//    @Test
//    public void editNonexistentTask() {
//        Task taskNew = new Task();
//        User user = new User();
//        TaskInfo taskInfo = new TaskInfo();
//        LocalDateTime dateTime = LocalDateTime.of(2019,8,15,20,0);
//
//        taskNew.setTitle("new");
//        taskNew.setText("newText");
//
//        Mockito.doReturn(null)
//                .when(taskRepository)
//                .getTaskById(ID);
//
//        taskService.editTask(ID, taskNew, taskInfo, user);
//
//        Mockito.verify(taskRepository, Mockito.times(0))
//                .updateTask(ID, "new", "newText");
//        Mockito.verify(taskInfoRepository, Mockito.times(0))
//                .updateTaskInfoByTaskId(ID, dateTime, 100L);
//    }

    @Test
    public void createExistingTask() {
        Task task = new Task();
        User user = new User();
        Course course = new Course();
        TaskInfo taskInfo = new TaskInfo();

        task.setTitle("test");
        user.setId(ID);
        course.setId(ID);

        Mockito.doReturn(new Task())
                .when(taskRepository)
                .getTaskByCourseAndAuthorAndTitle(course, user, "test");

        boolean isCreated = taskService.createTask(course, user, task, taskInfo);

        Assert.assertFalse(isCreated);

        Mockito.verify(taskRepository, Mockito.times(1))
                .getTaskByCourseAndAuthorAndTitle(course, user, "test");
        Mockito.verify(taskRepository, Mockito.times(0)).save(task);
    }

    @Test
    public void deleteTask() {
        User user = new User();
        Course course = new Course();

        user.setId(ID);
        course.setId(ID);
        course.setCourseUsers(new HashSet<>(Collections.singletonList(new CourseUser(course, user, Role.TEACHER))));

        Mockito.doReturn(course)
                .when(courseRepository)
                .findCourseById(ID);

        taskService.deleteTask(ID, ID, user);

        Mockito.verify(courseRepository, Mockito.times(1)).findCourseById(ID);
        Mockito.verify(taskRepository, Mockito.times(1)).deleteById(ID);
    }

}