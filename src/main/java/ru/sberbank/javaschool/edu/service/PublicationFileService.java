package ru.sberbank.javaschool.edu.service;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.sberbank.javaschool.edu.domain.*;
import ru.sberbank.javaschool.edu.repository.PublicationFileRepository;
import ru.sberbank.javaschool.edu.repository.PublicationRepository;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class PublicationFileService {

    private final PublicationFileRepository publicationFileRepository;
    private final PublicationRepository publicationRepository;

    private static final Logger logger = LoggerFactory.getLogger(PublicationFileService.class);

    @Value("${upload.path}")
    private String uploadPath;
    @Value("${spring.mail.username}")
    private String email;
    @Value("${spring.mail.password}")
    private String emailPass;

    @Autowired
    public PublicationFileService(
            PublicationFileRepository publicationFileRepository,
            PublicationRepository publicationRepository
    ) {
        this.publicationFileRepository = publicationFileRepository;
        this.publicationRepository = publicationRepository;
    }


    public void saveFile(MultipartFile file, long idPublication, User user) {
        if (file != null) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String filename = uuidFile + "_" + file.getOriginalFilename();
            try {
                file.transferTo(new File(uploadPath, filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveFileToYDisk(filename);

            PublicationFile publicationFile = new PublicationFile();
            publicationFile.setFilename(filename);
            Publication publication = publicationRepository.findPublicationById(idPublication);
            publicationFile.setPublication(publication);
            publicationFile.setPath(uploadPath);
            publicationFile.setUser(user);
            publicationFileRepository.save(publicationFile);

            logger.info("Successfully save pubfile to DB, filename: " + filename);
        }

    }

    private void saveFileToYDisk(String filename) {
        String URL = "https://webdav.yandex.ru/";

        Sardine sardine = SardineFactory.begin(email, emailPass);

        InputStream inStr = null;

        try {
            inStr = new FileInputStream(uploadPath + "/" + filename);
            sardine.put(URL + "educlassroom/" + filename, inStr);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        } finally {
            if (inStr != null) {
                try {
                    inStr.close();
                } catch (Exception ignored) {
                    //ignore
                }
            }
        }

        logger.info("Successfully save file to YDisk, filename: " + filename);
    }

    public void getFilesFromYDisk(User user, Task task) {
        List<PublicationFile> publicationFileList
                = publicationFileRepository.findAllByUserAndPublication(user, task);
        gettingFromYDisk(publicationFileList);

    }

    public void getMaterialsFilesFromYDisk(List<Material> materials) {
        List<PublicationFile> publicationFileList = new LinkedList<>();
        for (Material material : materials) {
            publicationFileList.addAll(publicationFileRepository.findAllByPublication(material));
        }
        gettingFromYDisk(publicationFileList);
    }

    private void gettingFromYDisk(List<PublicationFile> publicationFileList) {
        String URL = "https://webdav.yandex.ru/";

        Sardine sardine = SardineFactory.begin(email, emailPass);

        for (PublicationFile pf : publicationFileList) {
            try {
                InputStream is = sardine.get(URL + "educlassroom/" + pf.getFilename());

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while (true) {

                    if ((nRead = is.read(data, 0, data.length)) == -1) {
                        break;
                    }

                    buffer.write(data, 0, nRead);
                }

                try (OutputStream outputStream
                             = new FileOutputStream(uploadPath + "/" + pf.getFilename())) {
                    buffer.writeTo(outputStream);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteFile(Long fileId) {
        PublicationFile file = publicationFileRepository.findPublicationFileById(fileId);
        if (file == null) {
            return;
        }
        try {
            deleteFileFromYDisk(file.getFilename());
            logger.info("Successfully removing file " + file.getFilename() + " from YDisk");
        } catch (IOException e) {
            logger.debug("Error during removing file " + file.getFilename() + " from YDisk");
            e.printStackTrace();
        }
        publicationFileRepository.deleteById(fileId);
        logger.info("Successfully removing file from DB");
    }

    private void deleteFileFromYDisk(String filename) throws IOException {
        String URL = "https://webdav.yandex.ru/" + "educlassroom/" + filename;
        Sardine sardine = SardineFactory.begin(email, emailPass);

        if (sardine.exists(URL)) {
            sardine.delete(URL);
        }

    }

}
