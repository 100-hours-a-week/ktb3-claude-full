package ktb.common.pagination;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Slice<T> implements Collection<T> {
    private Collection<T> data;
    private final PageInfo pageInfo;
    public static <T> Slice<T> of(Collection<T> content, boolean hasNext, Long nextCursor) {
        return new Slice<>(content, new PageInfo(hasNext, nextCursor));
    }

    public Collection<T> content() {
        return this.data;
    }

    public static <T> Slice<T> of(List<T> content, PageInfo pageInfo) {
        return new Slice<>(content, pageInfo);
    }

    public boolean hasNext() {
        return this.pageInfo.hasNext();
    }

    public Long nextCursor() {
        return this.pageInfo.endCursor();
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.data.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.data.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        this.data.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return this.data.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.data.toArray(a);
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return Collection.super.toArray(generator);
    }

    @Override
    public boolean add(T t) {
        return this.data.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.data.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.data.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.data.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.data.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return this.data.removeIf(filter);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.data.retainAll(c);
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.data.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return this.data.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return this.data.parallelStream();
    }
}
