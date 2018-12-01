package com.example.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class preprocessingApplication {

	public static String sourcePath = "F:\\CT\\Spring\\tutorial\\preprocessing\\source";
	public static String resultPath = "F:\\CT\\Spring\\tutorial\\preprocessing\\result/";
	

	public static String relation = "Grocery_and_Gourmet_Food_5";
	
	public static String unsupervised_attribute = "summary_review";

	public static String supervised_attribute = "summary_review,emotional";
	public static String supervised_positive = "good,best,nice,great,yum,happy,okay,love,like,high,perfect,awesome";
	public static String supervised_negative = "bad,not,but,no,not fresh,low";
	public static String filePositive = "F:\\CT\\Spring\\tutorial\\preprocessing\\pos_neg/Lexicon-Positive.txt";
	public static String fileNegative = "F:\\CT\\Spring\\tutorial\\preprocessing\\pos_neg/Lexicon-Negative.txt";
	
	public static String json_requirment_key = "summary";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("--------------START READ-----------------");
//		unsupervised_textNormalFormat();
//		unsupervised_jsonFormat();
//		unsupervised_csvFormat();
		supervised_jsonFormat();
//		convertFormatEmo();
	}
	public static void unsupervised_csvFormat() {
		StringBuilder dataAffr = new StringBuilder();
		File aDirectory = new File(sourcePath);
		String result;
		File[] filesInDir = aDirectory.listFiles();

		dataAffr.append("@relation " + relation + "\n");
		dataAffr.append("@attribute " + unsupervised_attribute + "\n");
		dataAffr.append("@data" + "\n");

		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					result = readFileCsv(fileChild);
//					for (int i = 0; i < result.length; i++) {
//						System.out.println(result[i].trim());
//						dataAffr.append("'" + result[i].trim() + "'" + "\n");
//					}
					createFile(fileChild.getName(), result);
//					System.out.println(result);
				}
			}
		}
	}
	public static void unsupervised_jsonFormat() {
		StringBuilder dataAffr = new StringBuilder();
		File aDirectory = new File(sourcePath);
		String[] result;
		File[] filesInDir = aDirectory.listFiles();

		dataAffr.append("@relation " + relation + "\n");
		dataAffr.append("@attribute " + unsupervised_attribute + "\n");
		dataAffr.append("@data" + "\n");

		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					result = readJsonFile(fileChild);
					for (int i = 0; i < result.length; i++) {
						System.out.println(result[i].trim());
						dataAffr.append("'" + result[i].trim() + "'" + "\n");
					}
					createFile(fileChild.getName(), dataAffr.toString());
				}
			}
		}
	}
	public static void convertFormatEmo() {
		StringBuilder dataAffr = new StringBuilder();
		File aDirectory = new File(sourcePath);
		String result;
		File[] filesInDir = aDirectory.listFiles();

		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					result = readFileConv(fileChild);
					
					createFileConv(fileChild.getName(), result);
				}
			}
		}

	}
	public static void supervised_jsonFormat() {
		StringBuilder dataAffr = new StringBuilder();
		File aDirectory = new File(sourcePath);
		String[] result;
		File[] filesInDir = aDirectory.listFiles();
		String[] positiveInstruction = new String[supervised_positive.split(",").length];
		String[] negativeInstruction = new String[supervised_negative.split(",").length];
//		positiveInstruction = supervised_positive.split(",");
//		negativeInstruction = supervised_negative.split(",");
		File aDirectoryPos = new File(filePositive);
		positiveInstruction = readFileConv(aDirectoryPos).split(",");
		File aDirectoryNeg = new File(fileNegative);
		negativeInstruction = readFileConv(aDirectoryNeg).split(",");
		dataAffr.append("@relation " + relation + "\n");
		for(String str : supervised_attribute.split(",")) {
			dataAffr.append("@attribute " + str + " numberic\n");
		}
		
		dataAffr.append("@data" + "\n");

		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					result = readJsonFile(fileChild);
//					result = new String[5];
//					result[0] = "can't";
//					result[1] = "bad i'm fine";
//					result[2] = "nice";
//					result[3] = "can't";
					int emo;
					for(String str: result) {
						emo = 0;
						String[] strAry = new String[str.split(" ").length];
						strAry = str.split(" ");
						for(String word: strAry) {
							for(String pos : positiveInstruction) {
								if(word.contains(pos)) {
//									dataAffr.append("'" + result[i].trim().replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "") + "'" + ",positive" + "\n");
//									continue outerloop;
									emo++;
								}
							}
							for(String neg : negativeInstruction) {
								if(word.contains(neg)) {
//									dataAffr.append("'" + result[i].trim().replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "") + "'" + ",negative" + "\n");
//									continue outerloop;
									emo--;
								}
							}
						}
						System.out.println(emo);
						dataAffr.append("'" + str.trim().replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "") + "'" + ","+ emo + "\n");

					}
