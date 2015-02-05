import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ericmartinez on 2/4/15.
 */
public class Downloader extends Task<File> {
    private Release release;
    public String fileName;
    private String url;
    public int totalBytes = 0;
    private String destination;
    public Downloader(Release r){
        super();
        release = r;
        Assets asset  = AutoUpdate.assetFromRelease(r);
        totalBytes = asset.size;
        url = AutoUpdate.jarUrlFromRelease(r);
        String baseName = FilenameUtils.getBaseName(url);
        String extension = FilenameUtils.getExtension(url);
        fileName = baseName+"."+extension;
        destination = AutoUpdate.getAppDataDir("VersaTile", true)+"/versions/" + release.tag_name + "/";

    }

    public File call(){
        try {
            URL fileUrl = new URL(url);
            InputStream in = fileUrl.openStream();
            File f = new File(destination);
            f.mkdirs();
            String outputFile = f.getAbsolutePath()+"/versatile.jar";
            FileOutputStream fos = new FileOutputStream(outputFile);

            int length = -1;
            int downloaded = 0;
            byte[] buffer = new byte[1024];
            while ((length = in.read(buffer)) != -1) {
                downloaded += length;
                updateProgress(downloaded, totalBytes);
                fos.write(buffer, 0, length);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}