package ru.hogwarts.school.exception;

public class StudentNotFoundException extends RuntimeException {
    public StudentNotFoundException(Long id) {
        super("студент с " + id + " не найден.");
    }
}
