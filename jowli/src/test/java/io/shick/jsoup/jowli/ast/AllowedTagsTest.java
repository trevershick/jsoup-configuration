package io.shick.jsoup.jowli.ast;

import static io.shick.jsoup.util.Func.list;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

import org.junit.Test;

public class AllowedTagsTest {

  @Test
  public void allowedTags() {
    final MutableWhitelistConfiguration configuration = mock(MutableWhitelistConfiguration.class);

    final List<Tag> data = list(new Tag("a"), new Tag("b"), new Tag("c"));

    final AllowedTags o = new AllowedTags(data);

    // when
    o.accept(configuration);

    // then
    verify(configuration).allowTag("a");
    verify(configuration).allowTag("b");
    verify(configuration).allowTag("c");
    verifyNoMoreInteractions(configuration);
  }
}
