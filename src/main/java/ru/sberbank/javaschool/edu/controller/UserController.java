package ru.sberbank.javaschool.edu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.sberbank.javaschool.edu.domain.CourseUser;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.service.UserService;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userGreeting(Principal principal, Model model, HttpSession httpSession) {
        User user = (User)userService.loadUserByUsername(principal.getName());
        logger.debug(userService.getInfoFromSession(httpSession, user));

        Set<CourseUser> courseUsersSet = user.getCourseUsers();

        model.addAttribute("user", user);
        model.addAttribute("courseusers", courseUsersSet);
        return "user";
    }

    @PostMapping
    public String update(Model model, Principal principal, User user) {

        String login = principal.getName();
        if (userService.updateUser(login, user)) {
            //model.addAttribute("message", "Ваши данные успешно обновлены");

            logger.debug(principal.getName() + " change name & surname to " + user.getName() + " " + user.getSurname());

            return "redirect:/user";
        }
        logger.debug(principal.getName() + "can't change name & surname");
        return "redirect:/user";
    }

    @PostMapping("/delete")
    public String deleteAccount(Principal principal, HttpSession httpSession) {
        logger.debug("Deleting account ", principal.getName());
        userService.deleteUser(principal.getName());
        httpSession.invalidate();

        return "redirect:/login";
    }
}
