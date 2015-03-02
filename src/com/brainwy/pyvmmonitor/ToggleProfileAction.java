package com.brainwy.pyvmmonitor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class ToggleProfileAction extends AnAction {

    public ToggleProfileAction(){
        super("Toggle PyVmMonitor profile");
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        System.out.println("Toggle PyVmMonitor profile");
    }
}
