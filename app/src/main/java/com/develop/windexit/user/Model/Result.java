package com.develop.windexit.user.Model;

/**
 * Created by WINDEX IT on 12-Mar-18.
 */

public class Result {

    public String message_id;

    public Result() {
    }

    public Result(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
