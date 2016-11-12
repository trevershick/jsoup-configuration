package io.shick.jsoup.jowli.ast;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

/**
 * <p>AllowedTags class.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public final class AllowedTags extends ValueObject<List<Tag>> implements ConfigConsumer {

  /**
   * <p>Constructor for AllowedTags.</p>
   *
   * @param v a {@link java.util.List} object.
   */
  public AllowedTags(List<Tag> v) {
    super(v);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(MutableWhitelistConfiguration c) {
    value().stream().map(Tag::value).forEach(c::allowTag);
  }
}
