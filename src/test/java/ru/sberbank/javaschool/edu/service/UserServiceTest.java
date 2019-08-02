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
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.repository.CourseRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest("UserService")
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    CourseRepository courseRepository;
    @MockBean
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setLogin("test");
        user.setPassword("1");
        user.setEmail("guyizuneb@mail-point.net");
        user.setName("");
        user.setSurname("");
        userRepository.save(user);
    }

    @Test
    public void loadUserByUsername() {
        User user = userRepository.findUserByLogin("anaR");
        Mockito.doReturn(user)
                .when(userService)
                .loadUserByUsername("anaR");
        Mockito.doReturn(null)
                .when(userService)
                .loadUserByUsername("noUser");
    }

    @Test
    public void addUser() {
        User user = new User();
        user.setLogin("example");
        user.setPassword("");
        user.setEmail("");
        user.setName("");
        user.setSurname("");

        Mockito.doReturn("Необходимые для заполнения поля пусты")
                .when(userService)
                .addUser(user);
        user.setLogin("anaR");
        user.setPassword("123");
        user.setEmail("guyizuneb@mail-point.net");
        Mockito.doReturn("Пользователь существует")
                .when(userService)
                .addUser(user);
        user.setLogin("example");
        Mockito.doReturn("ok")
                .when(userService)
                .addUser(user);

    }

    @Test
    public void updateUser() {
        User userFromDB = userRepository.findUserByLogin("test");
        Assert.assertEquals(userFromDB.getName(), "");
        Assert.assertEquals(userFromDB.getSurname(), "");

        User user = new User();
        user.setName("NewName");
        user.setSurname("NewSurname");
        Mockito.doReturn(false)
                .when(userService)
                .updateUser("noUser", user);
        Mockito.doReturn(true)
                .when(userService)
                .updateUser("test", user);

    }

    @Test
    public void isUserActivated() {
        //юзер test создается каждый раз при запуске всего этого модуля тестов
        // и по окончании уничтожается - см @Before/@After
        User userFromDB = userRepository.findUserByLogin("test");
        if (userFromDB != null) {
            Assert.assertNull(userFromDB.getActcode());
        }
        User userFromDB2 = userRepository.findUserByLogin("anaR");
        Assert.assertEquals(userFromDB2.getActcode(), "ok");

    }

    @Test
    public void getUsersNotPresentOnCourse() {
        User userFromDB = userRepository.findUserByLogin("test");
        Course course = courseRepository.findCourseById(new Long(12));
        Assert.assertNotEquals(course, null);

//        Mockito.doReturn(users)
//                .when(userService)
//                .getUsersNotPresentOnCourse(12);
    }

    @After
    public void tearDown() throws Exception {
        userRepository.delete(userRepository.findUserByLogin("test"));
    }
}