import java.awt.*;
public class GridManager {
    private Tile[][] grid = new Tile[Var.gridWidth][Var.gridHeight];
    private DLList<Block> blocks = new DLList<Block>();
    private int blockNum = 0;
    public GridManager(){
        for(int i = 0;i<grid.length;i++){
            for(int j = 0;j<grid[i].length;j++){
                grid[i][j] = new Tile(i,j);
            }
        }
    }
    
    
    public void drawMe(Graphics g, int initX, int initY){
        int x = initX;
        int y = initY;
        for(int i = 0;i<grid.length;i++){
            for(int j = 0;j<grid[i].length;j++){
                grid[i][j].drawMe(g,x,y);
                y+=Var.widthBlock;
            }
            x+=Var.heightBlock;
            y = initX;

        }
    }
    public void createBlock(){
        System.out.println("CREATED A BLOCK");
        // let's just create an l block for now
        if((int)(Math.random()*2)==0){

            Block b = new Block('l',blockNum);
            blockNum++;
            blocks.add(b);
            b.setMoving(true);
    
            // add the tiles to the grid
            grid[3][0].addBlock(b);
            grid[4][0].addBlock(b);
            grid[5][0].addBlock(b);
            grid[6][0].addBlock(b);
    
            b.addTile(grid[3][0]);
            b.addTile(grid[4][0]);
            b.addTile(grid[5][0]);
            b.addTile(grid[6][0]);
        }else{
            Block b = new Block('o',blockNum);
            blockNum++;
            blocks.add(b);
            b.setMoving(true);
 
            grid[4][0].addBlock(b);
            grid[5][0].addBlock(b);
            grid[4][1].addBlock(b);
            grid[5][1].addBlock(b);

            b.addTile(grid[4][0]);
            b.addTile(grid[5][0]);
            b.addTile(grid[4][1]);
            b.addTile(grid[5][1]);
            

        }



    }
    public void moveRightActiveBlock(){
        Block b = blocks.get(blocks.size()-1);
        if(!b.isMoving()){
            return;
        }
        DLList<Tile> tiles = b.getTiles();
        if(collision('r',b)){

        }else{
            // move down
            b.clearTiles();
            for(int i = tiles.size()-1 ;i>=0;i--){

                // adding block to tile
                Tile t = tiles.get(i);
                int x = t.getX();
                int y = t.getY();
                t.removeBlock();

                // adding tile to block
                b.addTile(grid[x+1][y]);
            }
            // makes sure, when doing it separately, that the blocks dont get out of sync
            DLList<Tile> newTiles = b.getTiles();
            for(int i = 0;i<newTiles.size();i++){
                newTiles.get(i).addBlock(b);
            }
        }
    }
    public void moveLeftActiveBlock(){
        Block b = blocks.get(blocks.size()-1);
        if(!b.isMoving()){
            return;
        }
        DLList<Tile> tiles = b.getTiles();
        if(collision('l',b)){

        }else{
            // move down
            System.out.println("Moving the block left");
            b.clearTiles();
            for(int i = 0 ;i<tiles.size();i++){

                // adding block to tile
                Tile t = tiles.get(i);
                int x = t.getX();
                int y = t.getY();
                t.removeBlock();
                // adding tile to block
                b.addTile(grid[x-1][y]);
            }
            DLList<Tile> newTiles = b.getTiles();
            for(int i = 0;i<newTiles.size();i++){
                newTiles.get(i).addBlock(b);
            }

        }


    }
    
    public void moveDownActiveBlock(){
        // TODO: Make sure that it only gets the right block
        Block b = blocks.get(blocks.size()-1);
        // System.out.println("TRYING TO MOVE DOWN");
        // System.out.println("MOVING? "+b.isMoving());
        if(!b.isMoving()){
            // System.out.println("WHAT IS HAPPENING??");
            return;
        }
        // check for a collision
        if(collision('d',b)){
            // stop moving
            setMoving(false);
        }else{
            // move down
            DLList<Tile> tiles = b.getTiles();
            b.clearTiles();
            for(int i = 0 ;i<tiles.size();i++){

                // adding block to tile
                Tile t = tiles.get(i);
                int x = t.getX();
                int y = t.getY();
                t.removeBlock();

                // System.out.println("x "+x+" y "+y);

                // adding tile to block
                b.addTile(grid[x][y+1]);
            }
            DLList<Tile> newTiles = b.getTiles();
            for(int i = 0;i<newTiles.size();i++){
                newTiles.get(i).addBlock(b);
            }

        }
    }
    
    public boolean collision(char direction, Block b){
        if(direction=='d'){
            // System.out.println("Checking for collision");
            DLList<Tile> tiles = b.getTiles();
            for(int i = 0 ;i<tiles.size();i++){
                Tile t = tiles.get(i);
                int x = t.getX();
                int y = t.getY();
                if(y+1>=grid[0].length){
                    System.out.println("Hit the bottom?");
                    return true;
                }
                if(grid[x][y+1].containsBlock()){
                    boolean result = grid[x][y+1].getBlock().equals(b);
                    if(!result){
                        System.out.println("Hit a block?");
                        return true;
                    }
                }
            }
            // System.out.println("Not hitting anything");
            return false;
        }
        if(direction=='l'){
            DLList<Tile> tiles = b.getTiles();
            boolean collision = false;
            for(int i = 0 ;i<tiles.size();i++){
                Tile t = tiles.get(i);
                int x = t.getX();
                int y = t.getY();
                if(x-1<0){
                    System.out.println("Hit the side?");
                    collision = true;
                    break;
                }
                if(grid[x-1][y].containsBlock()){
                    boolean result = grid[x-1][y].getBlock().equals(b);
                    if(!result){
                        System.out.println("Hit a block?");
                        collision = true;
                        break;
                    }
                }
            }
            return collision;
        }
        if(direction=='r'){
            DLList<Tile> tiles = b.getTiles();
            boolean collision = false;
            for(int i = 0 ;i<tiles.size();i++){
                Tile t = tiles.get(i);
                int x = t.getX();
                int y = t.getY();
                if(x+1>=Var.gridWidth){
                    System.out.println("Hit the side?");
                    collision = true;
                    break;
                }
                if(grid[x+1][y].containsBlock()){
                    boolean result = grid[x+1][y].getBlock().equals(b);
                    if(!result){
                        System.out.println("Hit a block?");
                        collision = true;
                        break;
                    }
                }
            }
            return collision;
        }
        return false;

    }
    public boolean isMoving(){
        return blocks.get(blocks.size()-1).isMoving();
    }
    public void setMoving(boolean flag){
        if(!flag){
            
            // check for all rows, seeing if any are full
            // TODO: make this more efficient, checking only the rows that were just added to
            
            // starts from bottom goes to top
            for(int y = Var.gridHeight-1;y>=0;y--){
                boolean fullRow = true;
                for(int x = 0;x<Var.gridWidth;x++){
                    if(!grid[x][y].containsBlock()){
                        fullRow = false;
                    }
                }
                // somehow we need to transfer everything down???
                // we'll just get rid of it for now
                if(fullRow){
                    // get everything above it??
                    for(int changeY = y-1;changeY>=0;changeY--){
                        for(int x = 0;x<Var.gridWidth;x++){
                            grid[x][changeY+1] = grid[x][changeY];
                        }
    
                    }
                    y++;
                }
            }
            System.out.println("SETTING MOVING TO FALSE");
            blocks.get(blocks.size()-1).setMoving(false);

        }

    }

}
