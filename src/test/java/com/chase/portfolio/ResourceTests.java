package com.chase.portfolio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.chase.portfolio.services.ResourceService;

class ResourceTests {
    
    @Test
    void resourceTest() {
    	assertDoesNotThrow(() -> {
    		HashMap<?, ?> index = ResourceService.getStaticIndex();
    		assertFalse(index.isEmpty());
    	});
    }

}
