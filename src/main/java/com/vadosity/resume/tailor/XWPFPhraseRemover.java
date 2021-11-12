package com.vadosity.resume.tailor;

import java.util.TreeSet;

import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class XWPFPhraseRemover extends XWPFPhraseBase {

	final TreeSet<Integer> emptyParagraphsToRemove = new TreeSet<>();	
	
	public void removePhrase( String phrase, XWPFDocument doc)
	{
		pgIndex.set(0);
		doc.getParagraphs().forEach( pg-> 
		{
			searchFromRunIndex=0;
			while ( searchFromRunIndex!=-1)
			{
				int result = removePhrase(phrase, pg);
				if ( result==-1) break;
				if ( result==0 ) {
					result++; /// so it does not get stuck in infinite loop
				}
				searchFromRunIndex+=result;				
			}
			pgIndex.incrementAndGet();
		});
		
		emptyParagraphsToRemove.descendingSet().stream().forEach(n->doc.removeBodyElement(n));
		
	}
	

	public int removePhrase( String phrase, XWPFParagraph paragraph )
	{
		boolean runsRemoved = false;
		
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

		for ( int i =endRunIndex; i >= beginRunIndex; i-- )
		{
			runsRemoved=true;
			paragraph.removeRun(i);
		}	
		 
		if ( runsRemoved && paragraph.getText().trim().isBlank()) {
			emptyParagraphsToRemove.add(pgIndex.get());
		}
		
		return endRunIndex;

	}



}
