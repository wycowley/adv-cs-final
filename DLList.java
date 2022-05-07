public class DLList<E> {
    private Node<E> head;
    private Node<E> tail;
    private int size;

    public DLList(){
        head = new Node<E>(null);
        tail = new Node<E>(null);
  
        head.setNext(tail);
        tail.setPrevious(head);
        size = 0;
   
    }
    public void clear(){
        head = new Node<E>(null);
        tail = new Node<E>(null);
        head.setNext(tail);
        tail.setPrevious(head);
        size = 0;

    }
    public boolean contains(E e){
        Node<E> current = head.next();
        while(current != tail){
            if(current.get().equals(e)){
                return true;
            }
            current = current.next();
        }

        return false;
    }
    private Node<E> getNode(int index){
        Node<E> current;
        
        if(index>size/2){
            current = tail.previous();
            for(int i = size-1; i > index; i--){
                current = current.previous();
            }
            return current;
        }
        current = head.next();
        for(int i = 0; i < index; i++){
            current = current.next();
        }
       
        return current;
    }
   
  
    public void add(E data){
        Node<E> toAdd = new Node<E>(data);
        toAdd.setNext(tail);
        toAdd.setPrevious(tail.previous());
        tail.previous().setNext(toAdd);
        tail.setPrevious(toAdd);
        size++;
    }

    
    public void add(E data, int index){
        Node<E> current;
        Node<E> toAdd = new Node<E>(data);
        if(index >= size){
            add(data);
            return;
        }
        if(size/2>index){
            // add from beginning
            current = head.next();
            int i = 0;
            while(current != null){
                if(index == i){
                    Node<E> previousNode = current.previous();

  
                   
                    toAdd.setPrevious(previousNode);
                    toAdd.setNext(current);
                   
                    current.setPrevious(toAdd);
                    previousNode.setNext(toAdd);
                    size++;
                    break;
                }
                i++;
                current = current.next();
            }
  
        }else{
            // add from end
            current = tail.previous();
            int i = size-1;
            while(current != null){
                if(index == i){
                    Node<E> previousNode = current.previous();
                   
                    toAdd.setPrevious(previousNode);
                    toAdd.setNext(current);
                   
                    current.setPrevious(toAdd);
                    previousNode.setNext(toAdd);
                    size++;
                    break;
                }
                i--;
                current = current.previous();
            }
  
            current = tail.previous();
           
        }
    }
  
    public E get(int index){
        return getNode(index).get();
    }
  
    public int size(){
        return size;
    }
    
    @Override
    public String toString(){
        Node<E> current = head.next();
        String returnString = "";
        while(current.next() != null){
            returnString = returnString + current.get() + " ";
            current = current.next();
        }
        return returnString;
    }
 


    public void remove(int index){
        Node<E> current;
        int i;
        if(size/2>index){
            i = 0;
            current = head.next();
            while(current!=null){
                if(i==index){
                    Node<E> previousNode = current.previous();
                    Node<E> nextNode = current.next();
                    previousNode.setNext(nextNode);
                    nextNode.setPrevious(previousNode);
                    size--;
                    break;
                }
                i++;
                current = current.next();
            }
        }else{
            i = size-1;
            current = tail.previous();
            while(current!=null){
                if(i==index){
                    Node<E> previousNode = current.previous();
                    Node<E> nextNode = current.next();
                    previousNode.setNext(nextNode);
                    nextNode.setPrevious(previousNode);
                    size--;
                    break;
                }
                i--;
                current = current.previous();
            }
  
  
        }
    }
    public void remove(E data){
        Node<E> current = new Node<E>(data);
        current = head.next();
        while(current != null){
            if(current.get().equals(data)){
                Node<E> previousNode = current.previous();
                Node<E> nextNode = current.next();
                previousNode.setNext(nextNode);
                nextNode.setPrevious(previousNode);
                size--;
                break;
            }
            current = current.next();
        }
    }

    public E get(E data){
        Node<E> current = head.next();
        while(current != null){
            if(current.get().equals(data)){
                return current.get();
            }
            current = current.next();
        }
        return null;
    }
  
    public void set(int index, E data){
        this.remove(index);
        this.add(data,index);
    }

    public boolean hasItem(E data){
        Node<E> current = head.next();

        while(current != null){
            if(current.get() == null){
                return false;
            }
            else if(current.get().equals(data)){
                return true;
            }
            current = current.next();
        }
        return false;
    }
   
 }
 