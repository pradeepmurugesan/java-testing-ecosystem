package de.rieckpil.blog.microshedtesting;

import javax.json.Json;

import de.rieckpil.blog.SampleResource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;
import org.mockserver.client.MockServerClient;

import static de.rieckpil.blog.microshedtesting.SampleApplicationConfig.mockServer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.MediaType.JSON_UTF_8;

@Disabled
@MicroShedTest
@SharedContainerConfig(SampleApplicationConfig.class)
class SampleResourceIT {

  @RESTClient
  public static SampleResource sampleEndpoint;

  @Test
  void shouldReturnSampleMessage() {
    assertEquals("Hello World from MicroShed Testing",
      sampleEndpoint.getMessage());
  }

  @Test
  void shouldReturnQuoteOfTheDay() {

    var resultQuote = Json.createObjectBuilder()
      .add("contents",
        Json.createObjectBuilder().add("quotes",
          Json.createArrayBuilder().add(Json.createObjectBuilder()
            .add("quote", "Do not worry if you have built your castles in the air. " +
              "They are where they should be. Now put the foundations under them."))))
      .build();

    new MockServerClient(mockServer.getContainerIpAddress(), mockServer.getServerPort())
      .when(request("/qod"))
      .respond(response().withBody(resultQuote.toString(), JSON_UTF_8));

    var result = sampleEndpoint.getQuotes();

    System.out.println("Quote of the day: " + result);

    assertNotNull(result);
    assertFalse(result.isEmpty());
  }
}
