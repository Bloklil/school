package ru.hogwarts.school.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;

@Service
@Slf4j
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        log.info("Вызван метод создания факультета: {}", faculty);
        Faculty saved = facultyRepository.save(faculty);
        log.debug("Факультет сохранён:{}", saved);
        return saved;
    }

    public Faculty getFaculty(Long id) {
        log.info("Вызван метод получения факультета по id = {}", id);
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            log.warn("Факультет с id = {} не найден", id);
        } else {
            log.debug("Получен факультет с: {}", faculty);
        }
        return faculty;
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        log.info("Вызван метод обновления студента id = {}", id);
        if (!facultyRepository.existsById(id)) {
            log.error("Факультет с id = {} не найден", id);
            throw new FacultyNotFoundException(id);
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public List<Faculty> getAllFaculties() {
        log.info("Вызван метод получения всех факультетов");
        return facultyRepository.findAll();
    }

    public void deleteFaculty(Long id) {
        log.info("Вызван метод цдаления факультета");
        facultyRepository.deleteById(id);
    }

    public List<Faculty> findByColor(String color) {
        log.info("Вызван метод поиска факультетов по цвету");
        return facultyRepository.findByColorIgnoreCase(color);
    }

    public List<Faculty> findByNameOrColorIgnoreCase(String query) {
        log.info("Вызван метод поиска факультетов по имени или цвету");
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(query, query);
    }

    public String getLongestFacultyName() {
        log.info("Вызван метод для поиска самого длинного названия факультета");
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max((name1, name2) -> Integer.compare(name1.length(), name2.length()))
                .orElse("Факультеты отсутствуют");
    }

}
