package tomoyak31.rundeck.plugin;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.descriptions.SelectValues;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import freemarker.template.TemplateException;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by tomoya-k31 on 2016/03/25.
 */
@Plugin(service= "Notification", name="ChatworkNotification")
@PluginDescription(title="Chatwork")
public class ChatworkNotificationPlugin implements NotificationPlugin {

    private static final String TRIGGER_START = "start";
    private static final String TRIGGER_SUCCESS = "success";
    private static final String TRIGGER_FAILURE = "failure";
    private static final String MENTION_ON = "on";
    private static final String MENTION_OFF = "off";

    @PluginProperty(title = "User ID", description = "通知するユーザIDを指定したい場合に記入してください。複数指定する場合はカンマ区切りで記入。")
    String users;

    @PluginProperty(title = "API Token")
    String apiToken;

    @PluginProperty(title = "Room ID")
    String roomId;

    @PluginProperty(title = "To:通知", required = true, description = "on: 指定したユーザ or デフォルトのユーザ宛のメッセージに[To]を付けて通知します。")
    @SelectValues(values = {MENTION_OFF, MENTION_ON})
    String mention;

    @PluginProperty(title = "エラー時のデフォルトTo:通知")
    boolean isDefaultFailureMention;

    @PluginProperty(title = "Message Template", description = "Freemarkerを使っています。<a href=\"http://rundeck.org/docs/developer/notification-plugin.html\">Notification Plugin</a>の'Execution data'を参照してください。")
    String messageTemplate;

    @Override
    public boolean postNotification(String trigger, Map executionData, Map config) {

        if (users == null || "".equals(users))
            throw new IllegalStateException("users is required");

        if (apiToken == null || "".equals(apiToken))
            throw new IllegalStateException("apiToken is required");

        if (roomId == null || "".equals(roomId))
            throw new IllegalStateException("roomId is required");

        if (messageTemplate == null || "".equals(messageTemplate))
            throw new IllegalStateException("messageTemplate is required");

        MessageGenerator messageGenerator = new MessageGenerator();

        executionData.put("trigger", trigger);
        executionData.put("toUsers", messageGenerator.toUsers(CommonUtils.split(targetUsers(trigger))));

        try {
            // generate messageTemplate
            String message = messageGenerator.renderMessage(this.messageTemplate, executionData);
            if (message != null && !"".equals(message)) {
                // post messageTemplate
                new ApiClient(apiToken).postMessage(message, roomId);
            }

        } catch (IOException | TemplateException e) {
            System.err.println("Chatworkメッセージ投稿を失敗しました。" + ExceptionUtils.getFullStackTrace(e));
        }

        return true;
    }

    String targetUsers(String trigger) {
        if (MENTION_ON.equals(mention))
            return users;

        // 以下MENTION_OFFのみ
        if (TRIGGER_FAILURE.equals(trigger) && isDefaultFailureMention)
            return users;

        return null;
    }
}
