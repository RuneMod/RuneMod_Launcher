package com;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String LOCAL_VERSION_FILE = TEMP_DIR + "localVersion.txt";
    private static final String LATEST_VERSION_FILE = TEMP_DIR + "latestVersion.txt";
    private static final String VERSION_URL = "https://runemodfiles.xyz/launcher/version.txt";
    private static final String JAR_URL = "https://runemodfiles.xyz/launcher/runemod-all.jar";
    private static final String JAR_FILE = TEMP_DIR + "runemod-all.jar";
    private static final String rmLogsLocation = System.getProperty("user.home") + "\\.runemod\\logs\\";

    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get(rmLogsLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            log("RM_Launcher_START");

            String localVersion = readLocalVersion();
            log("Local Version: " + localVersion);

            String latestVersion = downloadLatestVersion();
            log("Latest Version: " + latestVersion);

            if (latestVersion.equals(localVersion)) {
                log("RM Plugin version is up to date!");
            } else {
                log("RM Plugin updating to version " + latestVersion + "...");
                downloadJarFile();
                writeLocalVersion(latestVersion);
            }

            runJarFile();
        } catch (IOException e) {
            log("Error: " + e.getMessage());
        }
    }

    private static String readLocalVersion() throws IOException {
        if (!Files.exists(Paths.get(LOCAL_VERSION_FILE))) {
            return "-1";
        }
        return new String(Files.readAllBytes(Paths.get(LOCAL_VERSION_FILE)));
    }

    private static String downloadLatestVersion() throws IOException {
        return downloadFile(VERSION_URL, LATEST_VERSION_FILE);
    }

    private static void downloadJarFile() throws IOException {
        downloadFile(JAR_URL, JAR_FILE);
    }

    private static String downloadFile(String fileUrl, String destination) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64)");
        connection.connect();

        try (InputStream in = connection.getInputStream()) {
            Files.copy(in, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        }
        return new String(Files.readAllBytes(Paths.get(destination)));
    }

    private static void writeLocalVersion(String version) throws IOException {
        Files.write(Paths.get(LOCAL_VERSION_FILE), version.getBytes());
    }

    private static void runJarFile() throws IOException {
        String command = "java -jar -ea %temp%\\runemod-all.jar --developer-mode";

        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.directory(new File(System.getProperty("user.dir"))); // Set the working directory
        processBuilder.redirectErrorStream(true); // Merge error stream with output stream

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Log output from the process
            while ((line = reader.readLine()) != null) {
                log(line);
            }

            int exitCode = process.waitFor(); // Wait for the process to complete
            log("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            log("Error: " + e.getMessage());
        }
    }

    private static void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rmLogsLocation +"RM_launcher_log.txt", true))) {
            writer.write(message);
            writer.newLine();
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}