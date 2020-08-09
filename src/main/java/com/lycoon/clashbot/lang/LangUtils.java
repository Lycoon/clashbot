package com.lycoon.clashbot.lang;

import java.util.Locale;
import java.util.ResourceBundle;

public class LangUtils
{
	public static Locale currentLang;
	public static ResourceBundle bundle;
	public static final String[] LANGUAGES = {"en", "fr", "de", "es", "nl", "pt"};
	
	public static boolean updateLanguage(String language)
	{
		if (!isSupportedLanguage(language))
			return false;
		
		currentLang = new Locale(language);
		bundle = ResourceBundle.getBundle("Messages", currentLang, new UTF16Control());
		
		return true;
	}
	
	public static boolean isSupportedLanguage(String language)
	{
		for (int i=0; i < LANGUAGES.length; i++)
		{
			if (LANGUAGES[i].equals(language))
				return true;
		}
		return false;
	}
	
	public static String getSupportedLanguages()
	{
		String res = "";
		for (int i=0; i < LANGUAGES.length; i++)
		{
			Locale lang = new Locale(LANGUAGES[i]);
			res += "- " + lang.getDisplayLanguage(currentLang) + " (" +LANGUAGES[i]+ ") \n";
		}
		
		return res;
	}
}
