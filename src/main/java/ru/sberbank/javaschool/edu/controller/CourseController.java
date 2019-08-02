package ru.sberbank.javaschool.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.ls.LSInput;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.service.*;

import java.security.Principal;
import java.util.*;

@Controller
public class CourseController {
    private final CourseService courseService;
    private final CourseUserService courseUserService;
    private final MaterialService materialService;
    private final PublicationFileService publicationFileService;
    private final UserService userService;
    private final UserTaskService userTaskService;
    private final TaskService taskService;

    @Autowired
    public CourseController(
            CourseService courseService,
            CourseUserService courseUserService,
            MaterialService materialService,
            PublicationFileService publicationFileService,
            UserService userService, UserTaskService userTaskService, TaskService taskService) {
        this.courseService = courseService;
        this.courseUserService = courseUserService;
        this.materialService = materialService;
        this.publicationFileService = publicationFileService;
        this.userService = userService;
        this.userTaskService = userTaskService;
        this.taskService = taskService;
    }

    @GetMapping("/course/{idCourse}")
    public String showCourse(
            @PathVariable long idCourse,
            @AuthenticationPrincipal User user,
            Model model
    ) {
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

        return "course";
    }

    @GetMapping("/courses")
    public String showAllUserCourses(Model model, @AuthenticationPrincipal User user) {
        Map<Long, Map<String, String>> coursesInfo = new HashMap<>();

        List<CourseUser> allUserCourses = courseUserService.getUserCourses(user.getLogin());
        for (CourseUser courseUser : allUserCourses) {
            Course course = courseUser.getCourse();
            coursesInfo.put(course.getId(), new HashMap<>());
            Map<String, String> params = coursesInfo.get(course.getId());
            params.put("usersCount", String.valueOf(course.getCourseUsers().size()));
            params.put("materialsCount", String.valueOf(course.getMaterials().size()));
        }

        model.addAttribute("courses", allUserCourses);
        model.addAttribute("currentUser", user);
        model.addAttribute("coursesInfo", coursesInfo);


        return "courses";
    }

    @PostMapping("/course/{idCourse}")
    public String addMaterial(@PathVariable long idCourse,
                              @AuthenticationPrincipal User user,
                              @RequestParam("file[]") MultipartFile[] files,
                              Material material
    ) {
        Course course = courseService.findCourseById(idCourse);
        materialService.createMaterial(course, user, material);
        for (MultipartFile file : files) {
            if (file.getSize() != 0) {
                publicationFileService.saveFile(file, material.getId(), user);
            }
        }


        return "redirect:/course/{idCourse}";
    }

    @GetMapping("/course/{idCourse}/users")
    public String showCourseUsers(@PathVariable long idCourse,
                                  Model model,
                                  @AuthenticationPrincipal User user) {

        Course course = courseService.findCourseById(idCourse);

        if (!courseUserService.isTeacher(user, course)) {
            return "redirect:/course/{idCourse}";
        }

        List<CourseUser> courseUsers = courseUserService.getCourseUsersWithoutTeachers(course);
        Role[] roles = {Role.TEACHER, Role.STUDENT};

        model.addAttribute("course", course);
        model.addAttribute("courseUsers", courseUsers);
        model.addAttribute("roles", roles);
        model.addAttribute("allUsers", userService.getUsersNotPresentOnCourse(idCourse));

        return "users";
    }

    @PostMapping("/course/{idCourse}/users/add")
    public String addUser(
            @PathVariable Long idCourse,
            @RequestParam String userLogin,
            @RequestParam String userRole
    ) {

        courseUserService.addCourseUser(idCourse, userLogin, userRole);
        userTaskService.createUserTasksForNewStudent(userLogin, idCourse);
        return "redirect:/course/{idCourse}/users";
    }

    @DeleteMapping("/course/{idCourse}/users/{idCourseUser}")
    public String removeCourseUser(@PathVariable long idCourseUser) {

        courseUserService.deleteCourseUser(idCourseUser);

        return "redirect:/course/{idCourse}/users";
    }


}
