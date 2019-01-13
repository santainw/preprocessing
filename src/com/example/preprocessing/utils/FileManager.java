package com.example.preprocessing.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManager {
	public static void createFile(String pathFile, String fileName, String data) {
		String directFile =  pathFile + fileName;
		Path path = Paths.get(directFile);
//		Logger logger = LoggerFactory.getLogger(LogUtils.class);
		try 
		{
			Files.write(path, data.getBytes());
		} 
		catch (Exception e) 
		{
			
		}
	}
	public static void writeFile(String pathFile, String fileName, String data) {
//		Logger logger = LoggerFactory.getLogger(LogUtils.class);
		File file = new File(pathFile+fileName);
		FileWriter fr;
		try {
			fr = new FileWriter(file, true);
//			fr.write("\n" + data);
			fr.append("\n" + data);
			
			fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static boolean makeDir(String pathFile) {
		File theDir = new File(pathFile);
		boolean result = false;
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + theDir.getName());
		    try{
		        theDir.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    	result = false;
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
		else
		{
			result = true;
		}
		return result;
	}
//	public List<String> findFileName(String folderName, TreeMap<String, String> app_config) {
//		List<String> arrFileNames = new ArrayList<>();
//		File folder = new File(app_config.get(EConfig.PATH_USER.getText()) + app_config.get(EConfig.FOLDERNAME_SOURCE.getText())+ folderName);
//		File[] listOfFiles = folder.listFiles();
//		for (int i = 0; i < listOfFiles.length; i++) 
//		{
//			if (listOfFiles[i].isFile()) 
//			{
//				arrFileNames.add(listOfFiles[i].getName());
//			}
//		}
//		return arrFileNames;
//	}
	
//	public List<String> findFolderName(String folderName, TreeMap<String, String> app_config) {
//		List<String> arrFolderNames = new ArrayList<>();
//		File folder = new File(app_config.get(EConfig.PATH_USER.getText()) + folderName);
//		File[] listOfFiles = folder.listFiles();
//		for (int i = 0; i < listOfFiles.length; i++) 
//		{
//			arrFolderNames.add(listOfFiles[i].getName());
//		}
//		return arrFolderNames;
//	}
	
	public boolean moveFile(String sourcePath, String targetPath) {

	    boolean fileMoved = true;

	    try {

	        Files.move(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);

	    } catch (Exception e) {

	        fileMoved = false;
	        e.printStackTrace();
	    }

	    return fileMoved;
	}
//	public static void zipFileDownload() {
//		List<String> listFile = new ArrayList<String>();
//		FileManager fileManager = new FileManager();
//		Date dateNow = new Date();
//		SimpleDateFormat formatDateFile = LogUtils.formatDateFile;
//		String firstFile;
//		String secondFile;
//		String zipName;
//		String stringByte;
////		StringBuilder sb = new StringBuilder();
////		sb.append("Test String");
////
////		File f = new File("d:\\test.zip");
////		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
////		ZipEntry e = new ZipEntry("mytext.txt");
////		out.putNextEntry(e);
////
////		byte[] data = sb.toString().getBytes();
////		out.write(data, 0, data.length);
////		out.closeEntry();
////
////		out.close();
//		for(String channel:Load_And_Reconcile.source_channel) {
//			channel = channel.trim();
//
//			File aDirectory = new File(Load_And_Reconcile.app_config.get(EConfig.FOLDERNAME_LOG.getText())+"/"+channel);
//		    File[] filesInDir = aDirectory.listFiles();
//
//		    if(filesInDir != null) {
//			    	for (File fileChild : filesInDir) {
//			        if (fileChild.isFile()){
//				        	if(channel.toLowerCase().equals(EConstant.KTB.getText())) {
//				        		firstFile = formatDateFile.format(dateNow)+"_"+channel.toLowerCase();
//				        		secondFile = fileChild.getName();
//				        		if(firstFile.equals(secondFile)) {
//				        			listFile.add(fileChild.getPath());
//				        		}
////				        		load_file_source.import_data(EConstant.KTB, fileChild);
////				        		loggerFormat(EConstant.KTB, load_file_source);
////				        		moveFile(aDirectory+"/"+fileChild.getName(), app_config.get(EConfig.FOLDERNAME_BACKUP_KTB.getText())+fileChild.getName());
//				        	} else if(channel.toLowerCase().equals(EConstant.COUNTER_SERVICE.getText())) {
//				        		firstFile = formatDateFile.format(dateNow)+"_"+channel.toLowerCase();
//				        		secondFile = fileChild.getName();
//				        		if(firstFile.equals(secondFile)) {
//				        			listFile.add(fileChild.getPath());
//				        		}
////				        		load_file_source.import_data(EConstant.COUNTER_SERVICE, fileChild);
////				        		loggerFormat(EConstant.COUNTER_SERVICE, load_file_source);
////				        		moveFile(aDirectory+"/"+fileChild.getName(), app_config.get(EConfig.FOLDERNAME_BACKUP_COUNTERSERVICE.getText())+fileChild.getName());
//				        	}
//			        }
//			    }
//		    }
//		}
//		File file;
//		for(String logFile: listFile) {
//			file = new File(logFile);
//			stringByte = fileManager.readFileText(file);
//    		zipName = formatDateFile.format(dateNow)+"_log.zip";
//    		if(FileManager.makeDir(Load_And_Reconcile.app_config.get(EConfig.ROOT_DOWNLOAD_ZIP.getText()))){
//    			try {
//        			Path path = Paths.get(Load_And_Reconcile.app_config.get(EConfig.ROOT_DOWNLOAD_ZIP.getText())+zipName);
//        			Map<String, String> env = new HashMap<>(); 
//        			env.put("create", "true");
//        			URI uri = URI.create("jar:" + path.toUri());
//        			try (FileSystem fs = FileSystems.newFileSystem(uri, env))
//        			{
//        			    Path nf = fs.getPath(file.getName());
//        			    try (Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
//        			        writer.write(stringByte);
//        			    }
//        			}
//    			}catch(Exception except) {
//    				File f = new File(Load_And_Reconcile.app_config.get(EConfig.ROOT_DOWNLOAD_ZIP.getText())+zipName);
//        			ZipOutputStream out;
//        			try {
//    					out = new ZipOutputStream(new FileOutputStream(f));
//    					ZipEntry e = new ZipEntry(file.getName());
//            			try {
//    						out.putNextEntry(e);
//    	        			byte[] data = stringByte.getBytes();
//    	        			out.write(data, 0, data.length);
//    	        			out.closeEntry();
//    	        			out.close();
//    					} catch (IOException e1) {
//    						// TODO Auto-generated catch block
//    						e1.printStackTrace();
//    					}
//    				} catch (FileNotFoundException e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				}
//    			}
//	    }
//		}
//	}
	
	public String readFileText(File file) {
		StringBuilder data = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			for (String line; (line = br.readLine()) != null;) {
				data.append(line+"\n");
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data.toString();
	}
}
