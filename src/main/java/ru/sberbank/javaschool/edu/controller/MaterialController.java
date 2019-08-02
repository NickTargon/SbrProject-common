package ru.sberbank.javaschool.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.Material;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.service.CourseService;
import ru.sberbank.javaschool.edu.service.MaterialService;

@Controller
public class MaterialController {

    private final CourseService courseService;
    private final MaterialService materialService;

    @Autowired
    public MaterialController(
            CourseService courseService,
            MaterialService materialService
    ) {
        this.courseService = courseService;
        this.materialService = materialService;
    }

    @DeleteMapping("/course/{idCourse}/delete/{idMaterial}")
    public String removeMaterial(
            @PathVariable long idMaterial,
            @AuthenticationPrincipal User user) {
        materialService.deleteMaterial(idMaterial, user);

        return "redirect:/course/{idCourse}";
    }

    @GetMapping("/course/{idCourse}/edit/{idMaterial}")
    public String showEditMaterial(
            @PathVariable Long idCourse,
            @PathVariable Long idMaterial,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        Course course = courseService.findCourseById(idCourse);
        Material material = materialService.getMaterialById(idMaterial);

        if (!materialService.canCreateMaterial(course, user)) {
            return "redirect:/course/{idCourse}";
        }

        model.addAttribute("course", course);
        model.addAttribute("material", material);

        return "materialEdit";
    }

    @PostMapping("/course/{idCourse}/edit/{idMaterial}")
    public String editMaterial(
            @PathVariable Long idMaterial,
            Material material
    ) {
        materialService.editMaterial(idMaterial, material);

        return "redirect:/course/{idCourse}";
    }



}
