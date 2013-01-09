package com.redhat.qe.jon.sahi.tests;


import java.util.HashMap;
import org.testng.Reporter;
import org.testng.annotations.Test;
import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.SahiTestScript;


/**
 * @author jkandasa (Jeeva Kandasamy)
 * Dec 08, 2011
 */
public class BuildVersionInfoTest extends SahiTestScript {
	
	@Test (groups="buildInfoTest")
	public void getBuildInfo(){
		HashMap<String, String> buildInfo = sahiTasks.getBuildVersion();
		
		Assert.assertTrue(buildInfo.get("version") != null, "Version validation");
		Assert.assertTrue(buildInfo.get("build.number") != null, "Build Number validation");
		Assert.assertTrue(buildInfo.get("gwt.version") != null, "GWT Version validation");
		Assert.assertTrue(buildInfo.get("smartgwt.version") != null, "Smart GWT Version validation");
		
		String version = buildInfo.get("version").replaceFirst("[^\\:]+\\:", "").trim();
		String buildNumber = buildInfo.get("build.number").replaceFirst("[^\\:]+\\:", "").trim();
		
		System.setProperty("rhq.build.version", version+" ("+buildNumber+")\n"+buildInfo.get("gwt.version")+" "+buildInfo.get("smartgwt.version"));
		
		Reporter.log("<BR><b>"+buildInfo.get("version")+"</b>");
		Reporter.log("<BR><b>"+buildInfo.get("build.number")+"</b>");
		Reporter.log("<BR><b>"+buildInfo.get("gwt.version")+"</b>");
		Reporter.log("<BR><b>"+buildInfo.get("smartgwt.version")+"</b><BR>");
	}
}