package imageSorter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ImageSorterBatch {
    private final File orgDir;
    private final File baseDir;
    private File leafDir;
    private int copyCnt = 0;
    private int skipCnt = 0;


    public static void main(String args []) {
        System.out.println(args.length);
        System.out.println(args[1]);
        imageSortAvaible(args.length != 2, "파라미터1: 원본 경로, 파라미터2: 대상경로");
        new ImageSorterBatch(args[0], args[1]);
    }

    public ImageSorterBatch(String ordDirStr, String tarDirStr) {
        this.orgDir = new File(ordDirStr);
        this.baseDir = new File(tarDirStr);
        List<File> orgFiles = getImgFiles();
        imageSortAvaible(orgFiles.size() < 1, "대상 디렉토리에 이미지 파일이 업습니다.");

        goImageArrange(orgFiles);
    }

    public void goImageArrange(List<File> orgFiles)    {

        try {
            //imageSorterValidator.checkImagArrange(imageSorterData);

            //orgFiles = getImgFiles();
            File[] tarDir = {new File("")};

            orgFiles.stream()
                    .filter(file -> {
                        ImageSortTarInfo tarInfo = new ImageSortTarInfo(file , this.baseDir);
                        makeTargetDir(tarInfo.getYearDir(), tarInfo.getLeafDir());
                        this.leafDir = tarInfo.getLeafDir();
                        return checkTarFile(file,tarInfo.getLeafDir());
                    })
                    //.filter(file -> checkTarFile(file, getTarDir(new ImageSortTarInfo(file))))
                    .forEach(file -> copyImgFile(file, this.leafDir));

            System.out.println("이미지 정리 완료 ========>");
            System.out.println("이미지 정리 건수 : " + this.copyCnt);
            System.out.println("이미지 Skip 건수 : " + this.skipCnt);


        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }

    private void makeTargetDir(File yearDir, File leafDir) {
        dirCheckAndCreate(this.baseDir);

        dirCheckAndCreate(yearDir);

        dirCheckAndCreate(leafDir);
    }

    private void dirCheckAndCreate(File baseDir) {
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }
    }

    public boolean checkTarFile(File orgFile, File tarDir){
        File tarFile = new File(tarDir,orgFile.getName());
        if(tarFile.exists()){
            skipCnt++;
            return false;
        }
        return true;
    }


    private static void imageSortAvaible(boolean b, String s) {
        if (b) {
            System.out.println(s);
            System.exit(0);
        }
    }

    private List<File> getImgFiles() {
        return Arrays.stream(orgDir.listFiles())
                .filter(
                        file -> file.getName().toUpperCase().endsWith("JPG") ||
                                file.getName().toUpperCase().endsWith("JPEG") ||
                                file.getName().toUpperCase().endsWith("GIF")
                )
                .collect(Collectors.toList());
    }

    private void copyImgFile(File orgFile, File targetLeafDir){
        File tarFile = new File(targetLeafDir,orgFile.getName());

        try{
            FileInputStream is = new FileInputStream(orgFile);
            FileOutputStream out = new FileOutputStream(tarFile);

            byte[] byteArray = new byte[1024];
            int readLength = 0 ;

            while ( (readLength = is.read(byteArray,0,byteArray.length)) != -1 ){
                out.write(byteArray, 0, readLength);
            }
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        copyCnt++;

    }







}
