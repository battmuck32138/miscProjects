import edu.princeton.cs.algs4.Picture;

import java.awt.Color;


/*
 Seam-carving is a content-aware image resizing technique where
 the image is reduced in size by one pixel of height (or width)
  at a time. A vertical seam in an image is a path of pixels connected
  from the top to the bottom with one pixel in each row. (A horizontal
  seam is a path of pixels connected from the left to the right with one
  pixel in each column.) Unlike standard content-agnostic resizing
  techniques (e.g. cropping and scaling), the most interesting features
   (aspect ratio, set of objects present, etc.) of the image are preserved.
   REMOVES A ROW OR COLUMN OF PIXELS THAT ARE NOT IMPORTANT TO THE IMAGE
   WHILE LEAVING THE IMPORTANT PARTS ALONE.
   In image processing, pixel (x, y) refers to the pixel in column x and row y,
    with pixel (0, 0) at the upper left corner and pixel (W − 1, H − 1)
  at the bottom right corner.
  The color of a pixel is represented in RGB space, using three integers
   between 0 and 255. This is consistent with the java.awt.Color data type.
*/
public class SeamCarver {

    private final Picture PICTURE;
    private Picture picture;  //deep copy of PICTURE


    public SeamCarver(Picture picture) {
        this.PICTURE = picture;
        this.picture = new Picture(PICTURE);
    }


    // current picture
    public Picture picture() {
        //Returns a deep copy of the original.
        Picture newPicture = new Picture(picture);
        return newPicture;
    }


    // width of current picture
    public int width() {
        return picture.width();
    }


