package ank.featurefinder.impl.installation;

import ank.featurefinder.impl.communicator.ScriptCommand;
import ank.featurefinder.impl.communicator.ScriptExporter;
import ank.featurefinder.impl.communicator.ScriptSender;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.UserInterfaceAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.feature.FeatureContributionModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.RobotPositionCallback2;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.ur.urcap.api.domain.value.Pose;
import com.ur.urcap.api.domain.value.PoseFactory;
import com.ur.urcap.api.domain.value.robotposition.PositionParameters;
import com.ur.urcap.api.domain.value.simple.Angle;
import com.ur.urcap.api.domain.value.simple.Length;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;

public class FeatureFinderInstallationNodeContribution implements InstallationNodeContribution {


  private String keyboardString = "";
  private final DataModel model;
  private final FeatureFinderInstallationNodeView view;
  private FeatureContributionModel featureContributionModel;
  private final InputValidationFactory validatorFactory;
  private final KeyboardInputFactory keyboardInputFactory;
  private PoseFactory poseFactory;
  private Pose zeroPose;
  private UserInterfaceAPI uiapi;

  FeatureFinderInstallationNodeContribution(InstallationAPIProvider apiProvider, DataModel model, FeatureFinderInstallationNodeView view) {
    this.uiapi = apiProvider.getUserInterfaceAPI();
    this.model = model;
    this.view = view;
    this.keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
    this.validatorFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getInputValidationFactory();
    featureContributionModel = apiProvider.getInstallationAPI().getFeatureContributionModel();

    this.poseFactory = apiProvider.getInstallationAPI().getValueFactoryProvider().getPoseFactory();
  }

  @Override
  public void openView() {
    // create an empty list of Features
    String[] FeatureList = model.get("FeatureList", (String[]) null);
    view.updateFeatureList(FeatureList);
    if (FeatureList == null) {
      view.disableButtons();
    } else {
      view.setFeatureListIndex(0);
      view.enableButtons();
    }
  }

  @Override
  public void closeView() {
    // Intentionally left empty
  }

  @Override
  public void generateScript(ScriptWriter writer) {}

  void listItemClicked(ListSelectionEvent e) {
    int index = view.featureFrame.getSelectedIndex();
    // String item = view.featureFrame.getSelectedValue();
    // System.out.println("Item at index " + index + " selected: " + item);

    // check that mouse button was released
    if (e.getValueIsAdjusting()) {
      return;
    }

    if (index != -1) {
      view.enableButtons();
      String[] PoseFeatureList = model.get("ProbeFeatureList", (String[]) null);
      view.updateProbeFeatureLables(new ProbeFeatureClass(PoseFeatureList[index]));
    }
  }

