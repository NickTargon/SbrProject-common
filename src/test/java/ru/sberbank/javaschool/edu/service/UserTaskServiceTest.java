package ru.sberbank.javaschool.edu.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sberbank.javaschool.edu.domain.Task;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.domain.UserTask;
import ru.sberbank.javaschool.edu.repository.TaskRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;
import ru.sberbank.javaschool.edu.repository.UserTaskRepository;

@RunWith(SpringRunner.class)
@SpringBootTest("UserTaskService")
public class UserTaskServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskRepository taskRepository;
    @MockBean
    private UserTaskRepository userTaskRepository;
    @MockBean
    private UserTaskService userTaskService;

    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setLogin("test");
        user.setPassword("1");
        user.setEmail("guyizuneb@mail-point.net");
        user.setName("");
        user.setSurname("");
        userService.addUser(user);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.delete(userRepository.findUserByLogin("test"));
    }

    @Test
    public void createUserTask() {
        User user = userRepository.findUserByLogin("test");
        Task task = taskRepository.getTaskById(17);
        User admin = userRepository.findUserByLogin("admin");

        UserTask realUserTask = new UserTask();
        realUserTask.setTask(taskRepository.getTaskById(21));
        realUserTask.setUser(userRepository.findUserByLogin("testUser"));

        UserTask newUserTask = userTaskService.createUserTask(user, task);
        userTaskRepository.findUserTaskByUserAndTask(user, task);

        Mockito.doReturn(realUserTask)
                .when(userTaskRepository)
                .findUserTaskByUserAndTask(userRepository.findUserByLogin("testUser"), taskRepository.getTaskById(21));
        Mockito.verify(userTaskRepository,
                Mockito.times(1)).findUserTaskByUserAndTask(user, task);
        Assert.assertNull(userTaskService.createUserTask(admin, task));

    }

    @Test
    public void submitTask() {
        User realUser = userRepository.findUserById(new Long(2));
        User testUser = userRepository.findUserByLogin("test");

        Mockito.doReturn(true)
                .when(userTaskService)
                .submitTask(21, realUser, 12);
        Mockito.doReturn(false)
                .when(userTaskService)
                .submitTask(21, testUser, 12);

    }

    @Test
    public void setMarkToUser() {
        Mockito.doReturn(true)
                .when(userTaskService)
                .setMarkToUser(21, 2, 100);
        Mockito.doReturn(false)
                .when(userTaskService)
                .setMarkToUser(21, 2, 110);
        Mockito.doReturn(false)
                .when(userTaskService)
                .setMarkToUser(21, 2, -1);
        Mockito.doReturn(false)
                .when(userTaskService)
                .setMarkToUser(21, 11, 100);
    }
}