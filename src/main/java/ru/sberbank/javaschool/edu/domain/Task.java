package ru.sberbank.javaschool.edu.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@DiscriminatorValue("Task")
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class Task extends Publication {
    @OneToOne(
            mappedBy = "task",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private TaskInfo taskInfo;

}
