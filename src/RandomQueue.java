import java.util.LinkedList;

public class RandomQueue<E> {

    private LinkedList<E> queue;

    public RandomQueue() { queue = new LinkedList<E>();}

    public void add( E e) {

        if( Math.random() > 0.5)
            queue.addLast( e);
        else
            queue.addFirst( e);
    }

    public E remove() {

        if( queue.size() == 0)
            throw new IllegalArgumentException("Empty Random-Queue");

        if( Math.random() > 0.25)
            return queue.removeLast();
        else
            return queue.removeFirst();

    }

    public int size() { return queue.size();}

    public boolean empty() { return queue.size() == 0;}
}
