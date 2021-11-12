package com.vadosity.resume.tailor;

public record DocxUpdateRequest ( 
		KeywordStyleRequest keywordStyleRequest,
		SearchAndReplaceRequest searchAndReplaceRequest,
		TrimBulletsRequest trimBulletsRequest,
		boolean removeKeywordlessBullets,
		boolean removeBracketedStrings
		) {
}
