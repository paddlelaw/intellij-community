// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.refactoring.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.rename.PsiElementRenameHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author ven
 */
public class RenameFileAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull final AnActionEvent e) {
    final PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
    assert file != null;
    final VirtualFile virtualFile = file.getVirtualFile();
    assert virtualFile != null;
    final Project project = e.getData(CommonDataKeys.PROJECT);
    assert project != null;
    PsiElementRenameHandler.invoke(file, project, file, null);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
    Presentation presentation = e.getPresentation();
    String place = e.getPlace();
    ActionManager actionManager = ActionManager.getInstance();
    boolean enabled =
      file != null && file.isWritable()
      && Objects.nonNull(file.getVirtualFile()) && !(file.getVirtualFile().getFileSystem().isReadOnly())
      && (enabledInProjectView(file) || !ActionPlaces.PROJECT_VIEW_POPUP.equals(place))
      && place != ActionPlaces.EDITOR_POPUP && e.getData(CommonDataKeys.PROJECT) != null
      && !(ActionPlaces.isShortcutPlace(place) &&
           e.getData(CommonDataKeys.EDITOR) != null &&
           Arrays.equals(Objects.requireNonNull(actionManager.getAction(IdeActions.ACTION_RENAME)).getShortcutSet().getShortcuts(),
                         getShortcutSet().getShortcuts()));
    presentation.setEnabledAndVisible(enabled);
  }

  protected boolean enabledInProjectView(@NotNull PsiFile file) {
    return RenameFileActionProvider.EP_NAME.getExtensionList().stream().anyMatch(provider -> provider.enabledInProjectView(file));
  }
}
