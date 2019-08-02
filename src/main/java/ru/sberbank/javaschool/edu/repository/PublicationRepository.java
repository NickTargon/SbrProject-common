package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sberbank.javaschool.edu.domain.Publication;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Publication findPublicationById(Long id);
}
