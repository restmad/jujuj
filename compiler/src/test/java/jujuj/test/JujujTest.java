package jujuj.test;

import com.google.common.io.Files;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import framework.core.Jujuj;
import framework.inj.ViewInj;
import jujuj.internal.JujujProcessor;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class JujujTest {

    @Test
    public void jujuj() throws IOException {
        File file = new File("src/test/java/jujuj/test/JujujTest.java");
        String content = Files.toString(file, Charset.forName("UTF-8"));

        ASSERT.about(javaSource())
                .that(JavaFileObjects.forSourceString("jujuj.test.JujujTest", content))
                .processedWith(new JujujProcessor())
                .compilesWithoutError();
    }

    @ViewInj
    public String name;

    @ViewInj
    public String id;

    @Test
    public void testGenerateViewInject() throws Exception {
        new Jujuj().generateViewInject(null, null, this);
    }

}
