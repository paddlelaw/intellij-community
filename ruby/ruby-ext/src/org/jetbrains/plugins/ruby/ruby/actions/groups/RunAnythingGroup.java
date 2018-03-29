package org.jetbrains.plugins.ruby.ruby.actions.groups;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import gnu.trove.TIntArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.actions.RunAnythingCache;
import org.jetbrains.plugins.ruby.ruby.actions.RunAnythingItem;
import org.jetbrains.plugins.ruby.ruby.actions.RunAnythingSearchListModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents 'run anything' list group. See {@link RunAnythingCommandGroup} and {@link RunAnythingRunConfigurationGroup} as examples.
 */
public abstract class RunAnythingGroup {
  public static final ExtensionPointName<RunAnythingGroup> EP_NAME =
    ExtensionPointName.create("org.jetbrains.plugins.ruby.runAnythingGroup");

  /**
   * {@link #myMoreIndex} is a group 'load more..' index in the main list.
   */
  private volatile int myMoreIndex = -1;

  /**
   * {@link #myTitleIndex} is an index of group title.
   */
  private volatile int myTitleIndex = -1;

  /**
   * @return Current group title in the main list.
   */
  @NotNull
  public abstract String getTitle();

  /**
   * @return Unique settings key to store current group visibility property.
   */
  @NotNull
  public abstract String getVisibilityKey();

  /**
   * @return Current group maximum number of items to be shown.
   */
  protected abstract int getMaxInitialItems();

  /**
   * @return Current group maximum number of items to be insert by click on 'load more..'.
   */
  protected int getMaxItemsToInsert() {
    return 5;
  }

  /**
   * @return Defines whether this group should be shown with empty input or not.
   */
  public boolean shouldBeShownInitially() {
    return false;
  }

  /**
   * Gets current group items to add into the main list.
   *
   * @param model           list model
   * @param pattern         input search string
   * @param isInsertionMode if true gets {@link #getMaxItemsToInsert()} group items, else limits to {@link #getMaxInitialItems()}
   * @param check           checks 'load more' calculation process to be cancelled
   */
  protected abstract SearchResult getItems(@NotNull Project project,
                                           @Nullable Module module,
                                           @NotNull RunAnythingSearchListModel model,
                                           @NotNull String pattern,
                                           boolean isInsertionMode,
                                           @NotNull Runnable check);

  /**
   * Gets all current group matched by {@code pattern} items if its visibility turned on and empty collection otherwise
   *
   * @param model
   * @param pattern         input search string
   * @param isInsertionMode limits group items to get by a constant group specific value
   * @param check           checks 'load more' calculation process to be cancelled
   */
  public SearchResult getVisibleItems(@NotNull Project project,
                                      @Nullable Module module,
                                      @NotNull RunAnythingSearchListModel model,
                                      @NotNull String pattern,
                                      boolean isInsertionMode,
                                      @NotNull Runnable check) {
    return RunAnythingCache.getInstance(project).isGroupVisible(getVisibilityKey())
           ? getItems(project, module, model, pattern, isInsertionMode, check) : new SearchResult();
  }

  /**
   * Resets current group {@link #myMoreIndex}.
   */
  public void dropMoreIndex() {
    myMoreIndex = -1;
  }

  /**
   * Shifts {@link #myMoreIndex} starting from {@code baseIndex} to {@code shift}.
   */
  private static void shiftMoreIndex(int baseIndex, int shift) {
    Arrays.stream(EP_NAME.getExtensions()).filter(runAnythingGroup -> runAnythingGroup.myMoreIndex >= baseIndex)
          .forEach(runAnythingGroup -> runAnythingGroup.myMoreIndex += shift);
  }

  /**
   * Finds group title by {@code index}.
   *
   * @return group title if {@code index} is equals to group {@link #myTitleIndex} and {@code null} if nothing found
   */
  @Nullable
  public static String getTitle(int index) {
    return Arrays.stream(EP_NAME.getExtensions()).filter(runAnythingGroup -> index == runAnythingGroup.myTitleIndex).findFirst()
                 .map(RunAnythingGroup::getTitle).orElse(null);
  }

