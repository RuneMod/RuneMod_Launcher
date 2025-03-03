package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.Global.TEMP_DIR;
import static com.Global.downloadFile;
import static com.Global.log;
import static com.Global.prepDirectories;

//this class is an updater and launcher for the launcher. Hopefully we wont ever need to update this.
public class RuneMod
{
	private static final String LOCAL_LAUNCHER_VERSION_FILE = TEMP_DIR + "launcher_localVersion.txt";
	private static final String LATEST_LAUNCHER_VERSION_FILE = TEMP_DIR + "launcher_latestVersion.txt";
	private static final String LAUNCHER_VERSION_URL = "https://runemodfiles.xyz/launcher/RuneMod_Launcher_Version.txt";
	private static final String LAUNCHER_JAR_URL = "https://runemodfiles.xyz/launcher/RuneMod_Launcher.jar";
	private static final String LAUNCHER_JAR_FILEPATH = TEMP_DIR + "RuneMod_Launcher.jar";

	public static void main(String[] args) {
		prepDirectories();

		try {
			log("RM_LauncherExecutor_START");
			checkFor_RuneMod_Launcher_Updates();
			runLauncherJarFile();
		} catch (IOException e) {
			log("Error: " + e.getMessage());
		}
	}

	private static void runLauncherJarFile() throws IOException {
		String command = "java -jar %temp%\\RuneMod\\RuneMod_Launcher.jar";
		log("Running jar file with command "+command);

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

	public static void checkFor_RuneMod_Launcher_Updates() {
		try {
			//check runelite build version and update if needed
			String localLauncherVersion = readLocalLauncherVersion();
			log("Launcher_Jar Local Version: " + localLauncherVersion);

			String latestLauncherVersion = downloadLatestLauncherVersion();
			log("Launcher_Jar Latest Version: " + latestLauncherVersion);

			if (latestLauncherVersion.equals(localLauncherVersion)) {
				log("RM Launcher version is up to date!");
			} else {
				log("RM Launcher updating to version " + latestLauncherVersion + "...");
				downloadLatestLauncherJarFile();
				writeLocalLauncherVersion(latestLauncherVersion);
			}
		} catch (IOException e) {
			log("Error: " + e.getMessage());
		}
	}

	private static String readLocalLauncherVersion() throws IOException {
		if (!Files.exists(Paths.get(LOCAL_LAUNCHER_VERSION_FILE))) {
			return "-1";
		}
		return new String(Files.readAllBytes(Paths.get(LOCAL_LAUNCHER_VERSION_FILE)));
	}

	private static String downloadLatestLauncherVersion() throws IOException {
		return downloadFile(LAUNCHER_VERSION_URL, LATEST_LAUNCHER_VERSION_FILE);
	}

	private static void downloadLatestLauncherJarFile() throws IOException {
		log("downloadLatestLauncherJarFile()");
		downloadFile(LAUNCHER_JAR_URL, LAUNCHER_JAR_FILEPATH);
	}

	private static void writeLocalLauncherVersion(String version) throws IOException {
		Files.write(Paths.get(LOCAL_LAUNCHER_VERSION_FILE), version.getBytes());
	}
}
