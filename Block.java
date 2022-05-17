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
            drawSquare(g,x,y);
            drawSquare(g,x+Var.widthBlock,y);
            drawSquare(g,x+Var.widthBlock*2,y);
            drawSquare(g,x+Var.widthBlock*3,y);
        }
        if(this.getType()=='o'){
            drawSquare(g,x,y);
            drawSquare(g,x+Var.widthBlock,y);
            drawSquare(g,x,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock,y+Var.heightBlock);

        }
        if(this.getType()=='s'){
            drawSquare(g,x+Var.widthBlock,y);
            drawSquare(g,x+Var.widthBlock*2,y);
            drawSquare(g,x,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock,y+Var.heightBlock);
        }
        if(this.getType()=='z'){
            drawSquare(g,x,y);
            drawSquare(g,x,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock*2,y+Var.heightBlock);
        }
        if(this.getType()=='l'){
            drawSquare(g,x+Var.widthBlock*0,y);
            drawSquare(g,x+Var.widthBlock*1,y);
            drawSquare(g,x+Var.widthBlock*1,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock*2,y+Var.heightBlock);
        }
        if(this.getType()=='j'){
            drawSquare(g,x+Var.widthBlock*2,y);
            
            drawSquare(g,x+Var.widthBlock*0,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock*1,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock*2,y+Var.heightBlock);
        }
        if(this.getType()=='t'){
            drawSquare(g,x+Var.widthBlock,y);
            drawSquare(g,x,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock,y+Var.heightBlock);
            drawSquare(g,x+Var.widthBlock*2,y+Var.heightBlock);
        }
    }
    private void drawSquare(Graphics g, int x, int y){
        g.setColor(color.brighter());
        g.fillRect(x+3,y,Var.widthBlock-3,3);
        g.fillRect(x,y,3,Var.heightBlock);

        g.setColor(color.darker());
        g.fillRect(x,y+Var.heightBlock-3,Var.widthBlock,3);
        g.fillRect(x+Var.widthBlock-3,y,3,Var.heightBlock);


        g.setColor(color);
        g.fillRect(x+3,y+3,Var.widthBlock-6,Var.heightBlock-6);
    }
    public static void drawSquare(Graphics g, int x, int y, Color color, int width, int height){
        g.setColor(color.brighter());
        g.fillRect(x+3,y,width-3,3);
        g.fillRect(x,y,3,height);

        g.setColor(color.darker());
        g.fillRect(x,y+height-3,width,3);
        g.fillRect(x+width-3,y,3,height);


        g.setColor(color);
        g.fillRect(x+3,y+3,width-6,height-6);

    }
}
