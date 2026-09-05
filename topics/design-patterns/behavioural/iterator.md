# Iterator Pattern

## Intent

Traverse a collection sequentially without exposing its internal structure (array, linked list, tree). Client iterates uniformly regardless of the underlying storage.

## Built Into Java

`Iterable` + `Iterator` IS this pattern. The `for-each` loop works on anything `Iterable`.

```java
interface Iterator<T> {
    boolean hasNext();
    T next();
}
interface Iterable<T> {
    Iterator<T> iterator();
}
```

---

## Custom Iterator

```java
class PlaylistCollection implements Iterable<Song> {
    private Song[] songs;   // internal structure — hidden from client
    private int size;

    public Iterator<Song> iterator() {
        return new Iterator<>() {
            private int index = 0;
            public boolean hasNext() { return index < size; }
            public Song next() {
                if (!hasNext()) throw new NoSuchElementException();
                return songs[index++];
            }
        };
    }
}

// Client iterates without knowing it's an array internally
for (Song song : playlist) { play(song); }
```

---

## Why It Matters

| Benefit | Explanation |
|---|---|
| Hides internal structure | Client doesn't know array vs list vs tree |
| Uniform traversal | Same for-each for any collection |
| Multiple iterators | Several independent traversals at once |
| Different orders | Forward, backward, filtered iterators |

---

## Real-World Iterator

- Java `for-each` (anything `Iterable`)
- `Iterator`, `ListIterator`, `Spliterator`
- Database result set cursors
- Paginated API results (fetch next page lazily)
- Tree traversal (in-order, pre-order)

---

## Interview Note

Rarely a full design question — Java gives it to you. Know it conceptually. More important to correctly USE `Iterable` than implement from scratch. Occasionally asked: "implement a custom iterator over a paginated/lazy source."

---

## Resources

- [Refactoring Guru — Iterator](https://refactoring.guru/design-patterns/iterator)

## Related

- [Composite](../structural/composite.md) — iterators often traverse composite trees