  void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof JButton) {
      JButton button = (JButton) e.getSource();
      String name = button.getName();
      if (name.equals("addButton")) {
        addButton();
      } else if (name.equals("renameButton")) {
        renameButton();
      } else if (name.equals("deleteButton")) {
        deleteButton();
      } else if (name.equals("ZUpButton")) {
        // System.out.println("ZUpButton pressed");
        updatePose(0);
      } else if (name.equals("XUpButton")) {
        // System.out.println("XUpButton pressed");
        updatePose(1);
      } else if (name.equals("XDownButton")) {
        // System.out.println("XUpButton pressed");
        updatePose(2);
      } else if (name.equals("Y1UpButton")) {
        // System.out.println("Y1UpButton pressed");
        updatePose(3);
      } else if (name.equals("Y1DownButton")) {
        // System.out.println("Y1DownButton pressed");
        updatePose(4);
      } else if (name.equals("Y2UpButton")) {
        // System.out.println("Y2UpButton pressed");
        updatePose(5);
      } else if (name.equals("Y2DownButton")) {
        // System.out.println("Y2DownButton pressed");
        updatePose(6);
      } else if (name.equals("ProbeFeatureButton")) {
        ProbeFeature();
        // System.out.println("ProbeFeatureButton pressed");
      }
    } else if (e.getSource() instanceof JComboBox) {
      JComboBox<?> box = (JComboBox<?>) e.getSource();
      String name = box.getName();
      if (name.equals("ZDirectionBox")) {
        // System.out.println("ZDirection changed");
        updateDirection(0, box.getSelectedIndex());
      } else if (name.equals("XDirectionBox")) {
        // System.out.println("XDirection changed");
        updateDirection(1, box.getSelectedIndex());
      } else if (name.equals("YDirectionBox")) {
        // System.out.println("YDirection changed");
        updateDirection(2, box.getSelectedIndex());
      }
    }
  }

  private void updateDirection(final int id, int index) {
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    if (ProbeFeatureList == null) {
      System.out.println("ProbeFeatureList is still empty, something went wrong");
      return;
    }

    // get the selected feature
    final int selectedFeature = view.getFeatureListIndex();

    final ProbeFeatureClass currentProbeFeature = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);
    switch (id) {
      case 0:
        currentProbeFeature.setZDirection(index);
        break;
      case 1:
        currentProbeFeature.setXDirection(index);
        break;
      case 2:
        currentProbeFeature.setYDirection(index);
        break;
      default:
        System.out.println("Something went wrong");
        break;
    }

    String[] newProbeFeatureList = new String[ProbeFeatureList.length];
    for (int i = 0; i < ProbeFeatureList.length; i++) {
      if (i != selectedFeature) {
        newProbeFeatureList[i] = ProbeFeatureList[i];
      } else {
        newProbeFeatureList[i] = currentProbeFeature.toString();
      }
    }
    model.set("ProbeFeatureList", newProbeFeatureList);
    view.updateProbeFeatureLables(currentProbeFeature);

    // System.out.println(currentProbeFeature.generateXForceCommand());
    // System.out.println(currentProbeFeature.generateYForceCommand());
    // System.out.println(currentProbeFeature.generateZForceCommand());
  }

  private void updatePose(final int id) {
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    if (ProbeFeatureList == null) {
      System.out.println("ProbeFeatureList is still empty, something went wrong");
      return;
    }

    // get the selected feature
    final int selectedFeature = view.getFeatureListIndex();

    final ProbeFeatureClass currentProbeFeature = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);
    uiapi
      .getUserInteraction()
      .getUserDefinedRobotPosition(
        new RobotPositionCallback2() {
          @Override
          public void onOk(PositionParameters positionParameters) {
            Pose pose = positionParameters.getPose();
            switch (id) {
              case 0:
                currentProbeFeature.setZUpPose(pose);
                break;
              case 1:
                currentProbeFeature.setXUpPose(pose);
                break;
              case 2:
                currentProbeFeature.setXDownPose(pose);
                break;
              case 3:
                currentProbeFeature.setY1UpPose(pose);
                break;
              case 4:
                currentProbeFeature.setY1DownPose(pose);
                break;
              case 5:
                currentProbeFeature.setY2UpPose(pose);
                break;
              case 6:
                currentProbeFeature.setY2DownPose(pose);
                break;
              default:
                System.out.println("Something went wrong");
                break;
            }
            String[] newProbeFeatureList = new String[ProbeFeatureList.length];
            for (int i = 0; i < ProbeFeatureList.length; i++) {
              if (i != selectedFeature) {
                newProbeFeatureList[i] = ProbeFeatureList[i];
              } else {
                newProbeFeatureList[i] = currentProbeFeature.toString();
              }
            }
            model.set("ProbeFeatureList", newProbeFeatureList);
            view.updateProbeFeatureLables(currentProbeFeature);
            // System.out.println(newProbeFeatureList.toString());
          }
        }
      );
  }

  private void addEmptyProbeFeature() {
    String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    int ListLength = 0;
    if (ProbeFeatureList != (null)) {
      ListLength = ProbeFeatureList.length;
    }
    ProbeFeatureClass newProbeFeature = new ProbeFeatureClass();
    String[] newProbeFeatureList = new String[ListLength + 1];
    if (ProbeFeatureList != (null)) {
      for (int i = 0; i < ListLength; i++) {
        newProbeFeatureList[i] = ProbeFeatureList[i];
      }
    }
    newProbeFeatureList[ListLength] = newProbeFeature.toString();
    model.set("ProbeFeatureList", newProbeFeatureList);
  }

  private void removeProbeFeature(int index) {
    String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    int ListLength = 0;
    if (ProbeFeatureList != (null)) {
      ListLength = ProbeFeatureList.length;
    }
    String[] newProbeFeatureList = new String[ListLength - 1];
    for (int i = 0; i < ListLength; i++) {
      if (i < index) {
        newProbeFeatureList[i] = ProbeFeatureList[i];
      } else if (i > index) {
        newProbeFeatureList[i - 1] = ProbeFeatureList[i];
      }
    }
    model.set("ProbeFeatureList", newProbeFeatureList);
  }

  void addButton() {
    String[] FeatureList = model.get("FeatureList", (String[]) null);
    int ListLength = 0;
    if (FeatureList != (null)) {
      ListLength = FeatureList.length;
    }
    String newFeature = "Feature_" + (ListLength + 1);
    String[] newFeatureList = new String[ListLength + 1];
    if (FeatureList != (null)) {
      for (int i = 0; i < ListLength; i++) {
        newFeatureList[i] = FeatureList[i];
      }
    }
    newFeatureList[ListLength] = newFeature;
    try {
      zeroPose = poseFactory.createPose(0, 0, 0, 180, 0, 0, Length.Unit.M, Angle.Unit.DEG);
      addEmptyProbeFeature();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return;
    }

    model.set("FeatureList", newFeatureList);
    featureContributionModel.addFeature(newFeature, newFeature, zeroPose);
    view.updateFeatureList(newFeatureList);
    view.setFeatureListIndex(ListLength);
  }

  void renameButton() {
    int index = view.getFeatureListIndex();

    String newName = keyboardString;
    String[] FeatureList = model.get("FeatureList", (String[]) null);
    int ListLength = 0;
    if (FeatureList != (null)) {
      ListLength = FeatureList.length;
    }
    // verify that the new name is not already in use
    for (int i = 0; i < ListLength; i++) {
      if (i != index) {
        if (FeatureList[i].equals(newName)) {
          System.out.println("Feature name already in use");
          return;
        }
      }
    }

    String[] newFeatureList = new String[ListLength];
    for (int i = 0; i < ListLength; i++) {
      if (i != index) {
        newFeatureList[i] = FeatureList[i];
      } else {
        newFeatureList[i] = newName;
      }
    }

    try {
      String featureName = view.getFeatureListValue();
      Pose oldFeature = featureContributionModel.getFeature(featureName).getPose();
      featureContributionModel.removeFeature(featureName);
      featureContributionModel.addFeature(newName, newName, oldFeature);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return;
    }

    model.set("FeatureList", newFeatureList);
    view.updateFeatureList(newFeatureList);
    view.disableButtons();
  }

  void deleteButton() {
    int index = view.getFeatureListIndex();
    String featureName = view.getFeatureListValue();
    String[] FeatureList = model.get("FeatureList", (String[]) null);
    int ListLength = 0;
    if (FeatureList != (null)) {
      ListLength = FeatureList.length;
    } else {
      System.out.println("FeatureList is empty, something went wrong");
      return;
    }
    String[] newFeatureList = new String[ListLength - 1];
    for (int i = 0; i < ListLength; i++) {
      if (i < index) {
        newFeatureList[i] = FeatureList[i];
      } else if (i > index) {
        newFeatureList[i - 1] = FeatureList[i];
      }
    }

    try {
      featureContributionModel.removeFeature(featureName);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return;
    }

    model.set("FeatureList", newFeatureList);
    removeProbeFeature(index);
    view.updateFeatureList(newFeatureList);
    if (index == 0) {
      view.setFeatureListIndex(index);
    } else {
      view.setFeatureListIndex(index - 1);
    }
    if (ListLength == 1) {
      view.disableButtons();
    }
  }

  public KeyboardTextInput getKeyboard(String initialValue) {
    KeyboardTextInput keyboard = keyboardInputFactory.createStringKeyboardInput();
    keyboard.setErrorValidator(validatorFactory.createStringLengthValidator(1, 15));

    keyboard.setInitialValue(initialValue);
    return keyboard;
  }

  public KeyboardInputCallback<String> getCallbackForKeyboard() {
    return new KeyboardInputCallback<String>() {
      @Override
      public void onOk(String value) {
        keyboardString = value;
        renameButton();
      }
    };
  }

  public int getFeatureCount() {
    String[] FeatureList = model.get("FeatureList", (String[]) null);
    int ListLength = 0;
    if (FeatureList != (null)) {
      ListLength = FeatureList.length;
    }
    return ListLength;
  }

  public String[] getFeatureList() {
    String[] FeatureList = model.get("FeatureList", (String[]) null);
    return FeatureList;
  }

  private void ProbeFeature() {
    // get the selected feature
    final int selectedFeature = view.getFeatureListIndex();
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);

    final ProbeFeatureClass probeFeatureObject = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);

    ScriptSender sender = new ScriptSender();
    ScriptCommand sc = new ScriptCommand("ProbeFeature");
    ScriptExporter exporter = new ScriptExporter();

    sc.appendLine("pose_list=\"\"");
    sc.appendLine("movej(" + probeFeatureObject.getZUpPoseString() + ")");
    sc.appendLine("sleep(0.5)");
    sc.appendLine("starting_pos=get_actual_tcp_pose()");
    sc.appendLine(probeFeatureObject.generateZForceCommand());
    sc.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    sc.appendLine("sleep(0.01)");
    sc.appendLine("end");
    sc.appendLine("end_force_mode()");
    sc.appendLine("stopl(20)");
    sc.appendLine("pose_list = str_cat(pose_list,get_actual_tcp_pose())");
    sc.appendLine("pose_list= str_cat(pose_list,\";\")");
    sc.appendLine("movej(" + probeFeatureObject.getZUpPoseString() + ")");

    sc.appendLine("movel(" + probeFeatureObject.getXUpPoseString() + ")");
    sc.appendLine("movel(" + probeFeatureObject.getXDownPoseString() + ")");
    sc.appendLine("sleep(0.5)");
    sc.appendLine("starting_pos=get_actual_tcp_pose()");
    sc.appendLine(probeFeatureObject.generateXForceCommand());
    sc.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    sc.appendLine("sleep(0.01)");
    sc.appendLine("end");
    sc.appendLine("end_force_mode()");
    sc.appendLine("stopl(20)");
    sc.appendLine("pose_list = str_cat(pose_list,get_actual_tcp_pose())");
    sc.appendLine("pose_list= str_cat(pose_list,\";\")");
    sc.appendLine("movel(" + probeFeatureObject.getXDownPoseString() + ")");
    sc.appendLine("movel(" + probeFeatureObject.getXUpPoseString() + ")");

    sc.appendLine("movel(" + probeFeatureObject.getY1UpPoseString() + ")");
    sc.appendLine("movel(" + probeFeatureObject.getY1DownPoseString() + ")");
    sc.appendLine("sleep(0.5)");
    sc.appendLine("starting_pos=get_actual_tcp_pose()");
    sc.appendLine(probeFeatureObject.generateYForceCommand());
    sc.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    sc.appendLine("sleep(0.01)");
    sc.appendLine("end");
    sc.appendLine("end_force_mode()");
    sc.appendLine("stopl(20)");
    sc.appendLine("pose_list = str_cat(pose_list,get_actual_tcp_pose())");
    sc.appendLine("pose_list= str_cat(pose_list,\";\")");
    sc.appendLine("movel(" + probeFeatureObject.getY1DownPoseString() + ")");
    sc.appendLine("movel(" + probeFeatureObject.getY1UpPoseString() + ")");

    sc.appendLine("movel(" + probeFeatureObject.getY2UpPoseString() + ")");
    sc.appendLine("movel(" + probeFeatureObject.getY2DownPoseString() + ")");
    sc.appendLine("sleep(0.5)");
    sc.appendLine("starting_pos=get_actual_tcp_pose()");
    sc.appendLine(probeFeatureObject.generateYForceCommand());
    sc.appendLine("while (force()<5 and point_dist(get_actual_tcp_pose(),starting_pos)<0.1):");
    sc.appendLine("sleep(0.01)");
    sc.appendLine("end");
    sc.appendLine("end_force_mode()");
    sc.appendLine("stopl(20)");
    sc.appendLine("pose_list = str_cat(pose_list,get_actual_tcp_pose())");
    sc.appendLine("movel(" + probeFeatureObject.getY2DownPoseString() + ")");
    sc.appendLine("movel(" + probeFeatureObject.getY2UpPoseString() + ")");
    String Pose_list = exporter.exportStringFromURScript(sc, "pose_list");

    // convert Pose_list to List
    String[] Pose_list_array = Pose_list.split(";");

    sc = new ScriptCommand("ProbeFeature");
    sc.appendLine("textmsg(\"z_probe_pos: " + Pose_list_array[0] + "\")");
    sc.appendLine("textmsg(\"x_probe_pos: " + Pose_list_array[1] + "\")");
    sc.appendLine("textmsg(\"y1_probe_pos: " + Pose_list_array[2] + "\")");
    sc.appendLine("textmsg(\"y2_probe_pos: " + Pose_list_array[3] + "\")");
    sender.sendScriptCommand(sc);

    Pose origin = calculateFeatureOrigin(Pose_list_array);

    try {
      String featurename = view.getFeatureListValue();
      featureContributionModel.updateFeature(featurename, origin);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return;
    }


  }

  private Pose calculateFeatureOrigin(String[] poseList) {
    // poseList contains [ZProbePoint,XProbePoint,Y1ProbePoint,Y2ProbePoint]
    String[] ZProbePointString = poseList[0].split(",");
    String[] XProbePointString = poseList[1].split(",");
    String[] Y1ProbePointString = poseList[2].split(",");
    String[] Y2ProbePointString = poseList[3].split(",");

    ZProbePointString[0] = ZProbePointString[0].substring(2);
    ZProbePointString[5] = ZProbePointString[5].substring(0, ZProbePointString[5].length() - 1);
    XProbePointString[0] = XProbePointString[0].substring(2);
    XProbePointString[5] = XProbePointString[5].substring(0, XProbePointString[5].length() - 1);
    Y1ProbePointString[0] = Y1ProbePointString[0].substring(2);
    Y1ProbePointString[5] = Y1ProbePointString[5].substring(0, Y1ProbePointString[5].length() - 1);
    Y2ProbePointString[0] = Y2ProbePointString[0].substring(2);
    Y2ProbePointString[5] = Y2ProbePointString[5].substring(0, Y2ProbePointString[5].length() - 1);

    double[] ZProbePoint = new double[6];
    double[] XProbePoint = new double[6];
    double[] Y1ProbePoint = new double[6];
    double[] Y2ProbePoint = new double[6];

    for (int i = 0; i < 6; i++) {
      ZProbePoint[i] = Double.parseDouble(ZProbePointString[i]);
      XProbePoint[i] = Double.parseDouble(XProbePointString[i]);
      Y1ProbePoint[i] = Double.parseDouble(Y1ProbePointString[i]);
      Y2ProbePoint[i] = Double.parseDouble(Y2ProbePointString[i]);
    }

    double mY = (Y2ProbePoint[1] - Y1ProbePoint[1]) / (Y2ProbePoint[0] - Y1ProbePoint[0]);
    double mX = -(1 / mY);
    double bX = XProbePoint[1] - (mX * XProbePoint[0]);
    double bY = Y1ProbePoint[1] - (mY * Y1ProbePoint[0]);
    double x0 = (bX - bY) / (mY - mX);
    double y0 = mY * x0 + bY;
    double z0 = ZProbePoint[2];
    double rz = Math.atan(mY/ 1);

    Pose origin = poseFactory.createPose(x0, y0, z0, 0, 0, rz, Length.Unit.M, Angle.Unit.RAD);
    return origin;
  }
  public ProbeFeatureClass getProbeFeatureObject(int index) {
    // get the selected feature
    final int selectedFeature = index;
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);

    final ProbeFeatureClass probeFeatureObject = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);
    return probeFeatureObject;
  }
}
