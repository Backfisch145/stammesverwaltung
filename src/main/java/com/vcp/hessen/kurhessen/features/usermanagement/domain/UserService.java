package com.vcp.hessen.kurhessen.features.usermanagement.domain;

import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.data.*;
import com.vcp.hessen.kurhessen.features.usermanagement.UsermanagementConfig;
import com.vcp.hessen.kurhessen.features.usermanagement.domain.importer.UserImporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final AuthenticatedUser authenticatedUser;
    private final UserRepository repository;
    private final UserImporter userImporter;
    private final TribeRepository repositoryTribe;
    private final UsermanagementConfig usermanagementConfig;


    @PreAuthorize("hasAuthority('MEMBER:READ')")
    public List<User> getAll() {
        if (authenticatedUser.get().isPresent()) {
            User u = authenticatedUser.get().get();

            return repository.findAllByTribe(u.getTribe());
        } else {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PreAuthorize("hasAuthority('MEMBER:READ')")
    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(User user) {
        repository.delete(user);

        for (UserFile file : user.getUserFiles()) {
            new File(file.getPath()).delete();
        }

        try {
            log.info("User " + this.authenticatedUser.get().get().getUsername() + " deleted user with id" + user.getUsername());
        } catch (Exception e) {
            log.error("User got deleted by Unauthorized User!", e);
        }

    }

    public Page<User> list(Pageable pageable) {
        return list(pageable, null);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        User user = authenticatedUser.get().orElseThrow(() -> new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        Specification<User> tribeFilter = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("tribe"), user.getTribe());

        if (filter != null) {
            tribeFilter = tribeFilter.and(filter);
        }

        return repository.findAll(tribeFilter, pageable);
    }

    public int countUser() {
        if (authenticatedUser.get().isPresent()) {
            User u = authenticatedUser.get().get();

            return repository.countAllByTribe(u.getTribe());
        } else {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }

    public String importGruenFile(File file) {
        return userImporter.importUsers(file);
    }


}
