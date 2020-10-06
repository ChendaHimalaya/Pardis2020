package Lab3;

import java.util.concurrent.atomic.AtomicReference;

public class SimpleQueue<T> {
    static class Node<T>{
        T val;
        volatile Node<T> next;
    }
    AtomicReference<Node<T>> head=new AtomicReference<Node<T>>();
    Node<T> tail;

    SimpleQueue(){
        tail=new Node<T>();
        head.set(tail);
    }
    public boolean offer(T val){
        Node<T> n= new Node<T>();
        n.val=val;
        Node<T> prev=head.getAndSet(n);
        prev.next=n;
        return true;
    }

    public T poll(){
        Node<T> next=tail.next;
        if(next!=null){
            T val=next.val;
            next.val=null;
            tail=next;
            return val;
        }
        return null;
    }

    public boolean ifEmpty(){
        return head.get()==tail;

    }

}
