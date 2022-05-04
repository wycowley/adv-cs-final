import java.awt.*;
public class OpponentGrid {
    private Color s = new Color(2,240,0);
    private Color z = new Color(241,0,0);
    private Color l = new Color(240,160,3);
    private Color j = new Color(0,0,240);
    private Color o = new Color(241,240,0);
    private Color i = new Color(0,240,239);
    private Color t = new Color(158,0,240);
    private Color x = new Color(155,155,155);
    private String data;
    private int uid;

    public OpponentGrid(){

    }
    public void setData(String data){
        this.data = data;
    }
    public void setUid(int uid){
        this.uid = uid;
    }
    public int getUid(){
        return this.uid;
    }

    public void drawMe(Graphics g, int x, int y){
        String[] rows = data.split(" ");
        for(int i = 0;i<rows.length;i++){
            String[] tiles = rows[i].trim().split("");
            for(int j = 0;j<tiles.length;j++){
                g.setColor(Color.BLACK);
                if(tiles[j].charAt(0)=='s'){
                    g.setColor(s);
                }
                if(tiles[j].charAt(0)=='z'){
                    g.setColor(z);
                }
                if(tiles[j].charAt(0)=='l'){
                    g.setColor(l);
                }
                if(tiles[j].charAt(0)=='j'){
                    g.setColor(this.j);
                }
                if(tiles[j].charAt(0)=='o'){
                    g.setColor(o);
                }
                if(tiles[j].charAt(0)=='i'){
                    g.setColor(this.i);
                }
                if(tiles[j].charAt(0)=='t'){
                    g.setColor(t);
                }
                if(tiles[j].charAt(0)=='x'){
                    g.setColor(this.x);
                }

                g.fillRect(x+j*Var.opponentWidthBlock,y+i*Var.opponentHeightBlock,Var.opponentWidthBlock,Var.opponentHeightBlock);
            }
        }
    }
}
