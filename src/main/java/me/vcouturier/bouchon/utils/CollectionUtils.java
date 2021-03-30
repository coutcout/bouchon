package me.vcouturier.bouchon.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CollectionUtils {

    public static <T> Optional<T> getFirstDuplicatedEntry(List<T> list){
        Set<T> set = new HashSet<>();
        for(T element : list){
            if(!set.add(element)){
                return Optional.of(element);
            }
        }

        return Optional.empty();
    }
}
