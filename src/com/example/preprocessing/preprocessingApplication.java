package com.example.preprocessing;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import com.example.preprocessing.config.Config;
import com.example.preprocessing.controller.PreProcessingController;
import com.example.preprocessing.enums.EConfig;
import com.example.proprocessing.testlab.stemming.Stemming;

public class preprocessingApplication {
	private static HashMap<String, String> app_config = Config.loadConfig();
	private static String sourcePath = app_config.get(EConfig.PATH_RESOURCE_SOURCE.getText());
	private static String resultPath = app_config.get(EConfig.PATH_RESOURCE_RESULT.getText());
	private static String relation = app_config.get(EConfig.RESULT_RELATION.getText());
	private static String unsupervised_attribute = app_config.get(EConfig.RESULT_ATTRIBRUTE_UNSUPERVISED.getText());
	private static String supervised_attribute = app_config.get(EConfig.RESULT_ATTRIBRUTE_SUPERVISED.getText());
	private static String supervised_positive = app_config.get(EConfig.RESULT_POSITIVE_SUPERVISED.getText());
	private static String supervised_negative = app_config.get(EConfig.RESULT_NEGATIVE_SUPERVISED.getText());
	private static String filePositive = app_config.get(EConfig.PATH_RESOURCE_POSITIVE.getText());
	private static String fileNegative = app_config.get(EConfig.PATH_RESOURCE_NEGATIVE.getText());
	private static String fileWord = app_config.get(EConfig.PATH_RESOURCE_WORD.getText());

//	public static String json_requirment_key = "summary";
	public static String json_requirment_key = "reviewText";
	public static void main(String[] args) {
		System.out.println("--------------START READ-----------------");
		System.out.println(Runtime.getRuntime().maxMemory() / 1048576 + "MB");

		//----------- unsupervised text normal format ---------//
//		unsupervised_textNormalFormat();
		
		//----------- unsupervised json format ---------//
//		unsupervised_jsonFormat();
		
		//----------- unsupervised csv format ---------//
//		unsupervised_csvFormat();
		
		//----------- supervised json format ---------//
		supervised_jsonFormat();
		
		//----------- supervised json format ---------//
//		requirment_teacher_supervised_jsonFormat();
		
		//----------- Tool convert format emo ---------//
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
					createFile(fileChild.getName(), result);
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
	public static void requirment_teacher_supervised_jsonFormat() {
		Stemming stem = new Stemming();
		StringBuilder dataAffr = new StringBuilder();
		String[] result = null;
		File aDirectory = new File(sourcePath);
		File[] filesInDir = aDirectory.listFiles();
		String[] positiveInstruction = new String[supervised_positive.split(",").length];
		String[] negativeInstruction = new String[supervised_negative.split(",").length];
//		positiveInstruction = supervised_positive.split(",");
//		negativeInstruction = supervised_negative.split(",");
		File aDirectoryPos = new File(filePositive);
		positiveInstruction = readFileConv(aDirectoryPos).trim().substring(1).split(",");
		File aDirectoryNeg = new File(fileNegative);
		negativeInstruction = readFileConv(aDirectoryNeg).trim().substring(1).split(",");
		File aDirectoryWord = new File(fileWord);
		HashMap<String, String> hashWord = readFileWord(aDirectoryWord);
		HashMap<String, String> hashPositive = new HashMap<>();
		HashMap<String, String> hashNegative = new HashMap<>();
		
		for (String pos : positiveInstruction) {
			hashPositive.put(pos.trim(), "positive");
		}
		for (String neg : negativeInstruction) {
			hashNegative.put(neg.trim(), "negative");
		}
		
		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					if (fileChild.getName().equals(".DS_Store")) {
						continue;
					}
					int maxLength = maxLengthFromJson(fileChild);
					dataAffr.append("@relation " + relation + "\n");
					for (int i = 1; i <= maxLength; i++) {
						if(i==maxLength) {
							dataAffr.append("@attribute emotional {positive, negative, neural}" + "\n");
						}else {
							dataAffr.append("@attribute " + "x" + i + " numeric\n");
						}
					}
					dataAffr.append("@data\n");
//					result = readJsonFile(fileChild, maxLength);
					String totalMsg = "";
					String[] preStrAry = null;
					String preStr = "";
					try (BufferedReader br = new BufferedReader(new FileReader(fileChild))) {
						int lineAt = 0;
						System.out.println("Exctract json");
						for (String line; (line = br.readLine()) != null;) {
							lineAt++;
							if (line.contains("\"" + json_requirment_key + "\"")) {
								preStr = line
										.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
								System.out.println(lineAt);
								preStrAry = new String[maxLength-1];
								for(int j =0; j<maxLength-1; j++) {
									if(j>preStr.split(" ").length-1) {
										preStrAry[j] = "x";
									}else {
										if(!preStr.split(" ")[j].isEmpty()&&preStr.split(" ")[j]!=null) {
											preStrAry[j] = stem.stemString(preStr.split(" ")[j].replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "").replace(",", ""));
										}else{
											preStrAry[j] = "x";
										}
									}
								}
								String buildData = "";
								for(String str: preStrAry) {
									buildData = buildData + "," + str;
								}
								buildData = buildData.substring(1);
								buildData = buildData.trim();
								totalMsg = totalMsg + "@!" + buildData;
								totalMsg = totalMsg.trim();
							}
							if((lineAt%100000)==0) {
								System.out.println("mod 1000 == 0");
								result = new String[totalMsg.split("@!").length];
								result = totalMsg.split("@!");
								
								ArrayList<String[]> supAry = new ArrayList<String[]>();
								ArrayList<String[]> supAryText = new ArrayList<String[]>();
								for (String data : result) {
									if (data != "") {
										String[] child = new String[data.split(",").length];
										child = data.split(",");
										supAry.add(child);
									}

								}
								for (String data : result) {
									if (data != "") {
										String[] child = new String[data.split(",").length];
										child = data.split(",");
										supAryText.add(child);
									}

								}
								System.out.println("End Analysis");
								int emo;
								supAry.remove(0);
								for (int i = 0; i < supAry.size(); i++) {
									outer1loop: for (int a = 0; a < supAry.get(i).length; a++) {
										emo = 0;
										System.out.println("convert : "+supAry.get(i)[a]);
										if(hashPositive.containsKey(supAry.get(i)[a].toLowerCase())) {
											emo++;
											supAry.get(i)[a] = String.valueOf(emo);
											continue outer1loop;
										}
										if(hashNegative.containsKey(supAry.get(i)[a].toLowerCase())) {
											emo--;
											supAry.get(i)[a] = String.valueOf(emo);
											continue outer1loop;
										}
//										for (String pos : positiveInstruction) {
//											if (supAry.get(i)[a].toLowerCase().contains(pos.toLowerCase())) {
//												emo++;
//												supAry.get(i)[a] = String.valueOf(emo);
//												continue outer1loop;
			//
//											}
//										}
//										for (String neg : negativeInstruction) {
//											if (supAry.get(i)[a].toLowerCase().contains(neg.toLowerCase())) {
//												emo--;
//												supAry.get(i)[a] = String.valueOf(emo);
//												continue outer1loop;
			//
//											}
//										}
										supAry.get(i)[a] = "0";
									}
								}
								emo = 0;
								outerloop: for (int i=0; i<supAry.size(); i++) {
									String newstr = "";
									String currentWord = "";
									String newOfnewStr = "";
									for (String st : supAry.get(i)) {
//										newstr = newstr+","+"'"+st.replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "")+"'";
										newstr = newstr + "," + st.replace("\\", "").replace("'", "").replace("'", "")
												.replace("'", "").replace("'", "");
										
									}
//									System.out.println(supAryText.get(i)[0]);
									String[] currentSupWord = supAryText.get(i);
									for(String stOfSt: currentSupWord) {
//										System.out.println(stOfSt);
										currentWord = stOfSt.replace("\\", "").replace("'", "").replace("'", "")
												.replace("'", "").replace("'", "");
//										System.out.println(hashWord.containsKey(currentWord.toLowerCase()));
										if(hashWord.containsKey(currentWord.toLowerCase())) {
											newOfnewStr = newOfnewStr + "," + hashWord.get(currentWord.toLowerCase());
										}else {
											newOfnewStr = newOfnewStr + "," + "0";
										}
									}
//									for(String st : supAryText.get(i)) {
//										currentWord = st.replace("\\", "").replace("'", "").replace("'", "")
//												.replace("'", "").replace("'", "");
//										if(hashWord.containsKey(currentWord.toLowerCase())) {
//											newOfnewStr = newOfnewStr + "," + hashWord.get(st.replace("\\", "").replace("'", "").replace("'", "")
//													.replace("'", "").replace("'", ""));
//										}else {
//											newOfnewStr = newOfnewStr + "," + "0";
//										}
//									}
									newOfnewStr = newOfnewStr.substring(1);
									System.out.println("SPLIT : "+newOfnewStr.split(",").length);
									newstr = newstr.substring(1);
									String[] strAry = new String[newstr.split(",").length];
									strAry = newstr.split(",");
									int sum = 0;
									for (String word : strAry) {
										int compare = !word.isEmpty() ? Integer.valueOf(word) : 0;
										if (compare > 0) {
											sum++;
										}
										if (compare < 0) {
											sum--;
										}
									}
									if (sum > 0) {
										dataAffr.append(newOfnewStr.trim() + ",positive" + "\n");
									} else if (sum < 0) {
										dataAffr.append(newOfnewStr.trim() + ",negative" + "\n");
									} else {
										dataAffr.append(newOfnewStr.trim() + ",neural" + "\n");
									}
									if(dataAffr.toString().startsWith(",")) {
										StringBuilder tempData = new StringBuilder();
										tempData.append(dataAffr.toString().substring(1));
										dataAffr = tempData;
									}
										
									createFile(fileChild.getName(), dataAffr.toString());
									dataAffr = new StringBuilder();
								}
								
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//end point
					System.out.println("END point");
					result = new String[totalMsg.split("@!").length];
					result = totalMsg.split("@!");
					
					ArrayList<String[]> supAry = new ArrayList<String[]>();
					ArrayList<String[]> supAryText = new ArrayList<String[]>();
					for (String data : result) {
						if (data != "") {
							String[] child = new String[data.split(",").length];
							child = data.split(",");
							supAry.add(child);
						}

					}
					for (String data : result) {
						if (data != "") {
							String[] child = new String[data.split(",").length];
							child = data.split(",");
							supAryText.add(child);
						}

					}
					System.out.println("End Analysis");
					int emo;
					supAry.remove(0);
					for (int i = 0; i < supAry.size(); i++) {
						outer1loop: for (int a = 0; a < supAry.get(i).length; a++) {
							emo = 0;
							System.out.println("convert : "+supAry.get(i)[a]);
							if(hashPositive.containsKey(supAry.get(i)[a].toLowerCase())) {
								emo++;
								supAry.get(i)[a] = String.valueOf(emo);
								continue outer1loop;
							}
							if(hashNegative.containsKey(supAry.get(i)[a].toLowerCase())) {
								emo--;
								supAry.get(i)[a] = String.valueOf(emo);
								continue outer1loop;
							}
//							for (String pos : positiveInstruction) {
//								if (supAry.get(i)[a].toLowerCase().contains(pos.toLowerCase())) {
//									emo++;
//									supAry.get(i)[a] = String.valueOf(emo);
//									continue outer1loop;
//
//								}
//							}
//							for (String neg : negativeInstruction) {
//								if (supAry.get(i)[a].toLowerCase().contains(neg.toLowerCase())) {
//									emo--;
//									supAry.get(i)[a] = String.valueOf(emo);
//									continue outer1loop;
//
//								}
//							}
							supAry.get(i)[a] = "0";
						}
					}
					emo = 0;
					outerloop: for (int i=0; i<supAry.size(); i++) {
						String newstr = "";
						String currentWord = "";
						String newOfnewStr = "";
						for (String st : supAry.get(i)) {
//							newstr = newstr+","+"'"+st.replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "")+"'";
							newstr = newstr + "," + st.replace("\\", "").replace("'", "").replace("'", "")
									.replace("'", "").replace("'", "");
							
						}
//						System.out.println(supAryText.get(i)[0]);
						String[] currentSupWord = supAryText.get(i);
						for(String stOfSt: currentSupWord) {
//							System.out.println(stOfSt);
							currentWord = stOfSt.replace("\\", "").replace("'", "").replace("'", "")
									.replace("'", "").replace("'", "");
//							System.out.println(hashWord.containsKey(currentWord.toLowerCase()));
							if(hashWord.containsKey(currentWord.toLowerCase())) {
								newOfnewStr = newOfnewStr + "," + hashWord.get(currentWord.toLowerCase());
							}else {
								newOfnewStr = newOfnewStr + "," + "0";
							}
						}
//						for(String st : supAryText.get(i)) {
//							currentWord = st.replace("\\", "").replace("'", "").replace("'", "")
//									.replace("'", "").replace("'", "");
//							if(hashWord.containsKey(currentWord.toLowerCase())) {
//								newOfnewStr = newOfnewStr + "," + hashWord.get(st.replace("\\", "").replace("'", "").replace("'", "")
//										.replace("'", "").replace("'", ""));
//							}else {
//								newOfnewStr = newOfnewStr + "," + "0";
//							}
//						}
						newOfnewStr = newOfnewStr.substring(1);
						System.out.println("SPLIT : "+newOfnewStr.split(",").length);
						newstr = newstr.substring(1);
						String[] strAry = new String[newstr.split(",").length];
						strAry = newstr.split(",");
						int sum = 0;
						for (String word : strAry) {
							int compare = !word.isEmpty() ? Integer.valueOf(word) : 0;
							if (compare > 0) {
								sum++;
							}
							if (compare < 0) {
								sum--;
							}
						}
						if (sum > 0) {
							dataAffr.append(newOfnewStr.trim() + ",positive" + "\n");
						} else if (sum < 0) {
							dataAffr.append(newOfnewStr.trim() + ",negative" + "\n");
						} else {
							dataAffr.append(newOfnewStr.trim() + ",neural" + "\n");
						}
						if(dataAffr.toString().startsWith(",")) {
							StringBuilder tempData = new StringBuilder();
							tempData.append(dataAffr.toString().substring(1));
							dataAffr = tempData;
						}
							
						createFile(fileChild.getName(), dataAffr.toString());
						dataAffr = new StringBuilder();
					}	
				}
			}
		}
	}
	public static void supervised_jsonFormat() {
		StringBuilder dataAffr = new StringBuilder();
		String[] result = null;
		File aDirectory = new File(sourcePath);
		File[] filesInDir = aDirectory.listFiles();
		String[] positiveInstruction = new String[supervised_positive.split(",").length];
		String[] negativeInstruction = new String[supervised_negative.split(",").length];
		File aDirectoryPos = new File(filePositive);
		positiveInstruction = readFileConv(aDirectoryPos).trim().substring(1).split(",");
		File aDirectoryNeg = new File(fileNegative);
		negativeInstruction = readFileConv(aDirectoryNeg).trim().substring(1).split(",");
		File aDirectoryWord = new File(filePositive);
		HashMap<String, String> hashWord = readFileWord(aDirectoryWord);
		HashMap<String, String> hashPositive = readFileWord(aDirectoryPos);
		HashMap<String, String> hashNegative = readFileWord(aDirectoryNeg);
		
		if (filesInDir != null) {
			for (File fileChild : filesInDir) {

				if (fileChild.isFile()) {
					System.out.println("File name : " + fileChild.getName());
					if (fileChild.getName().equals(".DS_Store")) {
						continue;
					}
					int maxLength = maxLengthFromJson(fileChild);
					dataAffr.append("@relation " + relation + "\n");
					for (int i = 1; i <= maxLength; i++) {
						if(i != maxLength) {
							dataAffr.append("@attribute " + "x" + i + " numeric\n");
						}else {
							dataAffr.append("@attribute emotional {positve, negative, neural}\n");
						}
					}
					dataAffr.append("@data\n");
					String totalMsg = "";
					List<String> preStrAry = null;
					String preStr = "";
					try (BufferedReader br = new BufferedReader(new FileReader(fileChild))) {
						int lineAt = 0;
						System.out.println("Exctract json");
						for (String line; (line = br.readLine()) != null;) {
							lineAt++;
							if (line.contains("\"" + json_requirment_key + "\"")) {
								preStr = line
										.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length()).split("\"")[3];
								System.out.println(lineAt);
								//
								preStrAry = PreProcessingController.normalizedStringArray(preStr, maxLength);
//								for(int j =0; j<maxLength-1; j++) {
//									if(j>preStr.split(" ").length-1) {
//										preStrAry[j] = "x";
//									}else {
//										if(!preStr.split(" ")[j].isEmpty()&&preStr.split(" ")[j]!=null) {
//											preStrAry[j] = preStr.split(" ")[j].replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "").replace(",", "");
//										}else{
//											preStrAry[j] = "x";
//										}
//									}
//								}
								
								String buildData = "";
								for(String str: preStrAry) {
									buildData = buildData + "," + str;
								}
								buildData = buildData.substring(1);
								buildData = buildData.trim();
								totalMsg = totalMsg + "@!" + buildData;
								totalMsg = totalMsg.trim();
							}
							if((lineAt%100000)==0) {
								System.out.println("mod 1000 == 0");
								result = new String[totalMsg.split("@!").length];
								result = totalMsg.split("@!");
								
								ArrayList<String[]> supAry = new ArrayList<String[]>();
								for (String data : result) {
									if (data != "") {
										String[] child = new String[data.split(",").length];
										child = data.split(",");
										supAry.add(child);
									}

								}
								System.out.println("Analysis");
								int emo;
								supAry.remove(0);
								for (int i = 0; i < supAry.size(); i++) {
									outer1loop: for (int a = 0; a < supAry.get(i).length; a++) {
										emo = 0;
										if(hashPositive.containsKey(supAry.get(i)[a].toLowerCase())) {
											emo++;
											supAry.get(i)[a] = String.valueOf(emo);
											continue outer1loop;
										}else if(hashNegative.containsKey(supAry.get(i)[a].toLowerCase())) {
											emo--;
											supAry.get(i)[a] = String.valueOf(emo);
											continue outer1loop;
										}else {
											supAry.get(i)[a] = "0";
										}

									}

								}

								emo = 0;
								outerloop: for (String[] str : supAry) {
									String newstr = "";
									String currentWord = "";
									String newOfnewStr = "";
									for (String st : str) {
//										newstr = newstr+","+"'"+st.replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "")+"'";
										newstr = newstr + "," + st.replace("\\", "").replace("'", "").replace("'", "")
												.replace("'", "").replace("'", "");
										currentWord = st.replace("\\", "").replace("'", "").replace("'", "")
												.replace("'", "").replace("'", "");
										if(hashWord.containsKey(currentWord.toLowerCase())) {
											newOfnewStr = newOfnewStr + "," + hashWord.get(st.replace("\\", "").replace("'", "").replace("'", "")
													.replace("'", "").replace("'", ""));
										}
									}
									newstr = newstr.substring(1);
									String[] strAry = new String[newstr.split(",").length];
									strAry = newstr.split(",");
									int sum = 0;
									for (String word : strAry) {
										int compare = !word.isEmpty() ? Integer.valueOf(word) : 0;
										if (compare > 0) {
											sum++;
										}
										if (compare < 0) {
											sum--;
										}
									}
									if (sum > 0) {
										dataAffr.append(newOfnewStr.trim() + ",positive" + "\n");
									} else if (sum < 0) {
										dataAffr.append(newOfnewStr.trim() + ",negative" + "\n");
									} else {
										dataAffr.append(newOfnewStr.trim() + ",neural" + "\n");
									}

									createFile(fileChild.getName(), dataAffr.toString());
									dataAffr = new StringBuilder();
								}
								
							}
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//end point
					System.out.println("END point");
					result = new String[totalMsg.split("@!").length];
					result = totalMsg.split("@!");
					
					ArrayList<String[]> supAry = new ArrayList<String[]>();
					for (String data : result) {
						if (data != "") {
							String[] child = new String[data.split(",").length];
							child = data.split(",");
							supAry.add(child);
						}

					}
					System.out.println("end Analysis");
					int emo;
					supAry.remove(0);
					for (int i = 0; i < supAry.size(); i++) {
						outer1loop: for (int a = 0; a < supAry.get(i).length; a++) {
							emo = 0;
							if(hashPositive.containsKey(supAry.get(i)[a].toLowerCase())) {
								emo++;
								supAry.get(i)[a] = String.valueOf(emo);
								continue outer1loop;
							}else if(hashNegative.containsKey(supAry.get(i)[a].toLowerCase())) {
								emo--;
								supAry.get(i)[a] = String.valueOf(emo);
								continue outer1loop;
							}else {
								supAry.get(i)[a] = "0";
							}
//							for (String pos : positiveInstruction) {
//								if (supAry.get(i)[a].toLowerCase().contains(pos.toLowerCase())) {
//									emo++;
//									supAry.get(i)[a] = String.valueOf(emo);
//									continue outer1loop;
//
//								}
//							}
//							for (String neg : negativeInstruction) {
//								if (supAry.get(i)[a].toLowerCase().contains(neg.toLowerCase())) {
//									emo--;
//									supAry.get(i)[a] = String.valueOf(emo);
//									continue outer1loop;
//
//								}
//							}

						}

					}

					for (int i = 0; i < supAry.size(); i++) {
						String newstr = "";
						for (String st : supAry.get(i)) {
//							newstr = newstr+","+"'"+st.replace("\\", "").replace("'", "").replace("'", "").replace("'", "").replace("'", "")+"'";
							newstr = newstr + "," + st.replace("\\", "").replace("'", "").replace("'", "")
									.replace("'", "").replace("'", "");

						}
					
						newstr = newstr.substring(1);
						String[] strAry = new String[newstr.split(",").length];
						strAry = newstr.split(",");
						int sum = 0;
						for (String word : strAry) {
							int compare = !word.isEmpty() ? Integer.valueOf(word) : 0;
							if (compare > 0) {
								sum++;
							}
							if (compare < 0) {
								sum--;
							}
						}
						if (sum > 0) {
							dataAffr.append(newstr.trim() + ",positive" + "\n");
						} else if (sum < 0) {
							dataAffr.append(newstr.trim() + ",negative" + "\n");
						} else {
							dataAffr.append(newstr.trim() + ",neural" + "\n");
						}

						createFile(fileChild.getName(), dataAffr.toString());
						dataAffr = new StringBuilder();
					}
					
				
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
				str.append(line + "\n");

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
		if (file.getName().contains("DS")) {
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

					preStr = line.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length())
							.split("\"")[3];
					ArrayList<String> arry = new ArrayList<String>();
					for (String st : preStr.split(" ")) {
						if (!st.isEmpty() && st != " " && st != "") {
							arry.add(st.trim());
						}
					}
					initLength = arry.size();

					if (maxLength < initLength) {
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
					preStr = line.substring(line.indexOf("\"" + json_requirment_key + "\""), line.length())
							.split("\"")[3];
					System.out.println(lineAt);
					preStrAry = new String[maxLength - 1];
					for (int j = 0; j < maxLength - 1; j++) {
						if (j > preStr.split(" ").length - 1) {
							preStrAry[j] = "x";
						} else {
							if (!preStr.split(" ")[j].isEmpty() && preStr.split(" ")[j] != null) {
								preStrAry[j] = preStr.split(" ")[j].replace("\\", "").replace("'", "").replace("'", "")
										.replace("'", "").replace("'", "").replace(",", "");
							} else {
								preStrAry[j] = "x";
							}
						}
					}
					String buildData = "";
					for (String str : preStrAry) {
						buildData = buildData + "," + str;
					}
					buildData = buildData.substring(1);
					buildData = buildData.trim();
					totalMsg = totalMsg + "@!" + buildData;
					totalMsg = totalMsg.trim();
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
	public static HashMap<String, String> readFileWord(File file) {
		HashMap<String, String> hashWord = new HashMap<String, String>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineAt = 0;
			
			System.out.println("Creating hash word");
			for (String line; (line = br.readLine()) != null;) {
				String[] lineAry = new String[line.split(",").length];
				lineAry = line.split(",");
				for(String str: lineAry) {
					lineAt++;
					hashWord.put(str.toLowerCase(), String.valueOf(lineAt));
				}
			}
			System.out.println("hash word created");
			return hashWord;
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
				str.append("," + line);
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
		if (fileName.endsWith(".txt")) {
			directFile = resultPath + fileName.replaceAll(".txt", ".arff");
			fileName = fileName.replaceAll(".txt", ".arff");
		}
		if (fileName.endsWith(".json")) {
			directFile = resultPath + fileName.replaceAll(".json", ".arff");
			fileName = fileName.replaceAll(".json", ".arff");
		}
		if (fileName.endsWith(".csv")) {
			directFile = resultPath + fileName.replaceAll(".csv", ".arff");
			fileName = fileName.replaceAll(".csv", ".arff");
		}

//		Path path = Paths.get(directFile);
		
		File aDirectory = new File(resultPath);
	    File[] filesInDir = aDirectory.listFiles();

	    if(filesInDir != null&& filesInDir.length!=0) 
	    {
	    	for(File fileChild : filesInDir)
	    	{
	    		if(fileChild.isFile())
	    		{
	    			if(filesInDir.length==1&&filesInDir[0].getName().contains(".DS")){
	    				createFile(resultPath, fileName, data);
	    				System.out.println("Created file");
	    				continue;
	    			}
	 	    			if(!fileChild.getName().contains(".DS")) {
	    				if(fileChild.getName().equals(fileName))
		    			{
		    				writeFile(resultPath, fileName, data);
		    				System.out.println("append file");
		    			}
	    			}
	    		}
	    	
	    }
	}else {
		createFile(resultPath, fileName, data);
		System.out.println("Created file");
	}
	}

	public static void createFileConv(String fileName, String data) {
		String directFile = resultPath + fileName;

		Path path = Paths.get(directFile);

		try {
			Files.write(path, data.getBytes());
		} catch (Exception e) {

		}
	}
	
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
}
