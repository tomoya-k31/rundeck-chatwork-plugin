package tomoyak31.rundeck.plugin;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomoya-k31 on 2016/03/28.
 */
class ApiClient {

    private static final String CHATWORK_API_SCHEME = "https://";
    private static final String CHATWORK_API_HOST = "api.chatwork.com";
    private static final String CHATWORK_API_VER = "/v2";
    private static final String CHATWORK_API_ROOM_PATH = "/rooms";
    private static final String CHATWORK_API_ROOM_ACTION = "/messages";

    String apiToken;

    public ApiClient(String apiToken) {
        this.apiToken = apiToken;
    }

    /**
     * メッセージ送信
     */
    void postMessage(String message, String roomId) throws IOException {
        HttpClient client = null;
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("body", message));

            HttpPost httppost = new HttpPost(generateUrl(roomId));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            // headers
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("Accept-Charset", "utf-8"));
            headers.add(new BasicHeader("X-ChatWorkToken", apiToken));

            // request configuration
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000)
                    .setSocketTimeout(3000)
                    .build();

            // http client
            client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultHeaders(headers).build();

            client.execute(httppost);

        } finally {
            HttpClientUtils.closeQuietly(client);
        }
    }

    String generateUrl(String roomId) {
        return new StringBuilder()
                .append(CHATWORK_API_SCHEME)
                .append(CHATWORK_API_HOST)
                .append(CHATWORK_API_VER)
                .append(CHATWORK_API_ROOM_PATH)
                .append("/")
                .append(roomId)
                .append(CHATWORK_API_ROOM_ACTION)
                .toString();
    }
}
