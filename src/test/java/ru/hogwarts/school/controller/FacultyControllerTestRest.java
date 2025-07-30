package ru.hogwarts.school.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FacultyControllerTestRest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private FacultyRepository facultyRepository;

    @BeforeEach
    void CleanBd() {
        facultyRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /faculty - создание факультета")
    void createFacultyTest() {
        Faculty request = new Faculty(null, "Гриффиндор", "Красный");

        ResponseEntity<Faculty> response = testRestTemplate.postForEntity(getAddress(), request, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Гриффиндор");
        assertThat(response.getBody().getColor()).isEqualTo("Красный");
    }

    @Test
    @DisplayName("GET /faculty/{id} - получение факультета по ID")
    void getFacultyByIdTest() {
        Faculty saved = facultyRepository.save(new Faculty(null, "Пуффендуй", "Желтый"));

        ResponseEntity<Faculty> response = testRestTemplate.getForEntity(getAddress() + "/" + saved.getId(), Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Пуффендуй");
    }

    @Test
    @DisplayName("PUT /faculty/{id} - обновление факультета")
    void updateFacultyTest() {
        Faculty saved = facultyRepository.save(new Faculty(null, "Когтевран", "Синий"));
        Faculty update = new Faculty(saved.getId(), "Когтевран", "Голубой");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Faculty> entity = new HttpEntity<>(update, headers);

        ResponseEntity<Faculty> response = testRestTemplate.exchange(
                getAddress() + "/" + saved.getId(), HttpMethod.PUT, entity, Faculty.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getColor()).isEqualTo("Голубой");
    }

    @Test
    @DisplayName("DELETE /faculty/{id} - удаление факультета")
    void deleteFacultyTest() {
        Faculty saved = facultyRepository.save(new Faculty(null, "Слизерин", "Зеленый"));

        ResponseEntity<Void> response = testRestTemplate.exchange(
                getAddress() + "/" + saved.getId(), HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(facultyRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("GET /faculty - получение всех факультетов")
    void getAllFacultiesTest() {
        facultyRepository.save(new Faculty(null, "Гриффиндор", "Красный"));
        facultyRepository.save(new Faculty(null, "Слизерин", "Зеленый"));

        ResponseEntity<Faculty[]> response = testRestTemplate.getForEntity(getAddress(), Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("GET /faculty/filter/by-color - фильтр по цвету")
    void getFacultiesByColorTest() {
        facultyRepository.save(new Faculty(null, "Когтевран", "Синий"));
        facultyRepository.save(new Faculty(null, "ХЗ", "Синий"));

        ResponseEntity<Faculty[]> response = testRestTemplate.getForEntity(
                getAddress() + "/filter/by-color?color=Синий", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("GET /faculty/search?query=имяИлиЦвет - поиск по имени или цвету")
    void searchByNameOrColorTest() {
        facultyRepository.save(new Faculty(null, "Пуффендуй", "Желтый"));
        facultyRepository.save(new Faculty(null, "ХЗ", "Желтый"));

        ResponseEntity<Faculty[]> response = testRestTemplate.getForEntity(
                getAddress() + "/search?query=желтый", Faculty[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    private String getAddress() {
        return "http://localhost:" + port + "/faculty";
    }
}
