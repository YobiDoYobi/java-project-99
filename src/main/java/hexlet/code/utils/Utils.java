package hexlet.code.utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    /*public static String getHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return String.valueOf(hash);
    }*/

    public static Map<String, Object> urlNormalize(Map<String, String> inParams) {
        Map<String, Object> outParams = new HashMap<String, Object>();
        int start = inParams.containsKey("_start") ? Integer.parseInt(inParams.get("_start")) : 0;
        int end = inParams.containsKey("_end") ? Integer.parseInt(inParams.get("_end")) : 10;
        int perPage = end - start;
        int page = start / perPage;
        outParams.put("page", page);
        outParams.put("perPage", perPage);
        outParams.put("order", inParams.get("_order"));
        outParams.put("sort", inParams.get("_sort"));
        return outParams;
    }
}
