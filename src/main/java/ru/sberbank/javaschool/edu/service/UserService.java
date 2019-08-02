package ru.sberbank.javaschool.edu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.CourseUser;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.repository.CourseRepository;
import ru.sberbank.javaschool.edu.repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final CourseRepository courseRepository;
    private final CourseUserService courseUserService;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    public UserService(
            UserRepository userRepo,
            CourseRepository courseRepository,
            CourseUserService courseUserService,
            MailSender mailSender,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.courseRepository = courseRepository;
        this.courseUserService = courseUserService;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepo.findUserByLogin(login);
        if ((user != null) && (user.getActcode().equals("ok"))) {
            return user;
        }
        throw new UsernameNotFoundException("Invalid name and password or user is not activated: " + login);
    }

    public User findUserbyLogin(String login) {
        return userRepo.findUserByLogin(login);
    }

    @Transactional
    public String addUser(User user) {
        if (user.getLogin().isEmpty() || user.getEmail().isEmpty()) {
            return "Необходимые для заполнения поля пусты";
        }

        String newPassword = user.getPassword();

        if (newPassword.isEmpty()) {
            newPassword = UUID.randomUUID().toString().substring(1, 11);
            user.setPassword(newPassword);
        }

        User uFromDB = userRepo.findUserByLogin(user.getLogin());

        if (uFromDB != null) {
            return "Пользователь существует";
        }

        user.setRegdate(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (!user.getEmail().isEmpty()) {
            String code = UUID.randomUUID().toString();
            user.setActcode(code);
            String message = String.format(
                    "Привет, %s!\n Добро пожаловать в наш класс, мы почти как Гугл :D\n"
                            + "Ссылка для активации твоего аккаунта уже здесь: "
                            + "http://localhost:8080/activate/%s "
                            + "Логин: %s\n ",
                    user.getName(),
                    code,
                    user.getLogin()
            );
            try {
                mailSender.send(user.getEmail(), "Activation code EDUClassroom", message);
            } catch (MailSendException ignore) {
                //ignore
            }
        }

        userRepo.save(user);

        logger.info("Зарегистрирован пользователь " + user.getLogin());

        return "ok";
    }

    @Transactional
    public boolean updateUser(String login, User user) {
        User uFromDB = userRepo.findUserByLogin(login);
        if (uFromDB == null) {
            return false;
        }
        if (!user.getName().isEmpty()) {
            uFromDB.setName(user.getName());
        }
        if (!user.getSurname().isEmpty()) {
            uFromDB.setSurname(user.getSurname());
        }
        if (!user.getEmail().isEmpty()) {
            uFromDB.setEmail(user.getEmail());
        }

        userRepo.save(uFromDB);

        logger.info("Изменены данные пользователя " + user.getLogin());

        return true;
    }

    public String getInfoFromSession(HttpSession httpSession, User user) { //положить/вытащить данные в сессию, проба
        String sessionKey = "firstAccessTime";
        String sessionKeyLogin = "userLogin";
        Object userFromSession = httpSession.getAttribute(sessionKeyLogin);
        Object time = httpSession.getAttribute(sessionKey);

        if (time == null) {
            time = LocalDateTime.now();
            httpSession.setAttribute(sessionKey, time);
        }

        if (userFromSession == null) {
            userFromSession = user.getLogin();
            httpSession.setAttribute(sessionKeyLogin, userFromSession);
        }

        return "first access time : " + time + "\nsession id: " + httpSession.getId() + "\nUser login "
                + httpSession.getAttribute(sessionKeyLogin);

    }

    public List<User> getUsersNotPresentOnCourse(long idCourse) {
        List<User> users = userRepo.findAll();
        Course course = courseRepository.findCourseById(idCourse);

        List<User> presentUser = new ArrayList<>();

        for (CourseUser courseUser : course.getCourseUsers()) {
            presentUser.add(courseUser.getUser());
        }

        users.removeAll(presentUser);
        users.removeIf(u -> !u.isEnabled());

        return users;
    }

    //активация юзера по почте(доработать), можно потом для отсылки
    // пароля использовать, если регистрации не будет
    public boolean isUserActivated(String code) {
        User user = userRepo.findUserByActcode(code);

        if (user == null) {
            return false;
        }

        user.setActcode("ok");

        userRepo.save(user);

        return true;
    }

    @Transactional
    public void deleteUser(String login) {
        User user = userRepo.findUserByLogin(login);

        user.setName("DELETED");
        user.setSurname("DELETED");
        user.setEmail("DELETED");
        user.setPhone("DELETED");
        user.setCourseUsers(null);

        userRepo.save(user);

        List<CourseUser> usersCourses = courseUserService.getUserCourses(login);

        for (CourseUser courseUser : usersCourses) {
            courseUserService.deleteCourseUser(courseUser.getId());
        }

        logger.info("Удален пользователь " + login);
    }

}
