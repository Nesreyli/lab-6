package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private int callsMade = 0;
    private final BreedFetcher fetcher;
    // Keep Track of Breeds
    private Map<String, List<String>> cache;

    // Constructor
    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
        this.cache = new HashMap<>();
    }

    // Function
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        String key = breed.toLowerCase().trim();

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        callsMade++;
        try{
            List<String> result = fetcher.getSubBreeds(breed);

            cache.put(key, new ArrayList<>(result));
            return result;

        } catch (BreedNotFoundException e){
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}