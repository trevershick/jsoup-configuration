package io.shick.jsoup.jowli.ast;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

import org.codehaus.jparsec.functors.Pair;

public final class EnforcedAttributes extends ValueObject<List<Pair<Tag, List<Pair<Attr, String>>>>> implements
  ConfigConsumer {
  public EnforcedAttributes(List<Pair<Tag, List<Pair<Attr, String>>>> v) {
    super(v);
  }

  @Override
  public void accept(MutableWhitelistConfiguration c) {
    final List<Pair<Tag, List<Pair<Attr, String>>>> value = value();
    value.forEach(p -> {
      final String tagName = p.a.value();
      final List<Pair<Attr, String>> attributes = p.b;
      attributes.forEach(a -> {
        final String attr = a.a.value();
        final String enforcedValue = a.b;
        c.enforceAttribute(tagName, attr, enforcedValue);
      });
    });
  }
}