  /**
   * Shifts {@link #myTitleIndex} starting from {@code baseIndex} to {@code shift}.
   */
  private static void shift(int baseIndex, int shift) {
    Arrays.stream(EP_NAME.getExtensions())
          .filter(runAnythingGroup -> runAnythingGroup.myTitleIndex != -1 && runAnythingGroup.myTitleIndex > baseIndex)
          .forEach(runAnythingGroup -> runAnythingGroup.myTitleIndex += shift);
  }

  /**
   * Clears {@link #myMoreIndex} of all groups.
   */
  public static void clearMoreIndex() {
    Arrays.stream(EP_NAME.getExtensions()).forEach(runAnythingGroup -> runAnythingGroup.myMoreIndex = -1);
  }

  /**
   * Clears {@link #myTitleIndex} of all groups.
   */
  private static void clearTitleIndex() {
    Arrays.stream(EP_NAME.getExtensions()).forEach(runAnythingGroup -> runAnythingGroup.myTitleIndex = -1);
  }

  /**
   * Joins {@link #myTitleIndex} and {@link #myMoreIndex} of all groups; using for navigating by 'TAB' between groups.
   */
  public static int[] getAllIndexes() {
    TIntArrayList list = new TIntArrayList();
    for (RunAnythingGroup runAnythingGroup : EP_NAME.getExtensions()) {
      list.add(runAnythingGroup.myTitleIndex);
    }
    for (RunAnythingGroup runAnythingGroup : EP_NAME.getExtensions()) {
      list.add(runAnythingGroup.myMoreIndex);
    }

    return list.toNativeArray();
  }

  /**
   * Finds matched by {@link #myMoreIndex} group.
   */
  @Nullable
  public static RunAnythingGroup findRunAnythingGroup(int index) {
    return Arrays.stream(EP_NAME.getExtensions()).filter(runAnythingGroup -> index == runAnythingGroup.myMoreIndex).findFirst()
                 .orElse(null);
  }

  /**
   * Returns {@code true} if {@code index} is a {@link #myMoreIndex} of some group, {@code false} otherwise
   */
  public static boolean isMoreIndex(int index) {
    return Arrays.stream(EP_NAME.getExtensions()).anyMatch(runAnythingGroup -> runAnythingGroup.myMoreIndex == index);
  }

  /**
   * Shifts {@link #myMoreIndex} and {@link #myTitleIndex} of all groups starting from {@code baseIndex} to {@code shift}.
   */
  public static void shiftIndexes(int baseIndex, int shift) {
    shift(baseIndex, shift);
    shiftMoreIndex(baseIndex, shift);
  }

  /**
   * Clears {@link #myMoreIndex} and {@link #myTitleIndex} of all groups.
   */
  public static void clearIndexes() {
    clearTitleIndex();
    clearMoreIndex();
  }

  /**
   * Adds current group matched items into the list.
   *
   * @param model      main list model
   * @param pattern    input search string
   * @param check      checks 'load more' calculation process to be cancelled
   * @param isCanceled computes if 'load more' calculation process has already cancelled
   */
  public final synchronized void collectItems(@NotNull Project project,
                                              @Nullable Module module,
                                              @NotNull RunAnythingSearchListModel model,
                                              @NotNull String pattern,
                                              @NotNull Runnable check,
                                              @NotNull Computable<Boolean> isCanceled) {
    SearchResult result = getVisibleItems(project, module, model, pattern, false, check);

    check.run();
    if (result.size() > 0) {
      //noinspection SSBasedInspection
      SwingUtilities.invokeLater(() -> {
        if (isCanceled.compute()) return;

        myTitleIndex = model.size();
        for (Object element : result) {
          model.addElement(element);
        }
        myMoreIndex = result.myNeedMore ? model.getSize() - 1 : -1;
      });
    }
  }

  /**
   * Represents collection of the group items with {@code myNeedMore} flag is set to true is limit is exceeded
   */
  public static class SearchResult extends ArrayList<RunAnythingItem> {
    boolean myNeedMore;

    public boolean isNeedMore() {
      return myNeedMore;
    }

    public void setNeedMore(boolean needMore) {
      myNeedMore = needMore;
    }
  }
}