package io.shick.jsoup.gson;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import io.shick.jsoup.WhitelistConfigurationParserFactory;

import org.junit.Test;

public class WhitelistConfigurationParserFactoryTest {

  @Test
  public void test() {
    assertThat("Only the gson parser is loaded",
      WhitelistConfigurationParserFactory.registeredParserTypes().size(),
      is(1));

    assertThat(WhitelistConfigurationParserFactory.newParser("gson"),
      is(not(nullValue())));
  }
}
