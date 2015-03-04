package com.brainwy.pyvmmonitor;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ConfigPyVmMonitorAction extends AnAction {

    public ConfigPyVmMonitorAction() {
        super("Configure");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        JFrame f = new JFrame("Configure PyVmMonitor");
        final PyVmMonitorPythonRunConfigurationForm myForm = new PyVmMonitorPythonRunConfigurationForm(true);
        myForm.setInitialProfileMode(PyVmMonitorRunExtension.getGlobalInitialProfileMode());
        myForm.setPyVmMonitorLocation(PyVmMonitorRunExtension.getGlobalPyVmMonitorLocation());
        JComponent panel = myForm.getPanel();

        final JDialog d = new JDialog(f, true);
        d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d.setTitle("Configure PyVmMonitor");
        d.getContentPane().setLayout(new BorderLayout());
        d.getContentPane().add(panel, BorderLayout.CENTER);

        JComponent buttons = new JPanel();
        buttons.setLayout(new GridLayout(1, 2));

        JButton btOk = new JButton("Ok");
        buttons.add(btOk);
        btOk.setActionCommand("Ok");
        d.getRootPane().setDefaultButton(btOk);
        btOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String initialProfileMode = myForm.getInitialProfileMode();
                String location = myForm.getPyVmMonitorLocation();

                PyVmMonitorRunExtension.setGlobalInitialProfileMode(initialProfileMode);
                PyVmMonitorRunExtension.setGlobalInitialLocation(location);

                d.setVisible(false);
            }
        });

        JButton btCancel = new JButton("Cancel");
        buttons.add(btCancel);
        btCancel.setActionCommand("Cancel");
        btCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d.setVisible(false);
            }
        });
        d.getContentPane().add(buttons, BorderLayout.SOUTH);

        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                d.setVisible(false);
            }
        };

        d.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        d.setMinimumSize(new Dimension(640, 150));
        d.setLocationRelativeTo(null);
        d.pack();
        d.setVisible(true);

    }
}
