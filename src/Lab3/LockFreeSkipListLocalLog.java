package Lab3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicMarkableReference;

import static Lab3.Main.randomLevel;

/**
 * Task (4,5,6,7)
 */
public class LockFreeSkipListLocalLog<T> implements Iterable<T> {
    static final int MAX_LEVEL = 31;
    final Node<T> head = new Node<>(Integer.MIN_VALUE);
    final Node<T> tail = new Node<>(Integer.MAX_VALUE);
    //final ConcurrentLinkedQueue<LogEntry<T>> log = new ConcurrentLinkedQueue<>();

    public LockFreeSkipListLocalLog() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i] = new AtomicMarkableReference<>(tail, false);
        }
    }

    public static final class Node<T> {
        final T value;
        final int key;
        final AtomicMarkableReference<Node<T>>[] next;
        private int topLevel;

        // constructor for sentinel nodes
        public Node(int key) {
            this.key = key;
            value = null;
            next = new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<>(null, false);
            }
            topLevel = MAX_LEVEL;
        }

        // constructor for ordinary nodes
        public Node(T x, int height) {
            value = x;
            key = x.hashCode();
            next = new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<>(null, false);
            }
            topLevel = height;
        }
    }

    LogEntry<T> add(T x) {
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] preds = new Node[MAX_LEVEL + 1];
        Node<T>[] succs = new Node[MAX_LEVEL + 1];
        long timeStamp = -1;
        while (true) {
            LogEntry found = find(x, preds, succs, "add");
            if (found.ret) {
                return new LogEntry<>(Thread.currentThread().getId(),"add",x,false,found.timeStamp);
            } else {
                Node<T> newNode = new Node<>(x, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];

                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    continue;
                }
                timeStamp = System.nanoTime();

                for (int level = bottomLevel + 1; level <= topLevel; level++) {
                    while (true) {
                        pred = preds[level];
                        succ = succs[level];
                        if (pred.next[level].compareAndSet(succ, newNode, false, false))
                            break;
                        find(x, preds, succs, null);
                    }
                }
                //og.add(new LogEntry<>(Thread.currentThread().getId(), "add", x, true, timeStamp));
                //return true;
                return new LogEntry<>(Thread.currentThread().getId(),"add",x,true,timeStamp);
            }
        }
    }

    LogEntry<T> remove(T x) {
        int bottomLevel = 0;
        Node<T>[] preds = new Node[MAX_LEVEL + 1];
        Node<T>[] succs = new Node[MAX_LEVEL + 1];
        Node<T> succ;
        long timeStamp = -1;
        while (true) {
            LogEntry found = find(x, preds, succs, "remove");
            if (!found.ret) {
                return new LogEntry<>(Thread.currentThread().getId(),"remove",x,false,found.timeStamp);
            } else {
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
                    boolean[] marked = {false};
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].compareAndSet(succ, succ, false, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = {false};
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(
                            succ, succ, false, true
                    );
                    timeStamp = System.nanoTime();
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find(x, preds, succs, null);
//                        log.add(new LogEntry<>(Thread.currentThread().getId(), "remove", x, true, timeStamp));
//                        return true;
                        return new LogEntry<>(Thread.currentThread().getId(),"remove",x,true,timeStamp);
                    } else if (marked[0]) {
//                        log.add(new LogEntry<>(Thread.currentThread().getId(), "remove", x, false, timeStamp));
//                        return false;
                        return new LogEntry<>(Thread.currentThread().getId(),"remove",x,false,timeStamp);
                    }
                }
            }
        }
    }

    LogEntry<T> find(T x, Node<T>[] preds, Node<T>[] succs, String caller) {
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = {false};
        boolean snip;
        Node<T> pred, curr = null, succ;
        long timeStamp = -1;
        retry:
        while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {

                curr = pred.next[level].getReference();
                timeStamp = System.nanoTime();

                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ, false, false);
                        if (!snip) continue retry;

                        curr = pred.next[level].getReference();
                        timeStamp = System.nanoTime();

                        succ = curr.next[level].get(marked);
                    }
                    if (curr.key < key) {
                        pred = curr;
                        curr = succ;
                    } else {
                        break;
                    }
                }
                preds[level] = pred;
                succs[level] = curr;
            }
