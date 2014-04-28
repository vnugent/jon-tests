package com.redhat.qe.jon.sahi.tests.recentalerts;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.SahiTestScript;
import com.redhat.qe.jon.sahi.base.RecentAlerts.GroupRecentAlertsTasks;

/**
 * @author mmahoney
 */

public class GroupRecentAlertsTest extends  SahiTestScript {

	private GroupRecentAlertsTasks ga = null;
	
	@BeforeSuite
	public void setUp() {
		ga = new GroupRecentAlertsTasks(sahiTasks);
	}
	
	@BeforeTest
	public void navigateToCompatabilityGroups() {
		ga.navigateToCompatibleGroups();
	}
	
	@Test
	public void createCompatibilityGroupAndAddRecentAlerts() {
		ga.createNewCompatibleGroup();
		Assert.assertTrue(ga.addGroupRecentAlertsPortlet(), "Group Recent Alerts!");
		Assert.assertTrue(ga.recentAlertsNameSearch(), "Name Search!");
	}
}