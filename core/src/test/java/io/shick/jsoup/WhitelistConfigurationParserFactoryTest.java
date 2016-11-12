package io.shick.jsoup;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class WhitelistConfigurationParserFactoryTest {

  @Test(expected = IllegalArgumentException.class)
  public void askForUnknownParserGetIAE() {
    WhitelistConfigurationParserFactory.newParser("unknown");
  }

  @Test
  public void registrationAndInstantiation() {
    assertThat("the core has no access to any parser,so none are registered",
      WhitelistConfigurationParserFactory.registeredParserTypes().isEmpty(),
      is(true));

    final WhitelistConfigurationParser parser = mock(WhitelistConfigurationParser.class);
    WhitelistConfigurationParserFactory.register("wowzer", () -> parser);

    assertThat(WhitelistConfigurationParserFactory.newParser("wowzer"), is(parser));
  }
}
