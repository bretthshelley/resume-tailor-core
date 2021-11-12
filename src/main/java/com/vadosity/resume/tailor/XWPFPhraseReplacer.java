package com.vadosity.resume.tailor;

import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class XWPFPhraseReplacer extends XWPFPhraseBase {


	public void replacePhrase( String phrase, String replacementText, XWPFDocument doc)
	{
		pgIndex.set(0);
		doc.getParagraphs().forEach( pg-> 
		{
			searchFromRunIndex=0;
			while ( searchFromRunIndex!=-1)
			{
				int result = replacePhrase(phrase,replacementText, pg);
				if ( result==-1) break;
				if ( result==0 ) {
					result++; /// so it does not get stuck in infinite loop
				}
				searchFromRunIndex+=result;				
			}
			pgIndex.incrementAndGet();
		});
	}
	

	public int replacePhrase( String phrase, String replacementText, XWPFParagraph paragraph )
	{
		PositionInParagraph posInPg = new PositionInParagraph();	
		posInPg.setRun(searchFromRunIndex);
		
		TextSegment textSegment = paragraph.searchText(phrase, posInPg);
		if ( textSegment==null  ) return -1;

		int beginRunIndex = textSegment.getBeginRun();
		int endRunIndex = textSegment.getEndRun();

		int runsAdded = fixFirstRun(phrase, paragraph, beginRunIndex);		
		if ( phrase.length()>1 && endRunIndex>beginRunIndex)
		{
			fixLastRun(phrase, paragraph, endRunIndex+runsAdded);
		}

		/// search again now that the runs have been organized to contain no extraneous characters
		textSegment = paragraph.searchText(phrase, posInPg);
		if ( textSegment==null  ) return -1;

		beginRunIndex = textSegment.getBeginRun();
		endRunIndex = textSegment.getEndRun();
				
		XWPFRunStyle runStyle =  new XWPFRunStyle (paragraph.getRuns().get(beginRunIndex));
		
		
//		paragraph.insertNewRun(beginRunIndex);
//		XWPFRun replacementRun = paragraph.getRuns().get(beginRunIndex);
//		runStyle.copyStyleTo(replacementRun);
//		replacementRun.setText(replacementText, 0);
		
		
		for ( int i =endRunIndex; i >= beginRunIndex; i-- )
		{
			//paragraph.removeRun(i+1);
			paragraph.removeRun(i);
		}	
		
		paragraph.insertNewRun(beginRunIndex);
		XWPFRun replacementRun = paragraph.getRuns().get(beginRunIndex);
		runStyle.copyStyleTo(replacementRun);
		replacementRun.setText(replacementText, 0);
		
		return endRunIndex;
	}


}
