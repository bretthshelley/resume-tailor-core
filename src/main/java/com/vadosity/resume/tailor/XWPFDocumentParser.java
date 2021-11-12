package com.vadosity.resume.tailor;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class XWPFDocumentParser {
	
	public List<String> getSentenceTexts(XWPFDocument doc) 
	{
		if ( doc==null  ) {
			return new ArrayList<String>();
		}
		
		try (XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc))
		{
			return getSentenceTexts( xwpfWordExtractor.getText(), Locale.US);	
		}
		catch ( IOException e) {
			return new ArrayList<String>();
		}
	}
	
	
	public List<String> getSentenceTexts(XWPFParagraph paragraph)
	{
		if ( paragraph==null || paragraph.getText().isBlank() ) {
			return new ArrayList<String>();
		}
		return getSentenceTexts( paragraph.getText(), Locale.US);
	}

	public List<String> getSentenceTexts(String content, Locale locale)
	{

		List<String> sentences = new ArrayList<>();

		BreakIterator bi = BreakIterator.getSentenceInstance(Locale.US);

		bi.setText(content);

		int lastIndex = bi.first();
		while (lastIndex != BreakIterator.DONE) {
			int firstIndex = lastIndex;
			lastIndex = bi.next();

			if (lastIndex != BreakIterator.DONE) {
				String sentence = content.substring(firstIndex, lastIndex);
				sentences.add(sentence);
			}
		}
		
		List<String> results = new ArrayList<>();
		
		sentences.stream().forEach( sentence -> {
			sentence.lines().forEach(line->{
				results.add(line);
			});
		});

		return results;
	}

	public List<String> getWords( String text )
	{
		List<String> result = new ArrayList<String>();

		BreakIterator bi = BreakIterator.getWordInstance(Locale.US);
		bi.setText(text);

		int lastIndex = bi.first();
		while (lastIndex != BreakIterator.DONE) {
			int firstIndex = lastIndex;
			lastIndex = bi.next();

			if (lastIndex != BreakIterator.DONE) {
				String word = text.substring(firstIndex, lastIndex);
				if (! word.strip().isBlank())
				{
					result.add(word.strip());
				}
			}
		}
		return result;
	}
	
}
