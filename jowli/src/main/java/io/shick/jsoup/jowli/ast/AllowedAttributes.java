package io.shick.jsoup.jowli.ast;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

import org.codehaus.jparsec.functors.Pair;

public final class AllowedAttributes extends ValueObject<List<Pair<Tag, List<Attr>>>> implements ConfigConsumer {

  public AllowedAttributes(List<Pair<Tag, List<Attr>>> v) {
    super(v);
  }

  @Override
  public void accept(MutableWhitelistConfiguration c) {
    value().forEach(e -> {
      final String tag = e.a.value();
      e.b.stream().map(Attr::value).forEach(it -> c.allowAttribute(tag, it));
    });
  }
}
