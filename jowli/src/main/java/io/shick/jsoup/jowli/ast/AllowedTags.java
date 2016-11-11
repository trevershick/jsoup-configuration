package io.shick.jsoup.jowli.ast;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

public final class AllowedTags extends ValueObject<List<Tag>> implements ConfigConsumer {

    public AllowedTags(List<Tag> v) {
      super(v);
    }

    @Override
    public void accept(MutableWhitelistConfiguration c) {
      value().stream().map(Tag::value).forEach(c::allowTag);
    }
  }
