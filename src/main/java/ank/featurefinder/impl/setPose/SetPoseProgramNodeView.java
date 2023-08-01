package ank.featurefinder.impl.setPose;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import com.ur.urcap.api.domain.variable.Variable;

import javax.swing.*;

public class SetPoseProgramNodeView
  implements SwingProgramNodeView<SetPoseProgramNodeContribution> {

  private JComboBox<String> features = new JComboBox<String>();
  private JComboBox<String> variables = new JComboBox<String>();

  @Override
  public void buildUI(
    JPanel jPanel,
    final ContributionProvider<SetPoseProgramNodeContribution> contributionProvider
  ) {
    jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
    Box content = Box.createHorizontalBox();
    content.setPreferredSize(new Dimension(550, 50));
    content.setMaximumSize(content.getPreferredSize());
    content.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.setAlignmentY(Component.TOP_ALIGNMENT);
    features.setPreferredSize(new Dimension(250, 50));
    features.setMaximumSize(features.getPreferredSize());
    variables.setPreferredSize(new Dimension(250, 50));
    variables.setMaximumSize(variables.getPreferredSize());
    content.add(features);
    content.add(Box.createRigidArea(new Dimension(40, 50)));
    content.add(variables);
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

  public void updateVariableList(String[] list) {
    variables.removeAllItems();
    if (list == null) {
      return;
    }
    for (int i = 0; i < list.length; i++) {
      variables.addItem(list[i]);
    }
  }

  public String getFeatureName() {
    return (String) features.getSelectedItem();
  }

  public String getVariableName() {
	return (String) variables.getSelectedItem();
  }
}
