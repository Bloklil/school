package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(StudentController.class)
public class StudentControllerTestWebMvc {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    AvatarRepository avatarRepository;

    @SpyBean
    private StudentService studentService;

    @Spy
    private AvatarService avatarService;

    @Test
    @DisplayName("создание студента")
    public void createStudentTest() throws Exception {
        final String name = "Миравингин";
        final int age = 12;
        final long id = 1;
        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", age);

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(student));

        mockMvc.perform(post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }

    @Test
    @DisplayName("Проверка на получение студента по ID")
    public void readStudentTest() throws Exception {
        final String name = "Миравингин";
        final int age = 12;
        final long id = 1;
        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", age);

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);

        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()));
    }

    @Test
    @DisplayName("Проверка на получение всех студентов")
    public void readAllStudentsTest() throws Exception {
        final String name1 = "Миравингин";
        final int age1 = 10;
        final long id1 = 1;

        final String name2 = "Триннити";
        final int age2 = 12;
        final long id2 = 2;

        Student student1 = new Student();
        student1.setId(id1);
        student1.setName(name1);
        student1.setAge(age1);

        Student student2 = new Student();
        student2.setId(id2);
        student2.setName(name2);
        student2.setAge(age2);

        when(studentRepository.findAll()).thenReturn(List.of(student1, student2));

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id1))
                .andExpect(jsonPath("$[0].name").value(name1))
                .andExpect(jsonPath("$[0].age").value(age1))
                .andExpect(jsonPath("$[1].id").value(id2))
                .andExpect(jsonPath("$[1].name").value(name2))
                .andExpect(jsonPath("$[1].age").value(age2));
    }

    @Test
    @DisplayName("Проверка на редактирование студента")
    public void updateStudentTest() throws Exception {
        final String name = "Миравингин";
        final int age = 1;
        final long id = 1;

        Student student = new Student();
        student.setName(name);
        student.setAge(age);
        student.setId(id);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", age);

        when(studentRepository.existsById(anyLong())).thenReturn(true);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/student/" + id)
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age));
    }


    @Test
    @DisplayName("Проверка на удаление студента по ID")
    public void deleteStudentTest() throws Exception {
        final String name = "Миравингин";
        final int age = 12;
        final long id = 1;

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);

        when(studentRepository.existsById(anyLong())).thenReturn(true);
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        mockMvc.perform(delete("/student/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Проверка фильтрации студентов по возрасту")
    public void getStudentsByAgeTest() throws Exception {
        final String name = "Миравингин";
        final int age = 111;
        final long id = 1;

        Student student = new Student();
        student.setName("Миравингин");
        student.setAge(age);
        student.setId(1L);

        when(studentService.findByAge(age)).thenReturn(Collections.singletonList(student));

        mockMvc.perform(get("/student/filter/by-age")
                        .param("age", String.valueOf(age)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].age").value(age))
                .andExpect(jsonPath("$[0].name").value(student.getName()));
    }

    @Test
    @DisplayName("Проверка фильтрации студентов по диапазону возраста")
    public void getStudentsByAgeRangeTest() throws Exception {
        int min = 18;
        int max = 25;

        final String name1 = "Миравингин";
        final int age1 = 22;
        final long id1 = 1;

        final String name2 = "Триннити";
        final int age2 = 24;
        final long id2 = 2;

        Student student1 = new Student();
        student1.setId(id1);
        student1.setName(name1);
        student1.setAge(age1);

        Student student2 = new Student();
        student2.setId(id2);
        student2.setName(name2);
        student2.setAge(age2);

        List<Student> students = List.of(student1, student2);

        when(studentService.findByAgeBetween(min, max)).thenReturn(students);

        mockMvc.perform(get("/student/filter/by-ageMin-max")
                        .param("min", String.valueOf(min))
                        .param("max", String.valueOf(max)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(students.size()))
                .andExpect(jsonPath("$[0].age").value(student1.getAge()))
                .andExpect(jsonPath("$[1].age").value(student2.getAge()));
    }

    @Test
    @DisplayName("Проверка получения факультета по ID студента")
    public void getFacultyByStudentIdTest() throws Exception {

        Faculty faculty = new Faculty();
        faculty.setId(10L);
        faculty.setName("Ргиффиндор");

        Student student = new Student();
        long id = 1;
        student.setId(id);
        student.setName("Миравингин");
        student.setAge(20);
        student.setFaculty(faculty);

        when(studentService.getStudent(id)).thenReturn(student);

        mockMvc.perform(get("/student/{id}/faculty", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()));
    }

    @Test
    @DisplayName("Проверка получения студентов по ID факультета")
    public void getStudentsByFacultyIdTest() throws Exception {
        long facultyId = 10;

        final String name1 = "Миравингин";
        final int age1 = 22;
        final long id1 = 1;

        final String name2 = "Триннити";
        final int age2 = 24;
        final long id2 = 2;

        Student student1 = new Student();
        student1.setId(id1);
        student1.setName(name1);
        student1.setAge(age1);

        Student student2 = new Student();
        student2.setId(id2);
        student2.setName(name2);
        student2.setAge(age2);

        List<Student> students = List.of(student1, student2);

        when(studentService.findByFacultyId(facultyId)).thenReturn(students);

        mockMvc.perform(get("/student/faculty/{id}/students", facultyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(students.size()))
                .andExpect(jsonPath("$[0].id").value(student1.getId()))
                .andExpect(jsonPath("$[1].id").value(student2.getId()));
    }
}

