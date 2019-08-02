package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.User;

import java.util.List;
import java.util.Set;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findCourseByCaption(String caption);

    Course findCourseById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Course course SET course.caption = :caption WHERE course.id = :id")
    void updateCourse(@Param("id") Long idCourse, @Param("caption") String newCaption);

}
