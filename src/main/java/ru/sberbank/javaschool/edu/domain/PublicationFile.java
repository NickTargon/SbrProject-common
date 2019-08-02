package ru.sberbank.javaschool.edu.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "edu_files")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PublicationFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String filename;
    private String path;
    private String filetype;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication")
    //protected Task task;
    protected Publication publication;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    protected User user;


}
