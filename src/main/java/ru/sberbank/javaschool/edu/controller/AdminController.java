package ru.sberbank.javaschool.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.Role;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.service.CourseService;
import ru.sberbank.javaschool.edu.service.CourseUserService;
import ru.sberbank.javaschool.edu.service.UserService;

import javax.jws.soap.SOAPBinding;
import java.security.Principal;

@Controller
@RequestMapping("/administration")
public class AdminController {
    private final CourseService courseService;
    private final CourseUserService courseUserService;
    private final UserService userService;

    @Autowired
    public AdminController(
            CourseService courseService,
            CourseUserService courseUserService,
            UserService userService
    ) {
        this.courseService = courseService;
        this.courseUserService = courseUserService;
        this.userService = userService;
    }

    @GetMapping
    public String administration(Model model, Principal principal, @AuthenticationPrincipal User user) {

        if (!courseService.hasAccess(principal)) {

            return "redirect:/";
        }

        model.addAttribute("user", user);
        model.addAttribute("courses", courseService.findAll());

        return "courseList";
    }

    @PostMapping
    public String addCourse(Course course, Model model, @AuthenticationPrincipal User user) {

        if (!courseService.addCourse(course)) {
            model.addAttribute("message", course.getCaption() + " уже существует!");
            model.addAttribute("courses", courseService.findAll());
            model.addAttribute("user", user);
            return "courseList";
        }

        return "redirect:/administration";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {

        courseService.removeCourse(id);

        return "redirect:/administration";
    }

    @GetMapping("/course/{id}")
    public String courseEdit(@PathVariable Long id, Model model,
                             Principal principal, @AuthenticationPrincipal User user) {

        if (!courseService.hasAccess(principal)) {

            return "redirect:/";
        }

        Course course = courseService.findCourseById(id);
        Role[] roles = {Role.STUDENT, Role.TEACHER};

        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("courseusers", course.getCourseUsers());
        model.addAttribute("roles", roles);
        model.addAttribute("allUsers", userService.getUsersNotPresentOnCourse(id));

        return "courseEdit";
    }

    @PostMapping("/course/{id}")
    public String change(@PathVariable Long id, Course course) {

        courseService.updateCourse(id, course.getCaption());

        return "redirect:/administration/course/{id}";
    }

    @PostMapping("/course/{id}/add")
    public String addUser(
            @PathVariable Long id,
            @RequestParam String userName,
            @RequestParam String userRole
    ) {

        courseUserService.addCourseUser(id, userName, userRole);

        return "redirect:/administration/course/{id}";
    }

    @DeleteMapping("/course/{idCourse}/delete/{idCourseUser}")
    public String deleteUserFromCourse(@PathVariable long idCourseUser) {

        courseUserService.deleteCourseUser(idCourseUser);

        return "redirect:/administration/course/{idCourse}";

    }
}
