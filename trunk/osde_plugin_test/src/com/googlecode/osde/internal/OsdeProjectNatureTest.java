package com.googlecode.osde.internal;

import com.googlecode.osde.internal.builders.GadgetBuilder;
import com.googlecode.osde.test.EclipseTestCase;
import com.googlecode.osde.test.TestProject;
import junit.framework.Assert;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.osde.internal.OsdeProjectNature;

/**
 *	Test OsdeProject Nature
 */
public class OsdeProjectNatureTest extends EclipseTestCase {

	TestProject project;
	OsdeProjectNature nature;

    @Before
    public void setUp() throws Exception {
    	project = new TestProject();
    	nature = new OsdeProjectNature();
    	nature.setProject(project.getProject());
    }

    @After
    public void tearDown() throws Exception {
    	try {
			project.dispose();
		} catch (CoreException e) {
			e.printStackTrace();
		}
    }

	private boolean hasOsdeNature(IProject project) {
		try {
			for (String natureId : project.getDescription().getNatureIds()) {
				if(OsdeProjectNature.ID.equals(natureId)){
					return true;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean hasOsdeBuilder(IProject project) {
		try {
			for (ICommand command : project.getDescription().getBuildSpec()) {
				if (GadgetBuilder.ID.equals(command.getBuilderName())) {
					return true;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Test
	public void testConfigureAndDeconfigure() {

		// project should not have osde project nature and builder before adding.
		Assert.assertFalse(hasOsdeNature(project.getProject()));
		Assert.assertFalse(hasOsdeBuilder(project.getProject()));

		try {
			nature.configure();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// project should have osde project nature after adding
		Assert.assertTrue(hasOsdeNature(project.getProject()));
		Assert.assertTrue(hasOsdeBuilder(project.getProject()));

		try {
			nature.deconfigure();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		// project should not have osde project nature after removing.
		Assert.assertFalse(hasOsdeNature(project.getProject()));
		Assert.assertFalse(hasOsdeBuilder(project.getProject()));
	}

    @Test
    public void testGetProject() {
		nature.setProject(project.getProject());
		Assert.assertEquals(project.getProject(), nature.getProject());
    }

    @Test
    public void testSetProject() {
		nature.setProject(project.getProject());
		Assert.assertEquals(project.getProject(), nature.getProject());
    }

}