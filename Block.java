import java.awt.*;

public class Block {
    private Color color;
    private char type;
    private DLList<Tile> tiles = new DLList<Tile>();
    private int id;
    private boolean moving;
    private Tile rotationStart;
    public Block(char type, int id){
        this.id = id;
        if(type=='s'){
            color = new Color(2,240,0);
        }
        if(type=='z'){
            color = new Color(241,0,0);
        }
        if(type=='l'){
            color = new Color(240,160,3);
        }
        if(type=='j'){
            color = new Color(0,0,240);
        }
        // the square
        if(type=='o'){
            color = new Color(241,240,0);
        }
        if(type=='i'){
            color = new Color(0,240,239);
        }
        if(type=='t'){
            color = new Color(158,0,240);
        }
        if(type=='x'){
            color = new Color(155,155,155);
        }
        this.type = type;
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
    public void setRotationStart(Tile t){
        this.rotationStart = t;
    }
    public Tile getRotationStart(){
        return this.rotationStart;
    }
    public char getType(){
        return this.type;
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

    public void drawMe(Graphics g, int x, int y){
        if(this.getType()=='i'){
            g.setColor(new Color(0,240,239));
            g.fillRect(x,y,Var.widthBlock*4,Var.heightBlock);
        }
        if(this.getType()=='o'){
            g.setColor(new Color(241,240,0));
            g.fillRect(x,y,Var.widthBlock*2,Var.heightBlock*2);
        }
        if(this.getType()=='s'){
            g.setColor(new Color(2,240,0));
            g.fillRect(x+Var.widthBlock,y,Var.widthBlock*2,Var.heightBlock);
            g.fillRect(x,y+Var.heightBlock,Var.widthBlock*2,Var.heightBlock);
        }
        if(this.getType()=='z'){
            g.setColor(new Color(241,0,0));
            g.fillRect(x,y,Var.widthBlock,Var.heightBlock);
            g.fillRect(x,y+Var.heightBlock,Var.widthBlock*3,Var.heightBlock);
        }
        if(this.getType()=='l'){
            g.setColor(new Color(240,160,3));
            g.fillRect(x,y,Var.widthBlock*2,Var.heightBlock);
            g.fillRect(x+Var.widthBlock,y+Var.heightBlock,Var.widthBlock*2,Var.heightBlock);
        }
        if(this.getType()=='j'){
            g.setColor(new Color(0,0,240));
            g.fillRect(x+Var.widthBlock*2,y,Var.widthBlock,Var.heightBlock);
            g.fillRect(x,y+Var.heightBlock,Var.widthBlock*3,Var.heightBlock);
        }
        if(this.getType()=='t'){
            g.setColor(new Color(158,0,240));
            g.fillRect(x+Var.widthBlock,y,Var.widthBlock,Var.heightBlock*2);
            g.fillRect(x,y+Var.heightBlock,Var.widthBlock*3,Var.heightBlock);
        }
    }
}
