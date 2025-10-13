package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.prefs.BackingStoreException;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String url = "https://dog.ceo/api/breeds/list/all";
        final Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {

            String jsonData = response.body().string();
            JSONObject json = new JSONObject(jsonData);

            //Check if it had worked
            if (!json.getString("status").equalsIgnoreCase("success")) {
                throw new BreedNotFoundException("Wrong Response.");
            }

            //get subclasses within it and check if it has the breed
            JSONObject allBreeds = json.getJSONObject("message");

            if (!allBreeds.has(breed.toLowerCase())) {
                throw new BreedNotFoundException("Breed not found: " + breed);
            }

            JSONArray subBreedsArray = allBreeds.getJSONArray(breed.toLowerCase());
            List<String> subBreedList = new ArrayList<>();

            for (int i = 0; i < subBreedsArray.length(); i++) {
                subBreedList.add(subBreedsArray.getString(i));
            }

            return subBreedList;


        } catch (IOException | JSONException event){
            throw new BreedNotFoundException("No breeds found");

    } catch (BreedNotFoundException e) {
            throw new RuntimeException(e);
        }
    }}