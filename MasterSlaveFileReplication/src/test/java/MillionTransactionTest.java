import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MillionTransactionTest {

    public MillionTransactionTest() throws IOException {
        URL url = new URL("http://localhost:8080/masterserver");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.getInputStream();

    }
}
