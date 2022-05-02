import java.awt.*;
public class Tile{
    private boolean containsBlock;
    private Block block;
    private int x;
    private int y;

    public Tile(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void addBlock(Block block){
        this.containsBlock = true;
        this.block = block;
    }
    public void removeBlock(){
        this.containsBlock = false;
        this.block = null;
    }

    public void drawMe(Graphics g, int x, int y){
        if(block != null){
            g.setColor(block.getColor());
            g.fillRect(x,y,Var.widthBlock,Var.heightBlock);

            // g.setColor(new Color(0f,0f,0f,.5f));
            // g.drawRect(x,y,Var.widthBlock,Var.heightBlock);

        }else{
            g.setColor(Color.BLACK);
            g.fillRect(x,y,Var.widthBlock,Var.heightBlock);

            // g.setColor(new Color(0f,0f,0f,.5f));
            // g.drawRect(x,y,Var.widthBlock,Var.heightBlock);

        }
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void setXY(int x, int y){
        this.x = x;
        this.y =y;
    }
    
    public boolean containsBlock(){
        return this.containsBlock;
    }
    public Block getBlock(){
        return this.block;
    }
}