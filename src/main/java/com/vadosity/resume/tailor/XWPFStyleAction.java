package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.XWPFRun;

public interface XWPFStyleAction {
	
	public void execute(XWPFRun run,int positionInParagraph) ;
}
