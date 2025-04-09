package com.chase.portfolio;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.chase.portfolio.models.Badge;
import com.chase.portfolio.models.Skill;
import com.chase.portfolio.services.BadgeSkillService;

class BadgeSkillTests {

    @Test
    void unmodifiableListTest() {
    	List<List<Skill>> skillss = new ArrayList<>();
    	skillss.add(BadgeSkillService.CybersecuritySkills);
    	skillss.add(BadgeSkillService.DevOpsCloudSkills);
    	skillss.add(BadgeSkillService.FullStackSkills);
    	assertThrows(UnsupportedOperationException.class, () -> {
    		for (List<Skill> skills : skillss)
                skills.add(new Skill(null, null));
    		BadgeSkillService.Badges.add(new Badge(null, null, null, null));
    	});
    }

}
