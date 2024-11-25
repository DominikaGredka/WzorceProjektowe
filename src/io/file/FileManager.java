package io.file;

import model.LibraryCatalog;

public interface FileManager {
    LibraryCatalog importData();
    void exportData(LibraryCatalog  library);
}
