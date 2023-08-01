package ank.featurefinder.impl.installation;

import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import jframe
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FeatureFinderInstallationNodeView
  implements
    SwingInstallationNodeView<FeatureFinderInstallationNodeContribution> {

  private static final Dimension BUTTON_DIMENSION = new Dimension(200, 30);
  private static final Dimension DIRECTION_DROPDOWN_DIMENSION = new Dimension(
    100,
    30
  );
  private static final Dimension PROBE_FEATURE_BUTTON_DIMENSION = new Dimension(
    200,
    60
  );
  private JButton createFeatureButton;
  private JButton deleteFeatureButton;
  public JButton renameFeatureButton;
  private JTextField renameTextField = new JTextField(20);
  private Box defaultBox = Box.createHorizontalBox();
  public JList<String> featureFrame = new JList<String>();

  private Box buttonBox;

  private JButton ZUpButton = new JButton("Set Z Up");
  private JButton XUpButton = new JButton("Set X Up");
  private JButton XDownButton = new JButton("Set X Down");
  private JButton Y1UpButton = new JButton("Set Y1 Up");
  private JButton Y1DownButton = new JButton("Set Y1 Down");
  private JButton Y2UpButton = new JButton("Set Y2 Up");
  private JButton Y2DownButton = new JButton("Set Y2 Down");

  private JButton ProbeFeatureButton = new JButton("Probe Feature!");

  private JLabel ZUpLabel = new JLabel("");
  private JLabel XUpLabel = new JLabel("");
  private JLabel XDownLabel = new JLabel("");
  private JLabel Y1UpLabel = new JLabel("");
  private JLabel Y1DownLabel = new JLabel("");
  private JLabel Y2UpLabel = new JLabel("");
  private JLabel Y2DownLabel = new JLabel("");

  private String[] DirectionList = { "X+", "X-", "Y+", "Y-", "Z+", "Z-" };
  private JComboBox<String> ZDirectionBox = new JComboBox<String>(
    DirectionList
  );
  private JComboBox<String> XDirectionBox = new JComboBox<String>(
    DirectionList
  );
  private JComboBox<String> YDirectionBox = new JComboBox<String>(
    DirectionList
  );

  FeatureFinderInstallationNodeContribution contribution;

  private ActionListener generalActionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        contribution.actionPerformed(e);
      } catch (Exception ex) {
        System.out.println("Exception: " + ex);
      }
    }
  };

  @Override
  public void buildUI(
    JPanel panel,
    final FeatureFinderInstallationNodeContribution contribution
  ) {
    this.contribution = contribution;
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    defaultBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    defaultBox.setAlignmentY(Component.TOP_ALIGNMENT);
    defaultBox.add(defaultViewBox(contribution));
    panel.add(defaultBox);
    panel.add(createPoseBox(contribution));
    panel.add(Box.createRigidArea(new Dimension(0, 10)));
    panel.add(ProbeFeatureButton);
    ProbeFeatureButton.setName("ProbeFeatureButton");
    ProbeFeatureButton.setPreferredSize(PROBE_FEATURE_BUTTON_DIMENSION);
    ProbeFeatureButton.setMaximumSize(PROBE_FEATURE_BUTTON_DIMENSION);
    ProbeFeatureButton.addActionListener(generalActionListener);
    panel.add(renameTextField);
    renameTextField.setPreferredSize(new Dimension(0, 0));
    renameTextField.setMaximumSize(renameTextField.getPreferredSize());
  }

  Box defaultViewBox(
    final FeatureFinderInstallationNodeContribution contribution
  ) {
    Box containerbox = Box.createVerticalBox();
    containerbox.setAlignmentX(Component.LEFT_ALIGNMENT);
    containerbox.setAlignmentY(Component.TOP_ALIGNMENT);
    Box content = Box.createVerticalBox();
    content.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.setAlignmentY(Component.TOP_ALIGNMENT);
    containerbox.add(createfeatureBox(contribution));
    containerbox.add(createButtons(contribution));
    containerbox.add(
      new JLabel(
        "Names must follow the following convention: [a-zA-Z][a-zA-Z0-9_]{0,14}"
      )
    );

    return containerbox;
  }

  private Box createPoseBox(
    final FeatureFinderInstallationNodeContribution contribution
  ) {
    buttonBox = Box.createVerticalBox();
    buttonBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonBox.setAlignmentY(Component.TOP_ALIGNMENT);

    Box compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(ZUpButton);
    ZUpButton.setName("ZUpButton");
    ZUpButton.setHorizontalTextPosition(SwingConstants.LEFT);
    ZUpButton.setPreferredSize(BUTTON_DIMENSION);
    ZUpButton.setMaximumSize(BUTTON_DIMENSION);
    ZUpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(ZDirectionBox);
    ZDirectionBox.setName("ZDirectionBox");
    ZDirectionBox.setPreferredSize(DIRECTION_DROPDOWN_DIMENSION);
    ZDirectionBox.setMaximumSize(DIRECTION_DROPDOWN_DIMENSION);
    ZDirectionBox.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(ZUpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(new Dimension(0, 10)));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(XUpButton);
    XUpButton.setName("XUpButton");
    XUpButton.setHorizontalTextPosition(SwingConstants.LEFT);
    XUpButton.setPreferredSize(BUTTON_DIMENSION);
    XUpButton.setMaximumSize(BUTTON_DIMENSION);
    XUpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(XDirectionBox);
    XDirectionBox.setName("XDirectionBox");
    XDirectionBox.setPreferredSize(DIRECTION_DROPDOWN_DIMENSION);
    XDirectionBox.setMaximumSize(DIRECTION_DROPDOWN_DIMENSION);
    XDirectionBox.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(XUpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(new Dimension(0, 10)));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(XDownButton);
    XDownButton.setName("XDownButton");
    XDownButton.setHorizontalTextPosition(SwingConstants.LEFT);
    XDownButton.setPreferredSize(BUTTON_DIMENSION);
    XDownButton.setMaximumSize(BUTTON_DIMENSION);
    XDownButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));

    compartment.add(XDownLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(new Dimension(0, 10)));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y1UpButton);
    Y1UpButton.setName("Y1UpButton");
    Y1UpButton.setHorizontalTextPosition(SwingConstants.LEFT);
    Y1UpButton.setPreferredSize(BUTTON_DIMENSION);
    Y1UpButton.setMaximumSize(BUTTON_DIMENSION);
    Y1UpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(YDirectionBox);
    YDirectionBox.setName("YDirectionBox");
    YDirectionBox.setPreferredSize(DIRECTION_DROPDOWN_DIMENSION);
    YDirectionBox.setMaximumSize(DIRECTION_DROPDOWN_DIMENSION);
    YDirectionBox.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Y1UpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(new Dimension(0, 10)));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y1DownButton);
    Y1DownButton.setName("Y1DownButton");
    Y1DownButton.setHorizontalTextPosition(SwingConstants.LEFT);
    Y1DownButton.setPreferredSize(BUTTON_DIMENSION);
    Y1DownButton.setMaximumSize(BUTTON_DIMENSION);
    Y1DownButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Y1DownLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(new Dimension(0, 10)));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y2UpButton);
    Y2UpButton.setName("Y2UpButton");
    Y2UpButton.setHorizontalTextPosition(SwingConstants.LEFT);
    Y2UpButton.setPreferredSize(BUTTON_DIMENSION);
    Y2UpButton.setMaximumSize(BUTTON_DIMENSION);
    Y2UpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Y2UpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(new Dimension(0, 10)));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y2DownButton);
    Y2DownButton.setName("Y2DownButton");
    Y2DownButton.setHorizontalTextPosition(SwingConstants.LEFT);
    Y2DownButton.setPreferredSize(BUTTON_DIMENSION);
    Y2DownButton.setMaximumSize(BUTTON_DIMENSION);
    Y2DownButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(new Dimension(10, 0)));
    compartment.add(Y2DownLabel);
    buttonBox.add(compartment);

    ZUpButton.setEnabled(false);
    XUpButton.setEnabled(false);
    XDownButton.setEnabled(false);
    Y1UpButton.setEnabled(false);
    Y1DownButton.setEnabled(false);
    Y2UpButton.setEnabled(false);
    Y2DownButton.setEnabled(false);

    return buttonBox;
  }

  private Box createfeatureBox(
    final FeatureFinderInstallationNodeContribution contribution
  ) {
    Box featureBox = Box.createVerticalBox();
    featureBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    featureBox.setAlignmentY(Component.TOP_ALIGNMENT);
    featureBox.setPreferredSize(new Dimension(500, 200));
    featureBox.setMaximumSize(new Dimension(500, 200));

    DefaultListModel<String> model = new DefaultListModel<String>();
    featureFrame.setModel(model);
    JScrollPane scrollPane = new JScrollPane(featureFrame);

    featureFrame.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    featureFrame.addListSelectionListener(
      new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          contribution.listItemClicked(e);
        }
      }
    );
    featureBox.add(scrollPane);
    return featureBox;
  }

  Box createButtons(
    final FeatureFinderInstallationNodeContribution contribution
  ) {
    Box buttonBox = Box.createHorizontalBox();
    buttonBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonBox.setAlignmentY(Component.TOP_ALIGNMENT);

    createFeatureButton = new JButton("Add");
    createFeatureButton.setName("addButton");
    createFeatureButton.addActionListener(generalActionListener);
    createFeatureButton.setEnabled(true);

    deleteFeatureButton = new JButton("Delete");
    deleteFeatureButton.setName("deleteButton");
    deleteFeatureButton.addActionListener(generalActionListener);
    deleteFeatureButton.setEnabled(false);

    renameFeatureButton = new JButton("Rename");
    renameFeatureButton.setName("renameButton");
    renameFeatureButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            String defaultName = featureFrame.getSelectedValue();
            KeyboardTextInput keyboardInput = contribution.getKeyboard(
              defaultName
            );
            keyboardInput.show(
              renameTextField,
              contribution.getCallbackForKeyboard()
            );
            // renameTextField.setVisible(false);

          } catch (Exception ex) {
            // Handle exception
            System.out.println("Exception: " + ex);
          }
        }
      }
    );
    renameFeatureButton.setEnabled(false);

    buttonBox.add(createFeatureButton);
    buttonBox.add(Box.createRigidArea(new Dimension(50, 0)));
    buttonBox.add(deleteFeatureButton);
    buttonBox.add(Box.createRigidArea(new Dimension(50, 0)));
    buttonBox.add(renameFeatureButton);

    return buttonBox;
  }

  public void enableButtons() {
    renameFeatureButton.setEnabled(true);
    deleteFeatureButton.setEnabled(true);
    ZUpButton.setEnabled(true);
    XUpButton.setEnabled(true);
    XDownButton.setEnabled(true);
    Y1UpButton.setEnabled(true);
    Y1DownButton.setEnabled(true);
    Y2UpButton.setEnabled(true);
    Y2DownButton.setEnabled(true);
    ProbeFeatureButton.setEnabled(true);

    ZUpLabel.setVisible(true);
    XUpLabel.setVisible(true);
    XDownLabel.setVisible(true);
    Y1UpLabel.setVisible(true);
    Y1DownLabel.setVisible(true);
    Y2UpLabel.setVisible(true);
    Y2DownLabel.setVisible(true);

    ZDirectionBox.setEnabled(true);
    XDirectionBox.setEnabled(true);
    YDirectionBox.setEnabled(true);
  }

  public void disableButtons() {
    renameFeatureButton.setEnabled(false);
    deleteFeatureButton.setEnabled(false);
    ZUpButton.setEnabled(false);
    XUpButton.setEnabled(false);
    XDownButton.setEnabled(false);
    Y1UpButton.setEnabled(false);
    Y1DownButton.setEnabled(false);
    Y2UpButton.setEnabled(false);
    Y2DownButton.setEnabled(false);
    ProbeFeatureButton.setEnabled(false);

    ZUpLabel.setVisible(false);
    XUpLabel.setVisible(false);
    XDownLabel.setVisible(false);
    Y1UpLabel.setVisible(false);
    Y1DownLabel.setVisible(false);
    Y2UpLabel.setVisible(false);
    Y2DownLabel.setVisible(false);

    ZDirectionBox.setEnabled(false);
    XDirectionBox.setEnabled(false);
    YDirectionBox.setEnabled(false);
  }

  public void updateFeatureList(String[] FeatureList) {
    DefaultListModel<String> model = new DefaultListModel<String>();
    featureFrame.setModel(model);
    if (FeatureList == null) {
      return;
    }
    for (int i = 0; i < FeatureList.length; i++) {
      model.add(i, FeatureList[i]);
    }
  }

  public int getFeatureListIndex() {
    return featureFrame.getSelectedIndex();
  }

  public String getFeatureListValue() {
    return featureFrame.getSelectedValue();
  }

  public void hideButtons() {
    buttonBox.setVisible(false);
  }

  public void setFeatureListIndex(int index) {
    featureFrame.setSelectedIndex(index);
  }

  private String shortenPose(String poseString) {
    // truncate each value to 4 decimal places
    String[] PoseArray = poseString.split(",");
    String returnPose =
      "p" +
      "[" +
      String.format("%.4f", Double.parseDouble(PoseArray[0].substring(2))) +
      "," +
      String.format("%.4f", Double.parseDouble(PoseArray[1])) +
      "," +
      String.format("%.4f", Double.parseDouble(PoseArray[2])) +
      "," +
      String.format("%.4f", Double.parseDouble(PoseArray[3])) +
      "," +
      String.format("%.4f", Double.parseDouble(PoseArray[4])) +
      "," +
      String.format(
        "%.4f",
        Double.parseDouble(PoseArray[5].substring(0, PoseArray[5].length() - 1))
      ) +
      "]";
    return returnPose;
  }

  public void updateProbeFeatureLables(ProbeFeatureClass ProbeFeature) {
    ZUpLabel.setText(shortenPose(ProbeFeature.getZUpPoseString()));
    XUpLabel.setText(shortenPose(ProbeFeature.getXUpPoseString()));
    XDownLabel.setText(shortenPose(ProbeFeature.getXDownPoseString()));
    Y1UpLabel.setText(shortenPose(ProbeFeature.getY1UpPoseString()));
    Y1DownLabel.setText(shortenPose(ProbeFeature.getY1DownPoseString()));
    Y2UpLabel.setText(shortenPose(ProbeFeature.getY2UpPoseString()));
    Y2DownLabel.setText(shortenPose(ProbeFeature.getY2DownPoseString()));

    ZDirectionBox.setSelectedIndex(ProbeFeature.getZDirectionIndex());
    XDirectionBox.setSelectedIndex(ProbeFeature.getXDirectionIndex());
    YDirectionBox.setSelectedIndex(ProbeFeature.getYDirectionIndex());

  }
}
