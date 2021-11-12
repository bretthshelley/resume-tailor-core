package com.vadosity.resume.tailor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeywordMatchResults {

	private Map<String,Integer> stats = new HashMap<>();

	private List<KeywordMatch> matches = new ArrayList<>();
	
	private List<String> unmatchedKeywords = new ArrayList<>();
	
	private String outputFilename;
	
	public String getOutputFilename() {
		return outputFilename;
	}

	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	public void determineUnmatchedKeywords(List<String> keywordsSought) {
		if ( keywordsSought==null || keywordsSought.isEmpty())
		{
			unmatchedKeywords=List.of();
			return;
		}
		unmatchedKeywords= new ArrayList<>();
		keywordsSought.forEach(sought->{
			boolean hasKeyword = matches.stream().filter(k->k.keyword().equalsIgnoreCase(sought)).findAny().isPresent();
			if ( !hasKeyword) {
				unmatchedKeywords.add(sought);
			}
		});
	}

		public void addResult(KeywordMatch keywordMatch)
	{
		String keyword = keywordMatch.keyword();
		
		if ( stats.containsKey(keyword)){
			Integer count = stats.get(keyword);
			count = count+1;
			stats.put(keywordMatch.keyword(), count);
		}
		else{
			stats.put(keyword, 1);
		}
		matches.add(keywordMatch);
	}
	
	public void addResults(List<KeywordMatch> keywordMatches)
	{
		keywordMatches.forEach(keywordMatch->{
			addResult(keywordMatch);
		});
	}
	
	public void addUnmatchedKeyword(String keyword) {
		
	}
	
	@Override
	public String toString() {
		return "KeywordMatchResults [stats=" + stats + ", matches=" + matches + ", unmatchedKeywords="
				+ unmatchedKeywords + "]";
	}

	public List<String> getUnmatchedKeywords() {
		
		return unmatchedKeywords;
	}
	
	public List<KeywordMatch> getMatches() {
		return matches;
	}

	public Map<String, Integer> getStats() {

		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
		 
		stats.entrySet()
		    .stream()
		    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))		    
		    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		
		return sortedMap;
	}
	
}
