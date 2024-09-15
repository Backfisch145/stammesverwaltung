package com.vcp.hessen.kurhessen.features.usermanagement;

import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.data.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.spec.ECField;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {


    private final AuthenticatedUser user;
    private final UserRepository repository;
    private final TribeRepository repositoryTribe;
    private final UsermanagementConfig usermanagementConfig;

    private final SimpleDateFormat gruenDateFormat;

    public UserService(AuthenticatedUser user, UserRepository repository, TribeRepository repositoryTribe, UsermanagementConfig usermanagementConfig, TribeRepository tribeRepository) {
        this.user = user;
        this.repository = repository;
        this.repositoryTribe = repositoryTribe;
        this.usermanagementConfig = usermanagementConfig;

        gruenDateFormat = new SimpleDateFormat(usermanagementConfig.getGruenDateFormat());
    }

    public List<User> getAll() {
        if (user.get().isPresent()) {
            User u = user.get().get();

            if (u.hasRole(Role.ADMIN)) {
                return repository.findAll();
            } if (u.hasRole(Role.MODERATOR)) {
                return repository.findAllByTribe(user.get().get().getTribe());
            }
        }
        return new ArrayList<>();

    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(User user) {
        repository.deleteById(user.getId());

        try {
            log.info("User " + this.user.get().get().getUsername() + " deleted user with id" + user.getUsername());
        } catch (Exception e) {
            log.error("User got deleted by Unauthorized User!", e);
        }

    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> list(Pageable pageable, Specification<User> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }


    public String importGruenFile(File file) {

        StringBuilder errorString = new StringBuilder();
        try {
            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            // Skip table Head
            if (iterator.hasNext()) {
                iterator.next();
            } else {
                return "Could not read Users from File. The File seems to be empty!";
            }

            ArrayList<User> users = new ArrayList<>();


            HashMap<Tribe, ArrayList<User>> tribeHashMap = new HashMap<>();

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();

                int correctedRow = currentRow.getRowNum()+1;


                Cell stammIdCell = currentRow.getCell(0);
//                Cell stammCell = currentRow.getCell(1);
                Cell memberNrCell = currentRow.getCell(2);
                Cell firstNameCell = currentRow.getCell(3);
                Cell lastNameCell = currentRow.getCell(4);
//                Cell additionalCell = currentRow.getCell(5);
                Cell streetCell = currentRow.getCell(6);
                Cell postalCodeCell = currentRow.getCell(7);
                Cell cityCell = currentRow.getCell(8);
                Cell countryCell = currentRow.getCell(9);
                Cell phoneCell = currentRow.getCell(10);
                Cell mobilePhoneCell = currentRow.getCell(11);
                Cell emailCell = currentRow.getCell(12);
                Cell birthdayDateCell = currentRow.getCell(13);
                Cell joinDateCell = currentRow.getCell(14);
                Cell leaveDateCell = currentRow.getCell(15);
                Cell sexCell = currentRow.getCell(16);


                Long stammesId = null;
                try {
                    stammesId = (long) stammIdCell.getNumericCellValue();
                } catch (Exception e) {
                    log.error("importGruenFile: could not get TribeId from File");
                    return "Could not get TribeId from File";
                }

                Gender gender = null;
                try {
                    int sex = (int) sexCell.getNumericCellValue();
                    if (sex == 1) {
                        gender = Gender.MALE;
                    }
                    if (sex == 2) {
                        gender = Gender.FEMALE;
                    }
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not map Sex", e);
                }

                String email = null;
                try {
                    email = emailCell.getStringCellValue();
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not be processed!", e);
                }

                String phone = null;
                try {
                    phone = mobilePhoneCell.getStringCellValue();
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not be processed!", e);
                }

                if (phone == null || phone.isBlank()) {
                    try {
                        phone = phoneCell.getStringCellValue();
                    } catch (Exception e) {
                        log.debug("Row " + correctedRow + " could not be processed!", e);
                    }
                }

                LocalDate localDateBirthday = null;
                try {
                    String cellValue = birthdayDateCell.getStringCellValue();
                    java.util.Date birthday = gruenDateFormat.parse(cellValue);
                    localDateBirthday = LocalDate.from(birthday.toInstant().atZone(ZoneId.systemDefault()));
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not be processed!", e);
                }

                String address = "";
                try {
                    address += streetCell.getStringCellValue();
                    address += ", ";
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not be processed!", e);
                }

                try {
                    address += ((int) postalCodeCell.getNumericCellValue()) + " ";
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not be processed!", e);
                }

                try {
                    address += cityCell.getStringCellValue() + " ";
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not be processed!", e);
                }

                try {
                    address = address.trim();
                    address += ", " + countryCell.getStringCellValue();
                } catch (Exception e) {
                    log.debug("Row " + correctedRow + " could not be processed!", e);
                }

                Tribe t = null;

                try {
                    if (!tribeHashMap.containsKey(stammesId)) {
                        t = repositoryTribe.findById(stammesId).orElseGet(null);

                        log.info("importGruenFile: t = " + t.getId() + " " + t.getName());

                        tribeHashMap.put(t, new ArrayList<>());
                    }
                } catch (Exception e) {
                    log.warn("Row " + correctedRow + " could not find matching Tribe", e);
                }




                User newUser = new User(
                        (int) memberNrCell.getNumericCellValue(),
                        firstNameCell.getStringCellValue().trim().toLowerCase() + "." + lastNameCell.getStringCellValue().trim().toLowerCase(),
                        firstNameCell.getStringCellValue().trim(),
                        lastNameCell.getStringCellValue().trim(),
                        email,
                        phone,
                        address,
                        localDateBirthday,
                        null,
                        gender
                );

                log.debug("importGruenFile: User [" + newUser.getUsername() + "] was red from row " + correctedRow);

                if (newUser.getMembershipId() == null) {
                    tribeHashMap.get(t).add(newUser);
                } else if (repository.findByMembershipId(newUser.getMembershipId()).isEmpty()) {
                    log.info("importGruenFile: User [" + newUser.getUsername() + "] will be added to the Database");
                    tribeHashMap.get(t).add(newUser);
                }


            }

            for (Tribe tribe : tribeHashMap.keySet()) {
                for (User user1 : tribeHashMap.get(tribe)) {
                    User u = repository.save(user1);
                    u.setTribe(tribe);
                    repository.save(u);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("The uploaded File could not be found!", e);
            errorString.append("The uploaded File could not be found!");
        } catch (IOException e) {
            log.error("The uploaded File could processed!", e);
            errorString.append("The uploaded File could processed!");
        }

        return errorString.toString();
    }


}
