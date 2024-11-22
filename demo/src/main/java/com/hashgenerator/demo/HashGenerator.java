import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.json.JSONObject;
import org.json.JSONTokener;

public class HashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar HashGenerator.jar <roll_number> <json_file_path>");
            System.exit(1);
        }

        String rollNumber = args[0];
        String filePath = args[1];

        try {
            FileReader reader = new FileReader(filePath);
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject jsonObject = new JSONObject(tokener);

            String destinationValue = extractDestinationValue(jsonObject);

            Random random = new Random();
            String randomString = generateRandomString(8);

            String inputString = rollNumber + destinationValue + randomString;
            String md5Hash = calculateMD5Hash(inputString);

            System.out.println(md5Hash + ";" + randomString);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String extractDestinationValue(JSONObject jsonObject) {
        if (jsonObject.has("destination")) {
            return jsonObject.getString("destination");
        } else {
            for (Object key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    String result = extractDestinationValue((JSONObject) value);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String calculateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}