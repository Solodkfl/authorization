package com.java.file;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import com.java.dto.ResDTO;

public interface FileService {
    public ResDTO upload(MultipartFile file, Authentication authentication);
    public ResponseEntity<?> uri(String type, Long fileNo, Authentication authentication);
    public ResponseEntity<?> uri(Long fileNo, Authentication authentication);
}
