package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sberbank.javaschool.edu.domain.*;

import java.util.List;

@Repository
public interface PublicationFileRepository extends JpaRepository<PublicationFile, Long> {
    List<PublicationFile> findAllByUserAndPublication(User user, Task task);

    List<PublicationFile> findAllByPublication(Material material);

    PublicationFile findPublicationFileById(Long id);

}
