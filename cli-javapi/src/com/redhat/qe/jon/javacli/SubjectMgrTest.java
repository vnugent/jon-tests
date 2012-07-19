package com.redhat.qe.jon.javacli;


import org.rhq.core.domain.auth.Subject;
import org.rhq.core.domain.criteria.SubjectCriteria;
import org.rhq.core.domain.util.PageList;
import org.rhq.enterprise.clientapi.RemoteClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.google.inject.Inject;


@Test(groups={"unit"})
@Guice(modules = RemoteClientGuiceModule.class)
public class SubjectMgrTest {
	
	@Inject 
	private RemoteClient remoteClient;
	
	public void foo() throws Exception {
		
				
		SubjectCriteria subjectCriteria = new SubjectCriteria();
		subjectCriteria.addFilterName("rhqadmin");
		PageList<Subject> pList = remoteClient.getSubjectManager().findSubjectsByCriteria(remoteClient.getSubject(), subjectCriteria);
		
		Assert.assertTrue(pList.getTotalSize()==1, "expecting 1 rqhadmin user");
		
		
		
	}
	
	@AfterClass
	public void cleanup() {
		remoteClient.logout();
	}

}
