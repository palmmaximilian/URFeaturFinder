package ank.featurefinder.impl.setZ;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.data.DataModel;
import java.util.Locale;

public class SetZProgramNodeService
  implements
    SwingProgramNodeService<SetZProgramNodeContribution, SetZrogramNodeView> {

  @Override
  public String getId() {
    return "SetZNode";
  }

  @Override
  public void configureContribution(ContributionConfiguration configuration) {
    configuration.setChildrenAllowed(false);
  }

  @Override
  public String getTitle(Locale locale) {
    return "Set Z";
  }

  @Override
  public SetZrogramNodeView createView(ViewAPIProvider apiProvider) {
    return new SetZrogramNodeView();
  }

  @Override
  public SetZProgramNodeContribution createNode(
    ProgramAPIProvider apiProvider,
    SetZrogramNodeView view,
    DataModel model,
    CreationContext context
  ) {
    return new SetZProgramNodeContribution(apiProvider, view,model);
  }
}
