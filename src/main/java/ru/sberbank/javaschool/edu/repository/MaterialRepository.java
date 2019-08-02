package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.Course;
import ru.sberbank.javaschool.edu.domain.Material;

import java.time.LocalDateTime;
import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    Material getMaterialByCourseAndTitle(Course course, String title);

    Material getMaterialById(long id);

    @Query(
            value = "SELECT p.* FROM edu_publication p WHERE p.course = :course_id "
                    + "AND p.dtype = 'Material' ORDER BY id ASC",
            nativeQuery = true
    )
    List<Material> getMaterialByCourseOrderById(@Param("course_id") long courseId);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE Material material "
                    + "SET material.title=:newTitle, material.text=:newText,"
                    + " material.edited=true, material.createDate=:editDate"
                    + " WHERE material.id=:id"
    )
    void updateMaterial(
            @Param("id") long idMaterial,
            @Param("newTitle") String newTitle,
            @Param("newText") String newText,
            @Param("editDate") LocalDateTime editDate
            );

}
