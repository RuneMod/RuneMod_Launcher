package com;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import static com.Global.RUNELITE_DIR;
import static com.Global.TEMP_DIR;
import static com.Global.downloadFile;
import static com.Global.log;
import static com.Global.prepDirectories;

public class RuneMod_Launcher
{
    private static final String LOCAL_RL_VERSION_FILE = TEMP_DIR + "runemod-all_localVersion.txt";
    private static final String LATEST_RL_VERSION_FILE = TEMP_DIR + "runemod-all_latestVersion.txt";
    private static final String RL_VERSION_URL = "https://runemodfiles.xyz/launcher/runemod-all_Version.txt";
    private static final String RL_JAR_URL = "https://runemodfiles.xyz/launcher/runemod-all.jar";
    private static final String RL_JAR_FILEPATH = TEMP_DIR + "runemod-all.jar";

    public static void enableDarkMode() {
		log("Setting Dark Mode");
		FlatDarkLaf.install();
	}

	public static void disableJagAccountMode() {
    	log("disableJagAccountMode()");

    	//delete cred file so we can use normal accounts.
		Path filePath = Paths.get(RUNELITE_DIR+"\\credentials.properties");
		if (Files.exists(filePath)) {
			try {

				Files.delete(filePath);
				log("deleted Creds File.");

			} catch (Exception e) {
				System.err.println("Error deleting file: " + e.getMessage());
			}
		}

		removeWriteCredsArg();
	}

	public static void enableJagAccountMode() {
		log("enableJagAccountMode()");
		addWriteCredsArg();
	}

	public static void removeWriteCredsArg() {
		String settingsFilePath = System.getenv("LOCALAPPDATA") + "\\RuneLite\\settings.json";
		log("rl settings FilePath: " + settingsFilePath);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		try (FileReader reader = new FileReader(settingsFilePath)) {
			JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
			JsonArray clientArguments = jsonObject.getAsJsonArray("clientArguments");

			for (int i = 0; i < clientArguments.size(); i++) {
				if (clientArguments.get(i).getAsString().equals("--insecure-write-credentials")) {
					clientArguments.remove(i);
					i--;
				}
			}

			try (FileWriter writer = new FileWriter(settingsFilePath)) {
				gson.toJson(jsonObject, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addWriteCredsArg() {
		String settingsFilePath = System.getenv("LOCALAPPDATA")+"\\RuneLite\\settings.json";
		log("rl settings FilePath: " + settingsFilePath);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		try (FileReader reader = new FileReader(settingsFilePath)) {
			JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
			JsonArray clientArguments = jsonObject.getAsJsonArray("clientArguments");

			clientArguments.add("--insecure-write-credentials");

			try (FileWriter writer = new FileWriter(settingsFilePath)) {
				gson.toJson(jsonObject, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean credExists() {
		File f = new File(RUNELITE_DIR+"\\credentials.properties");
		return f.exists();
	}

    public static void accTypeSelector() {
		UIManager.put("OptionPane.okButtonText", "Continue");
		UIManager.put("OptionPane.cancelButtonText", "Back");

		UIManager.put("OptionPane.yesButtonText", "Jagex account");
		UIManager.put("OptionPane.noButtonText", "Legacy account");


			int response = JOptionPane.showConfirmDialog(null,
				"             Login with:",
				"Runemod Setup",
				JOptionPane.YES_NO_OPTION);

			if (response == JOptionPane.YES_OPTION) {
				if(!credExists()) { //if no creds exist, means setup is needed.
					String message = "To use a Jagex account:\n" +
						"Step 1: Launch RuneLite, using the Jagex launcher.\n" +
						"Step 2: Close RuneLite.\n" +
						"Step 3: Click the continue button bellow.";
					int continueResponse = JOptionPane.showConfirmDialog(null, message, "Runemod Setup", JOptionPane.OK_CANCEL_OPTION);

					if (continueResponse == JOptionPane.OK_OPTION) {
						enableJagAccountMode();
					} else if (continueResponse == JOptionPane.CANCEL_OPTION) {
						//Go back
						accTypeSelector();
					} else {
						System.exit(0);
					}
				} else {
					log("cred exists, so no setup needed");
				}
			} else if (response == JOptionPane.NO_OPTION) {
				disableJagAccountMode(); //delete creds so runelite lets you log in with non jagex account.
			} else {
				System.exit(0);
			}
		}

	public static void checkForRL_Jar_Updates() {
		try {
			//check runelite build version and update if needed
			String localVersion = readLocalRlVersion();
			log("RL_Jar Local Version: " + localVersion);

			String latestVersion = downloadLatestRlVersion();
			log("RL_Jar Latest Version: " + latestVersion);

			if (latestVersion.equals(localVersion)) {
				log("RL_Jar version is up to date!");
			} else {
				log("RL_JAR updating to version " + latestVersion + "...");
				downloadLatestRlJarFile();
				writeLocalRlVersion(latestVersion);
			}
		} catch (IOException e) {
			log("Error: " + e.getMessage());
		}
	}


    public static void main(String[] args) {
		enableDarkMode();

		prepDirectories();

        try {
            log("RM_Launcher_START");
			accTypeSelector();
			checkForRL_Jar_Updates();
            runRuneliteJarFile();
        } catch (IOException e) {
            log("Error: " + e.getMessage());
        }
    }

    private static String readLocalRlVersion() throws IOException {
        if (!Files.exists(Paths.get(LOCAL_RL_VERSION_FILE))) {
            return "-1";
        }
        return new String(Files.readAllBytes(Paths.get(LOCAL_RL_VERSION_FILE)));
    }

    private static String downloadLatestRlVersion() throws IOException {
        return downloadFile(RL_VERSION_URL, LATEST_RL_VERSION_FILE);
    }

    private static void downloadLatestRlJarFile() throws IOException {
        downloadFile(RL_JAR_URL, RL_JAR_FILEPATH);
    }

	private static void writeLocalRlVersion(String version) throws IOException {
		Files.write(Paths.get(LOCAL_RL_VERSION_FILE), version.getBytes());
	}

    private static void runRuneliteJarFile() throws IOException {
        String command = "java -jar -ea %temp%\\RuneMod\\runemod-all.jar --developer-mode";
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

	private static final String rmLogsLocation = System.getProperty("user.home") + "\\.runemod\\logs\\";
}