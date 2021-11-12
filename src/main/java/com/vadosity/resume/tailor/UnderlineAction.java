package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class UnderlineAction implements XWPFStyleAction {

	private final UnderlinePatterns underlinePatterns;
	
	public UnderlineAction()
	{
		this.underlinePatterns = UnderlinePatterns.SINGLE;
	}
	
	public UnderlineAction(UnderlinePatterns underlinePatterns)
	{
		this.underlinePatterns = underlinePatterns;
	}
	
	@Override
	public void execute(XWPFRun run, int positionInParagraph) {
		run.setUnderline(underlinePatterns);			
	}

}
