import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.stage.Stage;
import sun.tools.jar.resources.jar;

import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by ericmartinez on 2/3/15.
 */
public class AutoUpdate {

    public static Version getVersion() {
        File versionsDir= getAppVersionsDir("VersaTile", true);
        String[] directories = versionsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        Version latest = null;
        for(String folder : directories){
            try {
                Version v = new Version(folder);
                if(v.compareTo(latest)>0){
                    latest = v;
                }
            }catch(IllegalArgumentException iae){

            }
        }
        return latest;
    }

    public static File getLatestInstalled(){
        File versionsDir= getAppVersionsDir("VersaTile", true);
        String[] directories = versionsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        Version latest = null;
        for(String folder : directories){
            try {
                Version v = new Version(folder);
                if(v.compareTo(latest)>0){
                    latest = v;
                }
            }catch(IllegalArgumentException iae){

            }
        }
        if(latest!=null) {
            File jar = new File(versionsDir + "/" + latest.toString() +"/versatile.jar");
            return jar;
        }else return null;
    }

    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static boolean isInstalled(){
        return getVersion()==null;
    }

    public static Release getLatestAvailableRelease(){
        try {
            Gson gson = new Gson();
            String json = readUrl("https://api.github.com/repos/ericmichael/polyomino/releases");
            Release[] releases = gson.fromJson(json, Release[].class);

            if (releases.length > 0) {
                Release remoteRelease = releases[0];
                return remoteRelease;
            }
        }catch(Exception e){

        }
        return null;
    }

    public static Release checkForUpdates(){
       try {
           Release latestRelease = getLatestAvailableRelease();
           Version latestVersion = new Version(latestRelease.tag_name);
           //if installed and out of date
           if (getVersion() != null && getVersion().compareTo(latestVersion) == -1) {
               return latestRelease;
           }
           return null;
       }catch(NullPointerException npe){
           return null;
       }
    }

    public static Assets assetFromRelease(Release r){
        if(r!=null) {
            for (Assets a : r.assets) {
                String extension = getFileNameExtension(a.browser_download_url).toLowerCase();
                if (extension.equals("jar")) {
                    return a;
                }
            }
        }
        return null;
    }

    public static String jarUrlFromRelease(Release r){
        Assets a = assetFromRelease(r);
        if(a!=null)
            return a.browser_download_url;
        return null;
    }

    private static String getFileNameExtension(String fileName) {
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        else return "";
    }

    /**
     * Returns the AppData or Application Support directory file.
     */
    public static File getAppDataDir(String aName, boolean doCreate)
    {
        // Get user home + AppDataDir (platform specific) + name (if provided)
        String dir = System.getProperty("user.home");
        if(isWindows) dir += File.separator + "AppData" + File.separator + "Local";
        else if(isMac) dir += File.separator + "Library" + File.separator + "Application Support";
        if(aName!=null) dir += File.separator + aName;

        // Create file, actual directory (if requested) and return
        File dfile = new File(dir);
        if(doCreate && aName!=null) dfile.mkdirs();
        return dfile;
    }

    public static File getAppVersionsDir(String aName, boolean doCreate){
        File dataDir = getAppDataDir(aName, doCreate);
        String dir = dataDir.getAbsolutePath()+ "/versions";
        // Create file, actual directory (if requested) and return
        File dfile = new File(dir);
        if(doCreate && aName!=null) dfile.mkdirs();
        return dfile;
    }

    public static void launchLatest(String[] args) throws Exception{
        //Create URLClassLoader for main jar file, get App class and invoke main
        File jar = getLatestInstalled();
        if(jar!=null) {
            URLClassLoader child = new URLClassLoader(new URL[]{jar.toURI().toURL()});
            Class classToLoad = Class.forName("com.asarg.polysim.Main", true, child);
            final Method method = classToLoad.getMethod("main", new Class[]{String[].class});
            final Object instance = classToLoad.newInstance();


            if (classToLoad == Object.class) child.close(); // Getting rid of warning message for ucl
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        method.invoke (instance, new Object[] { new String[] { "Param1", "Param2" } });
                    } catch (Throwable th) {
                        System.out.println(th.getMessage());
                        th.printStackTrace();
                    }
                }
            });

            th.setContextClassLoader(child);
            th.start();

            th.join();
        }
    }

    public static void startLatest(String[] args) throws Exception{
        //Create URLClassLoader for main jar file, get App class and invoke main
        File jar = getLatestInstalled();
        if(jar!=null) {
            final URLClassLoader child = new URLClassLoader(new URL[]{jar.toURI().toURL()});
            final Class classToLoad = Class.forName ("com.asarg.polysim.SimulationApplication", true, child);
            final Method method = classToLoad.getMethod("start", Stage.class);
            final Object instance = classToLoad.newInstance ();


            if (classToLoad == Object.class) child.close(); // Getting rid of warning message for ucl
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread().setContextClassLoader(child);
                        method.invoke (instance, new Stage());
                    } catch (Throwable th) {
                        System.out.println(th.getMessage());
                        th.printStackTrace();
                    }
                }
            });
        }
    }

    // Whether Windows/Mac
    static boolean isWindows = (System.getProperty("os.name").indexOf("Windows") >= 0);
    static boolean isMac = (System.getProperty("os.name").indexOf("Mac OS X") >= 0);
}
