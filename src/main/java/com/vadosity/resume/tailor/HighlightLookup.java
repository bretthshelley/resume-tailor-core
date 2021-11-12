package com.vadosity.resume.tailor;

import java.util.TreeMap;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

public class HighlightLookup {

	
	private static TreeMap<String,String> hightlightTextMap= new TreeMap<>();
	
	static {
			hightlightTextMap.put(STHighlightColor.BLACK.toString(),"ffffff");
			hightlightTextMap.put(STHighlightColor.BLUE.toString(),"ffffff");
			hightlightTextMap.put(STHighlightColor.CYAN.toString(),"000000");
			hightlightTextMap.put(STHighlightColor.DARK_BLUE.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.DARK_CYAN.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.DARK_GRAY.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.DARK_GREEN.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.DARK_MAGENTA.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.DARK_RED.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.DARK_YELLOW.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.GREEN.toString(),"000000");
			hightlightTextMap.put(STHighlightColor.LIGHT_GRAY.toString(),"000000");
			hightlightTextMap.put(STHighlightColor.MAGENTA.toString(),"000000");
			hightlightTextMap.put(STHighlightColor.RED.toString(), "ffffff");
			hightlightTextMap.put(STHighlightColor.WHITE.toString(),"000000");
			hightlightTextMap.put(STHighlightColor.YELLOW.toString(),"000000");
	}
			
	public static String getMatchingHighlightColor( String color)
	{
		if ( color ==null ) throw new IllegalArgumentException("color argument is null");
		color = color.replaceAll("_", "").replaceAll(" ", "").trim();
		final String soughtColor = color;
		return hightlightTextMap.keySet().stream().filter(validColor->validColor.equalsIgnoreCase(soughtColor)).findFirst().orElseThrow( ()->new IllegalArgumentException("color '"+soughtColor+"' not available. Use one of:" + getAvailableHighlightColors()));
	}
	
	public static String getTextColorRGB(String color)
	{
		String highlightColor = getMatchingHighlightColor(color);
		return hightlightTextMap.get(highlightColor);
	}
	
	public static String getAvailableHighlightColors() {
		return String.join(",", hightlightTextMap.keySet());
	}
	
	
}
