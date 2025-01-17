// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.intellij.plugins.markdown.ui.preview;

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.file.Path;
import java.util.EventListener;

public interface MarkdownHtmlPanel extends Disposable {
  @NotNull JComponent getComponent();

  @Deprecated
  default void setHtml(@NotNull String html, int initialScrollOffset) {
    setHtml(html, initialScrollOffset, null);
  }

  void setHtml(@NotNull String html, int initialScrollOffset, @Nullable Path baseUrl);

  void reloadWithOffset(int offset);

  void scrollToMarkdownSrcOffset(int offset, boolean smooth);

  interface ScrollListener extends EventListener {
    void onScroll(int offset);
  }
  @SuppressWarnings("unused")
  void addScrollListener(ScrollListener listener);

  @SuppressWarnings("unused")
  void removeScrollListener(ScrollListener listener);
}
