package com.chase.portfolio;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.chase.portfolio.profiles.IntegrationTest;
import com.chase.portfolio.services.OCIStorageService;

@IntegrationTest
@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private OCIStorageService storageService;

    // Test-specific configuration
    @TestConfiguration
    static class TestConfig {
        @Bean
        public OCIStorageService storageService() {
        	OCIStorageService mockStorageService = mock(OCIStorageService.class);
            // Mock the behavior of getPreAuthURL to always return "mocked_url"
            when(mockStorageService.getPreAuthURL(any(String.class))).thenReturn("mocked_url");
            return mockStorageService;
        }
    }

    @Profile("int-test")
    @Test
    void redirectTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        		.andExpect(MockMvcResultMatchers.redirectedUrl("/home"));
    }
    @Profile("int-test")
    @Test
    void homeTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Profile("int-test")
    @Test
    void htbTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/htb"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Profile("int-test")
    @Test
    void journeyTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/journey"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
