package ru.sberbank.javaschool.edu.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table(name = "edu_publication")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id", "title"})
public abstract class Publication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    protected String title;
    @Lob
    @Column(name = "text", columnDefinition = "LONGTEXT")
    protected String text;
    @Column(name = "createdate")
    protected LocalDateTime createDate;
    @Column(name = "edited")
    protected boolean edited;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course")
    protected Course course;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    protected User author;
}
