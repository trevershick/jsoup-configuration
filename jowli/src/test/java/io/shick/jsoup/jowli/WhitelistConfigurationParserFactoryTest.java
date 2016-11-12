package io.shick.jsoup.jowli;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import io.shick.jsoup.WhitelistConfigurationParserFactory;

import org.junit.Test;

public class WhitelistConfigurationParserFactoryTest {

  @Test
  public void test() {
    assertThat("Only the jowli parser",
      WhitelistConfigurationParserFactory.registeredParserTypes().size(),
      is(1));

    assertThat(WhitelistConfigurationParserFactory.newParser("jowli"),
      is(not(nullValue())));
  }
}
