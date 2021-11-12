package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ItalicsAction implements XWPFStyleAction {

	public ItalicsAction()
	{
	}
	
	@Override
	public void execute(XWPFRun run, int positionInParagraph) {
		run.setItalic(true);			
	}

}
