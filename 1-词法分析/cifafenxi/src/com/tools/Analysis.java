package com.tools;

import java.io.IOException;

/**
 * 分析类
 * 
 * @author hasee
 */
public class Analysis {

	/**
	 * state = 0 : 空格或换行或一些不需要识别的字符等 state = 1 : 标识符 state = -1 :
	 * 
	 * @param str
	 * @throws IOException
	 */
	public static void analysis(String str) throws IOException {
		char[] arraystr = str.toCharArray();
		char c;
		String identify = "";
		int state = 0;
		int i = -1;
		int lenth = str.length();
		int tempw = 0;
		int pointcount = 0;
		while (true) {
			switch (state) {

			case 0:
				c = arraystr[++i];
				if (c == ' ' || c == '\t' || c == '\n') {
					state = 0;
				} else if (c == '>') {
					identify = String.valueOf(c);
					state = 1;
				} else if (c == '<') {
					identify = String.valueOf(c);
					state = 1;
				} else if (c == '=') {
					identify = String.valueOf(c);
					state = 1;
				} else if (c == ')' || c == '(') {
					// identify = String.valueOf(c);
					state = 0;
				} else if (c == ']' || c == '[') {
					// identify = String.valueOf(c);
					state = 0;
				} else if (c == '}' || c == '{') {
					identify = String.valueOf(c);
					FileOperationTools.writeToFile(identify, "界符");
					identify = "";
					state = 0;
				} else if (isletter(c)) {
					// identify = String.valueOf(c);
					state = 5;
				} else if (isdigital(c)) {
					// identify = String.valueOf(c);
					state = 6;
				} else if (isop(c)) {
					identify = String.valueOf(c);
					if (c == '+' || c == '-' || c == '*' || c == '%') {
						FileOperationTools.writeToFile(identify, "op");
						identify = "";
						state = 0;
					} else {
						char t = arraystr[i + 1];
						if (t == '/') {
							FileOperationTools.writeToFile("//", "单段注释");
							while (c != '\n' && i <= lenth - 2) {
								c = arraystr[++i];
							}
							identify = "";
							state = 0;
						} else if (t == '*') {
							while (i <= lenth - 2) {
								c = arraystr[++i];
								if (c == '*' && arraystr[i + 1] == '/') {
									FileOperationTools.writeToFile("/*", "多段注释");
									identify = "";
									state = 0;
									i++;
									break;
								}
							}
							if (i == lenth - 1) {
								c = arraystr[lenth - 2];
								if (!(c == '*' && arraystr[lenth-1] == '/')) {
									System.out.println("Error");
									i++;
									identify = "";
									state = 0;
								}
							}
							break;
						} else {
							FileOperationTools.writeToFile(identify, "op");
							System.out.println("else");
							identify = "";
							state = 0;
						}
					}
				}
				break;

			case 1:
				char c_before = arraystr[i];
				c = arraystr[++i];
				if (c_before == '<' || c_before == '>' || c_before == '=') {
					if (c_before == '<' && c == '>') {
						// <>
						identify = identify + String.valueOf(c);
						FileOperationTools.writeToFile(identify, "relop");
						identify = "";
						state = 0;
					} else if (c_before == '>' && (c == '<' || c == '>')) {
						System.out.println("Error");
						identify = "";
						state = 0;
						i--;
						break;
					} else if (c_before == '<' && (c == '<')) {
						System.out.println("Error");
						identify = "";
						state = 0;
						i--;
						break;
					} else {
						if ((c_before == '<' || c_before == '>') && c == '=') {
							// >= || <= || ==
							identify = identify + String.valueOf(c);
							FileOperationTools.writeToFile(identify, "relop");
							identify = "";
							state = 0;
							break;
						} else if (c_before == '=' && c == '=') {
							identify = identify + String.valueOf(c);
							FileOperationTools.writeToFile(identify, "relop");
							identify = "";
							state = 0;
							break;
						} else {
							if (identify.equals("=")) {
								FileOperationTools.writeToFile(identify, "赋值");
							} else {
								System.out.println("Error");
								identify = "";
								state = 0;
								i--;
								break;
							}
						}
					}
					identify = "";
					state = 0;
					i--;
				}
				break;

			case 5:
				char temp1 = arraystr[i];
				if (isdigital(temp1)) {
					System.out.println("Error");
					identify = "";
					state = 0;
					i++;
					break;
				} else {
					c = arraystr[i++];
					if (isletter(c)) {
						identify = identify + String.valueOf(c);
						state = 5;
					} else if (isdigital(c)) {
						identify = identify + String.valueOf(c);
						state = 5;
					} else {
						if (iskey(identify))
							FileOperationTools.writeToFile(identify, "key");
						else
							FileOperationTools.writeToFile(identify, "id");
						identify = "";
						state = 0;
						i = i - 2;
					}
				}
				break;

			// 数字catch
			/*
			 * case 6: c = arraystr[++i]; if (isdigital(c)) { identify =
			 * identify + String.valueOf(c); state = 6; } else {
			 * FileOperationTools.writeToFile(identify, "num"); identify = "";
			 * state = 0; i--; } break;
			 */

			case 6:
				char c_befor = arraystr[i++];
				int temp = 0;
				
				// System.out.println(c_befor);
				if (isdigital(c_befor)) {
//					if(c_befor == '.'){
//						pointcount ++ ;
//						if(pointcount>1){
//							System.out.println("Error");
//							identify = "";
//							state = 0;
//							i++;
//							break;
//						}
//					}
					if (c_befor >= '0' && c_befor <= '9') {
						identify = identify + String.valueOf(c_befor);
						state = 6;
						tempw = 1;
//						temp = 1;
					} else if(!(arraystr[i-2] >= '0' && arraystr[i-2] <= '9') ){
						System.out.println("Error");
						identify = "";
						state = 0;
						i++;
						break;
					}
					else if((arraystr[i-2] >= '0' && arraystr[i-2] <= '9') && (arraystr[i] >= 'a' && arraystr[i] <= 'z') ){
						System.out.println("Error");
						identify = "";
						state = 0;
						i++;
						break;
					}
					else if (c_befor == '.') {
						pointcount ++ ;
						if(pointcount > 1){
							System.out.println("Error-pointcount:" + pointcount);
							identify = "";
							state = 0;
							i++;
							pointcount = 0;
							break;
						}
						if (tempw == 1) {
							identify = identify + String.valueOf(c_befor);
							state = 6;
						} else {
							System.out.println("Error");
							identify = "";
							state = 0;
							i++;
							break;
						}
					}
				} else {
					FileOperationTools.writeToFile(identify, "num");
					identify = "";
					state = 0;
					i = i - 2;
					pointcount = 0;
				}
				break;
			}
			if (i == lenth - 1) {
				break;
			}
		}
	}

	/**
	 * 判断是否是变量
	 * @param c
	 * @return
	 */
	public static Boolean isletter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是数字
	 * @param c
	 * @return
	 */
	public static Boolean isdigital(char c) {
		if ((c >= '0' && c <= '9') || c == '.') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是key
	 * @param str
	 * @return
	 */
	public static Boolean iskey(String str) {
		// 若包含，则返回true
		if (KeyStr.keystrlist.contains(str)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是运算符op
	 */
	public static Boolean isop(char c) {
		if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%') {
			return true;
		} else {
			return false;
		}
	}

}
