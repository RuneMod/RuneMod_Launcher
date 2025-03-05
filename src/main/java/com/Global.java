package com;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JOptionPane;

public class Global
{
	public static final File RUNELITE_DIR = new File(System.getProperty("user.home"), ".runelite");
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir")+"RuneMod\\";

	public static String downloadFile(String fileUrl, String destination) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64)");
		connection.connect();

		InputStream in = connection.getInputStream();
		Files.copy(in, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
		return new String(Files.readAllBytes(Paths.get(destination)));
	}

	private static final String rmLogsLocation = System.getProperty("user.home") + "\\.runemod\\logs\\";

	public static boolean runningFromIntelliJ()
	{
		String classPath = System.getProperty("java.class.path");
		return classPath.contains("idea_rt.jar");
	}

	public static void log(String message) {
		if(runningFromIntelliJ()) {
			System.out.println(message);
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(rmLogsLocation +"RM_launcher_log.txt", true))) {
			writer.write(message);
			writer.newLine();

		} catch (IOException e) {
			e.printStackTrace();
		}
		if(message.toLowerCase().contains("error")) {
			displayError(message);
		}
	}

	public static void prepDirectories() {
		try {
			//prep logFile dir
			Files.createDirectories(Paths.get(rmLogsLocation));
			//prep temp dir
			Files.createDirectories(Paths.get(TEMP_DIR));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void displayError(String message) {
		JOptionPane.showMessageDialog(null,
			message,
			"Error",
			JOptionPane.ERROR_MESSAGE);
	}
}
