# Iterator Pattern

## Intent
Provide a way to access elements of a collection sequentially without exposing its underlying representation.

## Problem It Solves
- Different collections (list, tree, graph) should be traversable uniformly
- Client shouldn't know the internal structure
- Multiple traversal strategies over the same collection

## Java Example
Java's `Iterable` + `Iterator` is this pattern built into the language.

```java
public class PaginatedResultSet<T> implements Iterable<T> {
    private final Function<Integer, List<T>> pageFetcher;
    private final int pageSize;

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private int currentPage = 0;
            private List<T> currentBatch = Collections.emptyList();
            private int index = 0;

            public boolean hasNext() {
                if (index < currentBatch.size()) return true;
                currentBatch = pageFetcher.apply(currentPage++);
                index = 0;
                return !currentBatch.isEmpty();
            }

            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return currentBatch.get(index++);
            }
        };
    }
}
```

## Resources
- [Refactoring Guru — Iterator](https://refactoring.guru/design-patterns/iterator)
