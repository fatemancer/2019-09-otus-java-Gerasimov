package ru.otus;

import java.util.*;

public class DIYArrayList<E> implements List<E> {

    private static final float FACTOR = 0.75f;
    private static final int DEFAULT_SIZE = 32;
    private static final int MAX_SIZE = Integer.MAX_VALUE;

    private Object[] array;
    private int nextElementPointer;
    private int indexOfRemoved = -1;
    private int indexOfAdded = -1;

    public DIYArrayList(int size) {
        array = new Object[size];
        nextElementPointer = 0;
    }

    public DIYArrayList() {
        this(DEFAULT_SIZE);
    }

    @Override
    public int size() {
        return nextElementPointer;
    }

    @Override
    public Object[] toArray() {
        return copyEffectiveArray(array.getClass(), nextElementPointer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        return (T[]) copyEffectiveArray(a.getClass(), nextElementPointer);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            int currentElementIndex = 0;

            @Override
            public boolean hasNext() {
                return currentElementIndex < nextElementPointer;
            }

            @Override
            public E next() {
                E result = objectToType(array[currentElementIndex]);
                currentElementIndex++;
                return result;
            }
        };
    }

    @Override
    public boolean add(E e) {
        if (checkCapacity()) {
            array[nextElementPointer] = e;
            nextElementPointer++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        boolean result = false;
        for (int i = 0; i < this.nextElementPointer; i++) {
            E element = getByIndex(i);
            if (element.equals(o)) {
                remove(i);
                result = true;
            }
        }
        return result;
    }

    @Override
    public E get(int index) {
        return getByIndex(index);
    }

    @Override
    public E set(int index, E element) {
        validateIndex(index);
        array[index] = element;
        return element;
    }

    @Override
    public void add(int index, E element) {
        validateIndex(index);
        array[array.length - 1] = element;
        indexOfAdded = index;
        realign();
    }

    @Override
    public E remove(int index) {
        indexOfRemoved = index;
        E removedObject = getByIndex(index);
        realign();
        nextElementPointer--;
        return removedObject;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new ListIterator<E>() {

            int currentIndex;
            boolean addCalled = false;
            boolean setCalled = false;
            boolean previousCalled = false;
            boolean nextCalled = false;
            boolean removeCalled = false;

            int lastReturnedIndex;

            @Override
            public boolean hasNext() {
                return currentIndex < nextElementPointer;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                E result = objectToType(getByIndex(currentIndex));
                lastReturnedIndex = currentIndex;
                currentIndex++;
                nextCalled = true;
                removeCalled = false;
                setCalled = false;
                return result;
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex != 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                E result = objectToType(getByIndex(currentIndex));
                lastReturnedIndex = currentIndex;
                currentIndex--;
                previousCalled = true;
                removeCalled = false;
                setCalled = false;
                return result;
            }

            @Override
            public int nextIndex() {
                return currentIndex + 1;
            }

            @Override
            public int previousIndex() {
                return currentIndex - 1;
            }

            @Override
            public void remove() {
                if (!removeCalled
                        && !addCalled
                        && (nextCalled || previousCalled)
                ) {
                    DIYArrayList.this.remove(lastReturnedIndex);
                    currentIndex--;
                    removeCalled = true;
                } else {
                    throw new IllegalStateException();
                }
            }

            @Override
            public void set(E e) {
                if (!removeCalled
                        && !addCalled
                        && (nextCalled || previousCalled)
                ) {
                    DIYArrayList.this.set(lastReturnedIndex, e);
                    setCalled = true;
                } else {
                    throw new IllegalStateException();
                }
            }

            @Override
            public void add(E e) {
                DIYArrayList.this.add(currentIndex, e);
                addCalled = true;
            }
        };
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private boolean checkCapacity() {
        if (nextElementPointer == MAX_SIZE) {
            return false;
        } else {
            if (1.0f * nextElementPointer / array.length > FACTOR) {
                resizeIfFilled();
                return true;
            }
            return true;
        }
    }

    private void resizeIfFilled() {
        array = copyEffectiveArray(array.getClass(), array.length * 2);
    }

    private void realign() {
        array = copyEffectiveArray(array.getClass(), array.length);
    }

    private E getByIndex(int index) {
        validateIndex(index);
        return objectToType(array[index]);
    }

    private void validateIndex(int index) {
        if (index >= nextElementPointer) {
            throw new IndexOutOfBoundsException(String.format(
                    "Invalid index %s, array is %s elements long", index, nextElementPointer)
            );
        }
    }

    private <T> Object[] copyEffectiveArray(Class<? extends T[]> objectClass, int newSize) {
        if (indexOfAdded == -1 && indexOfRemoved == -1) {
            return Arrays.copyOf(array, newSize, objectClass);
        } else {
            Object[] newArray = new Object[newSize];
            for (int sourceIndex = 0, targetIndex = 0; sourceIndex < size(); sourceIndex++, targetIndex++) {

                if (indexOfRemoved == sourceIndex) {
                    sourceIndex++;
                    indexOfRemoved = -1;
                } else if (indexOfAdded == sourceIndex) {
                    Object elementToInsert = array[array.length - 1];
                    newArray[targetIndex] = elementToInsert;
                    targetIndex++;
                    indexOfAdded = -1;
                }
                newArray[targetIndex] = array[sourceIndex];
            }
            return newArray;
        }
    }

    @SuppressWarnings("unchecked")
    private E objectToType(Object o) {
        return (E) o;
    }
}
