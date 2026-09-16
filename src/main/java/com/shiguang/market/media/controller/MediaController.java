package com.shiguang.market.media.controller;

import com.shiguang.market.common.BusinessException;
import com.shiguang.market.common.Result;
import com.shiguang.market.media.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp", "image/gif", ".gif");

    @Value("${app.upload.directory:./uploads}")
    private String uploadDirectory;

    @PostMapping("/images")
    public Result<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty() || file.getSize() > 5 * 1024 * 1024L) {
            throw new BusinessException(400, "图片不能为空且不能超过 5MB");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException(400, "仅支持 JPEG、PNG、WebP 或 GIF 图片");
        }
        try {
            Path root = Path.of(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(root);
            String filename = UUID.randomUUID() + EXTENSIONS.get(file.getContentType());
            Path target = root.resolve(filename).normalize();
            if (!target.startsWith(root)) throw new BusinessException(400, "非法文件路径");
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return Result.ok(new UploadResponse("/api/v1/uploads/" + filename,
                    file.getOriginalFilename(), file.getSize()));
        } catch (IOException e) {
            throw new BusinessException(500, "图片保存失败");
        }
    }
}