//					outerloop:for (int i = 0; i < result.length; i++) {
//						System.out.println(result[i].trim());
//						for(String pos : positiveInstruction) {
//							if(result[i].contains(pos)) {
//								dataAffr.append("'" + result[i].trim().replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "") + "'" + ",positive" + "\n");
//								continue outerloop;
//							}
//						}
//						for(String neg : negativeInstruction) {
//							if(result[i].contains(neg)) {
//								dataAffr.append("'" + result[i].trim().replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "") + "'" + ",negative" + "\n");
//								continue outerloop;
//							}
//						}
//						dataAffr.append("'" + result[i].trim().replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "") + "'" + ",neutral" +"\n");
//					}
					createFile(fileChild.getName(), dataAffr.toString());
				}
			}
		}
	}

	public static void unsupervised_textNormalFormat() {
		StringBuilder dataAffr = new StringBuilder();
		File aDirectory = new File(sourcePath);
		String[] result;
		File[] filesInDir = aDirectory.listFiles();

		dataAffr.append("@relation " + relation + "\n");
		dataAffr.append("@attribute " + unsupervised_attribute + "\n");
		dataAffr.append("@data" + "\n");
		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					result = readFile(fileChild);
					for (int i = 0; i < result.length; i++) {
						System.out.println(result[i].trim());
						dataAffr.append("'" + result[i].trim().replaceAll(" ", "-").replaceAll(",", "") + "'" + "\n");
					}
					createFile(fileChild.getName(), dataAffr.toString());
				}
			}
		}

	}
	public static String readFileCsv(File file) {
		StringBuilder str = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			for (String line; (line = br.readLine()) != null;) {
				lineAt++;
				str.append(line+"\n");

			}
			return str.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String[] readJsonFile(File file) {
		String totalMsg = "";
		String[] preStrAry = null;
		String preStr = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			for (String line; (line = br.readLine()) != null;) {
				lineAt++;
				if (line.contains("\"" + json_requirment_key + "\"")) {
//					System.out.println(line.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length())
//							.split("\"")[3]);
					
//					preStr = line
//							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
//					preStrAry = new String[preStr.split(" ").length];
//					preStrAry = preStr.split(" ");
//					for(String str: preStrAry) {
//						System.out.println(str);
//						totalMsg = totalMsg + "@!" + str;
//					}
					
					totalMsg = totalMsg + "@!" + line
							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
				}

			}
			String[] result = new String[totalMsg.split("@!").length];
			result = totalMsg.split("@!");
			return result;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String readFileConv(File file) {
		StringBuilder str = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			for (String line; (line = br.readLine()) != null;) {
				lineAt++;
				str.append(","+line);
				System.out.println(line);

			}
			return str.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String readConfigPos(File file) {
		String result = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			for (String line; (line = br.readLine()) != null;) {
				lineAt++;
				return result;

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String[] readFile(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			for (String line; (line = br.readLine()) != null;) {
				lineAt++;
				String[] result = new String[line.split("\\.").length];
				result = line.split("\\.");

				return result;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void createFile(String fileName, String data) {
		String directFile = "";
		if (fileName.endsWith(".txt"))
			directFile = resultPath + fileName.replaceAll(".txt", ".arff");
		if (fileName.endsWith(".json"))
			directFile = resultPath + fileName.replaceAll(".json", ".arff");
		if (fileName.endsWith(".csv"))
			directFile = resultPath + fileName.replaceAll(".csv", ".arff");
		
		Path path = Paths.get(directFile);

		try {
			Files.write(path, data.getBytes());
		} catch (Exception e) {

		}
	}
	public static void createFileConv(String fileName, String data) {
		String directFile =  resultPath + fileName;

		Path path = Paths.get(directFile);

		try {
			Files.write(path, data.getBytes());
		} catch (Exception e) {

		}
	}
}
