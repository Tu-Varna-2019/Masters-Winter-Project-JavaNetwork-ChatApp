package controller.helpers;

import java.util.Base64;
import java.util.TreeMap;
import com.amazonaws.services.s3.model.S3Object;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import com.amazonaws.util.IOUtils;
import model.storage.S3Manager;

public class MaskData {
    // Return a string map of decoded payload and return them
    public static TreeMap<String, String> base64Decode(JSONObject jsonPayload) {
        JSONObject dataObject = jsonPayload.getJSONObject("data");
        TreeMap<String, String> sortedMap = new TreeMap<>();
        decode(dataObject, sortedMap);
        return sortedMap;
    }

    private static void decode(Object obj, TreeMap<String, String> sortedMap) {
        if (obj instanceof JSONObject) {
            JSONObject dataObject = (JSONObject) obj;
            for (String key : dataObject.keySet()) {
                Object value = dataObject.get(key);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    decode(value, sortedMap);
                } else if (value instanceof String) {
                    // Skip decoding if the key is 'attachmentURL'
                    if ("attachmentURL".equals(key)) {
                        sortedMap.put(key, (String) value);
                        continue;
                    }

                    String encodedValue = (String) value;
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);
                    sortedMap.put(key, new String(decodedBytes));
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            for (int i = 0; i < array.length(); i++) {
                Object value = array.get(i);
                if (value instanceof JSONObject || value instanceof JSONArray) {
                    decode(value, sortedMap);
                }
            }
        }
    }

    public static String base64DecodeSelectedValue(String encodedValue) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);
        return new String(decodedBytes);
    }

    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

    public static boolean checkHashedPassword(String password, String storedHash) {
        return BCrypt.checkpw(password, storedHash);
    }

    public static String base64EncodeS3File(String key) {
        try {
            S3Object object = S3Manager.getDownloadedFile(key);
            byte[] fileContent = IOUtils.toByteArray(object.getObjectContent());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
        }
        return "";
    }
}
