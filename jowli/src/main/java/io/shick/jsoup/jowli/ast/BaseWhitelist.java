package io.shick.jsoup.jowli.ast;

import io.shick.jsoup.MutableWhitelistConfiguration;

import java.util.List;

/**
 * <p>BaseWhitelist class.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public final class BaseWhitelist extends ValueObject<String> implements ConfigConsumer {
  /**
   * <p>Constructor for BaseWhitelist.</p>
   *
   * @param v a {@link List} object.
   */
  public BaseWhitelist(String v) {
    super(v);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(MutableWhitelistConfiguration c) {
    c.base(value());
  }
}
