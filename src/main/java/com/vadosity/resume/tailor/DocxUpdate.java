package com.vadosity.resume.tailor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public class DocxUpdate {

	private KeywordMatchResults keywordMatchResults;


	public void updateDocument(File inFile, File outFile, DocxUpdateRequest docxUpdateRequest) throws IOException {
		validateIOArguments(inFile, outFile);
		validateRequest(inFile, outFile, docxUpdateRequest);

		InputStream in = Files.newInputStream(inFile.toPath());
		OutputStream out = Files.newOutputStream(outFile.toPath());

		updateDocument(in,out,docxUpdateRequest);
	}

	public void updateDocument(InputStream in, OutputStream out, DocxUpdateRequest docxUpdateRequest) throws IOException {
		validateIOArguments(in, out);
		validateRequest(in, out, docxUpdateRequest);

		try (
				XWPFDocument doc = new XWPFDocument(in);
				XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
				) {
			removeBulletParagraphsWithoutKeywordMatches(doc);
			searchAndReplace(docxUpdateRequest, doc);
			fixDoubleSpaces( doc);
			searchAndStyle(docxUpdateRequest, doc);
			trimBullets( docxUpdateRequest, doc);			
			searchAndRemoveBracketedPhrases(docxUpdateRequest, doc);
			removeEmptyBullets(doc);

			try (out) {
				doc.write(out);
			} 
		}
	}


	private void trimBullets(DocxUpdateRequest docxUpdateRequest, XWPFDocument doc) {
		if ( docxUpdateRequest==null || docxUpdateRequest.trimBulletsRequest()==null ) return;
		new BulletTrimmer(docxUpdateRequest.trimBulletsRequest(), doc, keywordMatchResults).trimBullets();
	}

	private void validateRequest(Object in, Object out, DocxUpdateRequest docxUpdateRequest) {

		/// only validate the keyword style request if it is present
		boolean keywordStyleRequestPresent = Optional.ofNullable(docxUpdateRequest)
				.map(DocxUpdateRequest::keywordStyleRequest).isPresent();

		if ( keywordStyleRequestPresent) {

			Optional.ofNullable(docxUpdateRequest)
			.map(DocxUpdateRequest::keywordStyleRequest)
			.map(KeywordStyleRequest::keywords)
			.filter(list->!list.isEmpty())
			.orElseThrow(()->new IllegalArgumentException("keywords missing"));

			boolean isHighlighted = docxUpdateRequest.keywordStyleRequest().highlightKeywords() 
					|| docxUpdateRequest.keywordStyleRequest().highlightSentence();
			if (isHighlighted ) {
				String color = docxUpdateRequest.keywordStyleRequest().highlightColor();
				/// throw an exception is the color is not valid
				HighlightLookup.getMatchingHighlightColor(color);
			}
		}
	}

	private void validateIOArguments(File inFile, File outFile) {
		if ( inFile==null ) throw new IllegalArgumentException("'inFile' cannot be null");
		if ( outFile==null ) throw new IllegalArgumentException("'outFile' cannot be null");
	}

	private void validateIOArguments(InputStream inputStream, OutputStream outputStream) {
		if ( inputStream==null ) throw new IllegalArgumentException("'inputStream' cannot be null");
		if ( outputStream==null ) throw new IllegalArgumentException("'outputStream' cannot be null");
	}


	private void searchAndReplace(DocxUpdateRequest docxUpdateRequest, XWPFDocument doc) {

		Map<String,String> searchReplaceMap = Optional.ofNullable(docxUpdateRequest.searchAndReplaceRequest())
				.map(SearchAndReplaceRequest::searchReplaceMap)
				.filter(map->!map.isEmpty()).orElse(null);
		if ( searchReplaceMap==null) return;

		searchReplaceMap.forEach((key,value)->{
			new XWPFPhraseReplacer().replacePhrase(key, value, doc);
		});
	}

	private void searchAndStyle(DocxUpdateRequest docxUpdateRequest, XWPFDocument doc) {

		List<String> keywords = Optional.ofNullable( docxUpdateRequest)
				.map(DocxUpdateRequest::keywordStyleRequest)
				.map(KeywordStyleRequest::keywords).orElse(null);
		
		if ( keywords==null ) {
			keywords= new ArrayList<>();
		};

		KeywordStyleRequest keywordStyleRequest= docxUpdateRequest.keywordStyleRequest();

		keywordMatchResults = new XWPFDocumentSearch().searchDocument(doc, keywords);

		keywordMatchResults.getMatches().forEach(keywordMatch->{

			if ( keywordStyleRequest.highlightKeywords()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.keywordMatch(), doc, new HighlightAction(keywordStyleRequest.highlightColor()));
			}
			if ( keywordStyleRequest.highlightSentence()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.surroundingSentence(), doc, new HighlightAction(keywordStyleRequest.highlightColor()));
			}					
			if ( keywordStyleRequest.boldfaceKeywords()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.keywordMatch(), doc, new BoldAction());
			}
			if ( keywordStyleRequest.boldfaceSentence()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.surroundingSentence(), doc, new BoldAction());
			}					
			if ( keywordStyleRequest.italicizeKeywords()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.keywordMatch(), doc, new ItalicsAction());
			}
			if ( keywordStyleRequest.italicizeSentence()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.surroundingSentence(), doc, new ItalicsAction());
			}					
			if ( keywordStyleRequest.underlineKeywords()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.keywordMatch(), doc, new UnderlineAction());
			}
			if ( keywordStyleRequest.underlineSentence()) {
				new XWPFPhraseStyler().stylePhrase(keywordMatch.surroundingSentence(), doc, new UnderlineAction());
			}					
		});
	}

	private void searchAndRemoveBracketedPhrases(DocxUpdateRequest docxUpdateRequest, XWPFDocument doc) {

		if ( !docxUpdateRequest.removeBracketedStrings()){
			return;
		}
		Set<String> bracketedStrings = new XWPFDocumentSearch().searchForBracketedStrings(doc);

		bracketedStrings.stream().filter(s->!s.trim().isBlank()).forEach(s->{
			new XWPFPhraseRemover().removePhrase(s, doc);
		});
	}

	private void fixDoubleSpaces(XWPFDocument doc) {
		Map<String,String> sentenceFixMap = new XWPFDocumentSearch().searchForDoubleSpaceFixes(doc);
		sentenceFixMap.forEach((sentence,fix)->{
			new XWPFPhraseReplacer().replacePhrase(sentence, fix, doc);
		});
	}

	private void removeBulletParagraphsWithoutKeywordMatches(XWPFDocument doc)
	{

		List<Integer> bodyElementsToRemove = new ArrayList<>();

		doc.getParagraphs().stream().forEach(pg->{
			if ( "ListParagraph".equals(pg.getStyle()))
			{
				int pos = doc.getBodyElements().indexOf(pg);
				bodyElementsToRemove.add(pos);
			}
		});

		Collections.reverse(bodyElementsToRemove);

	}

	private void removeEmptyBullets(XWPFDocument doc)
	{
		List<Integer> bodyElementsToRemove = new ArrayList<>();

		doc.getParagraphs().stream().forEach(pg->{
			if ( pg.getText().isBlank() &&  "ListParagraph".equals(pg.getStyle()))
			{
				int pos = doc.getBodyElements().indexOf(pg);
				bodyElementsToRemove.add(pos);
			}
		});
		Collections.reverse(bodyElementsToRemove);
		bodyElementsToRemove.forEach(pos->{doc.removeBodyElement(pos);});
	}

	public KeywordMatchResults getKeywordMatchResults() {
		return keywordMatchResults;
	}

}
