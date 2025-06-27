package com.vcp.hessen.kurhessen.core.constants;

import java.io.File;

public enum Directories {
    ROOT("C:/Users/Fnabi/Desktop/stammesverwaltung"),
    FILES_ROOT(File.separator + "files"),
    USER_FILES(FILES_ROOT.path +  File.separator + "users");

    private final String path;

    Directories(String path) {
        this.path = path;
    }

    public File getDir() {
        return new File(ROOT.path, this.path);
    }


}
