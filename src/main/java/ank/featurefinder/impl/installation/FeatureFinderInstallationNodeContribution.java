package ank.featurefinder.impl.installation;

import ank.featurefinder.DefaultVariables;
import ank.featurefinder.impl.ProbeFeatureClass;
import ank.featurefinder.impl.communicator.ScriptCommand;
import ank.featurefinder.impl.communicator.ScriptExporter;
import ank.featurefinder.impl.communicator.ScriptSender;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.UserInterfaceAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.feature.FeatureContributionModel;
import com.ur.urcap.api.domain.function.FunctionModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.RobotPositionCallback2;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.ur.urcap.api.domain.userinteraction.robot.movement.MovementCompleteEvent;
import com.ur.urcap.api.domain.userinteraction.robot.movement.MovementErrorEvent;
import com.ur.urcap.api.domain.userinteraction.robot.movement.RobotMovement;
import com.ur.urcap.api.domain.userinteraction.robot.movement.RobotMovementCallback;
import com.ur.urcap.api.domain.value.Pose;
import com.ur.urcap.api.domain.value.PoseFactory;
import com.ur.urcap.api.domain.value.robotposition.PositionParameters;
import com.ur.urcap.api.domain.value.simple.Angle;
import com.ur.urcap.api.domain.value.simple.Length;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FeatureFinderInstallationNodeContribution implements InstallationNodeContribution {

  private String keyboardString = "";
  private Integer keyboardInteger = 0;
  private final DataModel model;
  private final FeatureFinderInstallationNodeView view;
  private FeatureContributionModel featureContributionModel;
  private final InputValidationFactory validatorFactory;
  private final KeyboardInputFactory keyboardInputFactory;
  private PoseFactory poseFactory;
  private Pose zeroPose;
  private UserInterfaceAPI uiapi;
  private FunctionModel functionModel;
  private final RobotMovement robotMovement;
  private RobotRealtimeReader robotRealtimeReader = new RobotRealtimeReader();
  private InstallationAPIProvider apiProvider;

  private ScriptExporter exporter = new ScriptExporter();

  FeatureFinderInstallationNodeContribution(InstallationAPIProvider apiProvider, DataModel model, FeatureFinderInstallationNodeView view) {
    this.uiapi = apiProvider.getUserInterfaceAPI();
    this.model = model;
    this.view = view;
    this.apiProvider = apiProvider;
    this.keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
    this.validatorFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getInputValidationFactory();
    featureContributionModel = apiProvider.getInstallationAPI().getFeatureContributionModel();
    this.robotMovement = apiProvider.getUserInterfaceAPI().getUserInteraction().getRobotMovement();
    this.poseFactory = apiProvider.getInstallationAPI().getValueFactoryProvider().getPoseFactory();
    this.functionModel = apiProvider.getInstallationAPI().getFunctionModel();

    try {
      functionModel.addFunction("CalculateOrigin", "ZProbePoint", "XProbePoint", "Y1ProbePoint", "Y2ProbePoint");
    } catch (Exception e) {
      System.out.println("Function did not get added: " + e);
      return;
    }
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

    view.setLicenseBoxVisible(!isLicenseValid());
  }

  @Override
  public void closeView() {
    // Intentionally left empty
  }

  @Override
  public void generateScript(ScriptWriter writer) {
    // add a function

    writer.appendLine("def CalculateOrigin(ZProbePoint,XProbePoint,Y1ProbePoint,Y2ProbePoint):");
    writer.appendLine(" mY = (Y2ProbePoint[1] - Y1ProbePoint[1]) / (Y2ProbePoint[0] - Y1ProbePoint[0])");
    writer.appendLine(" mX = -(1 / mY)");
    writer.appendLine(" bX = XProbePoint[1] - (mX * XProbePoint[0])");
    writer.appendLine(" bY = Y1ProbePoint[1] - (mY * Y1ProbePoint[0])");
    writer.appendLine(" x0 = (bX - bY) / (mY - mX)");
    writer.appendLine(" y0 = mY * x0 + bY");
    writer.appendLine(" z0 = ZProbePoint[2]");
    writer.appendLine(" rz = atan(mY/1)");
    writer.appendLine(" return (p[x0, y0, z0, 0, 0, rz])");
    writer.appendLine("end");

    writer.appendLine("def vectorSub(v1, v2):");
    writer.appendLine(" result = [0,0]");
    writer.appendLine("i=0");
    writer.appendLine("while i < length(v1):");
    writer.appendLine(" result[i] = v1[i] - v2[i]");
    writer.appendLine(" i = i + 1");
    writer.appendLine("end");
    writer.appendLine("return result");
    writer.appendLine("end");

    writer.appendLine("def vectorCross2D(v1, v2):");
    writer.appendLine(" return v1[0] * v2[1] - v1[1] * v2[0]");
    writer.appendLine("end");

    writer.appendLine("def vectorScalar(v1, v2):");
    writer.appendLine(" result = 0");
    writer.appendLine(" i=0");
    writer.appendLine(" while i < length(v1):");
    writer.appendLine("   result = result + v1[i] * v2[i]");
    writer.appendLine("   i = i + 1");
    writer.appendLine(" end");
    writer.appendLine(" return result");
    writer.appendLine("end");

    writer.appendLine("def vectorNorm(v):");
    writer.appendLine(" result = 0");
    writer.appendLine(" i=0");
    writer.appendLine(" while i < length(v):");
    writer.appendLine("   result = result + v[i] * v[i]");
    writer.appendLine("   i = i + 1");
    writer.appendLine(" end");
    writer.appendLine(" return sqrt(result)");
    writer.appendLine("end");
  }

  public void listItemClicked(ListSelectionEvent e) {
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
      if (PoseFeatureList != null) {
        // System.out.println("PoseFeatureList: " + PoseFeatureList[index]);
        // System.out.println("Index: " + index);
        view.updateProbeFeatureLables(new ProbeFeatureClass(PoseFeatureList[index]));
      }
      // view.updateProbeFeatureLables(new ProbeFeatureClass(PoseFeatureList[index]));
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
        ProbingThread newProbingThread = new ProbingThread();
        newProbingThread.start();
        // System.out.println("ProbeFeatureButton pressed");
      } else if (name.equals("ZUpMoveToButton")) {
        moveRobot(1);
      } else if (name.equals("XUpMoveToButton")) {
        moveRobot(2);
      } else if (name.equals("XDownMoveToButton")) {
        moveRobot(3);
      } else if (name.equals("Y1UpMoveToButton")) {
        moveRobot(4);
      } else if (name.equals("Y1DownMoveToButton")) {
        moveRobot(5);
      } else if (name.equals("Y2UpMoveToButton")) {
        moveRobot(6);
      } else if (name.equals("Y2DownMoveToButton")) {
        moveRobot(7);
      } else if (name.equals("LicenseSelectionButton")) {

        // ask user to select a file
        JFileChooser fileChooser = new JFileChooser();
        // Create a file filter for specific file endings (e.g., ".txt" files)
        FileNameExtensionFilter filter = new FileNameExtensionFilter("License Files", "lic");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File("/programs"));
        int result = fileChooser.showOpenDialog(view.renameTextField);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          model.set("LicensePath", selectedFile.getAbsolutePath());
          if(isLicenseValid()){
            // copy the license file to the current folder
            try {
              // check if file already exists and delete it
              File file = new File("/data/root/.urcaps/FeatureFinderLicense.lic");
              if (file.exists()) {
                file.delete();
              }
              Files.copy(Paths.get(selectedFile.getAbsolutePath()), Paths.get("/data/root/.urcaps/FeatureFinderLicense.lic"));
              model.set("LicensePath", "/data/root/.urcaps/FeatureFinderLicense.lic");
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          } else {
            view.setLicenseBoxVisible(true);
          }
          view.setLicenseBoxVisible(!isLicenseValid());
        }
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
    } else if (e.getSource() instanceof JCheckBox) {
      JCheckBox box = (JCheckBox) e.getSource();
      String name = box.getName();
      if (name.equals("DoubleProbeBox")) {
        // System.out.println("doubleProbeBox changed");
        updateDoubleProbe(box.isSelected());
      }
    }
  }

  private void updateDoubleProbe(boolean selected) {
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    if (ProbeFeatureList == null) {
      System.out.println("ProbeFeatureList is still empty, something went wrong");
      return;
    }

    // get the selected feature
    final int selectedFeature = view.getFeatureListIndex();

    final ProbeFeatureClass currentProbeFeature = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);
    currentProbeFeature.setDoubleProbe(selected);

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

  private void updateSpeeds(final int id) {
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    if (ProbeFeatureList == null) {
      System.out.println("ProbeFeatureList is still empty, something went wrong");
      return;
    }

    // get the selected feature
    final int selectedFeature = view.getFeatureListIndex();

    ProbeFeatureClass currentProbeFeature = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);
    switch (id) {
      case 1:
        currentProbeFeature.setRapidSpeed(keyboardInteger);
        break;
      case 2:
        currentProbeFeature.setRapidAcceleration(keyboardInteger);
        break;
      case 3:
        currentProbeFeature.setProbeSpeed(keyboardInteger);
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

    String regex = "[a-zA-Z][a-zA-Z0-9_]{0,14}";
    if (!newName.matches(regex)) {
      System.out.println("Invalid name");
      ScriptSender sc = new ScriptSender();
      sc.sendDashboardPopup("Name must: start with a letter, contain only letters, numbers and underscore. Maximum 15 characters");
      return;
    }
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
      featureContributionModel.addFeature(newName, newName, oldFeature);
      featureContributionModel.removeFeature(featureName);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return;
    }

    model.set("FeatureList", newFeatureList);
    view.updateFeatureList(newFeatureList);
    view.disableButtons();
  }

  private void deleteButton() {
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

  public KeyboardNumberInput<Integer> getKeyboardNumber(int initialValue) {
    KeyboardNumberInput<Integer> keyboard = keyboardInputFactory.createIntegerKeypadInput();
    keyboard.setErrorValidator(validatorFactory.createIntegerRangeValidator(0, 2000));
    keyboard.setInitialValue(initialValue);
    return keyboard;
  }

  public KeyboardInputCallback<Integer> getCallbackForKeyboardNumber(final int id) {
    return new KeyboardInputCallback<Integer>() {
      @Override
      public void onOk(Integer value) {
        keyboardInteger = value;
        // depending on id call 4 different not yet written functions
        updateSpeeds(id);
      }
    };
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

  public void moveRobot(int id) {
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    if (ProbeFeatureList == null) {
      System.out.println("ProbeFeatureList is still empty, something went wrong");
      return;
    }

    // get the selected feature
    final int selectedFeature = view.getFeatureListIndex();

    final ProbeFeatureClass currentProbeFeature = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);

    double[] poseList = new double[6];
    switch (id) {
      case 1:
        poseList = currentProbeFeature.poseStringToList(currentProbeFeature.getZUpPoseString());
        break;
      case 2:
        poseList = currentProbeFeature.poseStringToList(currentProbeFeature.getXUpPoseString());
        break;
      case 3:
        poseList = currentProbeFeature.poseStringToList(currentProbeFeature.getXDownPoseString());
        break;
      case 4:
        poseList = currentProbeFeature.poseStringToList(currentProbeFeature.getY1UpPoseString());
        break;
      case 5:
        poseList = currentProbeFeature.poseStringToList(currentProbeFeature.getY1DownPoseString());
        break;
      case 6:
        poseList = currentProbeFeature.poseStringToList(currentProbeFeature.getY2UpPoseString());
        break;
      case 7:
        poseList = currentProbeFeature.poseStringToList(currentProbeFeature.getY2DownPoseString());
        break;
    }
    Pose poseToMove = poseFactory.createPose(poseList[0], poseList[1], poseList[2], poseList[3], poseList[4], poseList[5], Length.Unit.M, Angle.Unit.RAD);

    robotMovement.requestUserToMoveRobot(
      poseToMove,
      new RobotMovementCallback() {
        @Override
        public void onComplete(MovementCompleteEvent event) {}

        @Override
        public void onError(MovementErrorEvent event) {}
      }
    );
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

  private class ProbingThread extends Thread {

    private volatile boolean running = true;

    public void stopThread() {
      running = false;
    }

    @Override
    public void run() {
      while (running) {
        this.stopThread();
        robotRealtimeReader.readNow();
        double robotMode = robotRealtimeReader.getRobotMode();
        System.out.println("Robotmode: " + robotMode);
        if (Math.round(robotMode) == 7) {
          ProbeFeature();
        } else {
          System.out.println("robot not turned on");
          ScriptSender scriptSender = new ScriptSender();
          scriptSender.sendDashboardPopup("Please power on the Robot first");
        }
        if (!running) {
          break; // Exit the loop and stop the thread
        }
      }
    }
  }

  private void ProbeFeature() {
    // get the selected feature
    final int selectedFeature = view.getFeatureListIndex();
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);

    final ProbeFeatureClass probeFeatureObject = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);

    // ScriptCommand sc = new ScriptCommand("ProbeFeature");
    ScriptCommand sc = probeFeatureObject.generateScriptCommand();
    System.out.println("right before sending command");
    String Pose_list = exporter.exportStringFromURScript(sc, "pose_list");

    System.out.println("right after sending command");

    // convert Pose_list to List
    String[] Pose_list_array = Pose_list.split(";");
    if (Pose_list_array.length != 4) {
      System.out.println("Error: Pose_list_array.length != 4");
      return;
    }
    Pose origin = calculateFeatureOrigin(Pose_list_array);

    try {
      String featurename = view.getFeatureListValue();
      // featureContributionModel.updateFeature(featurename, origin);
      featureContributionModel.removeFeature(featurename);
      featureContributionModel.addFeature(featurename, featurename, origin);
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
    // check if mY is 0
    if (mY == 0) {
      mY = 0.0000001;
    }
    double mX = -(1 / mY);
    double bX = XProbePoint[1] - (mX * XProbePoint[0]);
    double bY = Y1ProbePoint[1] - (mY * Y1ProbePoint[0]);
    double x0 = (bX - bY) / (mY - mX);
    double y0 = mY * x0 + bY;
    double z0 = ZProbePoint[2];

    double angle = 0;

    ScriptSender scriptSender = new ScriptSender();


    double[] v1 = { 1, 0 };
    double p2[] = { Y2ProbePoint[0], Y2ProbePoint[1] };
    double p1[] = { Y1ProbePoint[0], Y1ProbePoint[1] };

    double Cos = vectorScalar(v1, vectorSub(p2, p1)) / (vectorNorm(v1) * vectorNorm(vectorSub(p2, p1)));
    double det = vectorCross2D(v1, vectorSub(p2, p1));

    if (det > 0) {
      angle = Math.acos(Cos);
    } else {
      angle = 2 * Math.PI - Math.acos(Cos);
    }
    angle = angle - Math.PI / 2;


    Pose origin = poseFactory.createPose(x0, y0, z0, 0, 0, angle, Length.Unit.M, Angle.Unit.RAD);

    scriptSender.sendLogMsg("origin: " + origin.toString());
    scriptSender.sendLogMsg("Y1: " + Y1ProbePoint[0] + " " + Y1ProbePoint[1]);
    scriptSender.sendLogMsg("Y2: " + Y2ProbePoint[0] + " " + Y2ProbePoint[1]);
    scriptSender.sendLogMsg("angle: " + Math.toDegrees(angle));

    return origin;
  }

  public boolean isLicenseValid() {
    
      String licensePath = model.get("LicensePath", "/data/root/.urcaps/FeatureFinderLicense.lic");

      // read text file and save in String license
      String license = "";
      try {
        license = new String(Files.readAllBytes(Paths.get(licensePath)));

        if (license.equals(getLicenseHash())) {
          return true;
        } else {
          return false;
        }
      } catch (IOException e) {
        System.out.println("Exception: " + e);
        return false;
      }
    
  }

  private String getLicenseHash() {
    String serialnumber = apiProvider.getSystemAPI().getRobotModel().getSerialNumber();
    // System.out.println("Serialnumber: " + serialnumber);
    try {
      // Create a SHA-256 hash of the data

      // Create a JSON string with the robot serial number and secret
      String licenseDataJson = "{\"robot_serial_number\": \"" + serialnumber + "\", \"secret\": \"" + DefaultVariables.secret + "\"}";
      // System.out.println("licenseDataJson: " + licenseDataJson);
      // Create a SHA-256 hash of the JSON data
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(licenseDataJson.getBytes(StandardCharsets.UTF_8));

      // Encode the hash bytes as a base64 string
      String expectedLicenseKey = Base64.getEncoder().encodeToString(hashBytes);

      return expectedLicenseKey;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }
  }

  private double[] vectorSub(double[] v1, double[] v2) {
    double[] result = new double[v1.length];
    for (int i = 0; i < v1.length; i++) {
      result[i] = v1[i] - v2[i];
    }
    return result;
  }

  private double vectorCross2D(double[] v1, double[] v2) {
    return v1[0] * v2[1] - v1[1] * v2[0];
  }

  private double vectorScalar(double[] v1, double[] v2) {
    double result = 0;
    for (int i = 0; i < v1.length; i++) {
      result += v1[i] * v2[i];
    }
    return result;
  }

  private double vectorNorm(double[] v) {
    double result = 0;
    for (int i = 0; i < v.length; i++) {
      result += v[i] * v[i];
    }
    return Math.sqrt(result);
  }

  public ProbeFeatureClass getProbeFeatureObject(int index) {
    // get the selected feature
    final int selectedFeature = index;
    final String[] ProbeFeatureList = model.get("ProbeFeatureList", (String[]) null);
    final ProbeFeatureClass probeFeatureObject;
    try {
      probeFeatureObject = new ProbeFeatureClass(ProbeFeatureList[selectedFeature]);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      return null;
    }
    return probeFeatureObject;
  }
}
