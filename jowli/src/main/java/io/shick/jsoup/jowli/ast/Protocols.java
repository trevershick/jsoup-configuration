package io.shick.jsoup.jowli.ast;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

import org.codehaus.jparsec.functors.Pair;

public final class Protocols extends ValueObject<List<Pair<Tag, List<Pair<Attr, List<Prot>>>>>>
    implements ConfigConsumer {
    public Protocols(List<Pair<Tag, List<Pair<Attr, List<Prot>>>>> v) {
      super(v);
    }

    @Override
    public void accept(MutableWhitelistConfiguration c) {
      final List<Pair<Tag, List<Pair<Attr, List<Prot>>>>> value = value();
      value.forEach(p -> {
        final String tagName = p.a.value();
        final List<Pair<Attr, List<Prot>>> attributes = p.b;
        attributes.forEach(a -> {
          final String attrName = a.a.value();
          a.b.stream().map(Prot::value)
            .forEach(proto -> c.allowProtocol(tagName, attrName, proto));
        });
      });
    }
  }
