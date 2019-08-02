package ru.sberbank.javaschool.edu.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "edu_course_user")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", ""})
public class CourseUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "course")
    private Course course;
    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    public CourseUser(Course course, User user, Role role) {
        this.course = course;
        this.user = user;
        this.role = role;
    }
}
