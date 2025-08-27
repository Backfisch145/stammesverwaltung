package com.vcp.hessen.kurhessen.core;


import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.core.security.Role;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.data.UserFile;
import com.vcp.hessen.kurhessen.data.UserFileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController()
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final AuthenticatedUser authenticatedUser;
    private final UserFileRepository userFileRepository;


    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('MEMBER_READ')")
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable long id) throws IOException {
        UserFile file = userFileRepository.findById(id).orElseThrow(()-> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        User user = authenticatedUser.get().orElseThrow(()-> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        if (!user.hasPermission(Role.SUPERADMIN_ROLE)) {

            if (file.getUser().getTribe() != user.getTribe()) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            }

            if (!file.getUser().equals(user) && !user.hasPermission("MEMBER_READ")) {
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            }
        }


        File f =  new File(file.getPath());
        String extension = FilenameUtils.getExtension(f.getName()).toLowerCase();


        MediaType mediaType = MediaType.ALL;
        if (extension.equals("pdf")) {
            mediaType =  MediaType.APPLICATION_PDF;
        } else if (extension.equals("jpg") || extension.equals("jpeg")) {
            mediaType =  MediaType.IMAGE_JPEG;
        } else if (extension.equals("png")) {
            mediaType =  MediaType.IMAGE_PNG;
        }


        ByteArrayResource bytes = new ByteArrayResource(Files.readAllBytes(f.toPath()));
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(bytes);

    }


}
