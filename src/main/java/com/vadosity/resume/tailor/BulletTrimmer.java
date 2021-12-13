package com.vadosity.resume.tailor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class BulletTrimmer {
	
	private String fromMarker;
	private String toMarker;
	private Set<String> keepBulletMarkers;
	private KeywordMatchResults keywordMatchResults;	
	private XWPFDocument doc;
	private TrimBulletsRequest trimBulletsRequest;
	
	private int startIndex=-1;
	private int endIndex=-1;
	
	
	public BulletTrimmer( TrimBulletsRequest trimBulletsRequest, XWPFDocument doc, KeywordMatchResults keywordMatchResults ) {
		this.trimBulletsRequest=trimBulletsRequest;
		fromMarker = Optional.ofNullable(trimBulletsRequest.fromMarker()).filter(s->!s.isBlank()).orElse(null);
		toMarker = Optional.ofNullable(trimBulletsRequest.toMarker()).filter(s->!s.isBlank()).orElse(null);
		this.keywordMatchResults=keywordMatchResults;
		keepBulletMarkers=Optional.ofNullable(trimBulletsRequest.keepBulletMarkers()).filter(s->!s.isEmpty()).orElse(Set.of());
		this.doc=doc;
		determineStartIndex();
		determineEndIndex();
	}
	
	private void determineStartIndex()
	{
		if ( fromMarker==null ) {
			startIndex= 0;
			return;
		}
		
		for ( XWPFParagraph pg: doc.getParagraphs() )
		{
			final String pgText = pg.getText().toLowerCase(Locale.US);
			if ( pgText.contains(fromMarker.toLowerCase(Locale.US)))
			{
				startIndex= doc.getBodyElements().indexOf(pg);
				return;
			}
		}
		/// not able to find the 'fromMarker' in the document, so ignore by setting startIndex-0
		startIndex=0;
	}
	
	private void determineEndIndex()
	{
		int maxIndex = doc.getBodyElements().size()-1;
		if ( toMarker==null ) {
			endIndex= maxIndex;
			return;
		}
		
		for ( XWPFParagraph pg: doc.getParagraphs() )
		{
			int currentIndex =  doc.getBodyElements().indexOf(pg);
			if ( currentIndex< startIndex) {
				continue;
			}
			
			final String pgText = pg.getText().toLowerCase(Locale.US);
			if ( pgText.contains(toMarker.toLowerCase(Locale.US)))
			{
				endIndex=currentIndex;
				return;
			}
		}
		/// not able to find the 'toMarker' in the document, so set to maxIndex
		endIndex=maxIndex;
	}
	
	public void trimBullets() {
		if ( trimBulletsRequest==null ) return;
		
		if ( keywordMatchResults==null || keywordMatchResults.getMatches().isEmpty()) return;
		
		final List<Integer> bodyElementsToRemove = new ArrayList<>();
		
		// go through each paragraph
		doc.getParagraphs().stream().forEach(pg->{
		
			int removeIndex = determineRemoveParagraphIndex(doc, pg);
			if ( removeIndex>-1) {
				bodyElementsToRemove.add(removeIndex);
			}
		});

		Collections.reverse(bodyElementsToRemove);
		bodyElementsToRemove.forEach(pos->{doc.removeBodyElement(pos);});
	}

	private int determineRemoveParagraphIndex(XWPFDocument doc, XWPFParagraph pg) {
		
		/// if the paragraph is not a bullet, then we do not remove
		if ( !"ListParagraph".equals(pg.getStyle())) {
			return -1;
		}
		
		/// if a keyword match matches the paragraph, then we do not remove
		if ( keywordMatchResults.getMatches()
				.stream()
				.filter( keywordMatch->pg.getText().contains( keywordMatch.keywordMatch()))
				.findAny()
				.isPresent()) {
			return -1;
		}
		
		/// if a keep bullet marker is found in the paragraph, then we do not remove
		final String pgText = pg.getText().toLowerCase(Locale.US);
		if ( keepBulletMarkers
				.stream()
				.filter( marker->pgText.contains(marker.toLowerCase()))
				.findAny()
				.isPresent()) {
			return -1;
		}
		
		/// if the paragraph is not in the search area, then we do not remove 
		int pos = doc.getBodyElements().indexOf(pg);
		if ( pos<startIndex || pos> endIndex) {
			return -1;
		}
		return pos;
	}


}
