package com.vcp.hessen.kurhessen.features.usermanagement.domain;

import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.data.*;
import com.vcp.hessen.kurhessen.features.usermanagement.UsermanagementConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class UserFilesService {


    private final AuthenticatedUser authenticatedUser;
    private final UserRepository repository;
    private final UserFileRepository fileRepository;
    private final TribeRepository repositoryTribe;
    private final UsermanagementConfig usermanagementConfig;

    public UserFilesService(AuthenticatedUser user, UserRepository repository, TribeRepository repositoryTribe, UsermanagementConfig usermanagementConfig, TribeRepository tribeRepository, UserFileRepository fileRepository) {
        this.authenticatedUser = user;
        this.repository = repository;
        this.repositoryTribe = repositoryTribe;
        this.usermanagementConfig = usermanagementConfig;
        this.fileRepository = fileRepository;
    }

    public Optional<UserFile> get(Long id) {
        return fileRepository.findById(id);
    }

}
