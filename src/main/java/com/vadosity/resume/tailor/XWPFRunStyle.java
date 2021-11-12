package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class XWPFRunStyle {
	
	private final boolean bold;
	private final boolean capitalized;
	private final int characterSpacing;
	private final String color;
	private final boolean strikeThrough;
	private final UnderlinePatterns underlinePatterns;
	private final boolean doubleStrikethrough;
	private final boolean embossed;
	private final String markType;  /// dot or none
	private final String fontFamily;
	private final int fontSize;
	private final boolean imprinted;
	private final boolean italic;
	private final int kern;
	private final String lang;
	private final boolean shadow;
	private final boolean smallcaps;
	private final String styleId;
	private final String vAlignString; // "baseline", "superscript", or "subscript"
	private final int textScalePercentage;
	private final String textHighlightColor;
	private final int textPosition;
	private final String underlineColor;
	private final boolean vanish;

	XWPFRunStyle(XWPFRun run) {
		
	    bold = run.isBold();
	    capitalized= run.isCapitalized();
		characterSpacing = run.getCharacterSpacing();
		color = run.getColor();
		doubleStrikethrough=run.isDoubleStrikeThrough();
		embossed = run.isEmbossed();
		markType = run.getEmphasisMark().toString();
		fontFamily = run.getFontFamily();
	    fontSize = run.getFontSize();
	    imprinted = run.isImprinted();
	    italic = run.isItalic();
	    kern = run.getKerning();
	    lang = run.getLang();
	    shadow = run.isShadowed();
	    smallcaps = run.isSmallCaps();
	    strikeThrough = run.isStrikeThrough();
	    styleId = run.getStyle();
	    vAlignString = run.getVerticalAlignment().toString();
	    underlinePatterns = run.getUnderline();
	    textScalePercentage =run.getTextScale();
	    textHighlightColor =run.getTextHightlightColor().toString();
	    textPosition = run.getTextPosition();
	    underlineColor=run.getUnderlineColor();
	    vanish = run.isVanish();
	}
	
	public void copyStyleTo(XWPFRun run)
	{
		run.setBold(bold);
		run.setCapitalized(capitalized);
		run.setCharacterSpacing(characterSpacing);
		if ( color!=null ) run.setColor(color);
		run.setDoubleStrikethrough(doubleStrikethrough);
		run.setEmbossed(embossed);
		run.setEmphasisMark(markType);
		run.setFontFamily(fontFamily);
		if ( fontSize!=-1) run.setFontSize(fontSize);
		run.setImprinted(imprinted);
		run.setItalic(italic);
		run.setKerning(kern);
		if ( lang!=null ) run.setLang(lang);
		run.setShadow(shadow);
		run.setSmallCaps(smallcaps);
		run.setStrikeThrough(strikeThrough);
		if ( styleId==null || !styleId.equals("")) run.setStyle(styleId);
		run.setVerticalAlignment(vAlignString);
		run.setTextScale(textScalePercentage);
		run.setTextHighlightColor(textHighlightColor);
		run.setTextPosition(textPosition);
		run.setUnderlineColor(underlineColor);
		run.setUnderline(underlinePatterns);
		run.setVanish(vanish);
		
	}
	
}

