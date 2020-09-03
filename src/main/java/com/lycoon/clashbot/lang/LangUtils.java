package com.lycoon.clashbot.lang;

import java.util.Locale;
import java.util.ResourceBundle;

import com.lycoon.clashbot.utils.DBUtils;

public class LangUtils
{
	public static final String[] LANGUAGES = {"en", "fr", "de", "es", "nl", "pt"};
	
	public static Locale getLanguage(long id)
	{
		return new Locale(DBUtils.getUserLang(id));
	}
	
	public static ResourceBundle getTranslations(long id)
	{
		return getTranslations(getLanguage(id));
	}
	
	public static ResourceBundle getTranslations(Locale locale)
	{
		return ResourceBundle.getBundle(
				"Messages", 
				locale, 
				new UTF16Control());
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
	
	public static String getSupportedLanguages(Locale currentLanguage)
	{
		String res = "";
		for (int i=0; i < LANGUAGES.length; i++)
		{
			Locale lang = new Locale(LANGUAGES[i]);
			res += "▫️ " + lang.getDisplayLanguage(currentLanguage) + " (`" +LANGUAGES[i]+ "`) \n";
		}
		
		return res;
	}
}
