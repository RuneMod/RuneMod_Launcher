package com;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;

public class Main {
    //dfgfdg
    public static void main(String[] args) throws Exception {
        String tempFolder = System.getProperty("java.io.tmpdir");
        String resourceName = "/RuneMod_Launcher.bat";
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = Main.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = new File(tempFolder).getParentFile().getPath();
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            System.out.println("error...");
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
        //Process exec = Runtime.getRuntime().exec(jarFolder + resourceName);
        Desktop desktop = Desktop.getDesktop();

        desktop.open(new File(jarFolder + resourceName));


/*        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if(!(temp.mkdir()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        URL url = Main.class.getClassLoader().getResource("RuneMod_Launcher.bat");
        InputStream is = url.openStream();

        if(is == null) {
            System.out.println("is is null");
        }

        //InputStream is = Main.class.getClassLoader().getResourceAsStream("RuneMod_Launcher.bat");
        Files.copy(is, temp.toPath());

        Process exec = Runtime.getRuntime().exec(temp.getPath());*/

        //
        //File file = new File("/RuneMod_Launcher.bat");
       // System.out.println("heelo worldo");
        //frame.setVisible(true);
	// write your code here
/*        try {
*//*            ClassLoader loader = Main.class.getClassLoader();
            URL resource = loader.getResource("RuneMod_Launcher.bat");
            Process exec = Runtime.getRuntime().exec(resource.getPath());*//*

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
