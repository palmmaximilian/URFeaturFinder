package ank.featurefinder.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import ank.featurefinder.impl.installation.FeatureFinderInstallationNodeService;
import ank.featurefinder.impl.probeFeature.ProbeFeatureProgramNodeService;
/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {

		System.out.println("Activator says Hello World!");
		bundleContext.registerService(SwingInstallationNodeService.class, new FeatureFinderInstallationNodeService(), null);
		bundleContext.registerService(SwingProgramNodeService.class, new ProbeFeatureProgramNodeService(), null);

	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Goodbye World!");
	}
}

