package ru.sberbank.javaschool.edu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.javaschool.edu.domain.MaterialComment;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.repository.MaterialCommentRepository;

import java.time.LocalDateTime;

@Service
public class MaterialCommentService {

    private final MaterialCommentRepository commentRepository;
    private final MaterialService materialService;
    private static final Logger logger = LoggerFactory.getLogger(MaterialCommentService.class);

    @Autowired
    public MaterialCommentService(MaterialCommentRepository commentRepository, MaterialService materialService) {
        this.commentRepository = commentRepository;
        this.materialService = materialService;
    }

    @Transactional
    public void addComment(long idMaterial, User user, MaterialComment materialComment) {
        materialComment.setAuthor(user);
        materialComment.setMaterial(materialService.getMaterialById(idMaterial));
        materialComment.setCreatedate(LocalDateTime.now());

        commentRepository.save(materialComment);

        logger.info("Пользователь " + user.getLogin() + " прокомментировал материал с id = " + idMaterial);
    }

    @Transactional
    public void addNestedComment(long idMaterial, long idParent, User user, MaterialComment childComment) {
        childComment.setAuthor(user);
        childComment.setParentComment(commentRepository.findMaterialCommentById(idParent));
        childComment.setMaterial(materialService.getMaterialById(idMaterial));
        childComment.setCreatedate(LocalDateTime.now());

        commentRepository.save(childComment);

        logger.info("Пользователь " + user.getLogin() + " прокомментировал комментарий с id = " + idParent);
    }

    @Transactional
    public boolean canEditComment(User user, long id) {
        MaterialComment comment = commentRepository.findMaterialCommentById(id);

        if (comment == null) {
            return false;
        }

        return comment.getAuthor().equals(user);
    }

    @Transactional
    public void deleteComment(User user, long id) {

        if (canEditComment(user, id)) {
            commentRepository.deleteById(id);

            logger.info("Пользователь " + user.getLogin() + " удалил комментарий с id = " + id);

        }
    }

    @Transactional
    public void editComment(long id, MaterialComment comment, User user) {

        if (canEditComment(user, id)) {
            commentRepository.updateMaterialComment(id, comment.getText(), LocalDateTime.now());

            logger.info("Пользователь " + user.getLogin() + " отредактировал комментарий с id = " + id);
        }
    }

    public MaterialComment getMaterialComment(long idComment) {

        return commentRepository.findMaterialCommentById(idComment);
    }
}
