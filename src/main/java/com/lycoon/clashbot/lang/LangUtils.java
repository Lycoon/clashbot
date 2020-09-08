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
		for (String s : LANGUAGES)
		{
			if (s.equals(language))
				return true;
		}
		return false;
	}
	
	public static String getSupportedLanguages(Locale currentLanguage)
	{
		StringBuilder res = new StringBuilder();
		for (String language : LANGUAGES)
		{
			Locale lang = new Locale(language);
			res.append("▫ ").append(lang.getDisplayLanguage(currentLanguage)).append(" (`").append(language).append("`) \n");
		}
		
		return res.toString();
	}
}
