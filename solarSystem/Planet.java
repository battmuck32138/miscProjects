//Class that represents a planet that will be used by a simulator. 
public class Planet {

	public double xxPos;
	public double yyPos;
	public double xxVel;
	public double yyVel;
	public double mass;
	public String imgFileName;
	private static double g = 6.67 * Math.pow(10, -11);

	//constructor
	public Planet(double xP, double yP, double xV, 
						double yV, double m, String img) {
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}

	//copy constructor
	public Planet(Planet p) {
		xxPos = p.xxPos;
		yyPos = p.yyPos;
		xxVel = p.xxVel;
		yyVel = p.yyVel;
		mass = p.mass;
		imgFileName = p.imgFileName;
	}

	public double calcDistance(Planet p) {
		double dx = p.xxPos - this.xxPos;
		double dy = p.yyPos - this.yyPos;
		return Math.sqrt(dx*dx + dy*dy);
	}

	public double calcForceExertedBy(Planet p) {
		double r = calcDistance(p);
		return (p.mass * this.mass * g) / (r*r); 
	}

	public double calcForceExertedByX(Planet p) {
		double f = calcForceExertedBy(p);
		double r = calcDistance(p); 
		double dx = p.xxPos - this.xxPos;
		return (f * dx) / r;
	}

	public double calcForceExertedByY(Planet p) {
		double f = calcForceExertedBy(p);
		double r = calcDistance(p);
		double dy = p.yyPos - this.yyPos;
		return (f * dy) / r; 
	}

	public double calcNetForceExertedByX(Planet[] planets) {
		double total = 0; 
		for (Planet p : planets) {
			if (!this.equals(p)){
				total += calcForceExertedByX(p);
			}
		}
		return total; 
	}

	public double calcNetForceExertedByY(Planet[] planets) {
		double total = 0; 
		for (Planet p : planets) {
			if (!this.equals(p)) {
				total += calcForceExertedByY(p);
			}
		}
		return total; 
	}

	public void update(double dt, double fx, double fy) {
		double ax = fx / this.mass;
		double ay = fy / this.mass;
		xxVel += dt * ax;
		yyVel += dt * ay;
		xxPos += dt * xxVel;
		yyPos += dt * yyVel;
	}

	public void draw() {
		StdDraw.picture ( xxPos, yyPos, "images/" + imgFileName);
	}

}
