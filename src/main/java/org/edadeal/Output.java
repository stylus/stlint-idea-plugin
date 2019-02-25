package org.edadeal;

import java.util.ArrayList;

// already bundled with IntelliJ IDEA and WebStorm
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

class Output {
    private static final Gson gson = new Gson();

    static class MessagePart {
        String descr;
        String path;
        int line;
        int endline;
        int start;
        int end;
    }

    static class Error {
        ArrayList<MessagePart> message;
    }

    static class Response {
        boolean passed;
        ArrayList<Error> errors;
    }

    static @NotNull Response parse(@NotNull final String stylusOutput) {
        return gson.fromJson(stylusOutput, Response.class);
    }

}