package com.example.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class preprocessingApplication {

	public static String sourcePath = "source/";
	public static String resultPath = "result/";
	

	public static String relation = "Grocery_and_Gourmet_Food_5";
	
	public static String unsupervised_attribute = "summary_review";

	public static String supervised_attribute = "summary_review,emotional";
	public static String supervised_positive = "good,best,nice,great,yum,happy,okay,love,like,high,perfect,awesome";
	public static String supervised_negative = "bad,not,but,no,not fresh,low";
	public static String filePositive = "pos_neg/Lexicon-Positive.txt";
	public static String fileNegative = "pos_neg/Lexicon-Negative.txt";
	
//	public static String json_requirment_key = "summary";
	public static String json_requirment_key = "reviewText";
	
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
					result = readJsonFile(fileChild, 0);
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
		positiveInstruction = readFileConv(aDirectoryPos).trim().substring(1).split(",");
		File aDirectoryNeg = new File(fileNegative);
		negativeInstruction = readFileConv(aDirectoryNeg).trim().substring(1).split(",");
		

		
		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					if(fileChild.getName().equals(".DS_Store")) {
						continue;
					}
					int maxLength = maxLengthFromJson(fileChild);

					result = readJsonFile(fileChild, maxLength);
					ArrayList<String[]> supAry = new ArrayList<String[]>();
					for(String data: result) {
						if(data!="") {
							String[] child = new String[data.split(",").length];
							child = data.split(",");
							supAry.add(child);
						}
	
					}
					System.out.println("Analysis");
					int emo;
					dataAffr.append("@relation " + relation + "\n");
					for(int i = 1; i<= maxLength; i++) {
						dataAffr.append("@attribute " + "x"+i+ " numeric\n");
					}
						
					dataAffr.append("@data" + "\n");
					supAry.remove(0);
					for(int i =0;i<supAry.size();i++) {
						outer1loop:for(int a = 0;a<supAry.get(i).length;a++) {
							emo = 0;
							for(String pos : positiveInstruction) {
								if(supAry.get(i)[a].toLowerCase().contains(pos.toLowerCase())) {
									emo++;
									supAry.get(i)[a]=String.valueOf(emo);
									continue outer1loop;
									
								}
							}
							for(String neg : negativeInstruction) {
								if(supAry.get(i)[a].toLowerCase().contains(neg.toLowerCase())) {
									emo--;
									supAry.get(i)[a]=String.valueOf(emo);
									continue outer1loop;
									
								}
							}
							supAry.get(i)[a]="0";
							
						}
						
						
					}
					
					emo = 0;
					outerloop:for(String[] str: supAry) {
						String newstr = "";
						for(String st : str) {
//							newstr = newstr+","+"'"+st.replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "")+"'";
							newstr = newstr+","+st.replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "");

						}
						newstr =  newstr.substring(1);
						String[] strAry = new String[newstr.split(",").length];
						strAry = newstr.split(",");
						int sum = 0;
						for(String word: strAry) {
							int compare = !word.isEmpty()?Integer.valueOf(word): 0;
							if(compare>0) {
								sum++;
							}
							if(compare<0) {
								sum--;
							}
						}
						if(sum>0) {
							dataAffr.append(newstr.trim()+ ",positive" + "\n");
						}
						else if(sum<0) {
							dataAffr.append(newstr.trim()+ ",negative" + "\n");
						}
						else {
							dataAffr.append(newstr.trim()+ ",neural" + "\n");
						}
//						for(String word: strAry) {
//							for(String pos : positiveInstruction) {
//								if(word.toLowerCase().contains(pos.toLowerCase())) {
//									dataAffr.append(newstr.trim()+ ",positive" + "\n");
//									emo++;
//						
//						continue outerloop;
//									
//								}
//							}
//							for(String neg : negativeInstruction) {
//								if(word.toLowerCase().contains(neg.toLowerCase())) {
//									dataAffr.append(newstr.trim()+ ",negative" + "\n");
//									emo--;
//									continue outerloop;
//									
//								}
//							}
//						}
						
//						if(emo>0) {
//							dataAffr.append("postive");
//						}else if(emo==0) {
//							dataAffr.append("neural");
//						}else {
//							dataAffr.append("negative");
//						}
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
					System.out.println("Created file");
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
	public static int maxLengthFromJson(File file) {
		if(file.getName().contains("DS")) {
			return 0;
		}
		String totalMsg = "";
		String[] preStrAry = null;
		String preStr = "";
		int maxLength = 0;
		int initLength = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			System.out.println("Exctract json");
			for (String line; (line = br.readLine()) != null;) {
				lineAt++;
				if (line.contains("\"" + json_requirment_key + "\"")) {
//					System.out.println(line.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length())
//							.split("\"")[3]);
					
					preStr = line
							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
					ArrayList<String> arry = new ArrayList<String>();
					for(String st: preStr.split(" ")) {
						if(!st.isEmpty()&& st!=" " && st!= "") {
							arry.add(st.trim());
						}
					}
					initLength = arry.size();
					
					if(maxLength<initLength) {
						maxLength = initLength;
					}
//					System.out.println(line
//							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3]);
//					totalMsg = totalMsg + "@!" + line
//							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
				}

			}
			return maxLength;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxLength;
	}
	public static String[] readJsonFile(File file, int maxLength) {

		
		String totalMsg = "";
		String[] preStrAry = null;
		String preStr = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			System.out.println("Exctract json");
			for (String line; (line = br.readLine()) != null;) {
				lineAt++;
				if (line.contains("\"" + json_requirment_key + "\"")) {
//					System.out.println(line.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length())
//							.split("\"")[3]);
					preStr = line
							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
					System.out.println(preStr);
					preStrAry = new String[maxLength-1];
					for(int j =0; j<maxLength-1; j++) {
						if(j>preStr.split(" ").length-1) {
							preStrAry[j] = "x";
						}else {
							if(!preStr.split(" ")[j].isEmpty()&&preStr.split(" ")[j]!=null) {
								preStrAry[j] = preStr.split(" ")[j].replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "").replace(",", "");
							}else{
								preStrAry[j] = "x";
							}
//							preStrAry[j] = preStr.split(" ")[j];

						}
					}
//					preStrAry = preStr.split(" ");
//					for(int i = preStrAry.length-1; preStrAry.length<maxLength; i++) {
//					}
					String buildData = "";
					for(String str: preStrAry) {
						buildData = buildData + "," + str;
					}
					buildData = buildData.substring(1);
					buildData = buildData.trim();
					totalMsg = totalMsg + "@!" + buildData;
//					totalMsg = totalMsg.substring(2);
					totalMsg = totalMsg.trim();
//					System.out.println(line
//							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3]);
//					totalMsg = totalMsg + "@!" + line
//							.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
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
