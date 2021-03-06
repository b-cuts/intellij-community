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
package com.siyeh.ig.bugs;

import com.intellij.codeInsight.NullableNotNullDialog;
import com.intellij.codeInspection.InspectionsBundle;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.ide.DataManager;
import com.intellij.ide.util.SuperMethodWarningUtil;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiMethod;
import com.siyeh.InspectionGadgetsBundle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReturnNullInspection extends ReturnNullInspectionBase {

  @Override
  public JComponent createOptionsPanel() {
    final MultipleCheckboxOptionsPanel optionsPanel = new MultipleCheckboxOptionsPanel(this);
    optionsPanel.addCheckbox(InspectionGadgetsBundle.message("return.of.null.ignore.private.option"), "m_ignorePrivateMethods");
    optionsPanel.addCheckbox(InspectionGadgetsBundle.message("return.of.null.arrays.option"), "m_reportArrayMethods");
    optionsPanel.addCheckbox(InspectionGadgetsBundle.message("return.of.null.collections.option"), "m_reportCollectionMethods");
    optionsPanel.addCheckbox(InspectionGadgetsBundle.message("return.of.null.objects.option"), "m_reportObjectMethods");
    final JButton configureAnnotations = new JButton(InspectionsBundle.message("configure.annotations.option"));
    configureAnnotations.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Project project = CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(optionsPanel));
        if (project == null) project = ProjectManager.getInstance().getDefaultProject();
        new NullableNotNullDialog(project).show();
      }
    });
    optionsPanel.addComponent(configureAnnotations);
    return optionsPanel;
  }

  @Override
  protected int shouldAnnotateBaseMethod(PsiMethod method, PsiMethod superMethod) {
    return SuperMethodWarningUtil.askWhetherShouldAnnotateBaseMethod(method, superMethod);
  }

}
