package com.vadosity.resume.tailor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record TrimBulletsRequest (String fromMarker, String toMarker, Set<String> keepBulletMarkers){
	
	static final String CSV_WITH_QUOTES_REGEX=",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
	
	public TrimBulletsRequest(String fromMarker, String toMarker, String csvKeepBulletMarkers) {
		this(fromMarker,toMarker,convertToCsvSet(csvKeepBulletMarkers));
	}

	static Set<String> convertToCsvSet( String csvString)
	{
		if ( csvString==null || csvString.isBlank()) return Set.of();
		
		String[] sa = csvString.split(CSV_WITH_QUOTES_REGEX);
		final Set<String> set = new HashSet<>();
		List.of(sa).stream().filter(s->!s.isBlank()).forEach(s->set.add(s.trim()));
		return set;
	}

}
