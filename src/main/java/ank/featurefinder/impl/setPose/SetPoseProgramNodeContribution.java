package ank.featurefinder.impl.setPose;

import ank.featurefinder.impl.installation.FeatureFinderInstallationNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.program.ProgramModel;
import com.ur.urcap.api.domain.program.nodes.builtin.MoveNode;
import com.ur.urcap.api.domain.program.structure.TreeNode;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.util.Filter;
import com.ur.urcap.api.domain.value.PoseFactory;
import com.ur.urcap.api.domain.variable.Variable;
import com.ur.urcap.api.domain.variable.VariableFilterFactory;
import com.ur.urcap.api.domain.variable.VariableModel;

import java.io.BufferedReader;
// import java.io.BufferedReader;
// Used for reading from RealTime Client
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.io.DataOutputStream;


public class SetPoseProgramNodeContribution implements ProgramNodeContribution {

  private final ProgramAPI programAPI;
  private final ProgramModel programModel;
  private final SetPoseProgramNodeView view;
  private final PoseFactory poseFactory;
  private VariableModel variableModel;
  private VariableFilterFactory variableFilterFactory = new VariableFilterFactory();

  SetPoseProgramNodeContribution(
    ProgramAPIProvider apiProvider,
    SetPoseProgramNodeView view
  ) {
    poseFactory =
      apiProvider.getProgramAPI().getValueFactoryProvider().getPoseFactory();
    programAPI = apiProvider.getProgramAPI();
    programModel = apiProvider.getProgramAPI().getProgramModel();
    this.view = view;
    this.variableModel = apiProvider.getProgramAPI().getVariableModel();
  }

  @Override
  public void openView() {
    view.updateFeaturesList(getInstallationNode().getFeatureList());

    // create a filter that checks if the variable is a pose
    Filter<Variable> filter = new Filter<Variable>() {
      @Override
      public boolean accept(Variable variable) {
        return (variable.getType().equals(Variable.Type.GLOBAL));
      }
    };
    Collection<Variable> systemVariables = variableModel.get(filter);
    // print all variables
    String[] variableNames = new String[systemVariables.size()];
    int i = 0;
    for (Variable variable : systemVariables) {
      variableNames[i] = variable.getDisplayName();
      System.out.println(variable.getDisplayName());
      i++;
    }
    view.updateVariableList(variableNames);
  }

  @Override
  public void closeView() {
    // Intentionally left empty
  }

  @Override
  public String getTitle() {
    return "Use feature";
  }

  @Override
  public boolean isDefined() {
    return true;
  }

  @Override
  public void generateScript(ScriptWriter scriptWriter) {
    String featureName = view.getFeatureName();
    String variableName = view.getVariableName();
    // get the value of the variable and set it as the pose
  }

  private FeatureFinderInstallationNodeContribution getInstallationNode() {
    return programAPI.getInstallationNode(
      FeatureFinderInstallationNodeContribution.class
    );
  }

}
