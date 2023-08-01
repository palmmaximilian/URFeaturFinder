package ank.featurefinder.impl.probeNode;

import ank.featurefinder.impl.installation.FeatureFinderInstallationNodeContribution;
import ank.featurefinder.impl.setZ.SetZProgramNodeService;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.UserInterfaceAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.feature.Feature;
import com.ur.urcap.api.domain.feature.FeatureModel;
import com.ur.urcap.api.domain.program.ProgramModel;
import com.ur.urcap.api.domain.program.nodes.ProgramNode;
import com.ur.urcap.api.domain.program.nodes.ProgramNodeFactory;
import com.ur.urcap.api.domain.program.nodes.builtin.MoveNode;
import com.ur.urcap.api.domain.program.nodes.builtin.WaypointNode;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.movenode.TCPSelection;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.movenode.builder.MoveLConfigBuilder;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.movenode.builder.MovePConfigBuilder;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.BlendParameters;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointMotionParameters;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointNodeConfig;
import com.ur.urcap.api.domain.program.nodes.builtin.configurations.waypointnode.WaypointNodeConfigFactory;
import com.ur.urcap.api.domain.program.structure.TreeNode;
import com.ur.urcap.api.domain.program.structure.TreeStructureException;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.undoredo.UndoRedoManager;
import com.ur.urcap.api.domain.undoredo.UndoableChanges;
import com.ur.urcap.api.domain.userinteraction.RobotPositionCallback2;
import com.ur.urcap.api.domain.userinteraction.robot.movement.MovementCompleteEvent;
import com.ur.urcap.api.domain.userinteraction.robot.movement.MovementErrorEvent;
import com.ur.urcap.api.domain.userinteraction.robot.movement.RobotMovement;
import com.ur.urcap.api.domain.userinteraction.robot.movement.RobotMovementCallback;
import com.ur.urcap.api.domain.validation.ErrorHandler;
import com.ur.urcap.api.domain.value.Pose;
import com.ur.urcap.api.domain.value.ValueFactoryProvider;
import com.ur.urcap.api.domain.value.blend.Blend;
import com.ur.urcap.api.domain.value.robotposition.PositionParameters;
import com.ur.urcap.api.domain.value.simple.Acceleration;
import com.ur.urcap.api.domain.value.simple.Angle;
import com.ur.urcap.api.domain.value.simple.Length;
import com.ur.urcap.api.domain.value.simple.SimpleValueFactory;
import com.ur.urcap.api.domain.value.simple.Speed;
import java.util.ArrayList;
import java.util.List;
import javax.swing.undo.UndoManager;

public class ProbeNodeNodeContribution implements ProgramNodeContribution {

  private static final String DEFINED_KEY = "is_defined";
  private static final String CENTER_POSITION = "center_pose";

  private static final double SHARED_TOOL_SPEED = 250;
  private static final double SHARED_TOOL_ACCELERATION = 1200;
  private static final double SHARED_BLEND_RADIUS_IN_MM = 23;
  private static final double HORIZONTAL_RADIUS_IN_MM = 200.0;
  private static final double VERTICAL_RADIUS_IN_MM = 120.0;
  private static final int NUMBER_OF_WAYPOINTS = 16;

  private final ProgramAPIProvider apiProvider;
  private final ProgramAPI programAPI;
  private final ProgramModel programModel;
  private final ProgramNodeFactory programNodeFactory;
  private final UndoRedoManager undoRedoManager;

  private MoveNode moveNode;
  private final List<WaypointNode> waypointNodes = new ArrayList<WaypointNode>();

  private DataModel model;
  private TreeNode moveTreeNode;

  private WaypointNodeConfigFactory waypointNodeConfigFactory;

  private final ProbeNodeProgramNodeView view;
  private final RobotMovement robotMovement;
  private EllipseState ellipseState = new EllipseState();

  public ProbeNodeNodeContribution(
    ProgramAPIProvider apiProvider,
    ProbeNodeProgramNodeView view,
    DataModel model
  ) {
    this.apiProvider = apiProvider;
    this.model = model;
    this.view = view;
    this.programAPI = apiProvider.getProgramAPI();
    this.undoRedoManager = programAPI.getUndoRedoManager();
    this.programModel = programAPI.getProgramModel();
    programNodeFactory =
      apiProvider.getProgramAPI().getProgramModel().getProgramNodeFactory();
    waypointNodeConfigFactory =
      programNodeFactory.createWaypointNode().getConfigFactory();
    robotMovement =
      apiProvider.getUserInterfaceAPI().getUserInteraction().getRobotMovement();
  }

