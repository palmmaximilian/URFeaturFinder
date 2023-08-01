package ank.featurefinder.impl.probeNode;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProbeNodeProgramNodeView
  implements SwingProgramNodeView<ProbeNodeNodeContribution> {

  private JButton probeZButton;
  private JButton probeAllButton;
  private JComboBox<String> featureList = new JComboBox<String>();

  @Override
  public void buildUI(
    JPanel panel,
    final ContributionProvider<ProbeNodeNodeContribution> provider
  ) {
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    Box infoSection = createSection(BoxLayout.PAGE_AXIS);
    infoSection.add(
      createInfo("This node helps you probe the dimensions of your feature.")
    );
    panel.add(infoSection);
    panel.add(createVerticalSpacing());

    Box buttonSection = createSection(BoxLayout.LINE_AXIS);
    buttonSection.add(createHorizontalIndent());
    this.probeZButton = new JButton("Probe Z Dimension");
    this.probeZButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            provider.get().generateProbeZ();
          }
        }
      );
    probeZButton.setName("probeZButton");
    buttonSection.add(this.probeZButton, FlowLayout.LEFT);
    panel.add(buttonSection);
    panel.add(createVerticalSpacing());

    buttonSection = createSection(BoxLayout.LINE_AXIS);
    buttonSection.add(createHorizontalIndent());
    this.probeAllButton = new JButton("Probe all Dimensions");
    this.probeAllButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            provider.get().generateProbeAll();
          }
        }
      );
    probeAllButton.setName("probeAllButton");
    buttonSection.add(this.probeAllButton, FlowLayout.LEFT);
	panel.add(buttonSection);
	panel.add(createVerticalSpacing());

	buttonSection = createSection(BoxLayout.LINE_AXIS);
	buttonSection.add(createHorizontalIndent());
	featureList.setPreferredSize(new Dimension(200, 50));
	featureList.setMaximumSize(featureList.getPreferredSize());
	featureList.addActionListener(
		new ActionListener() {
		  @Override
		  public void actionPerformed(ActionEvent e) {
			provider.get().featurePicked();
		  }
		}
	);

	buttonSection.add(featureList, FlowLayout.LEFT);
    panel.add(buttonSection);
  }

  Box createInfo(String text) {
    Box infoBox = Box.createHorizontalBox();
    infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    infoBox.add(new JLabel(text));
    return infoBox;
  }

  Component createHorizontalIndent() {
    return Box.createRigidArea(new Dimension(50, 0));
  }

  Component createVerticalSpacing() {
    return Box.createRigidArea(new Dimension(0, 25));
  }

  Box createSection(int axis) {
    Box panel = new Box(axis);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.setAlignmentY(Component.TOP_ALIGNMENT);
    return panel;
  }

  void updateFeatureList(String[] features) {
	featureList.removeAllItems();
	for (String feature : features) {
	  featureList.addItem(feature);
	}
  }
  void setFeatureListIndex(int index) {
	System.out.println("Setting feature list index to " + index);
	featureList.setSelectedIndex(index);
  }
  String getFeatureListItem() {
	if(featureList.getSelectedItem() == null) {
	  return null;
	}
	return featureList.getSelectedItem().toString();
  }
}
