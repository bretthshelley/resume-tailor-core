package com.vadosity.resume.tailor;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class XWPFPhraseBase {

	protected int searchFromRunIndex=0;
	protected final AtomicInteger pgIndex = new AtomicInteger(-1);
	
	public XWPFPhraseBase() {
		super();
	}

	private String calcFirstPartOfPhrase(String phrase, String text) {
		String longestString = null;
	
		String eval="";
		for ( int i = 0; i < phrase.length(); i++)
		{
			eval+=phrase.charAt(i);
			if ( text.contains(eval))
			{
				longestString=eval;
			}
		}
	
		return longestString;
	}

	private String calcLastPartOfPhrase(String phrase, String text) {
		String longestString = null;
	
		String eval="";
	
		for ( int i = phrase.length()-1; i >= 0; i--)
		{
			eval=phrase.charAt(i)+eval;
			if ( text.contains(eval))
			{
				longestString=eval;
			}
		}
	
		return longestString;
	}

	/**
	 * 
	 * @param phrase
	 * @param pg
	 * @param beginRunIndex
	 * @return the number of runs added. 
	 */
	protected int fixFirstRun(String phrase, XWPFParagraph pg, int beginRunIndex) {
	
		int runsAdded =0;
	
		XWPFRun run =  pg.getRuns().get(beginRunIndex);
		XWPFRunStyle runStyle = new XWPFRunStyle(run);
		String runText = run.text();
	
		if (runText.equals(phrase)) {
			return runsAdded; /// no added runs 
		}
	
		/// this run may contain the entire phrase or it may contain part of the entire phrase		
		String firstPartOfPhrase = calcFirstPartOfPhrase(phrase, runText);
	
		if ( firstPartOfPhrase==null ) {
			/// the phrase was not found... so, let's increase the index by 1 and call redundantly
			return -1;
		}
	
		int startIndex = runText.lastIndexOf(firstPartOfPhrase);
		int endIndex = startIndex+firstPartOfPhrase.length();		
		int lastIndex = runText.length()-1;
	
		pg.removeRun(beginRunIndex);		
	
		String beforePart = startIndex==0?"":runText.substring(0,startIndex);
		String phrasePart = runText.substring(startIndex,endIndex); 
		String afterPart = endIndex>lastIndex?"":runText.substring(endIndex);
	
		if ( beforePart.length()>0)
		{
			XWPFRun beforeRun = pg.insertNewRun(beginRunIndex++);
			beforeRun.setText(beforePart);
			runStyle.copyStyleTo(beforeRun);
			runsAdded++;
	
			/// it is possible that the before Run also contains the same phrase 
			/// more than once, so we recursively check the beforeRun
			/// example run text:  you've got to beat the best to be the best
			/// sought phrase:  best
			/// goal:  highlight both 'best' phrases			
			if ( beforePart.contains(phrase)) {
				int beforeRunIndex = beginRunIndex-1; 
				int newRunsAdded = fixFirstRun(phrase, pg,  beforeRunIndex);
				runsAdded+=newRunsAdded;
				beginRunIndex+=newRunsAdded;			
			}
		}
	
		XWPFRun phraseRun = pg.insertNewRun(beginRunIndex++);
		phraseRun.setText(phrasePart);
		runStyle.copyStyleTo(phraseRun);
	
		if ( afterPart.length()>0){
			XWPFRun afterRun = pg.insertNewRun(beginRunIndex++);
			afterRun.setText(afterPart);
			runStyle.copyStyleTo(afterRun);
			runsAdded++;
		}
	
		return runsAdded;
	
	}

	protected int fixLastRun(String phrase, XWPFParagraph pg, int runIndex) {
	
		int runsAdded =0;
	
		XWPFRun run =  pg.getRuns().get(runIndex);
		XWPFRunStyle runStyle = new XWPFRunStyle(run);
		String runText = run.text();
	
		if (runText.equals(phrase)) {
			return runsAdded; /// no added runs 
		}
	
		/// this run may contain the entire phrase or it may contain part of the entire phrase		
		String lastPartOfPhrase = calcLastPartOfPhrase(phrase, runText);
	
		if ( lastPartOfPhrase==null ) {
			return runsAdded; /// signal no op
		}
	
		int startIndex = runText.indexOf(lastPartOfPhrase);
		int endIndex = startIndex+lastPartOfPhrase.length();		
		int lastIndex = runText.length()-1;
	
		pg.removeRun(runIndex);		
	
		String beforePart = startIndex==0?"":runText.substring(0,startIndex);
		String phrasePart = runText.substring(startIndex,endIndex); 
		String afterPart = endIndex>=lastIndex?"":runText.substring(endIndex);
	
		if ( beforePart.length()>0)
		{
			XWPFRun beforeRun = pg.insertNewRun(runIndex++);
			beforeRun.setText(beforePart);
			runStyle.copyStyleTo(beforeRun);
			runsAdded++;
		}
	
		XWPFRun phraseRun = pg.insertNewRun(runIndex++);
		phraseRun.setText(phrasePart);
		runStyle.copyStyleTo(phraseRun);
	
		if ( afterPart.length()>0){
			XWPFRun afterRun = pg.insertNewRun(runIndex++);
			afterRun.setText(afterPart);
			runStyle.copyStyleTo(afterRun);
			runsAdded++;
	
			/// it is possible that the before Run also contains the same phrase 
			/// more than once, so we recursively check the afterRun
			/// example run text:  you've got to beat the best to be the best
			/// sought phrase:  best
			/// goal:  highlight both 'best' phrases			
			if ( afterPart.contains(phrase)) {
				int afterRunIndex = runIndex-1; 
				int newRunsAdded = fixLastRun(phrase, pg,  afterRunIndex);
				runsAdded+=newRunsAdded;
				runIndex+=newRunsAdded;		
			}
		}
	
		return runsAdded;
	
	}

}