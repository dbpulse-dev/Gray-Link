package com.dbcat.gray.filter.selector;

/**
 * 用于权重分配置环形队列
 *
 * @param <T>
 */
public class CircularLinkedListQueue<T> {
    private volatile Node<T> head;
    private volatile Node<T> tail;
    private int size;

    public static class Node<T> {
        private T data;
        private Node<T> next;

        public Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public CircularLinkedListQueue() {
        head = null;
        tail = null;
        size = 0;
    }

    public T getNext() {
        T d = head.next.data;
        head = head.next;
        return d;
    }


    public boolean enqueue(T element) {
        Node<T> newNode = new Node<>(element);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            tail.next = head; // 形成环形
        } else {
            tail.next = newNode;
            tail = newNode;
            tail.next = head; // 更新环形链接
        }
        size++;
        return true;
    }

    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        T removedElement = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        size--;
        return removedElement;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}

