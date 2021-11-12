package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class XWPFPhraseStyler extends XWPFPhraseBase {

	public void stylePhrase( String phrase, XWPFDocument doc, XWPFStyleAction action)
	{
		doc.getParagraphs().forEach( pg-> 
		{
			searchFromRunIndex=0;
			while ( searchFromRunIndex!=-1)
			{
				int result = stylePhrase(phrase, pg,action);
				if ( result==-1) break;
				if ( result==0 ) {
					result++; /// so it does not get stuck in infinite loop
				}
				searchFromRunIndex+=result;				
			}
			pgIndex.incrementAndGet();
		});
	}
	
	
	public int stylePhrase( String phrase, XWPFParagraph pg, XWPFStyleAction action )
	{
		PositionInParagraph posInPg = new PositionInParagraph();	
		posInPg.setRun(searchFromRunIndex);
		
		TextSegment textSegment = pg.searchText(phrase, posInPg);
		if ( textSegment==null  ) return -1;

		int beginRunIndex = textSegment.getBeginRun();
		int endRunIndex = textSegment.getEndRun();
		
		int runsAdded = fixFirstRun(phrase, pg, beginRunIndex);		
		
		if ( runsAdded==0 && beginRunIndex==endRunIndex) {
			
			XWPFRun run = pg.getRuns().get(beginRunIndex);
			XWPFRunStyle newStyle = new XWPFRunStyle(run);
			
			/// remove old insert new
			XWPFRun newRun = pg.insertNewRun(beginRunIndex);
			newRun.setText(phrase);
			newStyle.copyStyleTo(newRun);
			pg.removeRun(beginRunIndex+1);			
			
			action.execute(newRun,beginRunIndex);		
			return endRunIndex; 
		}
		
		if ( beginRunIndex<endRunIndex)
		{
			fixLastRun(phrase, pg, endRunIndex+runsAdded);
		}

		/// search again now that the runs have been organized to contain no extraneous characters
		textSegment = pg.searchText(phrase, posInPg);
		if ( textSegment==null  ) return -1;

		beginRunIndex = textSegment.getBeginRun();
		endRunIndex = textSegment.getEndRun();
		
		for ( int i=beginRunIndex; i <= endRunIndex; i++)
		{
			XWPFRun run = pg.getRuns().get(i);
			action.execute(run,i);
		}
		
		return endRunIndex;

	}

	


}
