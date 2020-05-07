/**
 * 
 */
package ac.soton.multisim.master;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.junit.Test;

import ac.soton.multisim.VariableType;
import ac.soton.multisim.common.AbstractEventBProjectTest;
import ac.soton.multisim.ui.providers.ColumnProvider;
import ac.soton.multisim.util.SimulationUtil;

/**
 * @author vitaly
 *
 */
public class MasterTest extends AbstractEventBProjectTest {
	
	@Override
	protected String getProjectName() {
		return "test";
	}
	
	@Test
	public void test() {
		String title = "title";
		int bound = 5;
		ColumnLabelProvider labelProvider = new ColumnLabelProvider();
		
		ColumnProvider provider = new ColumnProvider(title, bound, labelProvider);
		
	}

}
