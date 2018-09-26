


public class ImageFileInfo {
    private String fileName;
    private double ullon;
    private double ullat;
    private double lrlon;
    private double lrlat;


    public double getUllon() {
        return ullon;
    }

    public void setUllon(double ullon) {
        this.ullon = ullon;
    }

    public double getUllat() {
        return ullat;
    }

    public void setUllat(double ullat) {
        this.ullat = ullat;
    }

    public double getLrlon() {
        return lrlon;
    }

    public void setLrlon(double lrlon) {
        this.lrlon = lrlon;
    }

    public double getLrlat() {
        return lrlat;
    }

    public void setLrlat(double lrlat) {
        this.lrlat = lrlat;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return fileName + ", ullon: " + ullon + ", ullat: " + ullat
                + ", lrlon: " + lrlon + ", lrlat: " + lrlat;
    }
}
