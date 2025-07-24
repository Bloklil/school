package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.StudentService;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("faculty")
public class FacultyController {

    private final FacultyService facultyService;


    public FacultyController(FacultyService facultyService, StudentService studentService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @GetMapping("{id}")
    public Faculty getFaculty(@PathVariable Long id) {
        return facultyService.getFaculty(id);
    }

    @PutMapping("{id}")
    public Faculty updateFaculty(@PathVariable Long id, @RequestBody Faculty faculty) {
        return facultyService.updateFaculty(id, faculty);
    }

    @DeleteMapping("{id}")
    public void deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
    }

    @GetMapping
    public Collection<Faculty> getAllFaculties() {
        return facultyService.getAllFaculties();
    }

    @GetMapping("/filter/by-color")
    public Collection<Faculty> getFacultiesByColor(@RequestParam String color) {
        return facultyService.findByColor(color);
    }

    @GetMapping("/search")
    public List<Faculty> searchByNameOrColor(@RequestParam String query) {
        return facultyService.findByNameOrColorIgnoreCase(query);
    }
}
