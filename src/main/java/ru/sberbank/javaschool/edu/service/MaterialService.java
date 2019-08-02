package ru.sberbank.javaschool.edu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.repository.MaterialRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MaterialService {
    private final MaterialRepository materialRepository;
    private static final Logger logger = LoggerFactory.getLogger(MaterialService.class);

    @Autowired
    public MaterialService(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Transactional
    public boolean createMaterial(Course course, User user, Material material) {
        Material materialFromDb =
                materialRepository.getMaterialByCourseAndTitle(course, material.getTitle());

        if (materialFromDb == null && canCreateMaterial(course, user)) {
            material.setAuthor(user);
            material.setCourse(course);
            material.setCreateDate(LocalDateTime.now());

            materialRepository.save(material);

            logger.info("Пользователь " + user.getLogin() + " добавил на курс " + course.getCaption()
                    + " новый материал: " + material.getTitle());

            return true;
        }

        return false;
    }

    @Transactional
    public void editMaterial(Long id, Material material) {

        materialRepository.updateMaterial(id, material.getTitle(), material.getText(), LocalDateTime.now());

        logger.info("Материал с id = " + id + " был изменен");
    }

    @Transactional
    public void deleteMaterial(long idMaterial, User user) {
        Material material = materialRepository.getMaterialById(idMaterial);

        if (material != null && canCreateMaterial(material.getCourse(), user)) {
            materialRepository.deleteById(idMaterial);

            logger.info("Материал с id = " + idMaterial + " был уделен пользоватем " + user.getLogin());
        }
    }

    public boolean canCreateMaterial(Course course, User user) {
        Map<User, Role> userRoles = course.getCourseUsers()
                .stream()
                .collect(Collectors.toMap(CourseUser::getUser, CourseUser::getRole));

        return userRoles.containsKey(user) && userRoles.get(user) == Role.TEACHER;
    }

    public List<Material> getCourseMaterials(long idCourse) {

        return materialRepository.getMaterialByCourseOrderById(idCourse);
    }

    public Material getMaterialById(long idMaterial) {

        return materialRepository.getMaterialById(idMaterial);
    }
}
