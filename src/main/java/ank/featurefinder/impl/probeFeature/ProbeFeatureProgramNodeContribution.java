package ank.featurefinder.impl.probeFeature;

import ank.featurefinder.impl.ProbeFeatureClass;
import ank.featurefinder.impl.installation.FeatureFinderInstallationNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import java.awt.event.ActionEvent;

public class ProbeFeatureProgramNodeContribution implements ProgramNodeContribution {

  private final ProgramAPI programAPI;
  private final ProbeFeatureProgramNodeView view;
  private UndoRedoManager UndoRedoManager;
  private DataModel model;

  ProbeFeatureProgramNodeContribution(ProgramAPIProvider apiProvider, ProbeFeatureProgramNodeView view, DataModel model) {
    programAPI = apiProvider.getProgramAPI();
    this.view = view;
    this.UndoRedoManager = apiProvider.getProgramAPI().getUndoRedoManager();
    this.model = model;
  }

  @Override
  public void openView() {
    int lastFeature = model.get("lastFeature", 0);
    view.updateFeaturesList(getInstallationNode().getFeatureList());
    view.setFeatureIndex(lastFeature);
  }

  @Override
  public void closeView() {
    // Intentionally left empty
  }

  @Override
  public String getTitle() {
    String featureName = model.get("Feature", "None");
    if (featureName.equals("None")) {
      return "Probe Feature";
    }
    return ("Probe: " + featureName);
  }

  @Override
  public boolean isDefined() {
    String featureName = model.get("Feature", "None");

    if(!getInstallationNode().isLicenseValid())
    {
      return false;
    }

    if (featureName.equals("None")) {
      return false;
    }

    int lastFeature = model.get("lastFeature", 0);
    if (lastFeature > 0) {
      ProbeFeatureClass probeFeatureObject = getInstallationNode().getProbeFeatureObject(model.get("lastFeature", 0));
      if (!probeFeatureObject.isDefined()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void generateScript(ScriptWriter scriptWriter) {
    String featureName = model.get("Feature", "None");
    if (featureName.equals("None")) {
      return;
    }
    ProbeFeatureClass probeFeatureObject = getInstallationNode().getProbeFeatureObject(model.get("lastFeature", 0));
    scriptWriter=probeFeatureObject.generateURScriptCommand(scriptWriter, featureName);

    // scriptWriter.appendLine("movej(" + probeFeatureObject.getZUpPoseString() + ")");
    // scriptWriter.appendLine("sleep(0.5)");
    // scriptWriter.appendLine("starting_pos=get_actual_tcp_pose()");
    // scriptWriter.appendLine(probeFeatureObject.generateZForceCommand());
    // scriptWriter.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    // scriptWriter.appendLine("sleep(0.01)");
    // scriptWriter.appendLine("end");
    // scriptWriter.appendLine("end_force_mode()");
    // scriptWriter.appendLine("stopl(20)");
    // scriptWriter.appendLine("ZProbePoint = get_actual_tcp_pose()");
    // scriptWriter.appendLine("movej(" + probeFeatureObject.getZUpPoseString() + ")");

    // scriptWriter.appendLine("movel(" + probeFeatureObject.getXUpPoseString() + ")");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getXDownPoseString() + ")");
    // scriptWriter.appendLine("sleep(0.5)");
    // scriptWriter.appendLine("starting_pos=get_actual_tcp_pose()");
    // scriptWriter.appendLine(probeFeatureObject.generateXForceCommand());
    // scriptWriter.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    // scriptWriter.appendLine("sleep(0.01)");
    // scriptWriter.appendLine("end");
    // scriptWriter.appendLine("end_force_mode()");
    // scriptWriter.appendLine("stopl(20)");
    // scriptWriter.appendLine("XProbePoint = get_actual_tcp_pose()");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getXDownPoseString() + ")");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getXUpPoseString() + ")");

    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY1UpPoseString() + ")");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY1DownPoseString() + ")");
    // scriptWriter.appendLine("sleep(0.5)");
    // scriptWriter.appendLine("starting_pos=get_actual_tcp_pose()");
    // scriptWriter.appendLine(probeFeatureObject.generateYForceCommand());
    // scriptWriter.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    // scriptWriter.appendLine("sleep(0.01)");
    // scriptWriter.appendLine("end");
    // scriptWriter.appendLine("end_force_mode()");
    // scriptWriter.appendLine("stopl(20)");
    // scriptWriter.appendLine("Y1ProbePoint = get_actual_tcp_pose()");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY1DownPoseString() + ")");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY1UpPoseString() + ")");

    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY2UpPoseString() + ")");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY2DownPoseString() + ")");
    // scriptWriter.appendLine("sleep(0.5)");
    // scriptWriter.appendLine("starting_pos=get_actual_tcp_pose()");
    // scriptWriter.appendLine(probeFeatureObject.generateYForceCommand());
    // scriptWriter.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    // scriptWriter.appendLine("sleep(0.01)");
    // scriptWriter.appendLine("end");
    // scriptWriter.appendLine("end_force_mode()");
    // scriptWriter.appendLine("stopl(20)");
    // scriptWriter.appendLine("Y2ProbePoint = get_actual_tcp_pose()");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY2DownPoseString() + ")");
    // scriptWriter.appendLine("movel(" + probeFeatureObject.getY2UpPoseString() + ")");

    // scriptWriter.appendLine(" mY = (Y2ProbePoint[1] - Y1ProbePoint[1]) / (Y2ProbePoint[0] - Y1ProbePoint[0])");
    // scriptWriter.appendLine(" mX = -(1 / mY)");
    // scriptWriter.appendLine(" bX = XProbePoint[1] - (mX * XProbePoint[0])");
    // scriptWriter.appendLine(" bY = Y1ProbePoint[1] - (mY * Y1ProbePoint[0])");
    // scriptWriter.appendLine(" x0 = (bX - bY) / (mY - mX)");
    // scriptWriter.appendLine(" y0 = mY * x0 + bY");
    // scriptWriter.appendLine(" z0 = ZProbePoint[2]");
    // scriptWriter.appendLine(" rz = atan(mY/1)");
    // scriptWriter.appendLine(featureName + " = p[x0, y0, z0, 0, 0, rz]");
  }

  private FeatureFinderInstallationNodeContribution getInstallationNode() {
    return programAPI.getInstallationNode(FeatureFinderInstallationNodeContribution.class);
  }

  public void actionPerformed(ActionEvent e) {
    final String featureName = view.getFeatureName();
    UndoRedoManager.recordChanges(
      new UndoableChanges() {
        @Override
        public void executeChanges() {
          model.set("Feature", featureName);
          model.set("lastFeature", view.getFeatureIndex());
        }
      }
    );
  }
}
