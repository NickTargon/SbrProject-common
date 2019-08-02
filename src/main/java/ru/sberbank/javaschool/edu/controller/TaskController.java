package ru.sberbank.javaschool.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.service.*;

import java.security.Principal;
import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;
    private final MaterialService materialService;
    private final UserTaskService userTaskService;
    private final CourseUserService courseUserService;
    private final PublicationFileService publicationFileService;
    private final UserService userService;

    @Autowired
    public TaskController(TaskService taskService, MaterialService materialService, UserTaskService userTaskService,
                          CourseUserService courseUserService, PublicationFileService publicationFileService,
                          UserService userService) {
        this.taskService = taskService;
        this.materialService = materialService;
        this.userTaskService = userTaskService;
        this.courseUserService = courseUserService;
        this.publicationFileService = publicationFileService;
        this.userService = userService;
    }


    @GetMapping("/course/{idCourse}/tasks/{idTask}")
    public String showTask(
            @PathVariable long idCourse,
            @PathVariable long idTask,
            Model model,
            @AuthenticationPrincipal User user) {
        Course course = taskService.findCourseById(idCourse);
        Task task = taskService.getTaskById(idTask);
        UserTask userTask = taskService.findUserTaskByUserAndTask(user, task);
        if (userTask == null) {
            userTask = userTaskService.createUserTask(user, task);
        }
        List<PublicationFile> publicationFiles = taskService.findAllPubFilesByUserAndTask(user, task);
        boolean isTeacher = taskService.canCreateTask(idCourse, user);
        List<UserTask> usertasks = taskService.findUserTaskByTask(task);

        publicationFileService.getFilesFromYDisk(user, task);

        model.addAttribute("course", course);
        model.addAttribute("task", task);
        model.addAttribute("usertask", userTask);
        model.addAttribute("isStudent", !isTeacher);
        model.addAttribute("isTeacher", isTeacher);
        model.addAttribute("usertasks", usertasks);
        model.addAttribute("currentUser", user);
        model.addAttribute("pubFiles", publicationFiles);

        return "task";
    }

    @PostMapping("/course/{idCourse}/tasks/{idTask}")
    public String submitTask(
            @PathVariable long idCourse,
            @PathVariable long idTask,
            @RequestParam("file[]") MultipartFile[] files,
            @AuthenticationPrincipal User user) {

        for (MultipartFile file : files) {
            if (file.getSize() != 0) {
                publicationFileService.saveFile(file, idTask, user);
            }
        }

        userTaskService.submitTask(idTask, user, idCourse);

        return "redirect:/course/{idCourse}/tasks/{idTask}";
    }

    @DeleteMapping("/course/{idCourse}/tasks/{idTask}/delete/{idFile}")
    public String removeFile(
            @PathVariable long idFile,
            @PathVariable long idCourse,
            @AuthenticationPrincipal User user) {

        publicationFileService.deleteFile(idFile);

        return "redirect:/course/{idCourse}/tasks/{idTask}";
    }

    @GetMapping("/course/{idCourse}/tasks/{idTask}/{taskCaption}/{idUser}")
    public String teacherPageTask(
            @PathVariable long idCourse,
            @PathVariable long idTask,
            @PathVariable long idUser,
            @AuthenticationPrincipal User currentUser,
            Model model) {
        Course course = taskService.findCourseById(idCourse);
        if (!courseUserService.isTeacher(currentUser, course)) {
            return "redirect:/course/{idCourse}/tasks/{idTask}";
        }
        Task task = taskService.getTaskById(idTask);
        User user = taskService.findUserById(idUser);
        List<PublicationFile> publicationFiles = taskService.findAllPubFilesByUserAndTask(user, task);
        UserTask userTask = taskService.findUserTaskByUserAndTask(user, task);

        model.addAttribute("course", course);
        model.addAttribute("task", task);
        model.addAttribute("usertask", userTask);
        model.addAttribute("user", user);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("pubFiles", publicationFiles);

        return "task_teacher";
    }

    @PostMapping("/course/{idCourse}/tasks/{idTask}/{taskCaption}/{idUser}")
    public String setMarkToUser(
            @PathVariable long idTask,
            @PathVariable long idUser,
            UserTask userTask) {

        userTaskService.setMarkToUser(idTask, idUser, userTask.getCurMark());

        return "redirect:/course/{idCourse}/tasks/{idTask}/{taskCaption}/{idUser}";
    }


    @GetMapping("/course/{id}/tasks")
    public String showTasks(
            @PathVariable long id,
            Model model,
            @AuthenticationPrincipal User user) {
        Course course = taskService.findCourseById(id);
        List<Task> tasks = taskService.getTaskByCourseOrderById(course);

        model.addAttribute("course", course);
        model.addAttribute("tasks", tasks);
        model.addAttribute("canEdit", materialService.canCreateMaterial(course, user));
        model.addAttribute("user", user);


        return "tasks";
    }

    @PostMapping("/course/{idCourse}/tasks")
    public String addTask(
            @PathVariable long idCourse,
            @AuthenticationPrincipal User user,
            Task task,
            TaskInfo taskInfo
    ) {
        Course course = taskService.findCourseById(idCourse);
        if (taskService.createTask(course, user, task, taskInfo)) {
            userTaskService.createUserTasksForAllStudents(task, course);
        }

        return "redirect:/course/{idCourse}/tasks";
    }

    @DeleteMapping("/course/{idCourse}/tasks/delete/{idTask}")
    public String removeTask(
            @PathVariable long idTask,
            @PathVariable long idCourse,
            @AuthenticationPrincipal User user
    ) {
        taskService.deleteTask(idTask, idCourse, user);

        return "redirect:/course/{idCourse}/tasks";
    }

    @GetMapping("/course/{idCourse}/tasks/edit/{idTask}")
    public String showEditTask(
            @PathVariable Long idCourse,
            @PathVariable Long idTask,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Course course = taskService.findCourseById(idCourse);
        Task task = taskService.getTaskById(idTask);

        if (!materialService.canCreateMaterial(course, user)) {
            return "redirect:/course/{idCourse}/tasks";
        }

        model.addAttribute("course", course);
        model.addAttribute("task", task);

        return "task_edit";
    }

    @PostMapping("/course/{idCourse}/tasks/edit/{idTask}")
    public String editTask(
            @PathVariable Long idTask,
            @AuthenticationPrincipal User user,
            Task task,
            TaskInfo taskInfo
    ) {
        taskService.editTask(idTask, task, taskInfo, user);

        return "redirect:/course/{idCourse}/tasks";
    }


    @GetMapping("/alltasks")
    public String showAllTasks(Principal principal, Model model) {
        User user = (User)userService.loadUserByUsername(principal.getName());
        List<CourseUser> courseUsers = courseUserService.getUserCourses(principal.getName());
        List<Course> courses = taskService.getAllCoursesFromUser(courseUsers);

        model.addAttribute("courses", courses);
        model.addAttribute("user", user);

        return "alltasks";
    }


}
