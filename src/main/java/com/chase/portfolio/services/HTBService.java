package com.chase.portfolio.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.chase.portfolio.models.HTBReport;
import com.chase.portfolio.models.HTBReport.ReportType;

@Service
public class HTBService {
	
	private static HTBReport red(String name, String verify)
	{
		return HTBReport.red(name, verify);
	}
	
	private static HTBReport blue(String name, ReportType type, String verify)
	{
		return HTBReport.blue(name, type, verify);
	}
	
	public static final List<HTBReport> Reports = List.of(
			red("escapetwo", "https://labs.hackthebox.com/achievement/sherlock/2297566/631"),
			red("shocker", "https://www.hackthebox.com/achievement/machine/2297566/108"),
			red("dog", "https://www.hackthebox.com/achievement/machine/2297566/651"),
			red("titanic", "https://www.hackthebox.com/achievement/machine/2297566/648"),
			red("underpass", "https://www.hackthebox.com/achievement/machine/2297566/641"),
			red("linkvortex", "https://www.hackthebox.com/achievement/machine/2297566/638"),
			blue("brutus", ReportType.DFIR ,"https://labs.hackthebox.com/achievement/sherlock/2297566/631"),
			blue("bumblebee", ReportType.DFIR, "https://labs.hackthebox.com/achievement/sherlock/2297566/554"),
			blue("lockpick", ReportType.MA ,"https://labs.hackthebox.com/achievement/sherlock/2297566/556"),
			blue("heartbreaker", ReportType.MA ,"https://labs.hackthebox.com/achievement/sherlock/2297566/699"),
			blue("litter", ReportType.SOC, "https://labs.hackthebox.com/achievement/sherlock/2297566/555"),
			blue("nubilum2", ReportType.SOC, "https://labs.hackthebox.com/achievement/sherlock/2297566/573"),
			blue("ufo1", ReportType.TI, "https://labs.hackthebox.com/achievement/sherlock/2297566/840")
			
			);
	//private static final HashSet<String> Reports = HashSet.of("brutus", "lockpick", "escapetwo");
	public static final Map<String, HTBReport> ReportMap;
	
	static
	{
		HashMap<String, HTBReport> map = new HashMap<String, HTBReport>();
		for (HTBReport report : Reports)
		{
			map.put(report.getId(), report);
			//System.out.println("Report ID added = " + report.getId());
		}
		ReportMap = Collections.unmodifiableMap(map);
	}
	
	public static HTBReport getReport(String id)
	{
		return ReportMap.get(id);
	}
	
	

}
