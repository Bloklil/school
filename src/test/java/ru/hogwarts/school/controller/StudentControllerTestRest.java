package ru.hogwarts.school.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StudentControllerTestRest {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;
    private Faculty savedFaculty;

    @BeforeEach
    void cleanBd() {
        studentRepository.deleteAll();
    }

    @DisplayName("Проверка не пустого контроллера")
    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
    }

    @DisplayName("Проверка работоспособности майнКонтроллера")
    @Test
    public void testDefaultMessage() throws Exception {
        Assertions.assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/", String.class))
                .contains("работает");
    }

    @DisplayName("Проверка на добавление студента")
    @Test
    public void createStudentTest() {
        Student student = new Student(null, "Волчок", 11);

        ResponseEntity<Student> response = testRestTemplate.postForEntity(getAddress(), student, Student.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
    }

    @DisplayName("Проверка на получение студента по ID")
    @Test
    public void readStudentTest() {
        Student student = new Student(null, "Волчок", 11);

        studentRepository.save(student);

        ResponseEntity<Student> response = testRestTemplate.getForEntity(
                getAddress() + "/" + studentRepository.findAll().get(0).getId().toString(), Student.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
    }

    @DisplayName("Проверка на получение всех студентов")
    @Test
    public void readAllStudentsTest() {
        Student student = new Student(null, "Волчок", 11);
        Student student1 = new Student(null, "Волк", 12);

        studentRepository.save(student);
        studentRepository.save(student1);

        ResponseEntity<List<Student>> response = testRestTemplate.exchange(getAddress(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);

        assertThat(response.getBody().get(0))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
        assertThat(response.getBody().get(1))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student1);
    }

    @DisplayName("Проверка на редактирование студента")
    @Test
    public void updateStudentTest() {
        Student student = new Student(null, "Волчок", 11);

        studentRepository.save(student);
        Long studentId = student.getId();

        student.setName("Волк");
        student.setAge(12);

        String url = getAddress() + "/" + studentId;

        RequestEntity<Student> request = new RequestEntity<>(student, HttpMethod.PUT, URI.create(url));

        ResponseEntity<Student> response = testRestTemplate.exchange(url, HttpMethod.PUT, request, Student.class);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
    }

    @DisplayName("Проверка на удаление студента по ID")
    @Test
    public void deleteStudentTest() {
        Student student = new Student(null, "Волк", 12);

        studentRepository.save(student);

        ResponseEntity<Student> responseDelete = testRestTemplate.exchange(
                getAddress() + "/" + studentRepository.findAll().get(0).getId().toString(),
                HttpMethod.DELETE, null, Student.class);

        assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @DisplayName("Проверка на нахождение студента по году")
    @Test
    public void filterStudentByAgeTest() {
        Student student = new Student(null, "Волк", 4);
        Student student1 = new Student(null, "Волчок", 7);

        studentRepository.save(student);
        studentRepository.save(student1);

        ResponseEntity<List<Student>> responseFilter = testRestTemplate.exchange(getAddress() + "/filter/by-age?age=7",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(responseFilter.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseFilter.getBody()).isNotNull();
        assertThat(responseFilter.getBody().get(0))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student1);
    }

    @DisplayName("Проверка на нахождение студентов по годам")
    @Test
    public void filterStudentByAgeBetweenTest() {
        Student student = new Student(null, "Волк", 4);
        Student student1 = new Student(null, "Волчок", 7);

        studentRepository.save(student);
        studentRepository.save(student1);

        ResponseEntity<List<Student>> responseFilter = testRestTemplate.exchange(getAddress() + "/filter/by-ageMin-max?min=6&max=8",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(responseFilter.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseFilter.getBody()).isNotNull();
        assertThat(responseFilter.getBody().size()).isEqualTo(1);
        assertThat(responseFilter.getBody().get(0))
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student1);
    }

    @DisplayName("Получение студентов по ID факультета")
    @Test
    public void getStudentsByFacultyIdTest() {
        Faculty faculty = new Faculty(null, "Пуффендуй", "Желтый");
        facultyRepository.save(faculty);

        Student s1 = new Student(null, "Седрик", 17);
        s1.setFaculty(faculty);
        studentRepository.save(s1);

        Student s2 = new Student(null, "Ханна", 16);
        s2.setFaculty(faculty);
        studentRepository.save(s2);

        ResponseEntity<List<Student>> response = testRestTemplate.exchange(
                getAddress() + "/faculty/" + faculty.getId() + "/students", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).hasSize(2);
        Assertions.assertThat(response.getBody())
                .extracting(Student::getName)
                .containsExactlyInAnyOrder("Седрик", "Ханна");
    }

    @DisplayName("Получение факультета по ID студента")
    @Test
    public void getFacultyByStudentIdTest() {
        Faculty faculty = new Faculty(null, "Гриффиндор", "Красный");

        facultyRepository.save(faculty);

        Student student = new Student(null, "Гарри Поттер", 11);

        student.setFaculty(faculty);
        studentRepository.save(student);

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(getAddress() + "/" + student.getId() + "/faculty",
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Гриффиндор");
    }

    @DisplayName("Ошибка при получении факультета по несуществующему ID студента")
    @Test
    public void getFacultyByNonexistentStudentIdTest() {
        ResponseEntity<String> response = testRestTemplate.getForEntity(
                getAddress() + "/999/faculty", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private String getAddress() {
        return "http://localhost:" + port + "/student";
    }
}
