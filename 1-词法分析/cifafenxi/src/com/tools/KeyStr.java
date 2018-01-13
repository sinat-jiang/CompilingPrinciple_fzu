package com.tools;

import java.util.ArrayList;

/**
 * ¹Ø¼ü×Ö
 * @author hasee
 */
public class KeyStr {
	
	static ArrayList<String> keystrlist = null;
	
	static{
		keystrlist = new ArrayList<String>();
		keystrlist.add("begin");
		keystrlist.add("constant");
		keystrlist.add("static");
		keystrlist.add("public");
		keystrlist.add("do");
		keystrlist.add("else");
		
		keystrlist.add("false");
		keystrlist.add("for");
		keystrlist.add("if");
		keystrlist.add("return");
		keystrlist.add("class");
		keystrlist.add("int");
		
		keystrlist.add("double");
		keystrlist.add("float");
		keystrlist.add("char");
		keystrlist.add("while");
		keystrlist.add("void");
		keystrlist.add("main");
		
		keystrlist.add("System");
		keystrlist.add("String");
		keystrlist.add("switch");
		keystrlist.add("case");
		keystrlist.add("private");
		keystrlist.add("Scanner");
	}
	
	public ArrayList<String> getKeystrlist() {
		return keystrlist;
	}
}
