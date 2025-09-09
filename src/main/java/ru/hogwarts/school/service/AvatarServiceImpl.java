package ru.hogwarts.school.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
@Slf4j
public class AvatarServiceImpl implements AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarDir;

    public AvatarServiceImpl(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public void uploadAvatar(Long studentId, MultipartFile avatarFile) throws IOException {
        log.info("Вызван метод загрузки аватара для студента id = {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Студент с id " + studentId + " не найден"));

        String originalFilename = avatarFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            log.error("Файл не содержит имени");
            throw new IllegalArgumentException("Файл не содержит имени (originalFilename == null)");
        }

        Path filePath = Path.of(avatarDir, student.getId() + "." + getExtension(avatarFile.getOriginalFilename()));
        log.debug("Путь для сохранения аватара: {}", filePath);

        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = avatarFile.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
            log.debug("файл аватара успешно сохранён по пути: {}", filePath);
        }
        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(avatarFile.getSize());
        avatar.setMediaType(avatarFile.getContentType());
        avatar.setData(generateDataForBD(filePath));
        avatarRepository.save(avatar);
        log.info("Аватар студента id = {} успешно сохранён", studentId);
    }

    private byte[] generateDataForBD(Path filePath) throws IOException {
        log.debug("Вызван метод генерации уменьшенной копии для аватара");
        try (
                InputStream is = Files.newInputStream(filePath);
                BufferedInputStream bis = new BufferedInputStream(is, 1024);
                ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);

            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics2D = preview.createGraphics();
            graphics2D.drawImage(image, 0, 0, 100, height, null);
            graphics2D.dispose();

            ImageIO.write(preview, getExtension(filePath.getFileName().toString()), baos);
            log.debug("Уменьшенное изображение создано");
            return baos.toByteArray();
        }
    }

    public Avatar findAvatar(Long studentId) {
        log.info("Вызван метод поиска аватара по студенту с id = {}", studentId);
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    private String getExtension(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        log.debug("Определено расширение файла: {}", extension);
        return extension;
    }

    public Page<Avatar> getAvatars(int page, int size) {
        log.info("Вызван метод получения списка аватаров, страница={}, размер={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return avatarRepository.findAll(pageable);
    }

    @Override
    public void writeAvatarToResponse(Long id, HttpServletResponse response) throws IOException {
        log.info("Вызван метод выгрузки аватара для студента id={}", id);
        Avatar avatar = findAvatar(id);

        Path path = Path.of(avatar.getFilePath());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(avatar.getMediaType());
        response.setContentLength((int) avatar.getFileSize());

        try (
                InputStream is = Files.newInputStream(path);
                OutputStream os = response.getOutputStream()
        ) {
            is.transferTo(os);
            log.debug("Аватар студента id={} отправлен в HTTP-ответ", id);
        }
    }
}
