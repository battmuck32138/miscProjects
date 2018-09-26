//Universe Simulator with driver. 
//NBody.java needs to be run with command line input, use this:
//java NBody 157788000.0 25000.0 data/planets.txt
public class NBody {

	public static double readRadius(String fileName) {
		In in = new In(fileName);
		in.readInt();
		double rad = in.readDouble();
		return rad;
	}

	public static Planet[] readPlanets(String fileName) {
		In in = new In(fileName);
		int numPlanets = in.readInt();
		Planet[] planets = new Planet[numPlanets];

		in.readDouble();
		for (int i = 0; i < planets.length; i++) {
			double xxPos = in.readDouble();
			double yyPos = in.readDouble();
			double xxVel = in.readDouble();
			double yyVel = in.readDouble();
			double mass = in.readDouble();
			String imgFileName = in.readString();
			planets[i] = new Planet(xxPos, yyPos, xxVel, yyVel, mass, imgFileName);
		}
		return planets; 
	}

	public static void main(String[] args) {
		//converts String to double 
		double T = Double.parseDouble(args[0]);
		double dt = Double.parseDouble(args[1]); 
		String fileName = args[2];
		double universeRadius = readRadius(fileName);
		Planet[] planets = readPlanets(fileName);
		double time = 0;

		StdDraw.setScale(-universeRadius, universeRadius);
		StdDraw.picture(0, 0, "images/starfield.jpg");

		for (Planet p : planets) {
			p.draw();
		}

		StdDraw.enableDoubleBuffering();
		while (time < T) {
			double[] xForces = new double[planets.length];
			double[] yForces = new double[planets.length];

			for (int i = 0; i < planets.length; i++) {
				xForces[i] = planets[i].calcNetForceExertedByX(planets);
				yForces[i] = planets[i].calcNetForceExertedByY(planets);
			}

			for (int i = 0; i < planets.length; i++) {
				planets[i].update(dt, xForces[i], yForces[i]);
			}

			StdDraw.picture(0, 0, "images/starfield.jpg");

			for (int i = 0; i < planets.length; i++) {
				planets[i].draw();
			}

			StdDraw.show();
			StdDraw.pause(10);
			time += dt;
		}

		StdOut.printf("%d\n", planets.length);
		StdOut.printf("%.2e\n", universeRadius);
		for (int i = 0; i < planets.length; i++) {
    		StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                  planets[i].xxPos, planets[i].yyPos, planets[i].xxVel,
                  planets[i].yyVel, planets[i].mass, planets[i].imgFileName);   
		}
	
	}

}