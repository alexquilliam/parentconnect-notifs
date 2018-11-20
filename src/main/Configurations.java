package main;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

public class Configurations {
	private static TreeMap<String, String> singleValueMappings = new TreeMap<String, String>();
	private static TreeMap<String, ArrayList<String>> multiValueMappings = new TreeMap<String, ArrayList<String>>();

	public static void readConfigurations() {
		String rawData = "";

		try {
			rawData = new String(Files.readAllBytes(Paths.get(ResourcePaths.CONFIG_PATH)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String[] halves = rawData.split("\\r");

		String[] single = halves[0].split("\\n");
		String[] multi = halves[1].split("\\n");

		for(String s : single) {
			String[] line = s.split("=");

			addSingleConfiguration(line[0], line[1]);
		}

		for(String s : multi) {
			String[] line = s.split("=");

			addMultiConfiguration(line[0], new ArrayList<String>(Arrays.asList(line[1].substring(1, line[1].length() - 1).split(","))));
		}
	}

	public static void writeConfigurations() {
		String configData = "";

		for(String s : singleValueMappings.keySet()) {
			configData += s + "=" + singleValueMappings.get(s) + "\n";
		}

		configData += "\r";

		for(String s : multiValueMappings.keySet()) {
			configData += s + "=" + multiValueMappings.get(s).toString().replace(" ", "") + "\n";
		}

		configData = configData.substring(0, configData.length() - 1);

		try {
			Files.write(Paths.get(ResourcePaths.CONFIG_PATH), configData.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addSingleConfiguration(String name, String value) {
		singleValueMappings.put(name, value);
	}

	public static void addMultiConfiguration(String name, ArrayList<String> value) {
		multiValueMappings.put(name, value);
	}

	public static void setSingleConfiguration(String name, String value) {
		singleValueMappings.replace(name, value);
	}

	public static void setMultiConfiguration(String name, ArrayList<String> value) {
		multiValueMappings.replace(name, value);
	}

	public static String getSingleConfiguration(String name) {
		return singleValueMappings.get(name);
	}

	public static TreeMap<String, String> getAllSingleConfigurations() {
		return singleValueMappings;
	}

	public static ArrayList<String> getMultiConfiguration(String name) {
		return multiValueMappings.get(name);
	}

	public static TreeMap<String, ArrayList<String>> getAllMultiConfigurations() {
		return multiValueMappings;
	}

	public static void removeSingleConfiguration(String name) {
		singleValueMappings.remove(name);
	}

	public static void removeMultiConfiguration(String name) {
		multiValueMappings.remove(name);
	}
}
