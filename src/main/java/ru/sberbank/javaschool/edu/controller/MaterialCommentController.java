package ru.sberbank.javaschool.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sberbank.javaschool.edu.domain.MaterialComment;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.service.MaterialCommentService;

@Controller
public class MaterialCommentController {
    private final MaterialCommentService commentService;

    @Autowired
    public MaterialCommentController(MaterialCommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/course/{idCourse}/comment/{idMaterial}")
    public String addComment(
            @PathVariable long idMaterial,
            @AuthenticationPrincipal User user,
            MaterialComment comment
    ) {
        commentService.addComment(idMaterial, user, comment);

        return "redirect:/course/{idCourse}";
    }

    @PostMapping("/course/{idCourse}/comment/{idMaterial}/{idComment}")
    public String addChildComment(
            @PathVariable long idMaterial,
            @PathVariable long idComment,
            @AuthenticationPrincipal User user,
            MaterialComment comment
    ) {
        commentService.addNestedComment(idMaterial, idComment, user, comment);

        return "redirect:/course/{idCourse}";
    }

    @DeleteMapping("/course/{idCourse}/delete_comment/{idComment}")
    public String deleteComment(
            @PathVariable Long idComment,
            @AuthenticationPrincipal User user
    ) {
        commentService.deleteComment(user, idComment);

        return "redirect:/course/{idCourse}";

    }

    @GetMapping("/course/{idCourse}/edit_comment/{idComment}")
    public String showEditingComment(
            @PathVariable long idCourse,
            @PathVariable long idComment,
            @AuthenticationPrincipal User user,
            Model model
    ) {
        MaterialComment comment = commentService.getMaterialComment(idComment);

        if (!commentService.canEditComment(user, idComment)) {
            return "redirect:/course/{idCourse}";
        }

        model.addAttribute("comment", comment);
        model.addAttribute("idCourse", idCourse);

        return "material_comment_edit";
    }

    @PostMapping("/course/{idCourse}/edit_comment/{idComment}")
    public String editMaterialComment(
            @PathVariable long idComment,
            MaterialComment comment,
            @AuthenticationPrincipal User user
    ) {
        commentService.editComment(idComment, comment, user);

        return "redirect:/course/{idCourse}";
    }
}
