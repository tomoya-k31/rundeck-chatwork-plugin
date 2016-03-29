package tomoyak31.rundeck.plugin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tomoya-k31 on 2016/03/28.
 */
@RunWith(JUnit4.class)
public class CommonUtilsTest {

    @Test
    public void test_split() {
        assertThat(CommonUtils.split(""), is(arrayWithSize(0)));
        assertThat(CommonUtils.split(" "), is(arrayWithSize(0)));
        assertThat(CommonUtils.split(","), is(arrayWithSize(0)));
        assertThat(CommonUtils.split(", "), is(arrayWithSize(0)));

        assertThat(CommonUtils.split("", ""), is(arrayWithSize(0)));
        assertThat(CommonUtils.split(" ", " "), is(arrayWithSize(0)));
        assertThat(CommonUtils.split(",", ","), is(arrayWithSize(0)));
        assertThat(CommonUtils.split(", ", ", "), is(arrayWithSize(0)));

        assertThat(CommonUtils.split("", "1234,1234"), is(arrayWithSize(2)));
        assertThat(CommonUtils.split(" ", "1234,1234"), is(arrayWithSize(2)));
        assertThat(CommonUtils.split(",", "1234,1234"), is(arrayWithSize(2)));
        assertThat(CommonUtils.split(", ", "1234,1234"), is(arrayWithSize(2)));
    }
}
