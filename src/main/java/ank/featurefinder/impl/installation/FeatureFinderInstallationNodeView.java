package ank.featurefinder.impl.installation;

import ank.featurefinder.DefaultVariables;
import ank.featurefinder.impl.ProbeFeatureClass;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FeatureFinderInstallationNodeView implements SwingInstallationNodeView<FeatureFinderInstallationNodeContribution> {

  private static final Dimension BUTTON_DIMENSION = new Dimension(190, 30);
  private static final Dimension DIRECTION_DROPDOWN_DIMENSION = new Dimension(110, 30);
  private static final Dimension PROBE_FEATURE_BUTTON_DIMENSION = new Dimension(190, 50);
  private static final Dimension BUTTONBAR_BUTTON_DIMENSION = new Dimension(90, 40);
  private static final Dimension SPEED_LABEL_DIMENSION = new Dimension(200, 30);
  private static final Dimension SPEED_TEXTFIELD_DIMENSION = new Dimension(70, 30);

  private static final Dimension MOVETOBUTTON_DIMENSION = new Dimension(250, 30);

  private static final Dimension SmallHorizontalRigidArea = new Dimension(10, 0);
  private static final Dimension SmallVerticalRigidArea = new Dimension(0, 10);

  private static final Dimension CoordinateLabelDimension = new Dimension(400, 30);
  private JButton createFeatureButton;
  private JButton deleteFeatureButton;
  public JButton renameFeatureButton;
  public JTextField renameTextField = new JTextField(20);
  private Box defaultBox = Box.createHorizontalBox();
  private Box licenseBox = Box.createHorizontalBox();
  public JList<String> featureFrame = new JList<String>();

  private Box buttonBox;

  private JButton ZUpButton = new JButton("Set Z Up");
  private JButton XUpButton = new JButton("Set X Up");
  private JButton XDownButton = new JButton("Set X Down");
  private JButton Y1UpButton = new JButton("Set Y1 Up");
  private JButton Y1DownButton = new JButton("Set Y1 Down");
  private JButton Y2UpButton = new JButton("Set Y2 Up");
  private JButton Y2DownButton = new JButton("Set Y2 Down");

  private JButton ZUpMoveToButton = new JButton("Move to Z Up");
  private JButton XUpMoveToButton = new JButton("Move to X Up");
  private JButton XDownMoveToButton = new JButton("Move to X Down");
  private JButton Y1UpMoveToButton = new JButton("Move to Y1 Up");
  private JButton Y1DownMoveToButton = new JButton("Move to Y1 Down");
  private JButton Y2UpMoveToButton = new JButton("Move to Y2 Up");
  private JButton Y2DownMoveToButton = new JButton("Move to Y2 Down");

  private JButton ProbeFeatureButton = new JButton("Probe Feature!");

  private JButton LicenseSelectionButton = new JButton("Select License File");

  private JLabel ZUpLabel = new JLabel();
  private JLabel XUpLabel = new JLabel();
  private JLabel XDownLabel = new JLabel();
  private JLabel Y1UpLabel = new JLabel();
  private JLabel Y1DownLabel = new JLabel();
  private JLabel Y2UpLabel = new JLabel();
  private JLabel Y2DownLabel = new JLabel();

  // add a textinput

  private JLabel RapidSpeedLabel = new JLabel("Rapid Speed (mm/s):");
  private JLabel RapidAccLabel = new JLabel("Rapid Acc. (mm/s^2):");
  private JLabel ProbingSpeedLabel = new JLabel("Probing Speed (mm/s):");
  private JLabel DoubleProbeLabel = new JLabel("Double Probe:");
  private JTextField RapidSpeedField = new JTextField();
  private JTextField RapidAccField = new JTextField();
  private JTextField ProbingSpeed = new JTextField();
  private JCheckBox DoubleProbeBox = new JCheckBox();

  private String[] DirectionList = { "X+", "X-", "Y+", "Y-", "Z+", "Z-" };
  private JComboBox<String> ZDirectionBox = new JComboBox<String>(DirectionList);
  private JComboBox<String> XDirectionBox = new JComboBox<String>(DirectionList);
  private JComboBox<String> YDirectionBox = new JComboBox<String>(DirectionList);

  private String image_path = ("/Diagram/ProbeDiagram.png");
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
  public void buildUI(JPanel panel, final FeatureFinderInstallationNodeContribution contribution) {
    this.contribution = contribution;
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    defaultBox = defaultViewBox(contribution);
    licenseBox = licenseSelectionBox(contribution);

    panel.add(defaultBox);
    panel.add(licenseBox);
    renameTextField.setPreferredSize(new Dimension(1, 1));
    renameTextField.setMaximumSize(renameTextField.getPreferredSize());
    // Make the text field transparent
    renameTextField.setOpaque(false);

    // Remove any border (optional)
    renameTextField.setBorder(null);
    renameTextField.setFocusable(false);

    panel.add(renameTextField);
  }

  private Box licenseSelectionBox(final FeatureFinderInstallationNodeContribution contribution) {
    Box containerbox = Box.createHorizontalBox();
    containerbox.setAlignmentX(Component.LEFT_ALIGNMENT);
    containerbox.setAlignmentY(Component.TOP_ALIGNMENT);
    containerbox.add(new JLabel("Please select the license file you want to use:"));
    LicenseSelectionButton.addActionListener(generalActionListener);
    LicenseSelectionButton.setName("LicenseSelectionButton");
    containerbox.add(Box.createRigidArea(new Dimension(10, 0)));
    containerbox.add(LicenseSelectionButton);

    return containerbox;
  }

  private Box defaultViewBox(final FeatureFinderInstallationNodeContribution contribution) {
    Box containerbox = Box.createVerticalBox();
    containerbox.setAlignmentX(Component.LEFT_ALIGNMENT);
    containerbox.setAlignmentY(Component.TOP_ALIGNMENT);
    containerbox.setMaximumSize(new Dimension(1080, 626));
    containerbox.setPreferredSize(new Dimension(1080, 626));

    Box upperBox = Box.createHorizontalBox();
    upperBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    upperBox.setAlignmentY(Component.TOP_ALIGNMENT);
    upperBox.setMaximumSize(new Dimension(1080, 190));
    upperBox.setPreferredSize(new Dimension(1080, 190));
    upperBox.add(createfeatureBox(contribution));
    upperBox.add(Box.createRigidArea(new Dimension(90, 190)));
    ImageIcon imageIcon = new ImageIcon(getClass().getResource(image_path));
    Image image = imageIcon.getImage();

    int maxWidth = 500; // The maximum width of the image
    int maxHeight = 190; // The maximum height of the container

    int newWidth;
    int newHeight;

    // Calculate new dimensions while maintaining aspect ratio
    if (image.getWidth(null) > maxWidth) {
      newWidth = maxWidth;
      newHeight = (int) (((double) image.getHeight(null) / image.getWidth(null)) * newWidth);
    } else if (image.getHeight(null) > maxHeight) {
      newHeight = maxHeight;
      newWidth = (int) (((double) image.getWidth(null) / image.getHeight(null)) * newHeight);
    } else {
      // No need to resize
      newWidth = image.getWidth(null);
      newHeight = image.getHeight(null);
    }
    // Scale the image
    Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

    // Create a new ImageIcon from the scaled image
    ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
    JLabel imagelLabel = new JLabel(scaledImageIcon);
    upperBox.add(imagelLabel);

    Box midSectionBox = createButtons(contribution);

    Box lowerBox = Box.createHorizontalBox();
    lowerBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    lowerBox.setAlignmentY(Component.TOP_ALIGNMENT);
    lowerBox.setMaximumSize(new Dimension(1080, 376));
    lowerBox.setPreferredSize(new Dimension(1080, 376));

    Box leftLowerBox = createPoseBox(contribution);

    Box rightLowerBox = createSpeedsBox(contribution);

    lowerBox.add(leftLowerBox);
    lowerBox.add(rightLowerBox);

    containerbox.add(upperBox);
    containerbox.add(Box.createRigidArea(new Dimension(0, 10)));
    containerbox.add(midSectionBox);
    containerbox.add(Box.createRigidArea(new Dimension(0, 10)));
    containerbox.add(lowerBox);

    return containerbox;
  }

  private Box createSpeedsBox(final FeatureFinderInstallationNodeContribution contribution) {
    RapidSpeedField.setEnabled(false);
    RapidAccField.setEnabled(false);
    ProbingSpeed.setEnabled(false);
    DoubleProbeBox.setEnabled(false);

    RapidSpeedField.setName("RapidSpeedField");
    RapidAccField.setName("RapidAccField");
    ProbingSpeed.setName("ProbingSpeed");
    DoubleProbeBox.setName("DoubleProbeBox");

    RapidSpeedField.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          System.out.println("RapidSpeedField: " + RapidSpeedField.getText());
          // get double value from text field
          Double dValue = Double.parseDouble(RapidSpeedField.getText());
          Integer value = dValue.intValue();
          KeyboardNumberInput<Integer> keyboardInput = contribution.getKeyboardNumber(value);
          keyboardInput.show(RapidSpeedField, contribution.getCallbackForKeyboardNumber(1));
        }
      }
    );
    RapidAccField.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          System.out.println("RapidAccField: " + RapidAccField.getText());
          Double dValue = Double.parseDouble(RapidSpeedField.getText());
          Integer value = dValue.intValue();
          KeyboardNumberInput<Integer> keyboardInput = contribution.getKeyboardNumber(value);
          keyboardInput.show(RapidAccField, contribution.getCallbackForKeyboardNumber(2));
        }
      }
    );
    ProbingSpeed.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          System.out.println("ProbingSpeed: " + ProbingSpeed.getText());
          Double dValue = Double.parseDouble(RapidSpeedField.getText());
          Integer value = dValue.intValue();
          KeyboardNumberInput<Integer> keyboardInput = contribution.getKeyboardNumber(value);
          keyboardInput.show(ProbingSpeed, contribution.getCallbackForKeyboardNumber(3));
        }
      }
    );
    DoubleProbeBox.addActionListener(generalActionListener);

    Box speedBox = Box.createVerticalBox();
    speedBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    speedBox.setAlignmentY(Component.TOP_ALIGNMENT);
    speedBox.setMaximumSize(new Dimension(330, 376));
    speedBox.setPreferredSize(new Dimension(330, 376));

    Box compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    RapidSpeedLabel = new JLabel("Rapid Speed (mm/s):");
    RapidSpeedLabel.setPreferredSize(SPEED_LABEL_DIMENSION);
    RapidSpeedLabel.setMaximumSize(SPEED_LABEL_DIMENSION);
    compartment.add(RapidSpeedLabel);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    RapidSpeedField.setPreferredSize(SPEED_TEXTFIELD_DIMENSION);
    RapidSpeedField.setMaximumSize(SPEED_TEXTFIELD_DIMENSION);

    // RapidSpeedField.addActionListener(generalActionListener);
    compartment.add(RapidSpeedField);
    speedBox.add(compartment);

    speedBox.add(Box.createRigidArea(SmallVerticalRigidArea));

    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    RapidAccLabel = new JLabel("Rapid Acc. (mm/s^2):");
    RapidAccLabel.setPreferredSize(SPEED_LABEL_DIMENSION);
    RapidAccLabel.setMaximumSize(SPEED_LABEL_DIMENSION);
    compartment.add(RapidAccLabel);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    RapidAccField.setPreferredSize(SPEED_TEXTFIELD_DIMENSION);
    RapidAccField.setMaximumSize(SPEED_TEXTFIELD_DIMENSION);
    RapidAccField.addActionListener(generalActionListener);
    compartment.add(RapidAccField);
    speedBox.add(compartment);

    speedBox.add(Box.createRigidArea(SmallVerticalRigidArea));

    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    ProbingSpeedLabel = new JLabel("Probing Speed (mm/s):");
    ProbingSpeedLabel.setPreferredSize(SPEED_LABEL_DIMENSION);
    ProbingSpeedLabel.setMaximumSize(SPEED_LABEL_DIMENSION);
    compartment.add(ProbingSpeedLabel);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    ProbingSpeed.setPreferredSize(SPEED_TEXTFIELD_DIMENSION);
    ProbingSpeed.setMaximumSize(SPEED_TEXTFIELD_DIMENSION);
    ProbingSpeed.addActionListener(generalActionListener);
    compartment.add(ProbingSpeed);
    speedBox.add(compartment);

    speedBox.add(Box.createRigidArea(SmallVerticalRigidArea));

    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    DoubleProbeLabel = new JLabel("Double Probe:");
    DoubleProbeLabel.setPreferredSize(SPEED_LABEL_DIMENSION);
    DoubleProbeLabel.setMaximumSize(SPEED_LABEL_DIMENSION);
    compartment.add(DoubleProbeLabel);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(DoubleProbeBox);
    speedBox.add(compartment);

    return speedBox;
  }

  private void buttonSetUp(JButton button, Dimension dimensions, String name, ActionListener actionListener) {
    button.setName(name);
    button.setPreferredSize(dimensions);
    button.setMaximumSize(dimensions);
    button.addActionListener(actionListener);
  }

  private void dropdownSetUp(JComboBox<String> dropdown, Dimension dimensions, String name, ActionListener actionListener) {
    dropdown.setName(name);
    dropdown.setPreferredSize(dimensions);
    dropdown.setMaximumSize(dimensions);
    dropdown.addActionListener(actionListener);
  }

  private Box createPoseBox(final FeatureFinderInstallationNodeContribution contribution) {
    Box outerBox = Box.createHorizontalBox();
    outerBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    outerBox.setAlignmentY(Component.TOP_ALIGNMENT);
    outerBox.setMaximumSize(new Dimension(750, 376));
    outerBox.setPreferredSize(new Dimension(750, 376));

    buttonBox = Box.createVerticalBox();
    buttonBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonBox.setAlignmentY(Component.TOP_ALIGNMENT);
    buttonBox.setMaximumSize(new Dimension(BUTTON_DIMENSION.width, 376));
    buttonBox.setPreferredSize(new Dimension(BUTTON_DIMENSION.width, 376));

    buttonSetUp(ZUpButton, BUTTON_DIMENSION, "ZUpButton", generalActionListener);
    buttonSetUp(XUpButton, BUTTON_DIMENSION, "XUpButton", generalActionListener);
    buttonSetUp(XDownButton, BUTTON_DIMENSION, "XDownButton", generalActionListener);
    buttonSetUp(Y1UpButton, BUTTON_DIMENSION, "Y1UpButton", generalActionListener);
    buttonSetUp(Y1DownButton, BUTTON_DIMENSION, "Y1DownButton", generalActionListener);
    buttonSetUp(Y2UpButton, BUTTON_DIMENSION, "Y2UpButton", generalActionListener);
    buttonSetUp(Y2DownButton, BUTTON_DIMENSION, "Y2DownButton", generalActionListener);
    buttonSetUp(ProbeFeatureButton, PROBE_FEATURE_BUTTON_DIMENSION, "ProbeFeatureButton", generalActionListener);

    dropdownSetUp(ZDirectionBox, DIRECTION_DROPDOWN_DIMENSION, "ZDirectionBox", generalActionListener);
    dropdownSetUp(XDirectionBox, DIRECTION_DROPDOWN_DIMENSION, "XDirectionBox", generalActionListener);
    dropdownSetUp(YDirectionBox, DIRECTION_DROPDOWN_DIMENSION, "YDirectionBox", generalActionListener);

    buttonSetUp(ZUpMoveToButton, MOVETOBUTTON_DIMENSION, "ZUpMoveToButton", generalActionListener);
    buttonSetUp(XUpMoveToButton, MOVETOBUTTON_DIMENSION, "XUpMoveToButton", generalActionListener);
    buttonSetUp(XDownMoveToButton, MOVETOBUTTON_DIMENSION, "XDownMoveToButton", generalActionListener);
    buttonSetUp(Y1UpMoveToButton, MOVETOBUTTON_DIMENSION, "Y1UpMoveToButton", generalActionListener);
    buttonSetUp(Y1DownMoveToButton, MOVETOBUTTON_DIMENSION, "Y1DownMoveToButton", generalActionListener);
    buttonSetUp(Y2UpMoveToButton, MOVETOBUTTON_DIMENSION, "Y2UpMoveToButton", generalActionListener);
    buttonSetUp(Y2DownMoveToButton, MOVETOBUTTON_DIMENSION, "Y2DownMoveToButton", generalActionListener);

    buttonBox.add(ZUpButton);
    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    buttonBox.add(XUpButton);
    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    buttonBox.add(XDownButton);
    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    buttonBox.add(Y1UpButton);
    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    buttonBox.add(Y1DownButton);
    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    buttonBox.add(Y2UpButton);
    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    buttonBox.add(Y2DownButton);
    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    buttonBox.add(ProbeFeatureButton);

    outerBox.add(buttonBox);
    outerBox.add(Box.createRigidArea(SmallHorizontalRigidArea));

    Box dropdownBox = Box.createVerticalBox();
    dropdownBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    dropdownBox.setAlignmentY(Component.TOP_ALIGNMENT);
    dropdownBox.setMaximumSize(new Dimension(DIRECTION_DROPDOWN_DIMENSION.width, 376));
    dropdownBox.setPreferredSize(new Dimension(DIRECTION_DROPDOWN_DIMENSION.width, 376));

    dropdownBox.add(ZDirectionBox);
    dropdownBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    dropdownBox.add(XDirectionBox);
    dropdownBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    dropdownBox.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    dropdownBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    dropdownBox.add(YDirectionBox);

    outerBox.add(dropdownBox);
    outerBox.add(Box.createRigidArea(SmallHorizontalRigidArea));

    Box moveToBox = Box.createVerticalBox();
    moveToBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    moveToBox.setAlignmentY(Component.TOP_ALIGNMENT);
    moveToBox.setMaximumSize(new Dimension(MOVETOBUTTON_DIMENSION.width, 376));
    moveToBox.setPreferredSize(new Dimension(MOVETOBUTTON_DIMENSION.width, 376));

    moveToBox.add(ZUpMoveToButton);
    moveToBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    moveToBox.add(XUpMoveToButton);
    moveToBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    moveToBox.add(XDownMoveToButton);
    moveToBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    moveToBox.add(Y1UpMoveToButton);
    moveToBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    moveToBox.add(Y1DownMoveToButton);
    moveToBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    moveToBox.add(Y2UpMoveToButton);

    outerBox.add(moveToBox);

    return outerBox;
  }

  private JScrollPane createfeatureBox(final FeatureFinderInstallationNodeContribution contribution) {
    DefaultListModel<String> model = new DefaultListModel<String>();
    featureFrame.setModel(model);
    JScrollPane scrollPane = new JScrollPane(featureFrame);
    scrollPane.setPreferredSize(new Dimension(490, 190));
    scrollPane.setMaximumSize(new Dimension(490, 190));

    featureFrame.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    featureFrame.addListSelectionListener(
      new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          contribution.listItemClicked(e);
        }
      }
    );
    featureFrame.setCellRenderer(new FeatureListRenderer());
    return scrollPane;
  }

  private Box createButtons(final FeatureFinderInstallationNodeContribution contribution) {
    Box buttonBox = Box.createHorizontalBox();
    buttonBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonBox.setAlignmentY(Component.TOP_ALIGNMENT);
    buttonBox.setMaximumSize(new Dimension(1080, 40));
    buttonBox.setPreferredSize(new Dimension(1080, 40));

    createFeatureButton = new JButton("Add");
    buttonSetUp(createFeatureButton, BUTTONBAR_BUTTON_DIMENSION, "addButton", generalActionListener);

    deleteFeatureButton = new JButton("Delete");
    buttonSetUp(deleteFeatureButton, BUTTONBAR_BUTTON_DIMENSION, "deleteButton", generalActionListener);

    renameFeatureButton = new JButton("Rename");
    renameFeatureButton.setName("renameButton");
    renameFeatureButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            String defaultName = featureFrame.getSelectedValue();
            KeyboardTextInput keyboardInput = contribution.getKeyboard(defaultName);
            keyboardInput.show(renameTextField, contribution.getCallbackForKeyboard());
            // renameTextField.setVisible(false);

          } catch (Exception ex) {
            // Handle exception
            System.out.println("Exception: " + ex);
          }
        }
      }
    );
    renameFeatureButton.setEnabled(false);
    renameFeatureButton.setPreferredSize(BUTTONBAR_BUTTON_DIMENSION);

    buttonBox.add(Box.createRigidArea(new Dimension(55, 0)));
    buttonBox.add(createFeatureButton);
    buttonBox.add(Box.createRigidArea(new Dimension(55, 0)));
    buttonBox.add(deleteFeatureButton);
    buttonBox.add(Box.createRigidArea(new Dimension(55, 0)));
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

    ZUpLabel.setEnabled(true);
    XUpLabel.setEnabled(true);
    XDownLabel.setEnabled(true);
    Y1UpLabel.setEnabled(true);
    Y1DownLabel.setEnabled(true);
    Y2UpLabel.setEnabled(true);
    Y2DownLabel.setEnabled(true);

    ZDirectionBox.setEnabled(true);
    XDirectionBox.setEnabled(true);
    YDirectionBox.setEnabled(true);

    RapidSpeedField.setEnabled(true);
    RapidAccField.setEnabled(true);
    ProbingSpeed.setEnabled(true);
    DoubleProbeBox.setEnabled(true);

    RapidSpeedLabel.setEnabled(true);
    RapidAccLabel.setEnabled(true);
    ProbingSpeedLabel.setEnabled(true);
    DoubleProbeLabel.setEnabled(true);

    ZUpMoveToButton.setEnabled(true);
    XUpMoveToButton.setEnabled(true);
    XDownMoveToButton.setEnabled(true);
    Y1UpMoveToButton.setEnabled(true);
    Y1DownMoveToButton.setEnabled(true);
    Y2UpMoveToButton.setEnabled(true);
    Y2DownMoveToButton.setEnabled(true);
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

    ZUpLabel.setEnabled(false);
    XUpLabel.setEnabled(false);
    XDownLabel.setEnabled(false);
    Y1UpLabel.setEnabled(false);
    Y1DownLabel.setEnabled(false);
    Y2UpLabel.setEnabled(false);
    Y2DownLabel.setEnabled(false);

    ZUpLabel.setText("p[0.000,0.000,0.000,0.000,0.000,0.000]");
    XUpLabel.setText("p[0.000,0.000,0.000,0.000,0.000,0.000]");
    XDownLabel.setText("p[0.000,0.000,0.000,0.000,0.000,0.000]");
    Y1UpLabel.setText("p[0.000,0.000,0.000,0.000,0.000,0.000]");
    Y1DownLabel.setText("p[0.000,0.000,0.000,0.000,0.000,0.000]");
    Y2UpLabel.setText("p[0.000,0.000,0.000,0.000,0.000,0.000]");
    Y2DownLabel.setText("p[0.000,0.000,0.000,0.000,0.000,0.000]");

    ZDirectionBox.setEnabled(false);
    XDirectionBox.setEnabled(false);
    YDirectionBox.setEnabled(false);

    RapidSpeedField.setEnabled(false);
    RapidAccField.setEnabled(false);
    ProbingSpeed.setEnabled(false);
    DoubleProbeBox.setEnabled(false);

    RapidSpeedField.setText("");
    RapidAccField.setText("");
    ProbingSpeed.setText("");
    DoubleProbeBox.setSelected(false);

    RapidSpeedLabel.setEnabled(false);
    RapidAccLabel.setEnabled(false);
    ProbingSpeedLabel.setEnabled(false);
    DoubleProbeLabel.setEnabled(false);

    ZUpButton.setBackground(null);
    XUpButton.setBackground(null);
    XDownButton.setBackground(null);
    Y1UpButton.setBackground(null);
    Y1DownButton.setBackground(null);
    Y2UpButton.setBackground(null);
    Y2DownButton.setBackground(null);

    ZUpMoveToButton.setEnabled(false);
    XUpMoveToButton.setEnabled(false);
    XDownMoveToButton.setEnabled(false);
    Y1UpMoveToButton.setEnabled(false);
    Y1DownMoveToButton.setEnabled(false);
    Y2UpMoveToButton.setEnabled(false);
    Y2DownMoveToButton.setEnabled(false);

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

  public void setFeatureListIndex(int index) {
    featureFrame.setSelectedIndex(index);
  }

  private String shortenPose(String poseString) {
    // truncate each value to 4 decimal places
    String[] PoseArray = poseString.split(",");
    String returnPose = "p" + "[" + String.format("%.3f", Double.parseDouble(PoseArray[0].substring(2))) + "," + String.format("%.3f", Double.parseDouble(PoseArray[1])) + "," + String.format("%.3f", Double.parseDouble(PoseArray[2])) + "," + String.format("%.3f", Double.parseDouble(PoseArray[3])) + "," + String.format("%.3f", Double.parseDouble(PoseArray[4])) + "," + String.format("%.3f", Double.parseDouble(PoseArray[5].substring(0, PoseArray[5].length() - 1))) + "]";
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

    RapidSpeedField.setText(Double.toString(ProbeFeature.getRapidSpeed()));
    RapidAccField.setText(Double.toString(ProbeFeature.getRapidAcceleration()));
    ProbingSpeed.setText(Double.toString(ProbeFeature.getProbeSpeed()));

    DoubleProbeBox.setSelected(ProbeFeature.getDoubleProbe());
    featureFrame.repaint();
    Color undefinedYellow = new Color(255, 255, 112);

    ZUpButton.setBackground(ProbeFeature.getZUpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    XUpButton.setBackground(ProbeFeature.getXUpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    XDownButton.setBackground(ProbeFeature.getXDownPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y1UpButton.setBackground(ProbeFeature.getY1UpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y1DownButton.setBackground(ProbeFeature.getY1DownPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y2UpButton.setBackground(ProbeFeature.getY2UpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y2DownButton.setBackground(ProbeFeature.getY2DownPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);

    ProbeFeatureButton.setEnabled(ProbeFeature.isDefined() ? true : false);
  }

  public void setLicenseBoxVisible(boolean visible) {
    licenseBox.setVisible(visible);
    defaultBox.setVisible(!visible);
  }

  private class FeatureListRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      // System.out.println("index: " + index);
      ProbeFeatureClass currentProbeFeature = contribution.getProbeFeatureObject(index);
      if (currentProbeFeature == null) {
        return component;
      }
      // Set custom background colors based on criteria
      if (!isSelected) {
        if (currentProbeFeature.isDefined()) { // Example: every even index has a yellow background
          component.setBackground(Color.WHITE);
        } else {
          // component.setBackground(Color.YELLOW);
          component.setBackground(new Color(255, 255, 112));
        }
      }

      return component;
    }
  }
}
