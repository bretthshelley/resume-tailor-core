package com.vadosity.resume.tailor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class XWPFDocumentSearch {
	
	private static final String bracketRegex="\\[(.*?)\\]";
	private static final Pattern bracketPattern = Pattern.compile(bracketRegex);
	
	public Map<String,String> searchForDoubleSpaceFixes(XWPFDocument document)
	{
		Map<String,String> sentenceFixMap = new HashMap<>();
		
		List<String> sentences = new XWPFDocumentParser().getSentenceTexts(document);
		sentences.forEach(sentence->{
			String fixedSentence = fixDoubleSpace(sentence);
			if ( !fixedSentence.equals(sentence)) {
				sentenceFixMap.put(sentence, fixedSentence);
			}
		});
		return sentenceFixMap;
	}
	
	public String fixDoubleSpace( String sentence) {
		if ( !sentence.contains("  ")) return sentence;
		
		
		String lastPart = "";
		String firstPart=sentence;
		if ( sentence.endsWith(".  ")) {
			lastPart=".  ";
			firstPart=sentence.substring(0,sentence.lastIndexOf(".  "));
		}
		
		/// convert double space matches to be replaces by single space matches
		/// examples:
		/// "  This sentence starts with a double space"
		///
		/// "here is a double  space that should be fixed with a single space"
		///
		/// "This sentence ends with a double space  ."
		firstPart = firstPart.replaceAll(Pattern.quote("  ."),".");		
		firstPart = firstPart.replaceAll(Pattern.quote("  "), " ");
		String result =  firstPart+lastPart;
		if ( result.endsWith(" .  ")) {
			result = result.substring(0, result.lastIndexOf(" .  "))+".  ";
		}
		else if ( result.endsWith(" .")) {
			result = result.substring(0, result.lastIndexOf(" ."))+".";
		}
		
		//result = result.stripLeading();
		return result;
	}	
	
	
	public Set<String> searchForBracketedStrings(XWPFDocument document)
	{
		Set<String> matchingStrings = new HashSet<>();
		
		List<String> sentences = new XWPFDocumentParser().getSentenceTexts(document);
		sentences.forEach(sentence->{
			matchingStrings.addAll(searchForBracketedStrings(sentence));
		});
		return matchingStrings;
	}
	
	private Set<String> searchForBracketedStrings(String sentence)
	{
		Matcher matcher = bracketPattern.matcher(sentence);
    	Set<String> matchingStrings = new HashSet<>();
    	
    	int start=0;
    	int end=-1;
    	
    	while ( matcher.find(start)) {
    		start = matcher.start();
    		end = matcher.end();
    		matchingStrings.add(tweakBracketMatch(sentence,start, end)); 
    		start=end;
    	}
		return matchingStrings;
	}
	
	private String tweakBracketMatch( String sentence, int start, int end) {
		
		/// the removal of the bracketed expression may cause an additional blank space that should be removed 
		///example:
		//  experience [hashtag,#hashtag]. --> experience . (excess blank space)
		// timed out [tying] or returned --> timed out  or returned (excess blank space)
		
		if ( start>0)
		{
			/// the char before should be removed if a blank space
			char charBefore = sentence.charAt(start-1);
			if ( charBefore==' ') {
				start=start-1;
			}			
		}
		
		if ( start==0) {
			int lastIndex= sentence.length()-1;
			int trailingCharacterIndex = end;
			
			if ( lastIndex>= trailingCharacterIndex) {
				/// the char after should be removed if a blank space
				char charAfter = sentence.charAt(trailingCharacterIndex);
				if ( charAfter==' ') {
					end++;
				}				
			}
		}
		
		return sentence.substring(start, end);
		
	}
	
	
	public KeywordMatchResults searchDocument(XWPFDocument document, final List<String> keywords)
	{
		final KeywordMatchResults results = new KeywordMatchResults();
		
		List<String> sentences = new XWPFDocumentParser().getSentenceTexts(document);
		sentences.forEach(sentence->{
			keywords.forEach(keyword->{
				results.addResults(searchSentence(sentence,keyword));
			});
		});
		results.determineUnmatchedKeywords(keywords);
		results.setPercentageMatch(results.determinePercentageMatched());
		return results;
	}
	
	private List<KeywordMatch> searchSentence(String sentence, String keyword)
	{
		List<KeywordMatch> keywordMatches = new ArrayList<>();
		
		int startIndex = sentence.toLowerCase().indexOf(keyword.toLowerCase());
		while (startIndex>-1)
		{
			int endIndex = startIndex+keyword.length();
			//if ( endIndex== sentence.length()) endIndex--;
			
			String exactKeyword = sentence.substring(startIndex,endIndex);
			
			KeywordMatch keywordMatch = new KeywordMatch(keyword,exactKeyword,sentence);
			keywordMatches.add(keywordMatch);
			
			startIndex = sentence.toLowerCase().indexOf(keyword.toLowerCase(),endIndex);
		}
		return keywordMatches;
	}
	
	
	
	
	
	
	

}
