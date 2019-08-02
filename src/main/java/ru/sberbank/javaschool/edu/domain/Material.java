package ru.sberbank.javaschool.edu.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@DiscriminatorValue("Material")
@Getter
@Setter
public class Material extends Publication {
    @OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
    @Where(clause = "parent is null")
    private List<MaterialComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "publication", fetch = FetchType.LAZY)
    private List<PublicationFile> publicationFiles = new ArrayList<>();
}
