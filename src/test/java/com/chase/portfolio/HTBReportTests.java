package com.chase.portfolio;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.chase.portfolio.models.HTBReport;
import com.chase.portfolio.services.HTBService;

class HTBReportTests {
    
    @Test
    void unmodifiableTest() {
    	assertThrows(UnsupportedOperationException.class, () -> {
    		HTBService.Reports.add(HTBReport.red(null, null));
    		HTBService.ReportMap.put("test", HTBReport.red(null, null));
    	});
    }

}
