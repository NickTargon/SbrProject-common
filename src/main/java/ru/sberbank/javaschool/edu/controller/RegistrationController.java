package ru.sberbank.javaschool.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.service.UserService;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        String userAdding = userService.addUser(user);
        if (!userAdding.equals("ok")) {
            model.addAttribute("message", userAdding);
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code, Model model) {
        boolean isActivated = userService.isUserActivated(code);
        if (isActivated) {
            model.addAttribute("active", "Аккаунт успешно активирован!");
        } else {
            model.addAttribute("active", "Аккаунт уже был активирован");
        }
        return "login";
    }


}
