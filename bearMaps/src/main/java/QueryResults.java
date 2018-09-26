

public class QueryResults {

    private String[][] render_grid;  //the files to display.
    private double raster_ul_lon;    //the bounding upper left longitude of the rastered image.
    private double raster_ul_lat;    //the bounding upper left latitude of the rastered image.
    private double raster_lr_lon;    //the bounding lower right longitude of the rastered image.
    private double raster_lr_lat;    //the bounding lower right latitude of the rastered image.
    private double depth;            //the depth of the nodes of the rastered image <br>
    private boolean query_success;   //whether the query was able to successfully complete;


    public void setGrid(int x, int y, String file) {
        render_grid[x][y] = file;
    }

    public void setUllon(double ullon) {
        raster_ul_lon = ullon;
    }

    public void setUllat(double ullat) {
        raster_ul_lat = ullat;
    }

    public void setLrlon(double lrlon) {
        raster_lr_lon = lrlon;
    }

    public void setLrlat(double lrlat) {
        raster_lr_lat = lrlat;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setSuccess(boolean success) {
        query_success = success;
    }



}
