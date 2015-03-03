package com.brainwy.pyvmmonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.run.PythonConfigurationFactoryBase;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfigurationEditor;
import icons.PythonIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * This is the main starting point: we define a run configuration type which will have an associated editor
 * (and this class is referenced from the plugin.xml).
 */
public class PyVmMonitorConfigurationType extends ConfigurationTypeBase{

    private final PyVmMonitorPythonConfigurationFactory myFactory = new PyVmMonitorPythonConfigurationFactory(this);

    protected PyVmMonitorConfigurationType() {
        super("PyVmMonitorConfigurationType", "PyVmMonitor Configuration Type", "Configuration for launching with PyVmMonitor", null);
    }

    public static PyVmMonitorConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(PyVmMonitorConfigurationType.class);
    }



    /**
     * Yeap, java code always loves a factory :)
     */
    public static class PyVmMonitorPythonConfigurationFactory extends PythonConfigurationFactoryBase {
        protected PyVmMonitorPythonConfigurationFactory(ConfigurationType configurationType) {
            super(configurationType);
        }

        public RunConfiguration createTemplateConfiguration(Project project) {
            return new PyVmMonitorPythonRunConfiguration(project, this);
        }
    }

    public String getDisplayName() {
        return "PyVmMonitor (Python profile run)";
    }

    public String getConfigurationTypeDescription() {
        return "This run allows running an application attached directly with PyVmMonitor.";
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
        return "PyVmMonitorConfigurationType";
    }

}
