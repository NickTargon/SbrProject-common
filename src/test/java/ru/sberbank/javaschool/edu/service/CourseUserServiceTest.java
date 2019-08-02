package ru.sberbank.javaschool.edu.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.CourseUser;
import ru.sberbank.javaschool.edu.domain.Role;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.repository.CourseRepository;
import ru.sberbank.javaschool.edu.repository.CourseUserRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseUserServiceTest {
    @Autowired
    private CourseUserService courseUserService;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private CourseUserRepository courseUserRepository;

    @MockBean
    private UserRepository userRepository;


    @Test
    public void addCourseUser() {
        User user = new User();
        Course course = new Course();

        user.setId(1L);
        user.setLogin("test");

        course.setId(1L);

        CourseUser courseUser = new CourseUser(course, user, Role.TEACHER);

        Mockito.doReturn(user)
                .when(userRepository)
                .findUserByLogin("test");
        Mockito.doReturn(course)
                .when(courseRepository)
                .findCourseById(1L);

        courseUserService.addCourseUser(1L,"test", "TEACHER");


        Mockito.verify(courseRepository, Mockito.times(1)).findCourseById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findUserByLogin("test");
        Mockito.verify(courseUserRepository, Mockito.times(1))
                .findCourseUserByCourseAndUser(course, user);
        Mockito.verify(courseUserRepository, Mockito.times(1)).save(courseUser);

    }

    @Test
    public void addCourseUserWhenSameExist() {
        User user = new User();
        Course course = new Course();

        user.setId(1L);
        user.setLogin("test");

        course.setId(1L);

        CourseUser courseUser = new CourseUser(course, user, Role.TEACHER);

        Mockito.doReturn(user)
                .when(userRepository)
                .findUserByLogin("test");
        Mockito.doReturn(course)
                .when(courseRepository)
                .findCourseById(1L);
        Mockito.doReturn(courseUser)
                .when(courseUserRepository)
                .findCourseUserByCourseAndUser(course, user);

        courseUserService.addCourseUser(1L,"test", "TEACHER");


        Mockito.verify(courseRepository, Mockito.times(1)).findCourseById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).findUserByLogin("test");
        Mockito.verify(courseUserRepository, Mockito.times(1))
                .findCourseUserByCourseAndUser(course, user);
        Mockito.verify(courseUserRepository, Mockito.times(0)).save(courseUser);

    }

    @Test
    public void deleteCourseUser() {
        courseUserService.deleteCourseUser(1L);

        Mockito.verify(courseUserRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void isTeacher() {
        User user = new User();
        Course course = new Course();

        CourseUser courseUser = new CourseUser(course, user, Role.TEACHER);

        Mockito.doReturn(courseUser)
                .when(courseUserRepository)
                .findCourseUserByCourseAndUser(course, user);

        Assert.assertTrue(courseUserService.isTeacher(user, course));
    }
}