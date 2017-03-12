package com.hujian.trident.hybrid.store;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by hujian on 2017/3/11.
 *
 * so,if you want to use kafka,memcache,redis .etc. whatever you
 * want to use to stored your result,you can implements this interface
 * to come true.
 */
public interface IStore<K,V> extends Serializable {
    /**
     * get the key's value
     *
     * @param key
     * @return
     */
    V get(K key);

    /**
     * multi version
     *
     * @param keyList
     * @return
     */
    List<V> multiGet(List<K> keyList);

    /**
     * put the value to cached
     *
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * multi version
     *
     * @param keyList
     * @param valueList
     */
    void multiPut(List<K> keyList, List<V> valueList);

    /**
     * if contains the key.
     * @param key
     * @return
     */
    Boolean containsKey(K key);

    /**
     * remove
     * @param key
     */
    void remove(K key);

    /**
     * get done map
     * @return
     */
    Map<String,String> doneMap();

    /**
     * get the map states
     * @return
     */
    Map<K,V> mapStates();

}
