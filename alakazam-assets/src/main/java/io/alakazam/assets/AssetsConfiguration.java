package io.alakazam.assets;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class AssetsConfiguration {
    //JG 12/10/2014 - property to enable file system watching of served assets
    @NotNull
    @JsonProperty("static_assets")
    private boolean staticAssets = true;

    @NotNull
    @JsonProperty("cache_spec")
    private String cacheSpec = ConfiguredAssetsBundle.DEFAULT_CACHE_SPEC.toParsableString();

    @NotNull
    @JsonProperty("overrides")
    private Map<String, String> overrides = Maps.newHashMap();

    @NotNull
    @JsonProperty("mime_types")
    private Map<String, String> mimeTypes = Maps.newHashMap();

    //JG 12/10/2014 - property to enable file system watching of served assets
    public boolean isStaticAssets() {
        return staticAssets;
    }

    /** The caching specification for how to memoize assets. */
    public String getCacheSpec() {
        return cacheSpec;
    }

    public Iterable<Map.Entry<String, String>> getOverrides() {
        return Iterables.unmodifiableIterable(overrides.entrySet());
    }

    public Iterable<Map.Entry<String, String>> getMimeTypes() {
        return Iterables.unmodifiableIterable(mimeTypes.entrySet());
    }
}
