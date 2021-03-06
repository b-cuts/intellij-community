/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.debugger.memory.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugSession;
import com.sun.jdi.ReferenceType;
import com.intellij.debugger.memory.utils.InstancesProvider;
import com.intellij.debugger.memory.ui.ClassesTable;
import com.intellij.debugger.memory.ui.InstancesWindow;

public class ShowNewInstancesAction extends ShowInstancesAction {
  private static final String POPUP_ELEMENT_LABEL = "Show New Instances";

  @Override
  protected boolean isEnabled(AnActionEvent e) {
    XDebugSession session = getDebugSession(e);
    ReferenceType selectedClass = getSelectedClass(e);
    InstancesProvider provider = e.getData(ClassesTable.NEW_INSTANCES_PROVIDER_KEY);
    return super.isEnabled(e) && session != null && selectedClass != null && provider != null;
  }

  @Override
  protected String getLabel() {
    return POPUP_ELEMENT_LABEL;
  }

  @Override
  protected int getInstancesCount(AnActionEvent e) {
    ClassesTable.ReferenceCountProvider countProvider = e.getData(ClassesTable.REF_COUNT_PROVIDER_KEY);
    ReferenceType selectedClass = getSelectedClass(e);
    if (countProvider == null || selectedClass == null) {
      return -1;
    }

    return countProvider.getNewInstancesCount(selectedClass);
  }

  @Override
  protected void perform(AnActionEvent e) {
    XDebugSession session = getDebugSession(e);
    ReferenceType selectedClass = getSelectedClass(e);
    InstancesProvider provider = e.getData(ClassesTable.NEW_INSTANCES_PROVIDER_KEY);
    if (selectedClass != null && provider != null && session != null) {
      new InstancesWindow(session, provider, selectedClass.name()).show();
    }
  }
}
