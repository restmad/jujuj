package jujuj.test;

import android.view.View;

import com.google.common.io.Files;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.lang.model.element.Modifier;

import framework.core.Jujuj;
import framework.inj.ViewInj;
import framework.inj.entity.Postable;
import jujuj.internal.JujujProcessor;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by Administrator on 2015/12/17.
 */
public class JujujTest {

    @Test
    public void jujuj() throws IOException {
        File file = new File("src/test/java/jujuj/test/JujujTest.java");
        String content = Files.toString(file, StandardCharsets.UTF_8);

        ASSERT.about(javaSource())
                .that(JavaFileObjects.forSourceString("jujuj.test.JujujTest", content))
                .processedWith(new JujujProcessor())
                .compilesWithoutError();
    }

    @ViewInj(1)
    public String name;

    @ViewInj(2)
    public String id;

    @Test
    public void testGenerateViewInject() throws Exception {
//        MethodSpec main = MethodSpec.methodBuilder("main")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(void.class)
//                .addParameter(String[].class, "args")
//                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
//                .build();
//
//        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addMethod(main)
//                .build();
//
//        JavaFile javaFile = JavaFile.builder("com.example.helloworld", helloWorld)
//                .build();
//
//        javaFile.writeTo(javaFile);
    }

}
