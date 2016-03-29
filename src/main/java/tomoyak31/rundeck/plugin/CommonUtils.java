package tomoyak31.rundeck.plugin;

/**
 * Created by tomoya-k31 on 2016/03/28.
 */
class CommonUtils {

    static String[] split(String str) {
        // ','でsplitするとlength:0になる
        return defaultString(str, ",").split("\\W+", 0);
    }

    static String[] split(String str, String defaultStr) {
        String[] s = split(str);
        if (s.length > 0)
            return s;

        return split(defaultStr);
    }

    private static String defaultString(String str, String defaultStr) {
        return str == null || "".equals(str) ? defaultStr : str;
    }
}
