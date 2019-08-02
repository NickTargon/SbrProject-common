package ru.sberbank.javaschool.edu.domain;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "edu_course")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "caption"})
public class Course implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String caption;
    @Column(name = "createdate")
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "course")
    private Set<CourseUser> courseUsers = new HashSet<>();

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    @Where(clause = "dtype='Material'")
    private List<Material> materials = new ArrayList<>();

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    @Where(clause = "dtype='Task'")
    private List<Task> tasks = new ArrayList<>();
}
