package ru.sberbank.javaschool.edu.domain;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "edu_publication_comment")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "text", "author"})
public class MaterialComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Lob
    @Column(name = "text", columnDefinition = "LONGTEXT")
    protected String text;
    protected LocalDateTime createdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication")
    protected Material material;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    protected User author;

    @Column(name = "edited")
    protected boolean edited;

    @ManyToOne
    @JoinColumn(name = "parent")
    private MaterialComment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<MaterialComment> comments = new ArrayList<>();
}
