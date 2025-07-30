package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTestWebMvc {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private FacultyRepository facultyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Создание факультета")
    public void createFacultyTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");
        faculty.setColor("красный");

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    @DisplayName("Получение факультета по id")
    public void getFacultyTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");
        faculty.setColor("красный");

        when(facultyService.getFaculty(anyLong())).thenReturn(faculty);

        mockMvc.perform(get("/faculty/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    @DisplayName("Обновление факультета")
    public void updateFacultyTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("грифы");
        faculty.setColor("красный");

        when(facultyService.updateFaculty(anyLong(), any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(put("/faculty/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()));
    }

    @Test
    @DisplayName("Проверка на удаление факультета по ID")
    public void deleteFacultyTest() throws Exception {
        final String name = "Гриффиндор";
        final String color = "красный";
        final long id = 1;

        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);

        when(facultyRepository.existsById(anyLong())).thenReturn(true);
        when(facultyRepository.findById(id)).thenReturn(Optional.of(faculty));

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение всех факультетов")
    public void getAllFacultiesTest() throws Exception {
        Faculty faculty1 = new Faculty();
        faculty1.setId(1L);
        faculty1.setName("Гриффиндор");
        faculty1.setColor("красный");

        Faculty faculty2 = new Faculty();
        faculty2.setId(2L);
        faculty2.setName("Пуффендуй");
        faculty2.setColor("жёлтый");

        List<Faculty> faculties = List.of(faculty1, faculty2);

        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(faculties.size()))
                .andExpect(jsonPath("$[0].id").value(faculty1.getId()))
                .andExpect(jsonPath("$[1].id").value(faculty2.getId()));
    }

    @Test
    @DisplayName("Фильтрация факультетов по цвету")
    public void getFacultiesByColorTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");
        faculty.setColor("чёрный");

        when(facultyService.findByColor("чёрный")).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/filter/by-color")
                        .param("color", "чёрный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].color").value("чёрный"));
    }

    @Test
    @DisplayName("Поиск факультетов по имени или цвету")
    public void searchByNameOrColorTest() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Гриффиндор");
        faculty.setColor("чёрный");

        when(facultyService.findByNameOrColorIgnoreCase("гриф")).thenReturn(List.of(faculty));

        mockMvc.perform(get("/faculty/search")
                        .param("query", "гриф"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Гриффиндор"));
    }

}
