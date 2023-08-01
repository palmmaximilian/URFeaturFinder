package ank.featurefinder.impl.installation;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

import java.util.Locale;

public class FeatureFinderInstallationNodeService implements SwingInstallationNodeService<FeatureFinderInstallationNodeContribution, FeatureFinderInstallationNodeView> {
	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		// Intentionally left empty
	}

	@Override
	public String getTitle(Locale locale) {
		return "Feature Finder";
	}

	@Override
	public FeatureFinderInstallationNodeView createView(ViewAPIProvider apiProvider) {
		return new FeatureFinderInstallationNodeView();
	}

	@Override
	public FeatureFinderInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider, FeatureFinderInstallationNodeView view, DataModel model, CreationContext context) {
		return new FeatureFinderInstallationNodeContribution(apiProvider, model, view);
	}
}
