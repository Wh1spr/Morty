package wh1spr.bot.morty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Tools {

	@Deprecated
	public static boolean isInFile(String filename, String toFind) {
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
	        return stream.anyMatch(e->e.equals(toFind));
		} catch (IOException e) {
			return false;
		}
	}
	
	@Deprecated
	public static boolean isInLineInFile(String filename, String toFind) {
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
	        return stream.anyMatch(e->e.contains(toFind));
		} catch (IOException e) {
			return false;
		}
	}
	
	@Deprecated
	public static List<String> getLinesFromFile(String filename) {
	
		List<String> lines = new ArrayList<String>();
		
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
	        stream.forEach(e->{lines.add(e);});
	        stream.close();
		} catch (IOException e) {
			lines.clear();
		}
		return lines;
	}
	
	@Deprecated
	public static boolean addLineToFile(String filename, String lineToAdd) {
		try(FileWriter fw = new FileWriter(new File(filename), true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			out.println(lineToAdd);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	@Deprecated
	public static boolean removeLineFromFile(String filename, String lineToRemove) {
		// The "try" traps any possible read/write exceptions which might occur
		try {
		    File inputFile = new File(filename);
		    File outputFile = new File("replacement.txt");
		    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
		        // Read each line from the reader and compare it with
		        // with the line to remove and write if required
		        String line = null;
		        while ((line = reader.readLine()) != null) {
		            if (!line.equals(lineToRemove)) {
		                writer.write(line);
		                writer.newLine();
		            }
		        }
		    }

		    if (inputFile.delete()) {
		        // Rename the output file to the input file
		        if (!outputFile.renameTo(inputFile)) {
		            throw new IOException("Could not rename " + "replacement.txt" + " to " + filename);
		        }
		    } else {
		        throw new IOException("Could not delete original input file " + filename);
		    }
		} catch (IOException ex) {
			return false;
		}
		return true;
	}
}
