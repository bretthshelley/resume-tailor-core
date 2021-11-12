package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class HighlightAction implements XWPFStyleAction {

	private final String highlightColor;
	
	public HighlightAction(String color)
	{
		highlightColor = HighlightLookup.getMatchingHighlightColor(color);
	}
	
	@Override
	public void execute(XWPFRun run, int positionInParagraph) {
		run.setTextHighlightColor(highlightColor);	
		run.setColor(HighlightLookup.getTextColorRGB(highlightColor));		
	}

}
