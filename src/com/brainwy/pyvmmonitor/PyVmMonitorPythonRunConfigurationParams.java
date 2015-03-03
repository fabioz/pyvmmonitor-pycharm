package com.brainwy.pyvmmonitor;

import com.jetbrains.python.run.PythonRunConfigurationParams;

public interface PyVmMonitorPythonRunConfigurationParams extends PythonRunConfigurationParams {

    public static final int PROFILE_MODE_YAPPI = 0;
    public static final int PROFILE_MODE_LSPROF = 1;
    public static final int PROFILE_MODE_NONE = 2;

    int getInitialProfileMode();

    void setInitialProfileMode(int initialProfileMode);
}