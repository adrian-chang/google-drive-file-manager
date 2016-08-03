import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

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
     * Size of query
     */
    private static final int PAGE_SIZE = 100;

    /**
     * Hold a logger here
     */
    private static final Logger logger = LoggerFactory.getLogger(Photos.class);

    /**
     * Constructor
     * @param drive the drive service object
     * @param name the name of the folder
     * @throws IOException if cannot find file
     */
    public Photos(Drive drive, String name) throws IOException {
        service = drive;

        // set the root folder
        service.files().list()
            .setQ(String.format("'root' in parents and name = '%s'", name))
            .setFields("nextPageToken, files(id, name)")
            .setPageSize(1)
            .execute()
            .getFiles()
            .forEach(file -> rootFolderId = file.getId());
    }

    /**
     * Run the management operation
     */
    public void manage() throws IOException {
        String pageToken = null;
        do {
            // get the list until there are no more page token
            FileList allVideosPhotos = getAllVideoImageFiles(pageToken);
            allVideosPhotos.getFiles().forEach(this::organizePhotoVideo);
            pageToken = allVideosPhotos.getNextPageToken();
        } while (pageToken != null);
    }

    /**
     * Pull all known videos and iamges
     * @param pageToken the current location of paging
     * @throws IOException if theres a problem getting them
     * @return all video and image files
     */
    private FileList getAllVideoImageFiles(String pageToken) throws IOException {
        return service.files().list()
                .setQ("mimeType contains 'image/' or mimeType contains 'video/'")
                .setFields("nextPageToken, files(id, mimeType, name, parents, webViewLink)")
                .setPageToken(pageToken)
                .setPageSize(PAGE_SIZE)
                .execute();
    }


    /**
     * Organize photo and video as necessary
     * @param file the file to organize
     */
    private void organizePhotoVideo(File file) {
        try {
            logger.debug("Examining file {}", file.getName());
            // need to check it's in the right root folder as it's all files we're looking at
            if (isInRootFolder(file)) {
                System.out.println("foo");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Is a file in a root folder
     * @param file google drive file
     * @return true if is, else false
     */
    private boolean isInRootFolder(File file) throws IOException {
        List<String> parents = file.getParents();
        // empty
        if (parents == null) {
            return false;
        }

        for (String parent: parents) {
            boolean isInRoot = parent.equals(rootFolderId);

            if (!isInRoot) {
                isInRoot = isInRootFolder(
                    service
                        .files()
                        .get(parent)
                        .setFields("id, mimeType, name, parents, webViewLink")
                        .execute()
                );
            }
            // only return if true
            if (isInRoot) {
                return true;
            }
        }

        return false;
    }


}
