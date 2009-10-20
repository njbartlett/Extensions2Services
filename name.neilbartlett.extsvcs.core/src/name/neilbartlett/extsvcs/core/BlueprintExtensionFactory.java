package name.neilbartlett.extsvcs.core;

import java.util.Dictionary;

import name.neilbartlett.extsvcs.core.internal.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IExecutableExtensionFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.osgi.util.tracker.ServiceTracker;

public class BlueprintExtensionFactory implements IExecutableExtension,
		IExecutableExtensionFactory {

	private static final String PROP_BLUEPRINT_CONTAINER_SYMBOLICNAME = "osgi.blueprint.container.symbolicname";
	private static final String PARAMETER_BEAN_ID = "beanId";
	private static final long BLUEPRINT_CONTAINER_WAIT_TIMEOUT = 5000;
	
	private IConfigurationElement config;
	private String propertyName;
	private String beanId;

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
		this.propertyName = propertyName;
		
		if(data == null)
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Missing bean ID parameter.", null));
		
		if(data instanceof String) {
			beanId = (String) data;
		} else if(data instanceof Dictionary<?,?>) {
			@SuppressWarnings("unchecked") Dictionary<String, String> dict = (Dictionary<String, String>) data;
			beanId = dict.get(PARAMETER_BEAN_ID);
		}
	}

	public Object create() throws CoreException {
		Object result;
		
		// Find the contributing bundle
		String bundleSymbName = config.getContributor().getName();
		Bundle bundle = Activator.findBundleBySymbolicName(bundleSymbName);
		if(bundle == null)
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Error finding bundle matching contributor name.", null));
		
		try {
			// 1. Start the bundle, if not already started
			bundle.start(Bundle.START_TRANSIENT);
			
			// 2. Obtain the BlueprintContainer for the bundle, if one exists. NB the BlueprintContainer is created asynchronously
			//    after bundle start, so we must wait a little for it to appear.
			// TODO: the timeout period is wasteful if the bundle does not declare blueprint configuration; find a way to fail
			//       fast in these cases (e.g. listen for BlueprintEvents?)
			Filter filter = FrameworkUtil.createFilter(String.format("(&(%s=%s)(%s=%s))",
					Constants.OBJECTCLASS, BlueprintContainer.class.getName(),
					PROP_BLUEPRINT_CONTAINER_SYMBOLICNAME, bundleSymbName));
			ServiceTracker tracker = new ServiceTracker(bundle.getBundleContext(), filter, null);
			tracker.open();
			BlueprintContainer container = (BlueprintContainer) tracker.waitForService(BLUEPRINT_CONTAINER_WAIT_TIMEOUT);
			if(container == null)
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Unable to get a Blueprint container for bundle " + bundleSymbName, null));
			
			// 3. Check that the specified bean exists, is a standard bean and has prototype scope
			ComponentMetadata metadata = container.getComponentMetadata(beanId);
			if(!(metadata instanceof BeanMetadata))
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Specified bean ID does not correspond to a standard bean.", null));
			if(!BeanMetadata.SCOPE_PROTOTYPE.equals(((BeanMetadata) metadata).getScope()))
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Specified bean does not have prototype scope.", null));
			
			// 4. Request an instance of the bean from the container
			Object object = container.getComponentInstance(beanId);
			
			// 5. If object is an instance of IExecutableExtension, supply it the configuration element (but not he adapter data, which we
			//    have just consumed
			if(object instanceof IExecutableExtension) {
				((IExecutableExtension) object).setInitializationData(config, propertyName, null);
			}
			
			// 6. If the object is an instance of IExecutableExtensionFactory, call the factory method and return the result.
			if(object instanceof IExecutableExtensionFactory) {
				result = ((IExecutableExtensionFactory) object).create();
			} else {
				result = object;
			}
		} catch (InvalidSyntaxException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Error creating Blueprint container service filter.", e));
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Interrupted waiting for Blueprint container service.", e));
		} catch (NoSuchComponentException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Specified bean ID does not correspond to any bean.", e));
		} catch (BundleException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, "Failed to start contributing bundle.", e));
		}
		
		return result;
	}
}
