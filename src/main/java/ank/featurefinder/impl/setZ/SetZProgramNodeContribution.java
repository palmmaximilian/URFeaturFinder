package ank.featurefinder.impl.setZ;

import ank.featurefinder.impl.installation.FeatureFinderInstallationNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.UserInterfaceAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.userinteraction.RobotPositionCallback2;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import com.ur.urcap.api.domain.value.Pose;
import com.ur.urcap.api.domain.value.robotposition.PositionParameters;
import com.ur.urcap.api.domain.value.simple.Angle;
import com.ur.urcap.api.domain.value.simple.Length;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.*;

public class SetZProgramNodeContribution implements ProgramNodeContribution {

  private final ProgramAPI programAPI;
  private final SetZrogramNodeView view;
  private UserInterfaceAPI userInterfaceAPI;
  public final DataModel model;
  private final UndoRedoManager undoRedoManager;

  private final InputValidationFactory validatorFactory;
  private final KeyboardInputFactory keyboardInputFactory;

  SetZProgramNodeContribution(
    ProgramAPIProvider apiProvider,
    SetZrogramNodeView view,
    DataModel model
  ) {
    programAPI = apiProvider.getProgramAPI();
    this.view = view;
    this.userInterfaceAPI = apiProvider.getUserInterfaceAPI();
    this.model = model;
    this.undoRedoManager = apiProvider.getProgramAPI().getUndoRedoManager();
    this.validatorFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getInputValidationFactory();
    this.keyboardInputFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
  }

  @Override
  public void openView() {
    String[] featureList = getInstallationNode().getFeatureList();
    String lastSelectedFeature = model.get("featureName", "");

    view.updateFeaturesList(featureList);

    int index = -1;
    for (int i = 0; i < featureList.length; i++) {
      // System.out.println("Comparing " + featureList[i] + " to " + lastSelectedFeature);
      if (featureList[i].equals(lastSelectedFeature)) {
        index = i;
        break;
      }
      i++;
    }
    if (index != -1) {
      view.setFeatureIndex(index);
    } else {
      view.setFeatureIndex(0);
    }
    view.setForce(model.get("force", 5.0));
    view.setSpeed(model.get("speed", 25));
    view.setDirection(model.get("direction", 5));
  }

  @Override
  public void closeView() {
    // Intentionally left empty
  }

  @Override
  public String getTitle() {
    return "Probe Z";
  }

  @Override
  public boolean isDefined() {
    return true;
  }

  @Override
  public void generateScript(ScriptWriter scriptWriter) {
    String featureName = view.getFeatureName();
    
    int direction = model.get("direction", 0);
    // 0 or 1 =>0, 2 or 3 =>1, 4 or 5 =>2
    direction = direction / 2;
    // empty array of zero
    int[] directionVector = new int[6];
    directionVector[direction] = 1;
    // System.out.println("Direction Vector: " + Arrays.toString(directionVector));

    int[] forceVector = new int[6];
    forceVector[direction] = (int) model.get("force", 5.0)+3;
    // System.out.println("Force Vector: " + Arrays.toString(forceVector));

    String taskFrame = "p[0,0,0,0,0,0]";
    double[] limits = new double[6];
    limits[direction] = model.get("speed", 25) / 1000.0;
    // System.out.println("Speed Vector: " + Arrays.toString(limits));
    String command =
      "force_mode(" +
      taskFrame +
      "," +
      Arrays.toString(directionVector) +
      "," +
      Arrays.toString(forceVector) +
      ",2," +
      Arrays.toString(limits) +
      ")";
    System.out.println(command);
    scriptWriter.appendLine(command);
    // wait untill the feature is found
    scriptWriter.appendLine("while(force()<" + model.get("force", 5.0) + "):");
    scriptWriter.appendLine("  sync()");
    scriptWriter.appendLine("end");
    scriptWriter.appendLine("end_force_mode()");
    // get current tcp
    scriptWriter.appendLine("current_tcp= get_actual_tcp_pose()");
    // set z of the feature as the z of the tcp
    scriptWriter.appendLine(
      featureName+"[2] = current_tcp[2]"
    );


  }

  private FeatureFinderInstallationNodeContribution getInstallationNode() {
    return programAPI.getInstallationNode(
      FeatureFinderInstallationNodeContribution.class
    );
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof JButton) {
      JButton button = (JButton) e.getSource();
      if (button.getName().equals("startPositionButton")) {
        System.out.println("Start Position Button Pressed");
        userInterfaceAPI
          .getUserInteraction()
          .getUserDefinedRobotPosition(
            new RobotPositionCallback2() {
              @Override
              public void onOk(PositionParameters positionParameters) {
                Pose startPosition = positionParameters.getPose();
                double[] startPose = new double[6];
                startPose[0] = startPosition.getPosition().getX(Length.Unit.M);
                startPose[1] = startPosition.getPosition().getY(Length.Unit.M);
                startPose[2] = startPosition.getPosition().getZ(Length.Unit.M);
                startPose[3] =
                  startPosition.getRotation().getRX(Angle.Unit.RAD);
                startPose[4] =
                  startPosition.getRotation().getRY(Angle.Unit.RAD);
                startPose[5] =
                  startPosition.getRotation().getRZ(Angle.Unit.RAD);
                System.out.println(
                  "Start Position: " + Arrays.toString(startPose)
                );
                model.set("startPose", startPose);
              }
            }
          );
      }
    } else if (e.getSource() instanceof JComboBox) {
      JComboBox<String> comboBox = (JComboBox) e.getSource();
      if (comboBox.getName().equals("directionList")) {
        int direction = comboBox.getSelectedIndex();
        setIntegerModel("direction", direction);
      } else if (comboBox.getName().equals("features")) {
        // System.out.println("Feature Selected");
        try {
          String featureName = comboBox.getSelectedItem().toString();
          setStringModel("featureName", featureName);
          // System.out.println("Feature Name: " + featureName);
        } catch (Exception ex) {
          System.out.println("Error: " + ex.getMessage());
        }
      }
    }
  }

  public void setIntegerModel(final String ID, final int value) {
    undoRedoManager.recordChanges(
      new UndoableChanges() {
        @Override
        public void executeChanges() {
          model.set(ID, value);
        }
      }
    );
  }

  public void setStringModel(final String ID, final String value) {
    undoRedoManager.recordChanges(
      new UndoableChanges() {
        @Override
        public void executeChanges() {
          model.set(ID, value);
          
        }
      }
    );
  }

  public KeyboardNumberInput<Integer> getKeyboard(Integer initialValue) {
    KeyboardNumberInput<Integer> keyboard = keyboardInputFactory.createIntegerKeypadInput();
    keyboard.setErrorValidator(
      validatorFactory.createIntegerRangeValidator(0, 100)
    );

    keyboard.setInitialValue(initialValue);
    return keyboard;
  }

  
  public KeyboardInputCallback<Integer> getCallbackForSpeedKeyboard() {
    return new KeyboardInputCallback<Integer>() {
      @Override
      public void onOk(Integer value) {
        setIntegerModel("speed", value);
        view.speed.setText(value.toString());
      }
    };
  }

  public KeyboardInputCallback<Integer> getCallbackForForceKeyboard() {
    return new KeyboardInputCallback<Integer>() {
      @Override
      public void onOk(Integer value) {
        setIntegerModel("force", value);
        view.force.setText(value.toString());
      }
    };
  }


}
