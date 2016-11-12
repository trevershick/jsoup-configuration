package io.shick.jsoup.jowli;

import static org.codehaus.jparsec.Parsers.between;
import static org.codehaus.jparsec.Parsers.sequence;
import static org.codehaus.jparsec.Scanners.isChar;

import io.shick.jsoup.WhitelistConfiguration;
import io.shick.jsoup.WhitelistConfigurationParser;
import io.shick.jsoup.WhitelistConfigurationParserFactory;
import io.shick.jsoup.jowli.ast.AllowedAttributes;
import io.shick.jsoup.jowli.ast.AllowedTags;
import io.shick.jsoup.jowli.ast.Attr;
import io.shick.jsoup.jowli.ast.ConfigConsumer;
import io.shick.jsoup.jowli.ast.EnforcedAttributes;
import io.shick.jsoup.jowli.ast.Prot;
import io.shick.jsoup.jowli.ast.Protocols;
import io.shick.jsoup.jowli.ast.Tag;

import java.text.ParseException;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.error.ParserException;
import org.codehaus.jparsec.functors.Pair;
import org.codehaus.jparsec.functors.Tuples;
import org.codehaus.jparsec.pattern.Pattern;
import org.codehaus.jparsec.pattern.Patterns;

public final class JowliMLParser implements WhitelistConfigurationParser {

  static final Parser<Void> RIGHT_BRACKET = isChar(']');
  static final Parser<Void> LEFT_BRACKET = isChar('[');

  static final Parser<Void> COMMA = isChar(',');
  static final Parser<Void> COLON = isChar(':');
  
  static final Pattern ALPHA_TOKEN = Patterns.regex("[a-zA-Z]+");

  static final Parser<Prot> PROTOCOL_NAME =
    ALPHA_TOKEN.toScanner("protocol name").source().map(Prot::new);

  static final Parser<Tag> TAG_NAME =
    ALPHA_TOKEN.toScanner("tag name").source().map(Tag::new);
  
  static final Parser<Attr> ATTR_NAME =
    ALPHA_TOKEN.toScanner("attribute name").source().map(Attr::new);

  static final Parser<String> ENFORCED_VALUE = Patterns.regex("[^\\],]+")
    .toScanner("enforced value").source();

  static <T> Parser<T> bracketed(Parser<T> parser) {
    return between(LEFT_BRACKET, parser, RIGHT_BRACKET);
  }
  
  static <C> Parser<List<C>> commaed(Parser<C> p) {
    return p.sepBy(COMMA);
  }

  static final Parser<Pair<Attr, List<Prot>>> ATTR_PROTOCOL_NAMES =
    sequence(ATTR_NAME,
      COLON,
      bracketed(commaed(PROTOCOL_NAME)),
      (name, __, value) -> Tuples.pair(name, value));

  static final Parser<Pair<Attr, String>> ATTR_ENFORCED_VALUE =
    sequence(ATTR_NAME,
      COLON,
      ENFORCED_VALUE,
      (name, __, value) -> Tuples.pair(name, value));
  
  static final Parser<Pair<Tag, List<Attr>>> TAG_LIST_OF_ATTR_NAMES =
    sequence(
      TAG_NAME,
      bracketed(commaed(ATTR_NAME)),
      Tuples::pair);

  static final Parser<Pair<Tag, List<Pair<Attr, List<Prot>>>>> TAG_ATTR_PROTOCOLS =
    sequence(
      TAG_NAME,
      bracketed(commaed(ATTR_PROTOCOL_NAMES)),
      (name, list) -> Tuples.pair(name, list));
  
  static final Parser<Pair<Tag, List<Pair<Attr, String>>>> TAG_ATTR_ENFORCED_VALUES =
    sequence(
      TAG_NAME,
      bracketed(commaed(ATTR_ENFORCED_VALUE)), 
      (name, list) -> Tuples.pair(name, list));
  
  static final Parser<AllowedTags> ALLOWED_TAGS_DIRECTIVE =
    sequence(
      isChar('t'),
      COLON,
      commaed(TAG_NAME),
      (__, ___, v) -> new AllowedTags(v));

  static final Parser<AllowedAttributes> ALLOWED_ATTRIBUTES_DIRECTIVE =
    sequence(
      isChar('a'),
      COLON,
      commaed(TAG_LIST_OF_ATTR_NAMES),
      (__, ___, v) -> new AllowedAttributes(v));

  static final Parser<EnforcedAttributes> ENFORCED_ATTRIBUTES_DIRECTIVE =
    sequence(
      isChar('e'),
      COLON,
      commaed(TAG_ATTR_ENFORCED_VALUES),
      (__, ___, v) -> new EnforcedAttributes(v));

  static final Parser<Protocols> PROTOCOLS_DIRECTIVE =
    sequence(
      isChar('p'),
      COLON,
      commaed(TAG_ATTR_PROTOCOLS),
      (__, ___, v) -> new Protocols(v));

  static final Parser<List<ConfigConsumer>> ROOT =
    Parsers.<ConfigConsumer>or(
      ALLOWED_ATTRIBUTES_DIRECTIVE,
      ENFORCED_ATTRIBUTES_DIRECTIVE,
      PROTOCOLS_DIRECTIVE,
      ALLOWED_TAGS_DIRECTIVE).sepBy(isChar(';'));
  
  
  public WhitelistConfiguration parse(CharSequence value) throws ParseException {
    return parse(new JowliMLWhitelistConfiguration(), value);
  }
  
  public WhitelistConfiguration parse(JowliMLWhitelistConfiguration c, CharSequence from) throws ParseException {
    try {
      ROOT.parse(from).stream()
        .forEach(consumer -> consumer.accept(c));
      return c;
    } catch (ParserException p) {
      throw new ParseException(p.getMessage(), p.getLocation().column);
    }
  }

  static {
    WhitelistConfigurationParserFactory.register("jowli", JowliMLParser::new);
  }

}
