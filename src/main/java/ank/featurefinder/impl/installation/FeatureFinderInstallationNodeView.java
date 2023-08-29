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

  private static final Dimension SmallHorizontalRigidArea = new Dimension(10, 0);
  private static final Dimension SmallVerticalRigidArea = new Dimension(0, 10);

  private static final Dimension CoordinateLabelDimension = new Dimension(400, 30);
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
    panel.add(defaultBox);
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
    // containerbox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // upperBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // midSectionBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // lowerBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // leftLowerBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // rightLowerBox.setBorder(BorderFactory.createLineBorder(Color.black, 1));

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

  private Box createPoseBox(final FeatureFinderInstallationNodeContribution contribution) {
    buttonBox = Box.createVerticalBox();
    buttonBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonBox.setAlignmentY(Component.TOP_ALIGNMENT);
    buttonBox.setMaximumSize(new Dimension(750, 376));
    buttonBox.setPreferredSize(new Dimension(750, 376));

    ZUpLabel.setPreferredSize(CoordinateLabelDimension);
    ZUpLabel.setMaximumSize(CoordinateLabelDimension);
    XUpLabel.setPreferredSize(CoordinateLabelDimension);
    XUpLabel.setMaximumSize(CoordinateLabelDimension);
    XDownLabel.setPreferredSize(CoordinateLabelDimension);
    XDownLabel.setMaximumSize(CoordinateLabelDimension);
    Y1UpLabel.setPreferredSize(CoordinateLabelDimension);
    Y1UpLabel.setMaximumSize(CoordinateLabelDimension);
    Y1DownLabel.setPreferredSize(CoordinateLabelDimension);
    Y1DownLabel.setMaximumSize(CoordinateLabelDimension);
    Y2UpLabel.setPreferredSize(CoordinateLabelDimension);
    Y2UpLabel.setMaximumSize(CoordinateLabelDimension);
    Y2DownLabel.setPreferredSize(CoordinateLabelDimension);
    Y2DownLabel.setMaximumSize(CoordinateLabelDimension);

    // ZUpLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // XUpLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // XDownLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // Y1UpLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // Y1DownLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // Y2UpLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    // Y2DownLabel.setBorder(BorderFactory.createLineBorder(Color.black, 1));

    Box compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(ZUpButton);
    ZUpButton.setName("ZUpButton");
    ZUpButton.setPreferredSize(BUTTON_DIMENSION);
    ZUpButton.setMaximumSize(BUTTON_DIMENSION);
    ZUpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(ZDirectionBox);
    ZDirectionBox.setName("ZDirectionBox");
    ZDirectionBox.setPreferredSize(DIRECTION_DROPDOWN_DIMENSION);
    ZDirectionBox.setMaximumSize(DIRECTION_DROPDOWN_DIMENSION);
    ZDirectionBox.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(ZUpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(XUpButton);
    XUpButton.setName("XUpButton");
    XUpButton.setPreferredSize(BUTTON_DIMENSION);
    XUpButton.setMaximumSize(BUTTON_DIMENSION);
    XUpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(XDirectionBox);
    XDirectionBox.setName("XDirectionBox");
    XDirectionBox.setPreferredSize(DIRECTION_DROPDOWN_DIMENSION);
    XDirectionBox.setMaximumSize(DIRECTION_DROPDOWN_DIMENSION);
    XDirectionBox.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(XUpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(XDownButton);
    XDownButton.setName("XDownButton");
    XDownButton.setPreferredSize(BUTTON_DIMENSION);
    XDownButton.setMaximumSize(BUTTON_DIMENSION);
    XDownButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));

    compartment.add(XDownLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y1UpButton);
    Y1UpButton.setName("Y1UpButton");
    Y1UpButton.setPreferredSize(BUTTON_DIMENSION);
    Y1UpButton.setMaximumSize(BUTTON_DIMENSION);
    Y1UpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(YDirectionBox);
    YDirectionBox.setName("YDirectionBox");
    YDirectionBox.setPreferredSize(DIRECTION_DROPDOWN_DIMENSION);
    YDirectionBox.setMaximumSize(DIRECTION_DROPDOWN_DIMENSION);
    YDirectionBox.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Y1UpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y1DownButton);
    Y1DownButton.setName("Y1DownButton");
    Y1DownButton.setPreferredSize(BUTTON_DIMENSION);
    Y1DownButton.setMaximumSize(BUTTON_DIMENSION);
    Y1DownButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Y1DownLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y2UpButton);
    Y2UpButton.setName("Y2UpButton");
    Y2UpButton.setPreferredSize(BUTTON_DIMENSION);
    Y2UpButton.setMaximumSize(BUTTON_DIMENSION);
    Y2UpButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Y2UpLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    compartment.add(Y2DownButton);
    Y2DownButton.setName("Y2DownButton");
    Y2DownButton.setPreferredSize(BUTTON_DIMENSION);
    Y2DownButton.setMaximumSize(BUTTON_DIMENSION);
    Y2DownButton.addActionListener(generalActionListener);
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Box.createRigidArea(DIRECTION_DROPDOWN_DIMENSION));
    compartment.add(Box.createRigidArea(SmallHorizontalRigidArea));
    compartment.add(Y2DownLabel);
    buttonBox.add(compartment);

    buttonBox.add(Box.createRigidArea(SmallVerticalRigidArea));
    compartment = Box.createHorizontalBox();
    compartment.setAlignmentX(Component.LEFT_ALIGNMENT);
    compartment.setAlignmentY(Component.TOP_ALIGNMENT);
    ProbeFeatureButton.setName("ProbeFeatureButton");
    ProbeFeatureButton.setPreferredSize(PROBE_FEATURE_BUTTON_DIMENSION);
    ProbeFeatureButton.setMaximumSize(PROBE_FEATURE_BUTTON_DIMENSION);
    ProbeFeatureButton.addActionListener(generalActionListener);
    compartment.add(ProbeFeatureButton);
    buttonBox.add(compartment);

    renameTextField.setPreferredSize(new Dimension(0, 0));
    renameTextField.setMaximumSize(renameTextField.getPreferredSize());

    buttonBox.add(renameTextField);

    ZUpButton.setEnabled(false);
    XUpButton.setEnabled(false);
    XDownButton.setEnabled(false);
    Y1UpButton.setEnabled(false);
    Y1DownButton.setEnabled(false);
    Y2UpButton.setEnabled(false);
    Y2DownButton.setEnabled(false);
    ProbeFeatureButton.setEnabled(false);

    return buttonBox;
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
    createFeatureButton.setName("addButton");
    createFeatureButton.addActionListener(generalActionListener);
    createFeatureButton.setEnabled(true);
    createFeatureButton.setPreferredSize(BUTTONBAR_BUTTON_DIMENSION);

    deleteFeatureButton = new JButton("Delete");
    deleteFeatureButton.setName("deleteButton");
    deleteFeatureButton.addActionListener(generalActionListener);
    deleteFeatureButton.setEnabled(false);
    deleteFeatureButton.setPreferredSize(BUTTONBAR_BUTTON_DIMENSION);

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
    Color undefinedYellow =new Color(255, 255, 112);

    ZUpButton.setBackground(ProbeFeature.getZUpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    XUpButton.setBackground(ProbeFeature.getXUpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    XDownButton.setBackground(ProbeFeature.getXDownPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y1UpButton.setBackground(ProbeFeature.getY1UpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y1DownButton.setBackground(ProbeFeature.getY1DownPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y2UpButton.setBackground(ProbeFeature.getY2UpPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);
    Y2DownButton.setBackground(ProbeFeature.getY2DownPoseString().equals(DefaultVariables.DefaultPose) ? undefinedYellow : null);


    ProbeFeatureButton.setEnabled(ProbeFeature.isDefined()? true : false);

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
