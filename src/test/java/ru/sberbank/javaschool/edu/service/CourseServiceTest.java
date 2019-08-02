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
import ru.sberbank.javaschool.edu.repository.CourseRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseServiceTest {
    @Autowired
    private CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private CourseUserService courseUserService;

    @Test
    public void addCourse() {
        Course course = new Course();
        course.setCaption("Test");

        boolean isCreated = courseService.addCourse(course);

        Assert.assertTrue(isCreated);
        Assert.assertNotNull(course.getCreateDate());

        Mockito.verify(courseRepository, Mockito.times(1)).findCourseByCaption("Test");
        Mockito.verify(courseRepository, Mockito.times(1)).save(course);
        Mockito.verify(courseUserService, Mockito.times(1)).addAdminToAllCourses(course);

    }

    @Test
    public void addCourseAlreadyExist(){
        Course course = new Course();

        course.setCaption("Test");

        Mockito.doReturn(new Course())
                .when(courseRepository)
                .findCourseByCaption("Test");

        boolean isCreated = courseService.addCourse(course);

        Assert.assertFalse(isCreated);

        Mockito.verify(courseRepository, Mockito.times(1)).findCourseByCaption("Test");
        Mockito.verify(courseRepository, Mockito.times(0)).save(course);
        Mockito.verify(courseUserService, Mockito.times(0)).addAdminToAllCourses(course);
    }

    @Test
    public void removeCourse() {
        courseService.removeCourse(1L);

        Mockito.verify(courseRepository, Mockito.times(1)).deleteById(1L);

    }

    @Test
    public void updateCourse() {
        courseService.updateCourse(1L, "new");

        Mockito.verify(courseRepository, Mockito.times(1))
                .updateCourse(1L, "new");
    }

}