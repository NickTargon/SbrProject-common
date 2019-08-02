package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.CourseUser;
import ru.sberbank.javaschool.edu.domain.Role;
import ru.sberbank.javaschool.edu.domain.User;

import java.util.List;

public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {
    CourseUser findCourseUserById(long id);

    CourseUser findCourseUserByCourseAndUser(Course course, User user);

    List<CourseUser> findCourseUserByUser(User user);

    List<CourseUser> findCourseUserByCourse(Course course);

    List<CourseUser> findCourseUserByCourseAndRole(Course course, Role role);
}
