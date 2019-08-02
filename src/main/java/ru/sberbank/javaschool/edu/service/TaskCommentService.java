package ru.sberbank.javaschool.edu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.repository.TaskCommentRepository;
import ru.sberbank.javaschool.edu.repository.TaskRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;
import ru.sberbank.javaschool.edu.repository.UserTaskRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskCommentService {
    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserTaskRepository userTaskRepository;

    private static final Logger logger = LoggerFactory.getLogger(TaskCommentService.class);

    public TaskCommentService(
            TaskCommentRepository commentRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            UserTaskRepository userTaskRepository
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userTaskRepository = userTaskRepository;
    }

    public void addComment(long idTask, long idUser, User user, TaskComment taskComment) {

        if (createTaskComment(idTask, idUser, user, taskComment))  {
            commentRepository.save(taskComment);
            logger.info("Пользователь " + user.getLogin() + " прокомментировал задание с id = " + idTask);
        }
    }

    @Transactional
    public void addNestedComment(long idTask, long idParent, long idUser, User user, TaskComment taskComment) {

        if (createTaskComment(idTask, idUser, user, taskComment)) {
            taskComment.setParentComment(commentRepository.findTaskCommentById(idParent));

            commentRepository.save(taskComment);

            logger.info("Пользователь " + user.getLogin() + " прокомментировал коммнетарий с id = " + idParent);
        }
    }

    @Transactional
    public void deleteComment(User user, long id) {
        TaskComment comment = commentRepository.findTaskCommentById(id);

        if (comment != null && cantEditComment(user, comment)) {
            commentRepository.deleteById(id);

            logger.info("Пользователь " + user.getLogin() + " удалил комментарий с id = " + id);
        }
    }

    @Transactional
    public void editComment(long id, TaskComment comment, User user) {
        TaskComment commentFromDb = commentRepository.findTaskCommentById(id);

        if (commentFromDb != null && cantEditComment(user, commentFromDb)) {
            commentRepository.updateTaskComment(id, comment.getText(), LocalDateTime.now());

            logger.info("Пользователь " + user.getLogin() + " отредактировал комментарий с id = " + id);
        }
    }

    public boolean cantEditComment(User user, TaskComment comment) {

        return comment.getAuthor().equals(user);
    }

    public boolean canAddComment(UserTask task, User user) {
        Course course = task.getTask().getCourse();

        Map<User, Role> userRoles = course.getCourseUsers()
                .stream()
                .collect(Collectors.toMap(CourseUser::getUser, CourseUser::getRole));


        return task.getUser().equals(user) || userRoles.get(user) == Role.TEACHER;
    }

    public TaskComment getTaskComment(long idComment) {

        return commentRepository.findTaskCommentById(idComment);
    }

    private boolean createTaskComment(long idTask, long idUser, User user, TaskComment taskComment) {
        Task commonTask = taskRepository.getTaskById(idTask);
        User taskUser = userRepository.findUserById(idUser);
        UserTask task = userTaskRepository.findUserTaskByUserAndTask(taskUser, commonTask);

        if (!canAddComment(task, user)) {
            return false;
        }

        taskComment.setUserTask(task);
        taskComment.setAuthor(user);
        taskComment.setCreateDate(LocalDateTime.now());

        return true;
    }
}
