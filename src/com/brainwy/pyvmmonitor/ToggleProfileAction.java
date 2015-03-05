package com.brainwy.pyvmmonitor;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class ToggleProfileAction extends AnAction {

    public ToggleProfileAction() {
        super(getCurrentText());
    }

    private static String getCurrentText() {
        if (PyVmMonitorRunExtension.isProfileOn()) {
            return "Enable profile for new launches: On";
        }
        return "Enable profile for new launches: Off";
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        if (PyVmMonitorRunExtension.isProfileOn()) {
            instance.setValue(PyVmMonitorRunExtension.PROFILE_ON, "false");
        } else {
            instance.setValue(PyVmMonitorRunExtension.PROFILE_ON, "true");
        }
        event.getPresentation().setText(getCurrentText());
    }
}
