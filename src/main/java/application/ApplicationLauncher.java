package application;

import infrastructure.DataAccessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.CountryDAO;

public abstract class ApplicationLauncher {
	
	// If you are running this in Eclipse, you may need to 
	// add the resources folder to the path, by right
	// clicking on it and Build Path > Use as Source Folder
	private String originalDataFileName = "CountryData.txt";

	protected DataAccessor dataAccessor;
	protected CountryDAO countryDAO;

	/*public static void main(String[] args) {
		ApplicationLauncher applauncher = new ApplicationLauncher();				
	}*/
	
	public ApplicationLauncher() {
		try {
			Path dataFilePath = initDataFile();
			dataAccessor = new DataAccessor(dataFilePath.toString());		
			countryDAO = new CountryDAO(dataAccessor);
		} catch (Exception e) {
			e.printStackTrace();
			// An Exception in application launcher means we should not go on
			System.exit(1);			
		}
	}
	
	// Creates a file ctrydata-arqs-30245/ctrydata-arqs-30245-DBfile.txt in 
	// the system tmp directory with the country data.
	// If the file already exists, it does not change it at all. That way
	// that file can be used as a "writable database" for the application,
	// as long as we do not delete it (or the system deletes it).
	// If the file does not exist, this method creates it, and populates
	// it with data from CountryData.txt (in the resources folder of this
	// project). 
	private Path initDataFile() throws Exception {		
		Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));		
		Path dirPath = tempDir.resolve("ctrydata-arqs-30245");
		try {
			dirPath = Files.createDirectory(dirPath);
		} catch (FileAlreadyExistsException e) {
			// Directory already exists, it must be from a previous run,
			// not a problem.
		} 
		
		Path filePath = Paths.get("ctrydata-arqs-30245-DBfile.txt");			
		try {
			filePath = Files.createFile(dirPath.resolve(filePath));
		} catch (FileAlreadyExistsException e) {
			// File already exists, it must be from a previous run,
			// not a problem. We are finished
			return dirPath.resolve(filePath);
		} 	
		
		// Populate the file with the original data
		List<String> values = readFromFile(originalDataFileName);					
		writeToFile(filePath.toString(), values);			
		
		return dirPath.resolve(filePath);		
	}
	
	private List<String> readFromFile(String resourceFileName) throws Exception  {
		// Assume it is a small file, that we can read fast into memory
		ClassLoader classLoader = getClass().getClassLoader();			
		File file = new File(classLoader.getResource(resourceFileName).getFile());
			
		List<String> data = new ArrayList<String>();
		try (Scanner scanner =  new Scanner(file, "UTF-8")) {
		      while (scanner.hasNextLine()){
		        data.add(scanner.nextLine());		        
		      }      
		}
		
		return data;		
	}
	
	private void writeToFile(String filePath, List<String> lines) throws Exception  {				
		File file = new File(filePath);
		
		try (FileWriter fw = new FileWriter(file)) {
			try (BufferedWriter bw = new BufferedWriter(fw)){
		      for(String line : lines){
		        bw.write(line);
		        bw.newLine();
		      }
		    }	
		}
	}

}
