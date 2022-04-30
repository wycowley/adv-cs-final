import java.awt.*;

public class Block {
    private Color color;
    private char type;
    private DLList<Tile> tiles = new DLList<Tile>();
    private int id;
    private boolean moving;
    public Block(char type, int id){
        this.id = id;
        if(type=='s'){
            color = Color.GREEN;
        }
        if(type=='z'){
            color = Color.RED;
        }
        if(type=='l'){
            color = Color.ORANGE;
        }
        if(type=='j'){
            color = Color.BLUE;
        }
        // the square
        if(type=='o'){
            color = Color.YELLOW;
        }
        if(type=='i'){
            color = Color.CYAN;
        }
        if(type=='t'){
            color = Color.MAGENTA;
        }
    }
    public Color getColor(){
        return this.color;
    }
    public DLList<Tile> getTiles(){
        return this.tiles;
    }
    public boolean isMoving(){
        return this.moving;
    }
    public void setMoving(boolean moving){
        this.moving = moving;
    }
    public void addTile(Tile t){
        tiles.add(t);
    }
    public void clearTiles(){
        // very important to instantiate a new array
        tiles = new DLList<Tile>();
    }
    @Override
    public boolean equals(Object o){
        Block b = (Block)o;
        if(this.id==b.id){
            return true;
        }
        return false;
    }
}
