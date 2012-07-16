package com.zjm.util.cre.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;


public class TestImpl {
	private Test test;

//	public void printAccessFlags(printAccessFlags cf) {
//	    // TODO Auto-generated method stub
//	}
	public static void main(String[] args) {
		new TestImpl().cc();
	}
	
	public void cc(){
		Properties pro = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(new File("src/freemarker.properties"));
			pro.load(fis);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(pro.get("DirectoryForTemplate"));
	}

}
