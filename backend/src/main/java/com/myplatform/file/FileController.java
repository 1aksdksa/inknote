package com.myplatform.file;

import com.myplatform.common.ApiResult;
import com.myplatform.common.BusinessException;
import com.myplatform.common.ErrorCodes;
import com.myplatform.security.AuthContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

  private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg", "gif", "webp");

  private final Path uploadDir;

  public FileController(@Value("${inknote.upload-dir:./data/uploads}") String uploadDir) throws IOException {
    this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    Files.createDirectories(this.uploadDir);
  }

  @PostMapping("/upload-image")
  public ApiResult<UploadImageVo> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
    AuthContext.requireUserId();
    if (file == null || file.isEmpty()) {
      throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "请选择图片文件");
    }
    String original = file.getOriginalFilename() == null ? "image.png" : file.getOriginalFilename();
    String ext = extensionOf(original);
    if (!ALLOWED_EXT.contains(ext)) {
      throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "仅支持 png/jpg/jpeg/gif/webp");
    }
    if (file.getSize() > 5 * 1024 * 1024L) {
      throw new BusinessException(ErrorCodes.VALIDATION_ERROR, "图片不能超过 5MB");
    }
    String storedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
    Path target = uploadDir.resolve(storedName);
    file.transferTo(target);
    return ApiResult.ok(new UploadImageVo("/uploads/" + storedName, storedName));
  }

  private String extensionOf(String filename) {
    int idx = filename.lastIndexOf('.');
    if (idx < 0 || idx == filename.length() - 1) {
      return "";
    }
    return filename.substring(idx + 1).toLowerCase(Locale.ROOT);
  }
}