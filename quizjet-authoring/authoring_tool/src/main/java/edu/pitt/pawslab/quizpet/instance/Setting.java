package edu.pitt.pawslab.quizpet.instance;

import java.util.regex.Pattern;

public class Setting {
	
	public final static String DOMAIN = "java";
	public final static String QJAVAPREFIX = "q_java";
	public final static String DBNAME = "webex21jet";
	public final static String TIMEFORMAT = "MM/dd/yyyy HH:mm:ss";
	
	public final static Pattern PYTHONPREFFIX = Pattern.compile("^q_java_(?=.*)");
	public final static Pattern VERSIONSUFFIX = Pattern.compile("(?=.*)_v(\\d+)$");
	
	public final static String JAVACLASSSUFFIX = "java";
	public final static String JAVACLASSFOLDER = System.getProperty("user.home");
	
	public final static String PARSERURL = "http://acos.cs.hut.fi/python-parser";
	public final static String PARSERCODENAME = "code";
	public final static String PARSERTYPENAME = "mode";
	public final static String PARSERTYPEVALUE = "hierarchical";

}
