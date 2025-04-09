package com.chase.portfolio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.chase.portfolio.models.Chapter;
import com.chase.portfolio.services.JourneyService;

class JourneyTests {

    
    @Test
    void unmodifiableTest() {
    	assertThrows(UnsupportedOperationException.class, () -> {
    		JourneyService.Chapters.add(new Chapter(0, null, null));
    	});
    }
    
    @Test
    void validChpTest() {
    	assertFalse(JourneyService.isValidChp("-1"));
    	assertFalse(JourneyService.isValidChp("+1"));
    	assertTrue(JourneyService.isValidChp("1"));
    }
    
    @Test
    void boundsTest() {
    	assertNull(JourneyService.getChapter(0));
    	assertNotNull(JourneyService.getChapter(1));
    	assertNotNull(JourneyService.getChapter(JourneyService.Chapters.size()));
    	assertNull(JourneyService.getChapter(JourneyService.Chapters.size() + 1));
    }

}
