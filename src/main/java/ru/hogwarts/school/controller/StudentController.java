package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    @PutMapping("{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) throws IOException {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Collection<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/filter/by-age")
    public Collection<Student> getStudentByAge(@RequestParam int age) {
        return studentService.findByAge(age);
    }

    @GetMapping("/filter/by-ageMin-max")
    public List<Student> getStudentsByAgeRange(@RequestParam int min, @RequestParam int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("{id}/faculty")
    public Faculty getFacultyByStudentsId(@PathVariable Long id) {
        if (studentService.getStudent(id) == null) {
            throw new StudentNotFoundException(id);
        }
        return studentService.getStudent(id).getFaculty();
    }

    @GetMapping("/faculty/{id}/students")
    public List<Student> getStudentsByFacultyId(@PathVariable Long id) {
        return studentService.findByFacultyId(id);
    }

    @GetMapping("/count-students")
    public long getTotalStudents() {
        return studentService.getTotalStudents();
    }

    @GetMapping("/average-age-students")
    public double getAverageAge() {
        return studentService.getAverageAge();
    }

    @GetMapping("/last-five-students")
    public List<Student> getLastFiveStudents() {
        return studentService.getLAstFiveStudents();
    }

    @GetMapping("/Students/names-with-A")
    public List<String> getStudentsNameWithA() {
        return studentService.getStudentsWithA();
    }

    @GetMapping("/students/*average-age")
    public double getAverageAgeAll() {
        return studentService.getAverageAgeFromFindAll();
    }

    @GetMapping("/sum-parallel")
    public long getSumParallel() {
        return Stream.iterate(1L, a -> a + 1)
                .limit(1_000_000)
                .parallel()
                .reduce(0L, Long::sum);
    }

}
