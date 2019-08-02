package ru.sberbank.javaschool.edu.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "edu_task_comment")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "text", "author"})
public class TaskComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Lob
    @Column(name = "text", columnDefinition = "LONGTEXT")
    protected String text;
    @Column(name = "createdate")
    protected LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usertask")
    protected UserTask userTask;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    protected User author;

    @Column(name = "edited")
    protected boolean edited;

    @ManyToOne
    @JoinColumn(name = "parent")
    private TaskComment parentComment;

    @OneToMany(mappedBy = "parentComment", fetch = FetchType.EAGER)
    private List<TaskComment> comments = new ArrayList<>();
}