    // height of current picture
    public int height() {
        return picture.height();
    }


    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int [] seam = findVerticalSeam();
        transpose();
        return seam;
    }


    /*
    Dual gradient energy function:
    Energy calculation: The energy of each pixel is a measure of the importance
    of each pixel, the higher the energy, the less likely that the pixel will
    be included as part of a seam (removed from the image).
    */
    public double energy(int x, int y) {

        if (x < 0 || x >= picture.width()) {
            throw new IndexOutOfBoundsException();
        }

        if (y < 0 || y >= picture.height()) {
            throw new IndexOutOfBoundsException();
        }

        return squareOfXGradient(x, y) + squareOfYGradient(x, y);
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        MinPQ<Path> paths = new MinPQ<>();
        Path path = null;
        for (int x = 0; x < picture.width(); x++) {
            path = pathFromTop(x);
            paths.insert(path);
        }
        return paths.delMin().path();
    }


    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {

        if (seam == null) {
            throw new NullPointerException("Input seam array cannot be null.");
        } else if (picture.width() == 1) {
            throw new IllegalArgumentException("Image width is 1.");
        } else if (seam.length != picture.width()) {
            throw new IllegalArgumentException("Seam length does not match image width.");
        } else {
            for (int var2 = 0; var2 < seam.length - 2; ++var2) {
                if (Math.abs(seam[var2] - seam[var2 + 1]) > 1) {
                    throw new IllegalArgumentException("Invalid seam, "
                            + "consecutive vertical indices are greater than one apart.");
                }
            }

            Picture edited = new Picture(picture.width(), picture.height() - 1);

            for (int x = 0; x < picture.width(); ++x) {
                int y;
                for (y = 0; y < seam[x]; ++y) {
                    edited.set(x, y, picture.get(x, y));
                }

                for (y = seam[x] + 1; y < picture.height(); ++y) {
                    edited.set(x, y - 1, picture.get(x, y));
                }
            }

            picture = edited;
        }
    }


    // remove vertical seam from picture
    public void removeVerticalSeam(int[] vertSeam) {
        if (vertSeam == null) {
            throw new NullPointerException("Input seam array cannot be null.");
        } else if (picture.height() == 1) {
            throw new IllegalArgumentException("Image height is 1.");
        } else if (vertSeam.length != picture.height()) {
            throw new IllegalArgumentException("Seam length does not match image height.");
        } else {

            for (int x = 0; x < vertSeam.length - 2; ++x) {
                if (Math.abs(vertSeam[x] - vertSeam[x + 1]) > 1) {
                    throw new IllegalArgumentException("Invalid seam, consecutive horizontal "
                            + " indices are greater than one apart.");
                }
            }

            Picture editedPic = new Picture(picture.width() - 1, picture.height());

            for (int y = 0; y < picture.height(); ++y) {
                int x;
                for (x = 0; x < vertSeam[y]; ++x) {
                    editedPic.set(x, y, picture.get(x, y));
                }

                for (x = vertSeam[y] + 1; x < picture.width(); ++x) {
                    editedPic.set(x - 1, y, picture.get(x, y));
                }
            }

            picture = editedPic;
        }
    }


    private double squareOfXGradient(int x, int y) {
        Color leftPix;
        Color rightPix;

        if (picture.width() == 1) {
            return 0.0;
        } else if (x == 0) {
            leftPix = picture.get(picture.width() - 1, y);
            rightPix = picture.get(x + 1, y);
        } else if (x == picture.width() - 1) {
            leftPix = picture.get(x - 1, y);
            rightPix = picture.get(0, y);
        } else {
            leftPix = picture.get(x - 1, y);
            rightPix = picture.get(x + 1, y);
        }

        double leftRed = leftPix.getRed();
        double rightRed = rightPix.getRed();
        double leftGreen = leftPix.getGreen();
        double rightGreen = rightPix.getGreen();
        double leftBlue = leftPix.getBlue();
        double rightBlue = rightPix.getBlue();

        double rx = Math.abs(leftRed - rightRed);
        double gx = Math.abs(leftGreen - rightGreen);
        double bx = Math.abs(leftBlue - rightBlue);

        return (rx * rx) + (gx * gx) + (bx * bx);
    }


    private double squareOfYGradient(int x, int y) {
        Color topPix;
        Color bottomPix;

        if (picture.height() == 1) {
            return 0.0;
        } else if (y == 0) {
            topPix = picture.get(x, picture.height() - 1);
            bottomPix = picture.get(x, y + 1);
        } else if (y == picture.height() - 1) {
            topPix = picture.get(x, y - 1);
            bottomPix = picture.get(x, 0);
        } else {
            topPix = picture.get(x, y - 1);
            bottomPix = picture.get(x, y + 1);
        }

        double ry = Math.abs(topPix.getRed() - bottomPix.getRed());
        double gy = Math.abs(topPix.getGreen() - bottomPix.getGreen());
        double by = Math.abs(topPix.getBlue() - bottomPix.getBlue());

        return ry * ry + gy * gy + by * by;
    }


    private Path pathFromTop(int x) {
        Node parent = null;
        Node min = null;
        int currentX = x;

        for (int y = 0; y < picture.height(); y++) {
            MinPQ<Node> nodes = new MinPQ<>();

            if (y == 0 || picture.width() == 1) {
                Node center = new Node(currentX, y, energy(currentX, y));
                nodes.insert(center);

            } else if (currentX == 0) {
                Node right = new Node(currentX + 1, y, energy(currentX + 1, y));
                nodes.insert(right);
                Node center = new Node(currentX, y, energy(currentX, y));
                nodes.insert(center);

            } else if (currentX == picture.width() - 1) {
                Node left = new Node(currentX - 1, y, energy(currentX - 1, y));
                nodes.insert(left);
                Node center = new Node(currentX, y, energy(currentX, y));
                nodes.insert(center);

            } else {
                Node left = new Node(currentX - 1, y, energy(currentX - 1, y));
                nodes.insert(left);
                Node center = new Node(currentX, y, energy(currentX, y));
                nodes.insert(center);
                Node right = new Node(currentX + 1, y, energy(currentX + 1, y));
                nodes.insert(right);
            }
            min = nodes.delMin();
            currentX = min.getX();
            min.setParent(parent);
            parent = min;
        }

        return new Path(min, picture.height());
    }


    private void transpose() {
        Picture trans = new Picture(picture.height(), picture.width());
        for (int x = 0; x < picture.height(); x++) {
            for (int y = 0; y < picture.width(); y++) {
                trans.set(x, y, picture.get(y, x));
            }
        }
        picture = trans;
    }

}
