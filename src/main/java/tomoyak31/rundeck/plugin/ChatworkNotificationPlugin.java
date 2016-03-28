package tomoyak31.rundeck.plugin;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tomoya-k31 on 2016/03/25.
 */
@Plugin(service= "Notification", name="ChatworkNotification")
@PluginDescription(title="Chatwork")
public class ChatworkNotificationPlugin implements NotificationPlugin {

    private static final String TRIGGER_START = "start";
    private static final String TRIGGER_SUCCESS = "success";
    private static final String TRIGGER_FAILURE = "failure";

    private static final String CHATWORK_API_SCHEME = "https://";
    private static final String CHATWORK_API_HOST = "api.chatwork.com";
    private static final String CHATWORK_API_VER = "/v1";
    private static final String CHATWORK_API_ROOM_PATH = "/rooms";
    private static final String CHATWORK_API_ROOM_ACTION = "/messages";

    @PluginProperty(title = "ユーザID(複数指定の場合はカンマ区切り)", required = true, description = "通知するユーザを指定したい場合に記入してください。")
    private String users;

    @PluginProperty(title = "ユーザID(複数指定の場合はカンマ区切り)", scope = PropertyScope.Project)
    private String defaultUsers;

    @PluginProperty(title = "APIトークン", scope = PropertyScope.Project)
    private String apiToken;

    @PluginProperty(title = "チャットグループID", scope = PropertyScope.Project)
    private String room;

    @PluginProperty(title = "通知する", required = true, description = "ありの場合は、指定したユーザ or デフォルトのユーザにTo付きのメッセージを送信します。")
    private boolean isSendToUsers;

    @Override
    public boolean postNotification(String trigger, Map executionData, Map config) {

        if (defaultUsers == null || "".equals(defaultUsers))
            throw new IllegalStateException("defaultUsers is required");

        if (apiToken == null || "".equals(apiToken))
            throw new IllegalStateException("apiToken is required");

        if (room == null || "".equals(room))
            throw new IllegalStateException("room is required");


//        if (TRIGGER_START.equals(trigger)) {
//            // 開始
//        }
//        if (TRIGGER_SUCCESS.equals(trigger)) {
//            // 成功
//        }
//        if (TRIGGER_FAILURE.equals(trigger)) {
//            // 失敗
//        }


        System.out.println("trigger: " + trigger);

        Map<String, Object> tExecutionData = executionData;
        Map<String, Object> executionMap = (Map<String, Object>) tExecutionData.get("execution");
        Map<String, Object> jobMap = (Map<String, Object>) tExecutionData.get("job");
        System.out.println("executionData.executionMap: ");
        executionMap.entrySet().stream().forEach(e -> {
            System.out.println(" - " + e.getKey() + " : " + e.getValue());
        });
        System.out.println("executionData.jobMap: ");
        jobMap.entrySet().stream().forEach(e -> {
            System.out.println(" - " + e.getKey() + " : " + e.getValue());
        });

        Map<String, Object> tConfig = config;
        System.out.println("config: ");
        tConfig.entrySet().stream().forEach(e -> {
            System.out.println(" - " + e.getKey() + " : " + e.getValue());
        });


        postMessage(generateMessage(getDefaultUsers(trigger), "テスト"));

        return true;
    }

    /**
     * メッセージ送信
     * @param message
     */
    private void postMessage(String message) {
        HttpClient client = null;
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("body", message));

            HttpPost httppost = new HttpPost(generateUrl());
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            // headers
            List<Header> headers = new ArrayList<>();
            headers.add(new BasicHeader("Accept-Charset", "utf-8"));
            headers.add(new BasicHeader("X-ChatWorkToken", apiToken));

            // request configuration
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(2)
                    .setSocketTimeout(2)
                    .build();

            // http client
            client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultHeaders(headers).build();

            client.execute(httppost);

        } catch (Exception e) {
            System.err.println("Chatworkメッセージ投稿を失敗しました。" + e.getMessage());
        } finally {
            HttpClientUtils.closeQuietly(client);
        }
    }

    /**
     * メッセージ生成
     * @param users
     * @param message
     * @return
     */
    private String generateMessage(String[] users, String message) {
        if (users == null || users.length == 0)
            return message;

        if (users.length == 1 && "".equals(users[0]))
            return message;

        return Stream.of(users)
                .collect(Collectors.joining("][To:", "[To:", "] \n" + message));
    }

    private String generateUrl() {
        return new StringBuilder()
                .append(CHATWORK_API_SCHEME)
                .append(CHATWORK_API_HOST)
                .append(CHATWORK_API_VER)
                .append(CHATWORK_API_ROOM_PATH)
                .append("/")
                .append(room)
                .append(CHATWORK_API_ROOM_ACTION)
                .toString();
    }

    private String[] getDefaultUsers(String trigger) {
        if (!TRIGGER_FAILURE.equals(trigger) && !isSendToUsers)
            return new String[]{};

        if (!TRIGGER_FAILURE.equals(trigger) && users != null && !"".equals(users))
            return users.split("\\W+");

        return defaultUsers.split("\\W+");
    }

    public static void main(String[] args) {
        ChatworkNotificationPlugin c = new ChatworkNotificationPlugin();
        System.out.printf(c.generateMessage(new String[]{}, "test"));
    }
}
