package imageSorter;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.StreamSupport;

@Data
public class ImageSortTarInfo {
    public static final int TAG_TYPE_CREATE_DATE = 306;

    private String createDt;
    private String createYear;
    private String createMonth;
    private LocalDateTime createDateTime;
    private File baseDir;
    private File yearDir;
    private File leafDir;


    public ImageSortTarInfo(File orgFile, File baseDir) {
        try {
            Metadata metadata = JpegMetadataReader.readMetadata(new FileInputStream(orgFile));
            Iterable<Directory> iter = metadata.getDirectories();

            FileTime fileTime = Files.readAttributes(orgFile.toPath(), BasicFileAttributes.class).creationTime();
            LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());


            LocalDateTime imgTime = StreamSupport.stream(iter.spliterator(), false)
                    .filter(dir -> dir.getName().equals("Exif IFD0"))
                    .map(dir -> LocalDateTime.ofInstant(dir.getDate(TAG_TYPE_CREATE_DATE).toInstant(), ZoneId.systemDefault()))
                    .findFirst()
                    .orElseGet(() -> LocalDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault()));

            this.createMonth = String.format("%02d", imgTime.getMonthValue());
            this.createYear = String.valueOf(imgTime.getYear());
            this.createDateTime = imgTime;
            this.createDt = imgTime.toString();
        }catch(Exception e){
            e.printStackTrace();
        }

        yearDir = new File(baseDir.getPath()+"/"+createYear);
        leafDir = new File(yearDir.getPath()+"/"+createMonth);
    }



}
