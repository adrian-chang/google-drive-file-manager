import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;

/**
 * Maintain the photos folder
 */
public class Photos {

    /**
     * Maintain the drive service
     */
    private Drive service;

    /**
     * Hold reference to root folder id
     */
    private String rootFolderId;

    /**
     * Constructor
     * @param drive the drive service object
     * @throws IOException if cannot find file
     */
    public Photos(Drive drive) throws IOException {
        service = drive;

        // set the root folder
        service.files().list()
            .setQ("'0B_Xix_3RsFU0dFJhNnFlaS10ejQ' in parents")
            .setFields("nextPageToken, files(id, name)")
            .execute()
            .getFiles()
            .forEach(file -> rootFolderId = file.getId());
    }

    /**
     * Run the management operation
     */
    public void manage() throws IOException {
        service.files().list()
                .setQ("mimeType contains 'image/' or mimeType contains 'video/'")
    }

    private void getAllVideoImageFiles() {
        
    }

    /**
     * Is a file in a root folder
     * @param file google drive file
     * @return true if is, else false
     */
    private boolean isInRootFolder(File file) {
        return false;
    }


}
