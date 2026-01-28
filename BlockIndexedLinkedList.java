package com.projects.opt;

import javax.naming.OperationNotSupportedException;
import java.util.*;

/**
 * A doubly-linked blocking indexed linked list
 * implementation because honestly, its more efficient
 * than the Java Collections implementation
 * <br>
 * Uses stored indexes of a customisable distance to
 * allow for quicker list traversal by index.
 *
 * @author Ronnie Westhead
 * @see java.util.List
 */
public class BlockIndexedLinkedList<E> implements List<E> {
    private final int BLOCK_SIZE = 10;
    ArrayList<ListNode<E>> indexList = new ArrayList<>();
    ListNode<E> first;
    ListNode<E> last;
    int size = 0;

    /**
     * It's a doubly-linked list node that holds data, next and previous items :)
     * @param <E> The Data Type
     */
    private static final class ListNode<E> {
        E data;
        ListNode<E> next;
        ListNode<E> previous;

        ListNode(E data) {
            this.data = data;
        }

        void destroy() {
            next = null;
            previous = null;
            data = null;
        }
    }

    private class CustomIterator implements Iterator<E> {

        private ListNode<E> nextNode = first;

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                E data = nextNode.data;
                nextNode = nextNode.next;
                return data;
            }
        }
    }

    private class CustomListIterator implements ListIterator<E> {

        private ListNode<E> nextNode;
        private ListNode<E> lastReturnedNode;
        private int nextIndex;

        public CustomListIterator(int index) {
            nextNode = index == 0 ?  first : listTraverse(index);
            nextIndex = index;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastReturnedNode = nextNode;
            nextNode = nextNode.next;
            nextIndex++;
            return lastReturnedNode.data;
        }

        @Override
        public boolean hasPrevious() {
            return nextNode != null;
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            lastReturnedNode = nextNode;
            nextNode = nextNode.previous;
            nextIndex--;
            return lastReturnedNode.data;
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return nextIndex - 1;
        }

        @Override
        public void remove() {
            if  (lastReturnedNode == null) {
                throw new IllegalStateException("next() or previous() must be called before performing this operation");
            }
            BlockIndexedLinkedList.this.remove(nextIndex);
            if (lastReturnedNode == nextNode) {
                nextIndex--;
            }
            lastReturnedNode = null;
        }

        @Override
        public void set(E e) {
            lastReturnedNode.data = e;
        }

        @Override
        public void add(E e) {
            BlockIndexedLinkedList.this.add(nextIndex, e);
        }
    }

    /**
     * Get the size of this list
     * @return {@link Integer} containing the size
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        ListNode<E> n = first;
        while (n.next != null) {
            if (n.data.equals(o)) {
                return true;
            }
            n = n.next;
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new CustomIterator();
    }

    @Override
    public Object[] toArray() {
        try {
            throw new OperationNotSupportedException("This method is not implemented yet");
        } catch (OperationNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        try {
            throw new OperationNotSupportedException("This method is not implemented yet");
        } catch (OperationNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean add(E e) {
        if (first == null){
            first = new ListNode<>(e);
            last = first;
            size++;
        } else {
            ListNode<E> prevLast = last;
            ListNode<E> newNode = new ListNode<>(e);
            newNode.previous = prevLast;
            prevLast.next = newNode;
            last = newNode;
            size++;
            if (size % BLOCK_SIZE == 0) {
                indexList.add(newNode);
            }
        }
        return true;
    }

    @Override
    public boolean remove(Object o) {
        ListNode<E> n = first;
        int c = 0;
        do {
            if (n.data.equals(o)) {
                n.previous.next = n.next;
                n.next.previous = n.previous;
                if (size % BLOCK_SIZE == 0) {
                    for (int i = c / BLOCK_SIZE + 1; i < indexList.size(); i++) {
                        indexList.set(i, indexList.get(i).previous);
                    }
                }
                size--;
                n.destroy();
                return true;
            }
            c++;
            n = n.next;
        } while (n.next != null);
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        try {
            throw new OperationNotSupportedException("This method is not implemented yet");
        } catch (OperationNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E e : c) {
            add(e);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        for (E e : c) {
            add(index, e);
            index++;
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object e : c) {
            remove(e);
        }
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        try {
            throw new OperationNotSupportedException("This method is not implemented yet");
        } catch (OperationNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        while (first != null) {
            first.previous = null;
            first.data = null;
            first = first.next;
        }
        size = 0;
        first = null;
        last = null;
    }

    /* Operation not implemented */
    @Override
    public boolean equals(Object o) {
        try {
            throw new OperationNotSupportedException("This method is not implemented yet");
        } catch (OperationNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /* Operation not implemented */
    @Override
    public int hashCode() {
        try {
            throw new OperationNotSupportedException("This method is not implemented yet");
        } catch (OperationNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E get(int index) {
        ListNode<E> cur = listTraverse(index);
        return cur.data;
    }

    @Override
    public E set(int index, E element) {
        ListNode<E> cur = listTraverse(index);
        E old = cur.data;
        cur.data = element;
        return old;
    }

    private ListNode<E> listTraverse(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + " out of bounds for list size: " + size);
        } else if (index == size - 1) {
            return last;
        }
        int i = index / BLOCK_SIZE;
        ListNode<E> cur = i == 0 ? first : indexList.get(i-1);
        i *= 10;
        for (; i < index; i++) {
            cur = cur.next;
        }
        return cur;
    }

    @Override
    public void add(int index, E element) {
        ListNode<E> prev = listTraverse(index);
        ListNode<E> newNode = new ListNode<>(element);
        newNode.next = prev;
        newNode.previous = prev.previous;
        prev.previous = newNode;
        size++;
        if (size % BLOCK_SIZE == 0) {
            indexList.add(index / BLOCK_SIZE, newNode);
        }
    }

    @Override
    public E remove(int index) {
        ListNode<E> removable =  listTraverse(index);
        E data = removable.data;
        ListNode<E> prev = removable.previous;
        ListNode<E> next = removable.next;
        prev.next = next;
        next.previous = prev;
        if (size % BLOCK_SIZE == 0) {
            for (int i = index / BLOCK_SIZE + 1; i < indexList.size(); i++) {
                indexList.set(i, indexList.get(i).previous);
            }
        }
        size--;
        return data;
    }

    /**
     * Returns the index of the first occurrence of an item within the LinkedList
     * @param o element to search for
     * @return the index of the element, or -1 if the item doesn't exist
     */
    @Override
    public int indexOf(Object o) {
        ListNode<E> cur = first;
        for(int i = 0; i < size; i++) {
            if (cur.data.equals(o)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index that an item appears at within the list
     * @param o element to search for
     * @return Returns the index of the last occurrence or -1 if the item is not found.
     */
    @Override
    public int lastIndexOf(Object o) {
        int curLast = -1;
        ListNode<E> cur = last;
        for(int i = size; i > 0; i--) {
            if (cur.data.equals(o)) {
                curLast = i;
            }
            cur = cur.previous;
        }
        return curLast;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new CustomListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new  CustomListIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> returnable = new BlockIndexedLinkedList<>();
        ListNode<E> cur = listTraverse(fromIndex);
        for (int i = fromIndex; i <= toIndex; i++) {
            returnable.add(cur.data);
        }
        return returnable;
    }

}