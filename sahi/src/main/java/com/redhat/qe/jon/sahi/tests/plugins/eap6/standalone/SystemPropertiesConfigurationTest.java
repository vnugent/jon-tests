package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.ConfigEntry;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * system properties of AS7 resource - add/edit/delete 
 * @author lzoubek
 *
 */
public class SystemPropertiesConfigurationTest extends AS7StandaloneTest {
	
	private static final String addedPropName="prop";
	private static final String addedPropValue="value";
	private static final String editedPropValue="value2";
	
	@BeforeClass()
    protected void setupEapPlugin() {               
        as7SahiTasks.importResource(server);
    }
	
	private void addSystemProperty(Resource resource) {
		Configuration config = resource.configuration();
    	CurrentConfig current = config.current();
    	ConfigEntry entry = current.newEntry(0);
    	entry.setField("name", addedPropName);
    	entry.setField("value", addedPropValue);
    	entry.OK();
        current.save();        
        config.history().failOnFailure();
	}
	
	@Test()
    public void addPropertyTest() {
    	addSystemProperty(server);
        Assert.assertTrue(readPropertyValue(addedPropName).equals(addedPropValue),"System property has correct value");
    }
	@Test(groups={"blockedByBug-708332"},dependsOnMethods="addPropertyTest")
	public void editPropertyTest() {
		Configuration config = server.configuration();
    	CurrentConfig current = config.current();
		ConfigEntry entry = current.getEntry(addedPropName);
		entry.setField("value", editedPropValue);
		entry.OK();
		current.save();		
        config.history().failOnFailure();
        Assert.assertTrue(readPropertyValue(addedPropName).equals(editedPropValue),"System property has correct value");
	}
	@Test(dependsOnMethods="editPropertyTest")
	public void deletePropertyTest() {
		Configuration config = server.configuration();
    	CurrentConfig current = config.current();
		current.removeEntry(addedPropName);
		current.save();		
        config.history().failOnFailure();
		mgmtClient.assertResourcePresence("/", "system-property", addedPropName, false);
	}
	

	
	private String readPropertyValue(String name) {    	
    	return mgmtClient.readAttribute("/system-property="+name, "value").get("result").asString();
    }
	
}