package ru.sberbank.javaschool.edu.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.sberbank.javaschool.edu.domain.Material;
import ru.sberbank.javaschool.edu.domain.MaterialComment;
import ru.sberbank.javaschool.edu.domain.User;
import ru.sberbank.javaschool.edu.repository.MaterialCommentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MaterialCommentServiceTest {
    @Autowired
    private MaterialCommentService commentService;

    @MockBean
    private MaterialCommentRepository commentRepository;

    @MockBean
    private MaterialService materialService;

    private final long ID = 1L;

    @Test
    public void addComment() {
        MaterialComment comment = new MaterialComment();
        User user = new User();
        Material material = new Material();

        material.setId(ID);
        user.setId(ID);

        Mockito.doReturn(material)
                .when(materialService)
                .getMaterialById(ID);

        commentService.addComment(ID, user, comment);

        Assert.assertEquals(user, comment.getAuthor());
        Assert.assertEquals(material, comment.getMaterial());
        Assert.assertNotNull(comment.getCreatedate());

        Mockito.verify(materialService, Mockito.times(1)).getMaterialById(ID);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
    }

    @Test
    public void deleteComment() {
        MaterialComment comment = new MaterialComment();
        User user = new User();

        user.setId(ID);
        comment.setId(ID);
        comment.setAuthor(user);

        Mockito.doReturn(comment)
                .when(commentRepository)
                .findMaterialCommentById(ID);

        commentService.deleteComment(user, ID);

        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(ID);

    }

    @Test
    public void deleteSomeoneElseComment() {
        MaterialComment comment = new MaterialComment();
        User user = new User();
        User elseUser = new User();

        user.setId(ID);
        elseUser.setId(2L);
        comment.setId(ID);
        comment.setAuthor(elseUser);

        commentService.deleteComment(user, ID);

        Mockito.verify(commentRepository, Mockito.times(0)).deleteById(ID);
    }



//    @Test
//    public void editComment() {
//        MaterialComment comment = new MaterialComment();
//        MaterialComment newComment = new MaterialComment();
//        User user = new User();
//
//        user.setId(ID);
//        comment.setId(ID);
//        comment.setAuthor(user);
//        comment.setText("oldText");
//        newComment.setText("newText");
//
//        commentService.editComment(ID, newComment, user);
//
//        Mockito.verify(commentRepository, Mockito.times(0))
//                .updateMaterialComment(ID, newComment.getText());
//    }

//    @Test
//    public void editSomeoneElseComment() {
//        MaterialComment comment = new MaterialComment();
//        MaterialComment newComment = new MaterialComment();
//        User user = new User();
//        User elseUser = new User();
//
//        user.setId(ID);
//        elseUser.setId(2L);
//        comment.setId(ID);
//        comment.setAuthor(elseUser);
//        comment.setText("oldText");
//        newComment.setText("newText");
//
//        Mockito.verify(commentRepository, Mockito.times(0))
//                .updateMaterialComment(ID, newComment.getText());
//    }
}