package io.shick.jsoup.jowli;

import static java.util.Objects.requireNonNull;

import io.shick.jsoup.BaseFactories;
import io.shick.jsoup.WhitelistConfiguration;
import io.shick.jsoup.WhitelistConfigurationFormatter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * <p>JowliMLFormatter class.</p>
 *
 * @author Trever Shick - trever@shick.io
 */
public class JowliMLFormatter implements WhitelistConfigurationFormatter {
  private static final Map<String,Character> FACTORY_TO_JOWLI_LETTER;

  public static final char LETTER_BASIC = 'b';

  public static final char LETTER_BASICWITHIMAGES = 'i';

  public static final char LETTER_RELAXED = 'r';

  public static final char LETTER_NONE = 'n';

  static {
    Map<String,Character> m = new HashMap<>();
    m.put(BaseFactories.BASIC, LETTER_BASIC);
    m.put(BaseFactories.BASICWITHIMAGES, LETTER_BASICWITHIMAGES);
    m.put(BaseFactories.RELAXED, LETTER_RELAXED);
    m.put(BaseFactories.NONE, LETTER_NONE);
    FACTORY_TO_JOWLI_LETTER = m;
  }

  /**
   * {@inheritDoc}
   */
  public CharSequence format(WhitelistConfiguration configuration) {
    final AtomicReference<CharSequence> ref = new AtomicReference<>();
    root(requireNonNull(configuration, "configuration cannot be null"), ref::set);
    return Optional.ofNullable(ref.get()).orElse("");
  }

  private void root(WhitelistConfiguration config, Consumer<CharSequence> c) {
    final StringJoiner sj = new StringJoiner(";");
    baseDirective(config, sj::add);
    tagsDirective(config, sj::add);
    allowedAttributesDirective(config, sj::add);
    enforcedAttributesDirective(config, sj::add);
    protocolsDirective(config, sj::add);
    if (sj.length() > 0) {
      c.accept(sj.toString());
    }
  }

  /**
   * "a:blockquote[cite],a[href,rel]"
   *
   * @param config
   * @param c
   */
  private void allowedAttributesDirective(WhitelistConfiguration config, Consumer<CharSequence> c) {
    final StringJoiner sj = new StringJoiner(",");

    config.allowedAttributes((tag, attrs) -> {
      sj.add(new StringBuilder(tag)
        .append(bracketed(join(attrs, ","))));
    });

    if (sj.length() > 0) {
      c.accept(new StringBuilder("a:").append(sj.toString()));
    }
  }

  /**
   * "e:a[rel:nofollow,x:y]"
   *
   * @param config
   * @param c
   */
  private void enforcedAttributesDirective(WhitelistConfiguration config, Consumer<CharSequence> c) {
    final StringJoiner sj = new StringJoiner(",");

    config.enforcedAttributes((tag, attrs) -> {
      final List<String> attrValues = new LinkedList<>();
      attrs.forEach((k, v) -> attrValues.add(colonSeparated(k, v)));

      sj.add(new StringBuilder(tag)
        .append(bracketed(join(attrValues, ","))));
    });

    if (sj.length() > 0) {
      c.accept(new StringBuilder("e:").append(sj.toString()));
    }
  }

  /**
   * "p:a[href:[ftp,http,https],z:[d]]"
   *
   * @param config
   * @param c
   */
  private void protocolsDirective(WhitelistConfiguration config, Consumer<CharSequence> c) {
    final StringJoiner sj = new StringJoiner(",");

    config.allowedProtocols((tag, attrToProtList) -> {
      final StringJoiner attrs = new StringJoiner(",");
      attrToProtList.forEach((attr, protlist) -> {
        attrs.add(colonSeparated(attr, bracketed(join(protlist, ","))));
      });
      sj.add(new StringBuilder(tag).append(bracketed(attrs.toString())));
    });

    if (sj.length() > 0) {
      c.accept(new StringBuilder("p:").append(sj.toString()));
    }
  }

  /**
   * "t:a,b"
   *
   * @param config
   * @param c
   */
  private void tagsDirective(WhitelistConfiguration config, Consumer<CharSequence> c) {
    final StringJoiner sj = new StringJoiner(",");
    config.allowedTags(sj::add);
    if (sj.length() > 0) {
      c.accept(new StringBuilder("t:").append(sj.toString()));
    }
  }

  /**
   * "b:b"
   *
   * @param config
   * @param c
   */
  private void baseDirective(WhitelistConfiguration config, Consumer<CharSequence> c) {
    String factory = config.base();
    if (factory != null) {
      c.accept(new StringBuilder("b:").append(FACTORY_TO_JOWLI_LETTER.get(factory)));
    }
  }

  private String join(List<String> attrs, String delimiter) {
    final StringJoiner sj = new StringJoiner(delimiter);
    attrs.forEach(sj::add);
    return sj.toString();
  }

  private String bracketed(String string) {
    return new StringBuilder("[").append(string).append("]").toString();
  }

  private String colonSeparated(String s, String s1) {
    return new StringBuilder(s).append(":").append(s1).toString();
  }
}
