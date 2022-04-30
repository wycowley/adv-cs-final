public class Node<E> {
    private Node<E> previous, next;
    private E data;
   
    public Node(E data){
        this.data = data;
    }
  
    public void setNext(Node<E> next){
        this.next = next;
    }
    public void setPrevious(Node<E> previous){
        this.previous = previous;
    }
    public Node<E> next(){
        return this.next;
    }
    public Node<E> previous(){
        return this.previous;
    }
    public E get(){
        return data;
    }
    public String toString(){
        return this.data+"";
    }
  
   
 } 