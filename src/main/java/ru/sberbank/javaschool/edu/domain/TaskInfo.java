package ru.sberbank.javaschool.edu.domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "edu_task_info")
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
public class TaskInfo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "compltime")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime completeTime;
    @Column(name = "maxmark")
    private Long maxMark;

    @OneToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private Task task;
}
