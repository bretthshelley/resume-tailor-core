package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public class BoldAction implements XWPFStyleAction {

	public BoldAction()
	{
	}
	
	@Override
	public void execute(XWPFRun run, int positionInParagraph) {
		
		run.setBold(true);		
	}

}
