package com.vadosity.resume.tailor;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Example {

	public static void main(String[] args) throws IOException {

		Map<String,String> searchReplaceMap = new HashMap<>();
		searchReplaceMap.put("Lead Engineer","Senior Java Developer");
		//searchReplaceMap.put("Java","JAVA");
		//searchReplaceMap.put("Getting There Baby","Development"); 
		//searchReplaceMap.put("Architected and coded a hybrid, custom mock engine that provides live VIA authentication and validation along with an extendable mechanism to mock results from multiple VistA instances.", "");
		SearchAndReplaceRequest searchAndReplaceRequest = new SearchAndReplaceRequest(searchReplaceMap);
		
		List<String> keywords = List.of("Sheez","Spring","Microservice","Microservices", "Java", "Spring Boot", "SQL","Javascript", "MySQL", "MongoDB", "Git","JIRA","Docker");
		KeywordStyleRequest keywordStyleRequest = new KeywordStyleRequest(true, false, false, false, false, false, false, false, "yellow", keywords);
		
		String trimBulletsFromMarker = "Professional Experience";
		String trimBulletsToMarker = "Applied";
		String keepBulletsWithCsv="Technologies:,keepme";
		
		TrimBulletsRequest trimBulletsRequest = new TrimBulletsRequest(trimBulletsFromMarker, trimBulletsToMarker, keepBulletsWithCsv);
		
		DocxUpdateRequest request = new DocxUpdateRequest(keywordStyleRequest,searchAndReplaceRequest,trimBulletsRequest,true,true);

		File in  = new File("c:\\word\\Brett Shelley - Lead Engineer.docx");
		File out = new File("C:\\word\\Brett Shelley-Senior Java Developer.docx");

		DocxUpdate docxUpdate =  new DocxUpdate();
		docxUpdate.updateDocument(in, out, request);
		KeywordMatchResults keywordMatchResults=  docxUpdate.getKeywordMatchResults();

		System.out.println("Done: " + keywordMatchResults);

	}

}