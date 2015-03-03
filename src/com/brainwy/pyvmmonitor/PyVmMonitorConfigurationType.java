package com.brainwy.pyvmmonitor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.notification.EventLog;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.jetbrains.python.run.PythonConfigurationFactoryBase;
import com.jetbrains.python.run.PythonRunConfiguration;
import com.jetbrains.python.run.PythonRunConfigurationEditor;
import icons.PythonIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

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
            PyVmMonitorPythonRunConfiguration pyVmMonitorPythonRunConfiguration = new PyVmMonitorPythonRunConfiguration(project, this);

            //TODO: Cover other platforms!
            if (SystemInfo.isWindows) {
                try {
                    //It may not be available in all versions of windows, but if it is, let's use it...
                    String env = System.getenv("LOCALAPPDATA");
                    if (env != null && env.length() > 0 && new File(env).exists()) {
                        File settings = new File(new File(env, "Brainwy"), "PyVmMonitor.ini");
                        if (settings.exists()) {
                            Properties props = new Properties();
                            props.load(new FileInputStream(settings));
                            String property = props.getProperty("pyvmmonitor_ui_executable");
                            pyVmMonitorPythonRunConfiguration.setPyVmMonitorLocation(property);
                        }
                    }
                } catch (Exception e) {
                    Log.log(e);
                }
            }

            return pyVmMonitorPythonRunConfiguration;
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
