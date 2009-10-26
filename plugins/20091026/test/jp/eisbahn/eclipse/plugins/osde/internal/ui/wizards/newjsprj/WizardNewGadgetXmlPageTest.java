/**
 * 
 */
package jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.newjsprj;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sega Shih-Chia Cheng (sccheng@gmail.com, shihchia@google.com)
 *
 */
public class WizardNewGadgetXmlPageTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link jp.eisbahn.eclipse.plugins.osde.internal.ui.wizards.newjsprj.WizardNewGadgetXmlPage#isValidEmail(java.lang.String)}.
	 */
	@Test
	public final void testIsValidEmail() {
		assertTrue(WizardNewGadgetXmlPage.isValidEmail("john@abc.com"));
		assertTrue(WizardNewGadgetXmlPage.isValidEmail("bob@www.co.jp"));
		assertTrue(WizardNewGadgetXmlPage.isValidEmail("jane.doe@abc.com.tw"));
		assertTrue(WizardNewGadgetXmlPage.isValidEmail("david-smith@abc.com"));
		assertTrue(WizardNewGadgetXmlPage.isValidEmail("abc-co@abc-corp.com"));
		
		assertFalse(WizardNewGadgetXmlPage.isValidEmail("abc"));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail("abc@"));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail("abc@as"));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail("abc@agf."));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail("abc@asfdas.afwef."));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail(".abc"));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail("...abc"));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail(".abc@"));
		assertFalse(WizardNewGadgetXmlPage.isValidEmail(".abc@.com.tw"));
	}

}
