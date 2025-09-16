package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    public List<Student> findByFacultyId(Long facultyId) {
        log.info("Ыфзван метод поиска студентов по факультету = {}", facultyId);
        return studentRepository.findByFacultyId(facultyId);
    }

    public Student createStudent(Student student) {
        log.info("был вызван метод создания студента: {}", student);
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        log.info("вызван метод получения студента по id = {}", id);
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Long id, Student student) {
        log.info("Вызван метод корректировки студента для id = {} c новыми данными: {}", id, student);
        if (!studentRepository.existsById(id)) {
            log.error("Не удалось обновить: студент с id = {} не найден", id);
            throw new StudentNotFoundException(id);
        }
        student.setId(id);
        log.debug("студент обновлён");
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        log.info("Вызван метод показа всех студентов");
        return studentRepository.findAll();
    }

    @Transactional
    public void deleteStudent(Long studentId) {
        log.info("Вызван метод удаления студента для = {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент с id " + studentId + " не найден"));

        avatarRepository.findByStudentId(studentId).ifPresent(avatar -> {
            try {
                Path avatarPath = Path.of(avatar.getFilePath());
                Files.deleteIfExists(avatarPath);
                log.debug("Удалён файл аватара: {}", avatarPath);
            } catch (IOException e) {
                log.error("Ошибка при удалении аватарки для студента = {}", studentId, e);
                throw new RuntimeException("Не удалось удалить файл аватарки", e);
            }
            avatarRepository.delete(avatar);
            log.debug("Удалена аватарка из БД: {}", avatar);
        });

        studentRepository.delete(student);
        log.info("Студент с id = {}", studentId);
    }


    public List<Student> findByAge(int age) {
        log.info("Вызван метод поиска по возрасту = {}", age);
        List<Student> result = studentRepository.findAll().
                stream()
                .filter(student -> student.getAge() == age)
                .toList();
        log.info("Получен список студентов");
        return result;
    }

    public List<Student> findByAgeBetween(int min, int max) {
        log.info("Вызван метод поиска студентов по промежутку возрастов: min={}, max={}", min, max);
        List<Student> result = studentRepository.findByAgeBetween(min, max);
        log.info("Получен список студентов  по промежутку возрастов: min={}, max={}", min, max);
        return result;
    }

    public long getTotalStudents() {
        log.info("Вызван метод показа колличества студентов");
        return studentRepository.getTotalStudents();
    }

    public double getAverageAge() {
        log.info("Вызван метод подсчёта среднего возраста студентов");
        return studentRepository.getAverageAge();
    }

    public List<Student> getLAstFiveStudents() {
        log.info("Вызван метод показа последжних пяти студентов");
        return studentRepository.getLastFiveStudents();
    }

    public List<String> getStudentsWithA() {
        log.info("Вызван метод получения всех студентов начинающихся с буквы А");

        return studentRepository.findAll().stream()
                .map(Student::getName)
                .filter(name -> name != null && name.toUpperCase().startsWith("А"))
                .map(String::toUpperCase)
                .sorted()
                .toList();
    }

    public double getAverageAgeFromFindAll(){
        log.info("Вызван метод для получения среднего возраста всех студентов");
        return studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
    }

}
