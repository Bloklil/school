package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
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
public class StudentService {

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public StudentService(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }

    public List<Student> findByFacultyId(Long facultyId) {
        return studentRepository.findByFacultyId(facultyId);
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Long id, Student student) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        student.setId(id);
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional
    public void deleteStudent(Long studentId) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент с id " + studentId + " не найден"));

        avatarRepository.findByStudentId(studentId).ifPresent(avatar -> {
            try {
                Path avatarPath = Path.of(avatar.getFilePath());
                Files.deleteIfExists(avatarPath);
            } catch (IOException e) {
                throw new RuntimeException("Не удалось удалить файл аватарки", e);
            }
            avatarRepository.delete(avatar);
        });

        studentRepository.delete(student);
    }



    public List<Student> findByAge(int age) {
        return studentRepository.findAll().
                stream()
                .filter(student -> student.getAge() == age)
                .toList();
    }

    public List<Student> findByAgeBetween(int min, int max) {
        return studentRepository.findByAgeBetween(min, max);
    }

}
