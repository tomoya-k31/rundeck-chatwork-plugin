package tomoyak31.rundeck.plugin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by tomoya-k31 on 2016/03/28.
 */
class MessageGenerator {

    String renderMessage(String templateStr, Map<String, Object> dataMap) throws IOException, TemplateException {
        StringWriter sw = new StringWriter();
        Template template = new Template(null, new StringReader(templateStr), templateConfig("UTF-8"));
        template.process(dataMap, sw);
        return sw.toString();
    }

    Configuration templateConfig(String encoding) {
        Configuration config = new Configuration(Configuration.VERSION_2_3_23);
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        config.setDefaultEncoding(encoding);
        config.setOutputEncoding(encoding);
        return config;
    }

    String toUsers(String[] users) {
        if (users == null || users.length == 0)
            return "";

        // Toありの場合は、行末に改行があります
        return Stream.of(users)
                .collect(Collectors.joining("][To:", "[To:", "] \n"));
    }
}
