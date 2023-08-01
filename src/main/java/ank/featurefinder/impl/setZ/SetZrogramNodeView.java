package ank.featurefinder.impl.setZ;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// mouse listener
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
// import com.ur.urcap.api.domain.variable.Variable;

import javax.swing.*;


public class SetZrogramNodeView
  implements SwingProgramNodeView<SetZProgramNodeContribution> {

  private JComboBox<String> features = new JComboBox<String>();
  private JComboBox<String> directionList = new JComboBox<String>(
    new String[] { "X+", "X-", "Y+", "Y-", "Z+", "Z-" }
  );
  private JButton startPositionButton = new JButton("Set Start Position");
  public JTextField speed = new JTextField();
  public JTextField force = new JTextField();

  @Override
  public void buildUI(
    JPanel jPanel,
    final ContributionProvider<SetZProgramNodeContribution> contributionProvider
  ) {
    jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
    Box content = Box.createHorizontalBox();
    content.setPreferredSize(new Dimension(550, 50));
    content.setMaximumSize(content.getPreferredSize());
    content.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.setAlignmentY(Component.TOP_ALIGNMENT);
    features.setPreferredSize(new Dimension(250, 50));
    features.setMaximumSize(features.getPreferredSize());
    features.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          contributionProvider.get().actionPerformed(e);
        }
      }
    );
    features.setName("features");
    content.add(features);
    jPanel.add(content);

    content = Box.createHorizontalBox();
    content.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.setAlignmentY(Component.TOP_ALIGNMENT);
    startPositionButton.setPreferredSize(new Dimension(200, 50));
    startPositionButton.setMaximumSize(startPositionButton.getPreferredSize());
    startPositionButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          contributionProvider.get().actionPerformed(e);
        }
      }
    );
    startPositionButton.setName("startPositionButton");
    // content.add(startPositionButton);
    // content.add(Box.createRigidArea(new Dimension(40, 50)));
    directionList.setPreferredSize(new Dimension(200, 50));
    directionList.setMaximumSize(directionList.getPreferredSize());
    directionList.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          contributionProvider.get().actionPerformed(e);
        }
      }
    );
    directionList.setName("directionList");
    content.add(directionList);

    jPanel.add(content);
    content = Box.createHorizontalBox();
    content.setPreferredSize(new Dimension(550, 50));
    content.setMaximumSize(content.getPreferredSize());
    content.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.setAlignmentY(Component.TOP_ALIGNMENT);
    content.add(new JLabel("Speed mm/s:"));
    speed.setBorder(BorderFactory.createLineBorder(Color.black));
    speed.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          KeyboardNumberInput<Integer> keyboardInput = contributionProvider.get().getKeyboard(contributionProvider.get().model.get("speed", 25));
          keyboardInput.show(
            speed,
            contributionProvider.get().getCallbackForSpeedKeyboard()
          );
        }
      }
    );
    content.add(speed);
    content.add(Box.createRigidArea(new Dimension(40, 50)));
    content.add(new JLabel("Distance mm:"));
    force.setBorder(BorderFactory.createLineBorder(Color.black));
    force.addMouseListener(
      new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          KeyboardNumberInput<Integer> keyboardInput = contributionProvider.get().getKeyboard(contributionProvider.get().model.get("force", 5));
          keyboardInput.show(
            force,
            contributionProvider.get().getCallbackForForceKeyboard()
          );
        }
      }
    );
    content.add(force);
    jPanel.add(content);
  }

  public void updateFeaturesList(String[] list) {
    features.removeAllItems();
    if (list == null) {
      return;
    }
    for (int i = 0; i < list.length; i++) {
      features.addItem(list[i]);
    }
  }

  public String getFeatureName() {
    return (String) features.getSelectedItem();
  }

  public int getDirection() {
    return directionList.getSelectedIndex();
  }

  public void setSpeed(double speed) {
    this.speed.setText(String.valueOf(speed));
  }

  public void setForce(double force) {
    this.force.setText(String.valueOf(force));
  }

  public void setDirection(int direction) {
    this.directionList.setSelectedIndex(direction);
  }

  public void setFeatureIndex(int index) {
    // System.out.println("Setting feature index to " + index);
    this.features.setSelectedIndex(index);
  }
}
