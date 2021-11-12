package com.vadosity.resume.tailor;

import java.util.List;

public record KeywordStyleRequest ( 
			boolean highlightKeywords, 
			boolean highlightSentence,
			boolean boldfaceKeywords,
			boolean boldfaceSentence,
			boolean italicizeKeywords,
			boolean italicizeSentence,
			boolean underlineKeywords,
			boolean underlineSentence,
			String highlightColor,
			List<String> keywords
			) 
{}
