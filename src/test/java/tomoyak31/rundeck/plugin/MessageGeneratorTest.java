package tomoyak31.rundeck.plugin;

import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tomoya-k31 on 2016/03/29.
 */
@RunWith(JUnit4.class)
public class MessageGeneratorTest {

    static final String MESSAGE_TEMPLATE = "" +
            "[${trigger}]\n" +
            "${toUsers}" +
            "User : ${user!} \n" +
            "Date : ${dateStarted} ~ ${dateEnded!} \n" +
            "Job  : ${job.group!} > ${job.name!} \n" +
            "${href}";

    @Test
    public void テンプレートレンダリング検証() throws IOException, TemplateException {

        MessageGenerator messageGenerator = new MessageGenerator();

        Map dataMap = new HashMap();
        dataMap.put("trigger", "start");
        dataMap.put("toUsers", messageGenerator.toUsers(new String[]{"1234","5678"}));
        dataMap.put("id", 12);
        dataMap.put("user", "admin");
        dataMap.put("href", "http://192.168.125.100/rundeck/project/Local-Vagrant/execution/follow/12");
        dataMap.put("dateStarted", "2016-03-28 10:28:52.03");

        Map jobMap = new HashMap();
        jobMap.put("id", "c12b0df7-a579-4746-9767-f70e96e5db7f");
        jobMap.put("href", "http://192.168.125.100/rundeck/project/Local-Vagrant/job/show/c12b0df7-a579-4746-9767-f70e96e5db7f");
        jobMap.put("name", "エラーテスト2");
        jobMap.put("group", null);
        jobMap.put("project", "Local-Vagrant");
        jobMap.put("description", "エラーのテストです。");
        dataMap.put("job", jobMap);

        String message = messageGenerator.renderMessage(MESSAGE_TEMPLATE, dataMap);
        assertThat(message, is(containsString("[start]")));
        assertThat(message, is(containsString("[To:1234][To:5678]")));
        assertThat(message, is(containsString("User : admin")));
        assertThat(message, is(containsString("Job  :  > エラーテスト2")));
    }
}
