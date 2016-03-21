package com.shinado.tagme;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        String json = "{\"resultCode\":\"-4\",\"msg\":\"There's no such account.\"}";
        BaseResult result = new Gson().fromJson(json, BaseResult.class);
        assertEquals(-4, result.resultCode);
    }

}