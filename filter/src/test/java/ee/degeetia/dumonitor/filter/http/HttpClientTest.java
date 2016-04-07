package ee.degeetia.dumonitor.filter.http;

import ee.degeetia.dumonitor.common.util.IOUtil;
import ee.degeetia.testutils.jetty.EmbeddedJettyIntegrationTest;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HttpClientTest extends EmbeddedJettyIntegrationTest {

  private HttpClient httpClient = new HttpClient();

  @BeforeClass
  public static void createServerMock() {
    createServlet(new HttpServlet() {
      @Override
      protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(request.getContentType());
        response.setContentLength(request.getContentLength());
        IOUtil.pipe(request.getInputStream(), response.getOutputStream());
      }
    }, "/rest/test");
  }

  @Test
  public void testPostObject() throws Exception {
    TestObject request = new TestObject();
    request.setString("stringValue");
    request.setNumber(123.456);
    request.setArray(new int[]{123, 456});
    request.setList(Arrays.asList(1, 2, 3));

    HttpResponse<TestObject> response =
        httpClient.post(new URL("http://localhost:8123/rest/test"), request, TestObject.class);
    HttpStatus status = response.getStatus();
    TestObject body = response.getBody();

    assertEquals(200, status.getCode());
    assertEquals("OK", status.getMessage());

    assertEquals("stringValue", body.getString());
    assertEquals(123.456, body.getNumber(), 0);
    assertArrayEquals(new int[]{123, 456}, body.getArray());
    assertEquals(Arrays.asList(1, 2, 3), body.getList());
  }

  @Test
  public void testPostMap() throws Exception {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("string", "stringValue");
    map.put("number", 123.456);
    HttpResponse<TestObject> response =
        httpClient.post(new URL("http://localhost:8123/rest/test"), map, TestObject.class);
    HttpStatus status = response.getStatus();
    TestObject body = response.getBody();

    assertEquals(200, status.getCode());
    assertEquals("OK", status.getMessage());

    assertEquals("stringValue", body.getString());
    assertEquals(123.456, body.getNumber(), 0);
  }

  private static class TestObject {
    private String string;
    private double number;
    private int[] array;
    private List<Integer> list;

    String getString() {
      return string;
    }

    void setString(String string) {
      this.string = string;
    }

    int[] getArray() {
      return array;
    }

    void setArray(int[] array) {
      this.array = array;
    }

    double getNumber() {
      return number;
    }

    void setNumber(double number) {
      this.number = number;
    }

    List<Integer> getList() {
      return list;
    }

    void setList(List<Integer> list) {
      this.list = list;
    }
  }

}
