package services;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;
import jdk.nashorn.internal.runtime.Version;

import java.util.*;

public class FollowerService {

    //key/version store
    private Map<String, ValueVersion> store = new HashMap<>();

    public FollowerService() {

    }

    /**
     * Gets a list of value versions for a given key
     *
     * @param key The key to read versions for
     * @return A list of versions of the value for the inputted key
     */
    public ValueVersion read(String key) {
        return store.get(key);
    }

    /**
     * Replaces versions of a value with a single "resolved" value
     *
     * @param key The key to resolve
     * @param resolvedValue The value to resolve to
     * @param version The version to resolve to (one greater than the last seen version)
     * @return An object containing the list of values for the key after resolution
     */
    public ValueVersion resolve(String key, String resolvedValue, int version) {
        return this.write(key, resolvedValue, version);
    }

    /**
     * Writes a version of a value for a given key
     *
     * @param key The key to write to
     * @param value A value to add to the key's version list
     * @param version The version of the write (should be one greater than last seen version)
     * @return An object containing versions of the key's values after the new value is added
     */
    public ValueVersion write (String key, String value, int version) {
        ValueVersion currentVersions = store.get(key);
        if (currentVersions == null) {
            List<String> newVersions = new ArrayList<String>();
            newVersions.add(value);
            store.put(key, new ValueVersion(version, newVersions));
            return store.get(key);
        } else if (currentVersions.getVersion() >= version) {
            currentVersions.addValue(value);
            return currentVersions;
        } else {
            currentVersions.setValues(value);
            currentVersions.setVersion(version);
            return currentVersions;
        }
    }

    /**
     * Adds a replica of a value list with a given key to a follower
     *
     * Only adds the list if the key does not already exist on the follower
     * @param key The key to add
     * @param valueVersions The value list to be replicated
     * @return The new value list, if the key does not already exist, else null
     */
    public ValueVersion addReplica (String key, ValueVersion valueVersions) {
        ValueVersion currentVersions = store.get(key);
        if (currentVersions != null) {
            return null;
        } else {
            store.put(key, valueVersions);
            return store.get(key);
        }
    }

    /**
     * Dumps entire kv store for replication
     *
     * @return The entire store of this replica
     */
    public Map<String, ValueVersion> getStore() {
        return store;
    }
}
