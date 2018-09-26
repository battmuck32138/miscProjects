import edu.princeton.cs.algs4.Picture;


public class Tests {


    public static void main(String[] args) {


        //Test findVerticalSeam()
        Picture p1 = new Picture("images/6x5.png");
        SeamCarver s1 = new SeamCarver(p1);
        int[] seam1 = s1.findVerticalSeam();
        System.out.print("Seam1: ");
        for (int x : seam1) {
            System.out.print(x + ", ");
        }
        System.out.println();


        //test removeVerticalSeam()
        Picture p2 = new Picture("images/chameleon.png");
        SeamCarver s2 = new SeamCarver(p2);
        s2.picture().show();
        for (int i = 0; i < 300; i++) {
            int[] seam2 = s2.findVerticalSeam();
            s2.removeVerticalSeam(seam2);
        }
        s2.picture().show();


        //test removeHorizontalSeam();
        for (int i = 0; i < 130; i++) {
            int[] seam2 = s2.findHorizontalSeam();
            s2.removeHorizontalSeam(seam2);
        }
        s2.picture().show();


        //test removeVerticalSeam()
        Picture p3 = new Picture("images/HJoceanSmall.png");
        SeamCarver s3 = new SeamCarver(p3);
        s3.picture().show();
        for (int i = 0; i < 200; i++) {
            int[] seam3 = s3.findVerticalSeam();
            s3.removeVerticalSeam(seam3);
        }
        s3.picture().show();


        //test removeHorizontalSeam();
        for (int i = 0; i < 100; i++) {
            int[] seam3 = s3.findHorizontalSeam();
            s3.removeHorizontalSeam(seam3);
        }
        s3.picture().show();


    }

}
