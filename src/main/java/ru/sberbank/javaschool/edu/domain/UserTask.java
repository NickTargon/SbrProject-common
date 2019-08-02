package ru.sberbank.javaschool.edu.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "edu_user_task")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "submitteddate")
    private LocalDateTime submittedDate;
    @Column(name = "taskstate")
    @Enumerated(EnumType.STRING)
    private TaskState taskState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task")
    protected Task task;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    protected User user;

    @Column(name = "curmark")
    private Long curMark;

    @OneToMany(mappedBy = "userTask", fetch = FetchType.LAZY)
    @Where(clause = "parent is null")
    private List<TaskComment> comments = new ArrayList<>();
}
