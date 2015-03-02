package com.brainwy.pyvmmonitor;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonConfigurationFactoryBase;
import com.jetbrains.python.run.PythonRunConfiguration;
import icons.PythonIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PyVmMonitorConfigurationType extends ConfigurationTypeBase{
    private final PyVmMonitorPythonConfigurationFactory myFactory = new PyVmMonitorPythonConfigurationFactory(this);

    protected PyVmMonitorConfigurationType(@NotNull String id, String displayName, String description, Icon icon) {
        super(id, displayName, description, icon);
    }

    public static PyVmMonitorConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(PyVmMonitorConfigurationType.class);
    }

    public static class PyVmMonitorPythonRunConfiguration extends PythonRunConfiguration{

        protected PyVmMonitorPythonRunConfiguration(Project project, ConfigurationFactory configurationFactory) {
            super(project, configurationFactory);
        }
    }
    public static class PyVmMonitorPythonConfigurationFactory extends PythonConfigurationFactoryBase {
        protected PyVmMonitorPythonConfigurationFactory(ConfigurationType configurationType) {
            super(configurationType);
        }

        public RunConfiguration createTemplateConfiguration(Project project) {
            return new PyVmMonitorPythonRunConfiguration(project, this);
        }
    }

    public String getDisplayName() {
        return "PyVmMonitor display name";
    }

    public String getConfigurationTypeDescription() {
        return "PyVmMonitor getConfigurationTypeDescription";
    }

    public Icon getIcon() {
        return PythonIcons.Python.Python;
    }

    public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myFactory};
    }

    public PyVmMonitorPythonConfigurationFactory getFactory() {
        return myFactory;
    }

    @NotNull
    @NonNls
    public String getId() {
        System.out.println("PyVmMonitorConfigurationType: getId");
        return "PyVmMonitorConfigurationType";
    }

}
