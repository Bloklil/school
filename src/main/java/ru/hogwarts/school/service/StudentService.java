package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {
    private final Map<Long, Student> students = new HashMap<>();
    private long idCounter = 1;

    public Student createStudent(Student student) {
        student.setId(idCounter++);
        students.put(student.getId(), student);
        return student;
    }

    public Student getStudent(Long id) {
        return students.get(id);
    }

    public Student updateStudent(Long id, Student student) {
        if (!students.containsKey(id)) {
            return null;
        }
        student.setId(id);
        students.put(id, student);
        return student;
    }

    public Collection<Student> getAllStudent() {
        return students.values();
    }

    public Student deleteStudent(Long id) {
        return students.remove(id);
    }

    public List<Student> findByAge (int age) {
        return students.values().stream()
                .filter(student -> student.getAge()==age)
                .toList();
    }

}
