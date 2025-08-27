package com.vcp.hessen.kurhessen.data;

import com.vcp.hessen.kurhessen.core.util.FileHelper;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_files")
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Getter
@Setter
public class UserFile {

    @Id
    @GeneratedValue()
    private Long id;

    @Column
    private String filename;

    @Column
    private String path;

    @Enumerated(EnumType.STRING)
    private UserFileType type;

    private LocalDateTime uploadDate =  LocalDateTime.now();

    private Long size;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private User uploader;


    public UserFile(User uploader, String filename, String path, UserFileType type) {
        this.uploader = uploader;
        this.filename = filename;
        this.path = path;
        this.type = type;
        this.size = new File(path).length();
    }

    public String getSizeOfFileString() {
        if (size == null) {
            size = new File(path).length();
        }

        return FileHelper.INSTANCE.getSizeOfFileString(size);
    }

    public enum UserFileType {
        MEMBERSHIP_AGREEMENT("MembershipAgreement");

        public final String translationKey;

        UserFileType(String translationKey) {
            this.translationKey = translationKey;
        }

    }
}
