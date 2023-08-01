package ank.featurefinder.impl.probeFeature;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;

import java.util.Locale;


public class ProbeFeatureProgramNodeService implements SwingProgramNodeService<ProbeFeatureProgramNodeContribution, ProbeFeatureProgramNodeView> {

	@Override
	public String getId() {
		return "SetFeatureNode";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		configuration.setChildrenAllowed(false);
	}

	@Override
	public String getTitle(Locale locale) {
		return "Probe Feature";
	}

	@Override
	public ProbeFeatureProgramNodeView createView(ViewAPIProvider apiProvider) {
		return new ProbeFeatureProgramNodeView();
	}

	@Override
	public ProbeFeatureProgramNodeContribution createNode(
			ProgramAPIProvider apiProvider,
			ProbeFeatureProgramNodeView view,
			DataModel model,
			CreationContext context) {
		return new ProbeFeatureProgramNodeContribution(apiProvider, view, model);
	}
}
