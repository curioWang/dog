package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private static final String BASE_URL = "https://dog.ceo/api";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = BASE_URL + "/breed/" + breed + "/list";

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                // 统一转成 BreedNotFoundException
                throw new BreedNotFoundException(breed);
            }

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);

            String status = json.optString("status", "");
            if ("error".equals(status)) {
                // 文档示例：{"status":"error","message":"Breed not found (main breed does not exist)","code":404}
                throw new BreedNotFoundException(breed);
            }

            JSONArray arr = json.getJSONArray("message");
            List<String> result = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                result.add(arr.getString(i));
            }
            return result;

        } catch (IOException e) {
            // 网络/IO 问题：按接口约定，Task 1 阶段也统一映射为 BreedNotFoundException
            throw new BreedNotFoundException(breed);
        }
    }
}