package com.hujian.trident.hybrid.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujian on 2017/3/11.
 *
 * just use hash map.
 */
public class SampleStored<K,V> implements IStore<K,V>{

    private static final long serialVersionUID = 19900121312341L;

    /**
     * the only instance of this class
     */
    private static SampleStored ourInstance = null;

    /**
     * maybe,you can use this list
     */
    private List<V> vList = null;

    /**
     * the cached
     */
    private Map<K,V> mapStates = null;

    private Map<String,String> doneMap = null;

    /**
     * you want to get the only instance of this class.
     * @return
     */
    public static SampleStored getInstance() {
        if( ourInstance == null ){
            ourInstance = new SampleStored();
        }
        return ourInstance;
    }

    /**
     * the constructor is private,so you can not invoke
     */
    private SampleStored() {
        //init the map states
        this.mapStates = new ConcurrentHashMap<K,V>();
        this.doneMap = new HashMap<>();
    }

    @Override
    public V get(K key) {
        return this.mapStates.get( key );
    }

    @Override
    public List<V> multiGet(List<K> keyList) {
        if( keyList == null || keyList.size() == 0 ){
            return null;
        }
        List<V> values = new ArrayList<V>();
        for( K k : keyList ){
            values.add( this.get( k ) );
        }
        return values;
    }

    @Override
    public void put(K key, V value) {
        this.mapStates.put( key,value );
    }

    @Override
    public void multiPut(List<K> keyList, List<V> valueList) {
        if( keyList == null || keyList.size() == 0 ){
            return;
        }
        for( int i = 0; i < keyList.size() ;  i ++){
            this.put( keyList.get( i ), valueList.get( i ) );
        }
    }

    @Override
    public Boolean containsKey(K key) {
        return this.mapStates.containsKey( key );
    }

    @Override
    public void remove(K key) {
        this.mapStates.remove( key );
    }

    @Override
    public Map<String, String> doneMap() {
        return this.doneMap;
    }

    @Override
    public Map<K, V> mapStates() {
        return this.mapStates;
    }

    public List<V> getvList() {
        return vList;
    }

    public void setvList(List<V> vList) {
        this.vList = vList;
    }
}
