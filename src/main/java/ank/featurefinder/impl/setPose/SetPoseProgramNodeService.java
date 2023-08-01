package ank.featurefinder.impl.setPose;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;

import java.util.Locale;


public class SetPoseProgramNodeService implements SwingProgramNodeService<SetPoseProgramNodeContribution, SetPoseProgramNodeView> {

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
		return "Set Feature";
	}

	@Override
	public SetPoseProgramNodeView createView(ViewAPIProvider apiProvider) {
		return new SetPoseProgramNodeView();
	}

	@Override
	public SetPoseProgramNodeContribution createNode(
			ProgramAPIProvider apiProvider,
			SetPoseProgramNodeView view,
			DataModel model,
			CreationContext context) {
		return new SetPoseProgramNodeContribution(apiProvider, view);
	}
}