  @Override
  public void openView() {
    String[] featureNames = getInstallationNode().getFeatureList();
    String previousFeature = model.get("feature", "");


    view.updateFeatureList(featureNames);
    // check if previous feature is still in list and get index

    int index = 0;
    for (int i = 0; i < featureNames.length; i++) {
		System.out.println("Comparing " + featureNames[i] + " to " + previousFeature);
      if (featureNames[i].equals(previousFeature)) {
        index = i;
        break;
      }
      i++;
    }

    if (index != -1) {
      view.setFeatureListIndex(index);
    } else {
      view.setFeatureListIndex(0);
    }
  }

  @Override
  public void closeView() {
    // nothing needs to happen here in this example
  }

  private void setTitle(String title) {
    setStringModel("title", title);
  }

  @Override
  public String getTitle() {
    return model.get("title", "Probe Feature");
  }

  @Override
  public boolean isDefined() {
    return model.get(DEFINED_KEY, false);
  }

  @Override
  public void generateScript(ScriptWriter writer) {
    writer.writeChildren();
  }

  public void selectCenterPoint() {
    clearErrors();
    UserInterfaceAPI uiapi = apiProvider.getUserInterfaceAPI();
    uiapi
      .getUserInteraction()
      .getUserDefinedRobotPosition(
        new RobotPositionCallback2() {
          @Override
          public void onOk(PositionParameters positionParameters) {
            removeNodes();
            createNodes();
            configureMoveNode();
            adjustWaypointsToCenterPoint(positionParameters);
          }
        }
      );
  }

  public void moveRobot() {
    clearErrors();
    Pose centerPose = model.get(CENTER_POSITION, (Pose) null);
    if (centerPose != null) {
      robotMovement.requestUserToMoveRobot(
        centerPose,
        new RobotMovementCallback() {
          @Override
          public void onComplete(MovementCompleteEvent event) {}

          @Override
          public void onError(MovementErrorEvent event) {
            updateError(
              new EllipseState(getErrorMessage(event.getErrorType()))
            );
          }
        }
      );
    }
  }

  private void updateError(EllipseState ellipseState) {
    this.ellipseState = ellipseState;
  }

  private void clearErrors() {
    this.ellipseState = new EllipseState();
  }

  private String getErrorMessage(MovementErrorEvent.ErrorType errorType) {
    switch (errorType) {
      case UNREACHABLE_POSE:
        return "Could not move to center point.";
      default:
        return "Error in move here";
    }
  }

