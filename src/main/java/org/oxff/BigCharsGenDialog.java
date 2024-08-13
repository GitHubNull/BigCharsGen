package org.oxff;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;

import javax.swing.*;
import java.awt.*;

public class BigCharsGenDialog extends JDialog {
    private final MontoyaApi api;
    private final ContextMenuEvent contextMenuEvent;

    private JPanel centerPanel;

    private final static String[] CHAR_COUNTERS = {
            "128KB",
            "256KB",
            "512KB",
            "1MB",
            "custom"
    };

    private final static JComboBox<String> selectCharCounter = new JComboBox<>(CHAR_COUNTERS);
    private final static JTextField customCharCounter = new JTextField("", 8);
    private final static JButton generateButton = new JButton("Generate");
    private final static JButton cancelButton = new JButton("Cancel");

    public BigCharsGenDialog(MontoyaApi api, ContextMenuEvent contextMenuEvent) {
        this.api = api;
        this.contextMenuEvent = contextMenuEvent;
        init();
    }

    public void init() {
       setLayout(new BorderLayout());
       setTitle("big chars gen");

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

       northPanel.add(new JLabel("select"));

       selectCharCounter.setSelectedIndex(0);
       selectCharCounter.setEnabled(true);
       customCharCounter.setEnabled(false);

       northPanel.add(selectCharCounter);

       centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
       centerPanel.add(new JLabel("custom"));
       centerPanel.add(customCharCounter);
       centerPanel.setVisible(false);
       centerPanel.setEnabled(false);

       selectCharCounter.addActionListener(e -> {
           if (selectCharCounter.getSelectedIndex() == 4) {
               customCharCounter.setEnabled(true);
               customCharCounter.setText("");
               customCharCounter.setColumns(8);
               customCharCounter.requestFocus();
               centerPanel.setEnabled(true);
               centerPanel.setVisible(true);

               setSize(new Dimension(350, 200));
               setLocationRelativeTo(null);
           } else {
               customCharCounter.setEnabled(false);
               centerPanel.setEnabled(false);
               centerPanel.setVisible(false);

               setSize(new Dimension(300, 150));
               setLocationRelativeTo(null);
           }
       });

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

       generateButton.addActionListener(e -> {
           BigCharsGenerator bigCharsGenerator = new BigCharsGenerator();
           if (selectCharCounter.getSelectedIndex() == 4) {
               bigCharsGenerator.generateByCustom(api, contextMenuEvent, customCharCounter.getText());
               dispose();
           } else {
               bigCharsGenerator.generateBySelected(api, contextMenuEvent, CHAR_COUNTERS[selectCharCounter.getSelectedIndex()]);
               dispose();
           }
       });

       cancelButton.addActionListener(e -> dispose());

       southPanel.add(generateButton);
       southPanel.add(cancelButton);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setSize(new Dimension(300, 150));
        setLocationRelativeTo(null);
//        setVisible(true);
    }


}
