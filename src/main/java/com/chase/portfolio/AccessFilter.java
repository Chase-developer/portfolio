package com.chase.portfolio;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessFilter extends OncePerRequestFilter {

	private static final Set<String> ALLOWED_REFERERS = PortfolioUtils.isInProject()
		    ? Set.of("http://localhost")
		    : Set.of("https://chase-developer.com", "https://www.chase-developer.com");
    
    private static final Map<String, AntPathRequestMatcher> Matchers;
	
	static
	{
		Map<String, AntPathRequestMatcher> map = new HashMap<String, AntPathRequestMatcher>();
		map.put("css", new AntPathRequestMatcher("/css/**"));
		map.put("js", new AntPathRequestMatcher("/js/**"));
		map.put("images", new AntPathRequestMatcher("/images/**"));
		map.put("fonts", new AntPathRequestMatcher("/fonts/**"));
		map.put("videos", new AntPathRequestMatcher("/videos/**"));
		map.put("texts", new AntPathRequestMatcher("/texts/**"));
		map.put("views", new AntPathRequestMatcher("/views/**"));
		Matchers = Collections.unmodifiableMap(map);
	}

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
    	String requestPath = request.getRequestURI(); // Get the full path

        // Split the path by "/" and get the first segment (skip the leading "/")
    	String[] segment = requestPath.split("/"); // This gets the first segment, like "home", "font", etc.
    	if (segment.length <= 1)
    	{
    		filterChain.doFilter(request, response);
    		return;
    	}
    		
        String firstPathSegment = segment[1]; // This gets the first segment, like "home", "font", etc.

        // Check if the first segment matches any of your known paths
        AntPathRequestMatcher matcher = Matchers.get(firstPathSegment);
        if (matcher != null && matcher.matches(request)) {
        	String referer = request.getHeader("Referer");

		    if (referer == null || ALLOWED_REFERERS.stream().noneMatch(referer::startsWith)) {
		        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
		        return;
		    }
        }

        filterChain.doFilter(request, response);
    }
}