//            if (caller != null) {
//                if (curr.key == key && caller.equals("add"))
//                    //log.add(new LogEntry<>(Thread.currentThread().getId(), "add", x, false, timeStamp));
//                    return new LogEntry<>(Thread.currentThread().getId(),"add",x,false,timeStamp);
//                else if (curr.key != key && caller.equals("remove"))
//                    return new LogEntry<>(Thread.currentThread().getId(),"remove",x,false,timeStamp);
//                    //log.add(new LogEntry<>(Thread.currentThread().getId(), "remove", x, false, timeStamp));
//            }
            //return curr.key == key;
            return new LogEntry<>(Thread.currentThread().getId(),"find",x,curr.key==key,timeStamp);
        }
    }

    LogEntry<T> contains(T x) {
        int bottomLevel = 0;
        int v = x.hashCode();
        boolean[] marked = {false};
        Node<T> pred = head, curr = null, succ;
        long timeStamp = -1;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {

            curr = pred.next[level].getReference();
            timeStamp = System.nanoTime();

            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {

                    curr = succ;
                    timeStamp = System.nanoTime();

                    succ = curr.next[level].get(marked);
                }
                if (curr.key < v) {
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
//        if (curr.key == v)
//            log.add(new LogEntry<>(Thread.currentThread().getId(), "contains", x, true, timeStamp));
//        else
//            log.add(new LogEntry<>(Thread.currentThread().getId(), "contains", x, false, timeStamp));
        return new LogEntry<>(Thread.currentThread().getId(),"contains",x,curr.key==v,timeStamp);
    }

    boolean verifyLog(List<LogEntry<T>> logList) {
//        List<LogEntry<T>> logList = new ArrayList<>(log);
        Collections.sort(logList);
        LogEntry<T> entry1;
        LogEntry<T> entry2 = null;
        for (int i = logList.size() - 1; i > -1; i--) {
            entry1 = logList.get(i);
            if ((entry1.op.equals("remove") || entry1.op.equals("contains"))) {
                if (entry1.ret) {
                    for (int j = i - 1; j > -1; j--) {
                        entry2 = logList.get(j);
                        if (entry2.val.equals(entry1.val)) {
                            if (entry2.op.equals("add") && entry2.ret) break;
                            if (entry2.op.equals("remove") && entry2.ret) {
                                System.out.println(1);
                                System.out.println(entry1);
                                System.out.println(entry2);
                                return false;
                            }

                        }
                    }
                    assert entry2 != null;
                    if (!(entry2.op.equals("add") && entry2.val.equals(entry1.val) && entry2.ret)) {
                        System.out.println(2);
                        System.out.println(entry1);
                        System.out.println(entry2);
                        return false;
                    }
                } else {
                    for (int j = i - 1; j > -1; j--) {
                        entry2 = logList.get(j);
                        if (entry2.val.equals(entry1.val)) {
                            if (entry2.op.equals("add") && entry2.ret) {
                                System.out.println(3);
                                System.out.println(entry1);
                                System.out.println(entry2);
                                return false;
                            }
                            if (entry2.op.equals("remove") && entry2.ret) break;
                        }
                    }
                }
            } else if (entry1.op.equals("add")) {
                if (entry1.ret) {
                    for (int j = i - 1; j > -1; j--) {
                        entry2 = logList.get(j);
                        if (entry2.val.equals(entry1.val)) {
                            if (entry2.op.equals("add") && entry2.ret) {
                                System.out.println(4);
                                System.out.println(entry1);
                                System.out.println(entry2);
                                return false;
                            }
                            if (entry2.op.equals("remove") && entry2.ret) break;
                            if (entry2.op.equals("contains") && entry2.ret) {
                                System.out.println(5);
                                System.out.println(entry1);
                                System.out.println(entry2);
                                return false;
                            }
                        }
                    }
                } else {
                    for (int j = i - 1; j > -1; j--) {
                        entry2 = logList.get(j);
                        if (entry2.val.equals(entry1.val)) {
                            if (entry2.op.equals("add") && entry2.ret) break;
                            if (entry2.op.equals("remove") && !entry2.ret) {
                                System.out.println(6);
                                System.out.println(entry1);
                                System.out.println(entry2);
                                return false;
                            }
                            if (entry2.op.equals("contains") && !entry2.ret) {
                                System.out.println(7);
                                System.out.println(entry1);
                                System.out.println(entry2);
                                return false;
                            }
                        }
                    }
                    assert entry2 != null;
                    if (!(entry2.op.equals("add") && entry2.val.equals(entry1.val) && entry2.ret)) {
                        System.out.println(8);
                        System.out.println(entry1);
                        System.out.println(entry2);
                        return false;
                    }
                }
            }
            if (i % 10000 == 0 && i != 0) {
                System.out.println(logList.size() - i + " entries checked");
            }
        }
        return true;
    }

    private class LockFreeSkipListIterator implements Iterator<T> {
        private Node<T> curr = head;
        private Node<T> next = curr.next[0].getReference();

        @Override
        public boolean hasNext() {
            return next.value != null;
        }

        @Override
        public T next() {
            curr = next;
            next = curr.next[0].getReference();
            return curr.value;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LockFreeSkipListIterator();
    }
}
