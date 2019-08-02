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
import ru.sberbank.javaschool.edu.repository.MaterialRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MaterialServiceTest {
    @Autowired
    private MaterialService materialService;

    @MockBean
    private MaterialRepository materialRepository;

    @MockBean
    private CourseRepository courseRepository;

    private final long ID = 1L;

    @Test
    public void createMaterial() {
        Material material = new Material();
        User user = new User();
        Course course = new Course();

        course.setCourseUsers(new HashSet<>(Collections.singletonList(new CourseUser(course, user, Role.TEACHER))));

        Mockito.doReturn(course)
                .when(courseRepository)
                .findCourseById(ID);

        material.setTitle("test");
        user.setId(ID);
        course.setId(ID);

        boolean isCreated = materialService.createMaterial(course, user, material);

        Assert.assertTrue(isCreated);
        Assert.assertEquals(course, material.getCourse());
        Assert.assertEquals(user, material.getAuthor());
        Assert.assertNotNull(material.getCreateDate());

        Mockito.verify(materialRepository, Mockito.times(1))
                .getMaterialByCourseAndTitle(course, "test");
        Mockito.verify(materialRepository, Mockito.times(1)).save(material);
    }

    @Test
    public void createExistingMaterial() {
        Material material = new Material();
        User user = new User();
        Course course = new Course();

        material.setTitle("test");
        user.setId(ID);
        course.setId(ID);

        Mockito.doReturn(new Material())
                .when(materialRepository)
                .getMaterialByCourseAndTitle(course,"test");

        boolean isCreated = materialService.createMaterial(course, user, material);

        Assert.assertFalse(isCreated);

        Mockito.verify(materialRepository, Mockito.times(1))
                .getMaterialByCourseAndTitle(course,"test");
        Mockito.verify(materialRepository, Mockito.times(0)).save(material);
    }

    @Test
    public void editMaterial() {
        Material materialOld = new Material();
        Material material = new Material();
        long ID = 1L;

        materialOld.setId(ID);
        materialOld.setTitle("old");
        materialOld.setText("oldText");

        material.setTitle("new");
        material.setText("newText");

        Mockito.doReturn(materialOld)
                .when(materialRepository)
                .getMaterialById(ID);

        materialService.editMaterial(ID, material);


//        Mockito.verify(materialRepository, Mockito.times(1))
//                .updateMaterial(ID, "new", "newText", LocalDateTime.);
    }

    @Test
    public void deleteMaterial() {
        Material material = new Material();
        User user = new User();
        Course course = new Course();

        course.setId(ID);
        course.setCourseUsers(new HashSet<>(Collections.singletonList(new CourseUser(course, user, Role.TEACHER))));
        material.setId(ID);
        material.setCourse(course);
        user.setId(ID);

        Mockito.doReturn(course)
                .when(courseRepository)
                .findCourseById(ID);
        Mockito.doReturn(material)
                .when(materialRepository)
                .getMaterialById(ID);

        materialService.deleteMaterial(ID, user);

        Mockito.verify(materialRepository, Mockito.times(1)).deleteById(ID);
    }
}