package org.evolvis.veraweb.onlinereg.event;

import org.json.JSONObject;

/**
 * Created by aalexa on 28.01.15.
 */
public class StatusConverter {

    public static String convertStatus(String responseStatus) {
        JSONObject jsonObject = new JSONObject();
        JSONObject status = jsonObject.put("status", responseStatus);
        return status.toString();
    }
}
