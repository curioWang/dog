package dogapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // 如果缓存中有这个 breed，直接返回
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        try {
            // 真实调用底层 fetcher
            callsMade++;
            List<String> result = fetcher.getSubBreeds(breed);
            // 只有成功的结果才缓存
            cache.put(breed, result);
            return result;
        } catch (BreedNotFoundException e) {
            // 错误不缓存
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}