  private void removeNodes() {
    final TreeNode rootTreeNode = programModel.getRootTreeNode(this);

    undoRedoManager.recordChanges(
      new UndoableChanges() {
        @Override
        public void executeChanges() {
          try {
            for (TreeNode child : rootTreeNode.getChildren()) {
              rootTreeNode.removeChild(child);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    );
  }

  private void adjustWaypointsToCenterPoint(
    PositionParameters positionParameters
  ) {
    model.set(CENTER_POSITION, positionParameters.getPose());
    try {
      configureWaypointNodes(positionParameters);
      setDefined(true);
    } catch (IllegalArgumentException e) {
      updateError(
        new EllipseState(
          "Could not create ellipse movement<br>Try a different center point."
        )
      );
      setDefined(false);
      resetWaypointNodes();
    }
    lockTreeNodes();
  }

  private void resetWaypointNodes() {
    BlendParameters blendParameters = waypointNodeConfigFactory.createSharedBlendParameters();
    WaypointMotionParameters motionParameters = waypointNodeConfigFactory.createSharedMotionParameters();
    for (WaypointNode waypointNode : waypointNodes) {
      WaypointNodeConfig waypointNodeConfig = waypointNodeConfigFactory.createFixedPositionConfig(
        blendParameters,
        motionParameters
      );
      waypointNode.setConfig(waypointNodeConfig);
    }
  }

  private void configureWaypointNodes(PositionParameters positionParameters) {
    // adjust orientation according to base
    double baseAngle =
      positionParameters
        .getJointPositions()
        .getAllJointPositions()[0].getAngle(Angle.Unit.RAD) +
      (Math.PI / 2);
    double xContribution = Math.cos(baseAngle);
    double yContribution = Math.sin(baseAngle);

    double angle = -Math.PI;
    double angularStepDistance = (2 * Math.PI) / (double) NUMBER_OF_WAYPOINTS;

    for (WaypointNode waypointNode : waypointNodes) {
      angle += angularStepDistance;
      double offsetX =
        Math.cos(angle) * HORIZONTAL_RADIUS_IN_MM * xContribution;
      double offsetY =
        Math.cos(angle) * HORIZONTAL_RADIUS_IN_MM * yContribution;
      double offsetZ = Math.sin(-angle) * VERTICAL_RADIUS_IN_MM;

      WaypointNodeConfig newWaypointNodeConfig = createWaypointConfig(
        positionParameters,
        offsetX,
        offsetY,
        offsetZ
      );
      waypointNode.setConfig(newWaypointNodeConfig);
    }
  }

  private WaypointNodeConfig createWaypointConfig(
    PositionParameters positionParameters,
    double xOffsetInMM,
    double yOffsetInMM,
    double zOffsetInMM
  ) {
    BlendParameters blendParameters = waypointNodeConfigFactory.createSharedBlendParameters();
    WaypointMotionParameters motionParameters = waypointNodeConfigFactory.createSharedMotionParameters();
    Pose pose = createPoseUsingCenterPoseAndOffset(
      positionParameters.getPose(),
      xOffsetInMM,
      yOffsetInMM,
      zOffsetInMM,
      Length.Unit.MM
    );

    return waypointNodeConfigFactory.createFixedPositionConfig(
      pose,
      positionParameters.getJointPositions(),
      positionParameters.getTCPOffset(),
      blendParameters,
      motionParameters
    );
  }

  private Pose createPoseUsingCenterPoseAndOffset(
    Pose pose,
    double xOffset,
    double yOffset,
    double zOffset,
    Length.Unit unit
  ) {
    double x = pose.getPosition().getX(unit) + xOffset;
    double y = pose.getPosition().getY(unit) + yOffset;
    double z = pose.getPosition().getZ(unit) + zOffset;
    double rx = pose.getRotation().getRX(Angle.Unit.RAD);
    double ry = pose.getRotation().getRY(Angle.Unit.RAD);
    double rz = pose.getRotation().getRZ(Angle.Unit.RAD);
    ValueFactoryProvider valueFactoryProvider = apiProvider
      .getProgramAPI()
      .getValueFactoryProvider();
    return valueFactoryProvider
      .getPoseFactory()
      .createPose(x, y, z, rx, ry, rz, unit, Angle.Unit.RAD);
  }

  private void createNodes() {
    ProgramAPI programAPI = apiProvider.getProgramAPI();
    ProgramModel programModel = programAPI.getProgramModel();
    try {
      moveNode = programNodeFactory.createMoveNodeNoTemplate();
      TreeNode rootTreeNode = programModel.getRootTreeNode(this);
      moveTreeNode = rootTreeNode.addChild(moveNode);

      waypointNodes.clear();
      for (int i = 1; i <= NUMBER_OF_WAYPOINTS; i++) {
        createAndAddWaypointNode(i);
      }
    } catch (TreeStructureException e) {
      e.printStackTrace();
    }
  }

  private void configureMoveNode() {
    ValueFactoryProvider valueFactoryProvider = programAPI.getValueFactoryProvider();

    SimpleValueFactory valueFactory = valueFactoryProvider.getSimpleValueFactory();

    Speed speed = valueFactory.createSpeed(SHARED_TOOL_SPEED, Speed.Unit.MM_S);
    Acceleration acceleration = valueFactory.createAcceleration(
      SHARED_TOOL_ACCELERATION,
      Acceleration.Unit.MM_S2
    );
    Length length = valueFactory.createLength(
      SHARED_BLEND_RADIUS_IN_MM,
      Length.Unit.MM
    );
    Blend blend = valueFactoryProvider.getBlendFactory().createBlend(length);
    FeatureModel featureModel = programAPI.getFeatureModel();
    Feature feature = featureModel.getBaseFeature();
    TCPSelection tcpSelection = moveNode
      .getTCPSelectionFactory()
      .createActiveTCPSelection();

    MovePConfigBuilder movePConfigBuilder = moveNode
      .getConfigBuilders()
      .createMovePConfigBuilder()
      .setToolSpeed(speed, ErrorHandler.AUTO_CORRECT)
      .setToolAcceleration(acceleration, ErrorHandler.AUTO_CORRECT)
      .setBlend(blend, ErrorHandler.AUTO_CORRECT)
      .setFeature(feature)
      .setTCPSelection(tcpSelection);

    MoveLConfigBuilder moveLConfigBuilder = moveNode
      .getConfigBuilders()
      .createMoveLConfigBuilder()
      .setToolSpeed(speed, ErrorHandler.AUTO_CORRECT)
      .setToolAcceleration(acceleration, ErrorHandler.AUTO_CORRECT)
      .setFeature(feature)
      .setTCPSelection(tcpSelection);
    moveNode.setConfig(moveLConfigBuilder.build());
  }

  private void createAndAddWaypointNode(String waypointName)
    throws TreeStructureException {
    WaypointNode waypointNode = programNodeFactory.createWaypointNode(
      waypointName
    );
    moveTreeNode.addChild(waypointNode);
    waypointNodes.add(waypointNode);
  }

  private void createAndAddWaypointNode(int waypointNumber)
    throws TreeStructureException {
    String waypointName = "Waypoint " + waypointNumber;
    WaypointNode waypointNode = programNodeFactory.createWaypointNode(
      waypointName
    );
    System.out.println("Adding waypoint " + waypointName.toString());
    moveTreeNode.addChild(waypointNode);
    waypointNodes.add(waypointNode);
  }

  private void lockTreeNodes() {
    ProgramModel programModel = programAPI.getProgramModel();
    TreeNode thisTreeNode = programModel.getRootTreeNode(this);
    thisTreeNode.setChildSequenceLocked(true);
    moveTreeNode.setChildSequenceLocked(true);
  }

  private void setDefined(boolean defined) {
    model.set(DEFINED_KEY, defined);
  }

  private static class EllipseState {

    private final String message;
    private final boolean isError;

    EllipseState() {
      this.isError = false;
      this.message = "";
    }

    EllipseState(String message) {
      this.isError = true;
      this.message = message;
    }

    public String getMessage() {
      return message;
    }

    public boolean isError() {
      return isError;
    }
  }

  public void generateProbeAll() {
    setTitle("Full Probe");
    moveNode = programNodeFactory.createMoveNodeNoTemplate();
    final TreeNode rootTreeNode = programModel.getRootTreeNode(this);
    undoRedoManager.recordChanges(
      new UndoableChanges() {
        @Override
        public void executeChanges() {
          try {
            // this is the code that is executed when the user presses the "Probe all Dimensions" button
            removeNodes();
            // createNodes();
            // create a move node

            moveTreeNode = rootTreeNode.addChild(moveNode);

            // create an instance of setZProgramNode

            waypointNodes.clear();
            ProgramNode setZNode = programNodeFactory.createURCapProgramNode(
              SetZProgramNodeService.class
            );

            createAndAddWaypointNode("PreProbePoint");
            createAndAddWaypointNode("ProbeZPoint");
            moveTreeNode.addChild(setZNode);
            createAndAddWaypointNode("ProbeZPoint");
            createAndAddWaypointNode("PreProbeXPoint");
            createAndAddWaypointNode("ProbeXPoint");
            createAndAddWaypointNode("ProbeXPoint");
            createAndAddWaypointNode("PreProbeXPoint");
            createAndAddWaypointNode("PreProbeY1Point");
            createAndAddWaypointNode("ProbeY1Point");
            createAndAddWaypointNode("ProbeY1Point");
            createAndAddWaypointNode("ProbeY2Point");
            createAndAddWaypointNode("ProbeY2Point");
            configureMoveNode();
          } catch (TreeStructureException e) {
            e.printStackTrace();
          }
        }
      }
    );
  }

  public void generateProbeZ() {
    setTitle("Z Probe");
    moveNode = programNodeFactory.createMoveNodeNoTemplate();
    final TreeNode rootTreeNode = programModel.getRootTreeNode(this);
    undoRedoManager.recordChanges(
      new UndoableChanges() {
        @Override
        public void executeChanges() {
          try {
            // this is the code that is executed when the user presses the "Probe all Dimensions" button
            removeNodes();
            // createNodes();
            // create a move node

            moveTreeNode = rootTreeNode.addChild(moveNode);

            // create an instance of setZProgramNode

            waypointNodes.clear();
            ProgramNode setZNode = programNodeFactory.createURCapProgramNode(
              SetZProgramNodeService.class
            );

            createAndAddWaypointNode("PreProbePoint");
            createAndAddWaypointNode("ProbeZPoint");
            moveTreeNode.addChild(setZNode);
            createAndAddWaypointNode("ProbeZPoint");
            configureMoveNode();
          } catch (TreeStructureException e) {
            e.printStackTrace();
          }
        }
      }
    );
  }

  public void featurePicked() {
    if (view.getFeatureListItem() == null) {
      return;
    }
    setStringModel("feature", view.getFeatureListItem());

  }

  private FeatureFinderInstallationNodeContribution getInstallationNode() {
    return programAPI.getInstallationNode(
      FeatureFinderInstallationNodeContribution.class
    );
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
}
