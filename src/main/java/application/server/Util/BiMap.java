package application.server.Util;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class BiMap<K, V> {
    Map<K, V> keyToValueMap;
    Map<V, K> valueToKeyMap;

    //读写锁
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    //获取写锁
    private final Lock wlock = rwLock.writeLock();
    //获取读锁
    private final Lock rLock = rwLock.readLock();

    @SuppressWarnings("unchecked")
    public BiMap(Supplier<Map<Object, Object>> mapFactory) {
        keyToValueMap = (Map<K, V>) mapFactory.get();
        valueToKeyMap = (Map<V, K>) mapFactory.get();
    }


    public void put(K key, V value){
        wlock.lock();
        try {
            keyToValueMap.put(key, value);
            valueToKeyMap.put(value, key);

        } finally {
            wlock.unlock();
        }
    }

    public V getByKey(K key){
        rLock.lock();
        try {
            return keyToValueMap.get(key);
        } finally {
            rLock.unlock();
        }
    }

    public K getByValue(V value) {
        rLock.lock();
        try {
            return valueToKeyMap.get(value);
        } finally {
            rLock.unlock();
        }
    }

    public Set<V> getValueSet() {
        rLock.lock();
        try {
            return new HashSet<>(keyToValueMap.values());
        } finally {
            rLock.unlock();
        }
    }

    public void removeByKey(K key) {
        wlock.lock();
        try {
            if (key == null) return;
            valueToKeyMap.remove(keyToValueMap.remove(key));
        } finally {
            wlock.unlock();
        }
    }

    public void removeByValue(V value) {
        wlock.lock();
        try {
            if (value == null) return;
            keyToValueMap.remove(valueToKeyMap.remove(value));
        } finally {
            wlock.unlock();
        }
    }

    public Set<Map.Entry<K,V>> entrySet(){
        return keyToValueMap.entrySet();
    }

    public void remove(K key, V value) {
        wlock.lock();
        try {
            if (key != null) {
                valueToKeyMap.remove(keyToValueMap.remove(key));
            }
            if (value != null) {
                keyToValueMap.remove(valueToKeyMap.remove(value));
            }
        } finally {
            wlock.unlock();
        }
    }
}
