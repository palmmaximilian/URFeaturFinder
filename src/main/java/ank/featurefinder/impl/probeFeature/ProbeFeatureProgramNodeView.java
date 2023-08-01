package ank.featurefinder.impl.probeFeature;

import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ProbeFeatureProgramNodeView implements SwingProgramNodeView<ProbeFeatureProgramNodeContribution> {

  private JComboBox<String> features = new JComboBox<String>();

  @Override
  public void buildUI(JPanel jPanel, final ContributionProvider<ProbeFeatureProgramNodeContribution> contributionProvider) {
    jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
    Box content = Box.createHorizontalBox();
    content.setPreferredSize(new Dimension(550, 50));
    content.setMaximumSize(content.getPreferredSize());
    content.setAlignmentX(Component.LEFT_ALIGNMENT);
    content.setAlignmentY(Component.TOP_ALIGNMENT);
    features.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            contributionProvider.get().actionPerformed(e);
          } catch (Exception ex) {
            System.out.println("Exception: " + ex);
          }
        }
      }
    );
    features.setPreferredSize(new Dimension(250, 50));
    features.setMaximumSize(features.getPreferredSize());
    content.add(features);
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

  public int getFeatureIndex() {
    return features.getSelectedIndex();
  }

  public void setFeatureIndex(int index) {
    if (index < features.getItemCount()) {
      features.setSelectedIndex(index);
    } else if(features.getItemCount() > 0){
      features.setSelectedIndex(0);
    }
  }
}
