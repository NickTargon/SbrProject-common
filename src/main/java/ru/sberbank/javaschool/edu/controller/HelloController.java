package ru.sberbank.javaschool.edu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.repository.CourseRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;
import ru.sberbank.javaschool.edu.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class HelloController {

    private final UserService userService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final CourseService courseService;
    private final TaskService taskService;
    private final PublicationFileService publicationFileService;
    private final MaterialService materialService;

    @Autowired
    public HelloController(
            UserService userService,
            UserRepository userRepository,
            CourseService courseService,
            TaskService taskService,
            PublicationFileService publicationFileService,
            MaterialService materialService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.courseService = courseService;
        this.taskService = taskService;
        this.publicationFileService = publicationFileService;
        this.materialService = materialService;
    }

    @GetMapping("/")
    public String greeting(Model model) {
        logger.info("Main page");
        return "redirect:/user";
    }

    @GetMapping("/login")  //чтобы не было двойной авторизации одного юзера
    public String isLogin() {
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)
        ) {
            logger.info("Try to login in login statement");
            return "redirect:/user";
        }

        return "/login";
    }

    @GetMapping("/test")
    public String greeting(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);

        activateUsers();

        return "test";
    }

    @GetMapping("/nested/{idCourse}")
    public String nested(@PathVariable long idCourse,
                         @AuthenticationPrincipal User user,
                         Model model) {
        Course course = courseService.findCourseById(idCourse);
        List<Material> materials = course.getMaterials();
        Set<CourseUser> courseUsers = course.getCourseUsers();
        List<Task> tasks = taskService.getTaskByCourseOrderById(course);
        List<CourseUser> teachers = new ArrayList<>();
        List<CourseUser> students = new ArrayList<>();

        for (CourseUser courseUser : courseUsers) {
            if (courseUser.getRole().equals(Role.TEACHER)) {
                if (!courseUser.getUser().getLogin().equals("admin")) {
                    teachers.add(courseUser);
                }
            } else {
                students.add(courseUser);
            }
        }

        publicationFileService.getMaterialsFilesFromYDisk(materials);
        model.addAttribute("course", course);
        model.addAttribute("materials", materials);
        model.addAttribute("canCreate", materialService.canCreateMaterial(course, user));
        model.addAttribute("currentUser", user);
        model.addAttribute("students", students);
        model.addAttribute("teachers", teachers);
        model.addAttribute("tasks", tasks);

        return "course_nested_comment";
    }


    @PostMapping("/sendcode")
    public String sendCodeOneMoreTime(@RequestParam String login) {
        User user = userService.findUserbyLogin(login);
        if (user == null) {
           return "redirect:/login";
        }
        if (user.getActcode().equals("ok")) {
            return "redirect:/login";
        }
        return "redirect:/activate/"+user.getActcode();
    }


    //для активации всех аккаунтов чтобы не активировать черех почту
    private void activateUsers() {
        for (User u : userRepository.findAll()) {
            u.setActcode("ok");
        }
    }


}
