package ac.soton.multisim.ui.policies;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;

import ac.soton.multisim.Component;
import ac.soton.multisim.Port;


/**
 * A command to set the bounds (location/size) of a <code>View</code>
 * 
 * @author melaasar
 */
public class AdjustSizeAndPortsCommand extends AbstractTransactionalCommand {

	private static final int PORT_HEIGHT = 10;
	private static final int PORT_VMARGIN = 8;
	private static final int PORT_VSPACE = PORT_HEIGHT + PORT_VMARGIN;
	private static final int MIN_HEIGHT = 20;
	private IAdaptable  adapter;
	private Point location;
	
	/**
	 * Creates a <code>AdjustSizeAndPortsCommand</code> for the given view adapter with a given location.
	 * 
     * @param editingDomain
     *            the editing domain through which model changes are made
	 * @param label The command label
	 * @param adapter An adapter to the <code>View</code>
	 * @param location The new location
	 */
	public AdjustSizeAndPortsCommand(TransactionalEditingDomain editingDomain, String label,IAdaptable adapter, Point location) {
        super(editingDomain, label, null);
		this.adapter = adapter;
		this.location = location;
	}

	protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info)
	    throws ExecutionException {

		if (adapter == null)
			return CommandResult.newErrorCommandResult("SetBoundsCommand: viewAdapter does not adapt to IView.class"); //$NON-NLS-1$
		
		View view  = (View)adapter.getAdapter(View.class);
		
		if (location != null) {
			ViewUtil.setStructuralFeatureValue(view,NotationPackage.eINSTANCE.getLocation_X(), Integer.valueOf(location.x));
			ViewUtil.setStructuralFeatureValue(view,NotationPackage.eINSTANCE.getLocation_Y(), Integer.valueOf(location.y));
		}
		
		Dimension size = calculateEastWestPortComponentSize(view);
		if (size != null) {
			
			ViewUtil.setStructuralFeatureValue(view,NotationPackage.eINSTANCE.getSize_Width(), Integer.valueOf(size.width));
			ViewUtil.setStructuralFeatureValue(view,NotationPackage.eINSTANCE.getSize_Height(), Integer.valueOf(size.height));
			
			int inputY = MIN_HEIGHT;
			int outputY = MIN_HEIGHT;
			
			for (Iterator it = view.getChildren().iterator(); it.hasNext(); ) {
				Object child = it.next();
				if (child instanceof View) {
					View childView = (View) child;
					EObject element = childView.getElement();
					if (element instanceof Port) {
						switch (((Port) element).getCausality()) {
						case INPUT: 
							ViewUtil.setStructuralFeatureValue(childView,NotationPackage.eINSTANCE.getLocation_X(), Integer.valueOf(-1));
							ViewUtil.setStructuralFeatureValue(childView,NotationPackage.eINSTANCE.getLocation_Y(), Integer.valueOf(inputY));
							inputY += PORT_VSPACE;
							break;
						case OUTPUT:
							ViewUtil.setStructuralFeatureValue(childView,NotationPackage.eINSTANCE.getLocation_X(), Integer.valueOf(500));	//XXX just big enough value to support resizing without automatic re-layout of the view
							ViewUtil.setStructuralFeatureValue(childView,NotationPackage.eINSTANCE.getLocation_Y(), Integer.valueOf(outputY));
							outputY += PORT_VSPACE;
						default:
						}
					}
				}
			}
		}
		
		return CommandResult.newOKCommandResult();
	}
    
    /**
     * Calculates component size of the view based on the east-west layout of the ports
     * i.e. input ports on one size, output ports on the other.
     * 
     * @param view component view
     * @return calculated component size
     */
    private Dimension calculateEastWestPortComponentSize(View view) {
    	Component component = (Component) view.getElement();
    	int numPortsVertical = Math.max(component.getInputs().size(), component.getOutputs().size());
    	
    	if (numPortsVertical == 0)
    		return null;	// use default size
    	
    	Integer width = (Integer) ViewUtil.getStructuralFeatureValue(view, NotationPackage.eINSTANCE.getSize_Width());
    	
		return new Dimension(width, MIN_HEIGHT + PORT_VSPACE * numPortsVertical);
	}

	public List getAffectedFiles() {
        if (adapter != null) {
            View view = (View) adapter.getAdapter(View.class);
            if (view != null) {
                return getWorkspaceFiles(view);
            }
        }
        return super.getAffectedFiles();
    }
}