package ank.featurefinder.impl.probeNode;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.configuration.debugging.ProgramDebuggingSupport;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;

import java.util.Locale;

public class ProbeNodeProgramNodeService
		implements SwingProgramNodeService<ProbeNodeNodeContribution, ProbeNodeProgramNodeView> {

	@Override
	public String getId() {
		return "setPoseNode";
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		configuration.setDeprecated(false);
		configuration.setChildrenAllowed(true);
		configuration.setUserInsertable(true);

		ProgramDebuggingSupport programDebuggingSupport = configuration.getProgramDebuggingSupport();
		programDebuggingSupport.setAllowBreakpointOnChildNodesInSubtree(true);
		programDebuggingSupport.setAllowStartFromChildNodesInSubtree(true);
	}

	@Override
	public String getTitle(Locale locale) {
		return "Probe Feature";
	}

	@Override
	public ProbeNodeProgramNodeView createView(ViewAPIProvider apiProvider) {
		SystemAPI systemAPI = apiProvider.getSystemAPI();
		return new ProbeNodeProgramNodeView();
	}

	@Override
	public ProbeNodeNodeContribution createNode(ProgramAPIProvider apiProvider, ProbeNodeProgramNodeView view,
			DataModel model, CreationContext context) {
		return new ProbeNodeNodeContribution(apiProvider, view, model);
	}
}
