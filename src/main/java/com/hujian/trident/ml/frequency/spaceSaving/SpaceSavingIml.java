package com.hujian.trident.ml.frequency.spaceSaving;

import java.util.*;

/**
 * Created by hujian on 2017/3/6.
 * @paper Efficient Computation of Frequent and Top-k Elements in Data Streams
 * @link http://www.docin.com/p-140893341.html
 * @link http://www.docin.com/p-1154774161.html
 *
 * you can find more information by searching the key word "data stream" in google
 */
public class SpaceSavingIml<T extends Comparable> implements ISpaceSaving<T> {

    private static final long serialVersionUID = 90872656L;

    private int counter;
    private Map<T,Long> items;
    private TreeMap<Long,TreeSet<T>> database;
    private long reachTimes;

    /**
     * the constructor
     * @param counter
     */
    public SpaceSavingIml( int counter ){
        this.counter = counter;
        this.reachTimes = 0;
        items = new HashMap<T, Long>();
        database = new TreeMap<Long, TreeSet<T>>();
    }


    @Override
    public Boolean add(T item, Long increment) {
        if( this.items.containsKey( item ) ){
            Long count = items.get( item );
            TreeSet<T> set = database.get( count );
            set.remove( item );
            if( set.isEmpty() ){
                this.database.remove( count );
            }
            this.items.put(item,++count);
            set = this.database.get( count );
            if( set == null ){
                set = new TreeSet<T>();
                database.put(count,set);
            }
            set.add( item );
        }else if( this.items.size() < this.counter ){
            this.items.put(item,1L);
            TreeSet<T> set = this.database.get( 1L );
            if( set == null ){
                set = new TreeSet<T>();
                this.database.put(1L,set);
            }
            set.add(item);
        }else{
            Map.Entry<Long,TreeSet<T>> treeSetEntry =
                    this.database.firstEntry();
            Long count = treeSetEntry.getKey();
            TreeSet<T> set = treeSetEntry.getValue();
            T removeItem = set.pollFirst();
            if( set.isEmpty() ){
                this.database.remove( count );
            }
            this.items.remove( removeItem );
            this.items.put( item, ++ count );
            set = this.database.get( count );
            if( set == null ){
                set = new TreeSet<T>();
                this.database.put(count,set);
            }
            set.add(item);
        }
        this.reachTimes ++;
        return true;
    }

    @Override
    public Long frequency(T item) {
        if( this.items.containsKey( item ) ){
            return this.items.get( item );
        }
        return 0L;
    }

    @Override
    public List<Boolean> multiAdd(List<T> items, List<Long> increments) {
        if( items == null || items.size() == 0 ){
            return null;
        }
        List<Boolean> result = new ArrayList<Boolean>();
        for( T item : items ){
            result.add( this.add( item ,1L) );
        }
        return result;
    }

    /**
     *
     * @param k
     * @return
     */
    public Set<T> TopkeySets( int k ){
       return this.items.keySet();
    }

    @Override
    public List<Map.Entry<T, Long>> peek(int k) {
        //sort the map
        List<Map.Entry<T,Long>> items_ = new ArrayList<Map.Entry<T, Long>>(this.items.entrySet());
        Collections.sort(items_, new Comparator<Map.Entry<T, Long>>() {
            @Override
            public int compare(Map.Entry<T, Long> o1, Map.Entry<T, Long> o2) {
                return o2.getValue().compareTo( o1.getValue() );
            }
        });

        int peekK = Math.min(k,this.items.size());
        return items_.subList(0,peekK);
    }

    @Override
    public Long totalItemsReach() {
        return this.reachTimes;
    }

    public TreeMap<Long, TreeSet<T>> getDatabase() {
        return database;
    }

    public void setDatabase(TreeMap<Long, TreeSet<T>> database) {
        this.database = database;
    }

    public Map<T, Long> getItems() {
        return items;
    }

    public void setItems(Map<T, Long> items) {
        this.items = items;
    }

    public long getReachTimes() {
        return reachTimes;
    }

    public void setReachTimes(long reachTimes) {
        this.reachTimes = reachTimes;
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public String help(String function) {
        return null;
    }

    @Override
    public String help(Object type, String var) {
        return null;
    }

}
