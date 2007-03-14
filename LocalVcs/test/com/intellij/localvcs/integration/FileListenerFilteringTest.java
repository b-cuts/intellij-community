package com.intellij.localvcs.integration;

import com.intellij.localvcs.Entry;
import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Before;
import org.junit.Test;

public class FileListenerFilteringTest extends FileListenerTestCase {
  @Before
  public void setUp() {
    l = new FileListener(vcs, new TestIdeaGateway(), new MyFileFilter());
  }

  @Test
  public void testRenamingFileToFilteredOne() {
    vcs.createFile("allowed", null, null);
    vcs.apply();

    VirtualFile renamed = new TestVirtualFile("filtered", null, null);
    fireRenamed(renamed, "allowed");

    assertFalse(vcs.hasEntry("allowed"));
    assertFalse(vcs.hasEntry("filtered"));
  }

  @Test
  public void testRenamingFileFromFilteredOne() {
    VirtualFile renamed = new TestVirtualFile("allowed", "content", 123L);
    fireRenamed(renamed, "filtered");

    Entry e = vcs.findEntry("allowed");
    assertNotNull(e);
    assertEquals(c("content"), e.getContent());
    assertEquals(123L, e.getTimestamp());
  }

  @Test
  public void testRenamingFileFromFilteredToFilteredOne() {
    VirtualFile renamed = new TestVirtualFile("filtered2", null, null);
    fireRenamed(renamed, "filtered1");

    assertFalse(vcs.hasEntry("filtered1"));
    assertFalse(vcs.hasEntry("filtered2"));
  }

  @Test
  public void testRenamingDirectoryFromFilteredOne() {
    VirtualFile renamed = new TestVirtualFile("filtered2", null, null);
    fireRenamed(renamed, "filtered1");

    assertFalse(vcs.hasEntry("filtered1"));
    assertFalse(vcs.hasEntry("filtered2"));
  }

  private class MyFileFilter extends FileFilter {
    public MyFileFilter() {
      super(null, null);
    }

    @Override
    public boolean isAllowedAndUnderContentRoot(VirtualFile f) {
      return f.getName().equals("allowed");
    }
  }
}