package io.shick.jsoup.jowli.ast;

import static io.shick.jsoup.util.Func.list;
import static org.codehaus.jparsec.functors.Tuples.pair;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

import org.codehaus.jparsec.functors.Pair;
import org.junit.Test;

public class AllowedAttributesTest {

  @Test
  public void allowedAttributes() {
    final MutableWhitelistConfiguration configuration = mock(MutableWhitelistConfiguration.class);
    
    final List<Pair<Tag, List<Attr>>> data = list(
      pair(new Tag("t1"), list(new Attr("a1"), new Attr("a2")))
    );
    final AllowedAttributes o = new AllowedAttributes(data);
    
    // when
    o.accept(configuration);
    
    // then
    verify(configuration).allowAttribute("t1","a1");
    verify(configuration).allowAttribute("t1","a2");
    verifyNoMoreInteractions(configuration);
  }
}
