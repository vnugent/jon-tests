package com.redhat.qe.jon.sahi.tests.plugins.eap6.standalone;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.redhat.qe.Assert;
import com.redhat.qe.jon.sahi.base.inventory.Configuration;
import com.redhat.qe.jon.sahi.base.inventory.Configuration.CurrentConfig;
import com.redhat.qe.jon.sahi.base.inventory.Inventory;
import com.redhat.qe.jon.sahi.base.inventory.Inventory.NewChildWizard;
import com.redhat.qe.jon.sahi.base.inventory.Resource;
/**
 * tests for infinispan subsystem
 * @author lzoubek
 *
 */
public class InfinispanTest extends AS7StandaloneTest {
	
	Resource cacheContainer;
	Resource infinispan;
	
	@BeforeClass()
	protected void setupAS7Plugin() {
		as7SahiTasks.importResource(server);
		infinispan = server.child("infinispan");
        cacheContainer = infinispan.child("cachecontainer");
    }
	@Test()
	public void addCacheContainer() {
		Inventory inventory = infinispan.inventory();
		NewChildWizard child = inventory.childResources().newChild("Cache Container");
		child.getEditor().setText("resourceName",cacheContainer.getName());
		child.next();
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=infinispan", "cache-container", cacheContainer.getName(),true);
		cacheContainer.assertExists(true);
	}
	
	@Test(dependsOnMethods={"addCacheContainer"})
	public void configureCacheContainer() {
		Configuration configuration = cacheContainer.configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().checkBox(1, false);
		config.getEditor().setText("jndi-name", "jboss:/cachecontainer");
		config.save();
		configuration.history().failOnFailure();
		String jndiName = mgmtClient.readAttribute("/subsystem=infinispan/cache-container="+cacheContainer.getName(), "jndi-name").get("result").asString();
		Assert.assertTrue("jboss:/cachecontainer".equals(jndiName), "JNDI Name configuration for cache-container was updated");
	}
	@DataProvider
	public Object[][] cacheTypeDataProvider() {
		CacheType[] types = new CacheType[] {
				new CacheType("default","distributed-cache","defaultcache"),
				new CacheType("Distributed Cache","distributed-cache","distcache"),
				new CacheType("Invalidation Cache","invalidation-cache","invalidcache"),
				new CacheType("Replicated Cache","replicated-cache","replcache")
		};
		Object[][] output = new Object[types.length][];
		for (int i=0;i<types.length;i++) {
			output[i] = new Object[] {types[i]};
		}		
		return output;
	}
	
	@Test(dataProvider="cacheTypeDataProvider", dependsOnMethods={"addCacheContainer"})
	public void addCache(CacheType cacheType) {
		Inventory inventory = cacheContainer.inventory();
		NewChildWizard child = inventory.childResources().newChild("Cache");
		child.getEditor().setText("resourceName",cacheType.resourceName);
		child.getEditor().selectCombo(0, cacheType.name);
		child.next();
		// required until https://bugzilla.redhat.com/show_bug.cgi?id=839320 is fixed
		child.getEditor().checkRadio("mode[0]");
		if (cacheType.name=="default") {
			// for default config template we have to select cache type
			child.getEditor().checkRadio(cacheType.type);
		}
		child.finish();
		inventory.childHistory().assertLastResourceChange(true);
		mgmtClient.assertResourcePresence("/subsystem=infinispan/cache-container="+cacheContainer.getName(), cacheType.type, cacheType.resourceName,true);
		cacheContainer.child(cacheType.resourceName).assertExists(true);
	}
	@Test(dataProvider="cacheTypeDataProvider",dependsOnMethods="addCache")
	public void configureCache(CacheType cacheType) {
		Configuration configuration  = cacheContainer.child(cacheType.resourceName).configuration();
		CurrentConfig config = configuration.current();
		config.getEditor().checkRadio("mode[1]");
		config.save();
		configuration.history().failOnFailure();
		String mode = mgmtClient.readAttribute("/subsystem=infinispan/cache-container="+cacheContainer.getName()+"/"+cacheType.type+"="+cacheType.resourceName, "mode").get("result").asString();
		Assert.assertTrue("ASYNC".equals(mode), "Cache configuration has been updated");
	}
	
	@Test(dataProvider="cacheTypeDataProvider",dependsOnMethods="configureCache")
	public void removeCache(CacheType cacheType) {
		cacheContainer.child(cacheType.resourceName).delete();
		mgmtClient.assertResourcePresence("/subsystem=infinispan/cache-container="+cacheContainer.getName(), cacheType.type, cacheType.resourceName,false);
		cacheContainer.child(cacheType.resourceName).assertExists(false);
	}
	
	@Test(dependsOnMethods={"configureCacheContainer","removeCache"})
	public void removeCacheContainer() {
		cacheContainer.delete();
		mgmtClient.assertResourcePresence("/subsystem=infinispan", "cache-container", cacheContainer.getName(),false);
		cacheContainer.assertExists(false);
	}
	public static class CacheType{
		final String name,type,resourceName;
		public CacheType(String name, String type, String resourceName) {
			this.name = name;
			this.type=type;
			this.resourceName = resourceName;
		}
		@Override
		public String toString() {
			return "name="+name+" type="+type;
		}
	}
}