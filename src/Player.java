import java.util.*;

/**
 * Save the Planet.
 * Use less Fossil Fuel.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // the number of points used to draw the surface of Mars.
        int flatPointXstart = 0;
        int flatPointXend = 0;
        int previousLandY = 0;
        int previousLandX = 0;
        int landingPointY = 0;

        int[] allXPoints = new int[N];
        int[] allYPoints = new int[N];

        for (int i = 0; i < N; i++) {

            int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            allXPoints[i] = landX;
            allYPoints[i] = landY;
            if (previousLandY - landY == 0) {
                flatPointXstart = previousLandX;
                flatPointXend = landX;
                landingPointY = landY;
            }
            previousLandY = landY;
            previousLandX = landX;
            debug(i + ". landX: " + landX + " landY: " + landY);
        }
        int landingPointX = flatPointXstart + (flatPointXend - flatPointXstart) / 2;

        Surface surface = new Surface(allXPoints, allYPoints);

        //siin saab vaadata, määratud lõiku (i=X), kui kõrgel on Y
        /*for(int i= 0; i<100;i++){
           debug(surface.getSurfaceY(i));
        }*/

        //siin on mängu toodud asukohtades Y kõrgused
        int c = 0;
        for (int x : allXPoints) {
            debug(c+". landX: " + x + " landY: " + surface.getSurfaceY(x));
            c++;
        }

        double headingVector = 0;
        double absoluteVS = 0;
        double absoluteHS = 0;
        double vectorSpeed = 0;
        //first input to get the start point
        int X = in.nextInt();
        int Y = in.nextInt();
        int HS = in.nextInt();
        int VS = in.nextInt();
        int F = in.nextInt();
        int R = in.nextInt();
        int P = in.nextInt();
        System.out.println("0 0"); // fist input from user
        final int startX = X; // where is the start
        final int startY = Y;
        final int startHS = HS;
        final int startVS = VS;
        final int startP = P;
        final int startR = R;

        //s

        Lander lander = new Lander();

        // game loop
        while (true) {
            X = in.nextInt();
            Y = in.nextInt();
            HS = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            VS = in.nextInt(); // the vertical speed (in m/s), can be negative.
            F = in.nextInt(); // the quantity of remaining fuel in liters.
            R = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            P = in.nextInt(); // the thrust power (0 to 4).

            //P = 1;
            //R = -45;

            // lander objekti loomine
            lander.setPos(X, Y, landingPointX, landingPointY, flatPointXstart);
            lander.setMove(HS, VS, R, P);
            lander.setStartAttr(startX, startY, startHS, startVS, startP, startR); //krt seda pole vaja vist nii mahukalt

            // lander objekti kasutamine
            //lander.landingRotation();
            //lander.maintainSpeedApp();


            if (lander.getLandAlpha() <= 52) {
                lander.setR(lander.isFromLeft() ? 15 : -15); //TODO peab valemi tegema, mis igal korral on erinev nurk, mis ohutu on
                lander.setP(4);
            }
            if (lander.getLandAlpha() > 52 || lander.getHeight() < 1000) {
                lander.landingRotation();
                debug("landing");
                if (lander.getVS() < 40) {
                    lander.setP(3);
                } else if (lander.getVS() > 40) {
                    lander.setP(4);

                }
            }

            //kolmanda taseme jaoks
            if (lander.getLandingDist() < 1050 && lander.getHS() > 40) {
                lander.setP(4);
            }

            /**
             if (lander.isFromLeft() && lander.getLandingDist() > 1000) {
             lander.setR(-10);
             lander.setP(3);
             }
             */

            if (lander.getHeight() < 100) {
                lander.setR(0);
            }


            //debugging info
            debug("speed: " + lander.getSpeed());
            //debug("findY: " + lander.findLandY());
            //debug("vector: " + lander.vector);
            //debug("acc distance: " + lander.pDistLandX());
            //debug("when landingspot X: " + lander.getLandX() + " Y: " + lander.distance() + ", but should be: " + lander.getLandY());
            //debug("rotation: "+ lander.R);
            //debug("varG: "+ lander.varG);
            //debug("landX: "+ lander.landX);
            //debug("R: "+lander.R + " P: "+lander.P);
            //debug(lander.HS);
            //debug("direct distance: " + lander.diagonalDistance);
            debug(" angle of impact: " + lander.getLandAlpha());
            //debug(lander.distance2());
            //debug(lander.getLandingDist() + " " + lander.getHeight());
            //debug(lander.getHeight());
            debug("surface height: " + surface.getSurfaceY(lander.getX()));
            //debug("height from surface: " + (lander.getY() - surface.getSurfaceY(lander.getX())));


            System.out.println(lander.getR() + " " + lander.getP());

        }
    }


    public static void debug(String string) {
        System.err.println(string);
    }

    public static void debug(double string) {
        System.err.println(string);
    }


    // 45 kraadine nurk on alati kõige optimaalsem projektiilile
}

class Lander {
    private int X;
    private int Y;
    private int xFlatStart;
    private int landX;
    private int landY;
    private int VS;
    private int HS;
    private int R;
    private int P;
    private int height;

    private double vector;
    private double landAlpha;

    private double speed;
    private double varG;

    private boolean fromLeft;
    private int landingDist;
    private int diagonalDistance;

    private int startX;
    private int startY;

    private int startHS;
    private int startVS;
    private int startP;
    private int startR;

    public Lander() {
        //no action needed
    }

    public void setPos(int X, int Y, int landX, int landY, int xFlatStart) {
        this.X = X;
        this.Y = Y;

        this.xFlatStart = xFlatStart;

        //maandumispunkt X teljel
        this.landX = landX;

        //maandumispunkt Y teljel
        this.landY = landY;

        //kaugus maapinnast Y teljel
        this.height = Y - landY;

        //kas paikneb maandumiskohast paremal või vasakul
        this.fromLeft = X < xFlatStart ? true : false;

        //kaugus maandumiskohast x teljel
        this.landingDist = fromLeft ? landX - X : X - landX;

        //kui pikk on otsetee maandumiskohta
        int x = Math.max(this.X, this.landX) - Math.min(this.X, this.landX);
        int y = Math.max(this.Y, this.landY) - Math.min(this.Y, this.landY);
        this.diagonalDistance = (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

        this.landAlpha = Math.toDegrees(Math.acos((double) this.landingDist / this.diagonalDistance));


    }

    public void setStartAttr(int startX, int startY, int startHS, int startVS, int startP, int startR) {
        this.startX = startX;
        this.startY = startY;
        this.startHS = startHS;
        this.startVS = startVS;
        this.startP = startP;
        this.startR = startR;


    }

    public void setMove(int HS, int VS, int R, int P) {
        this.HS = Math.abs(HS);
        this.VS = Math.abs(VS);
        this.R = R;
        this.P = P;
        this.vector = Math.toDegrees(Math.atan((this.VS - 3.711) / this.HS));
        this.speed = Math.abs(this.VS) / (Math.cos(Math.toRadians(Math.abs(this.HS) / this.vector)));
        this.varG = 3.711 - Math.sin(Math.toRadians(Math.abs(this.R))) * this.P; //custom g, mis võtab arvesse, mis suunas rakett on ja palju kiirendus on. kasutatakse all
    }

    public double findLandY() {
        //returns the Y for when X in landingspot.
        double g = 3.711;
        double heading = Math.toRadians(this.vector);
        double y = this.height + this.X * Math.tan(heading) - (g * this.X * this.X) / (2 * this.speed * this.speed * Math.cos(heading) * Math.cos(heading));
        return y;
    }

    public int pDistLandX() {
        //height -= 2500; // height 2300 läheb test2 läbi
        //TODO IMPORTANT - g arvutamisel on oluline, mis suunas parasjagu masin keeratud - suund

        double flyingDistance = this.height + this.speed * Math.cos(this.R) * (this.speed * Math.sin(this.R) + Math.sqrt(Math.pow((this.speed * Math.sin(this.R)), 2) + 2 * this.varG * this.height)) / this.varG;         //d = V₀ * cos(α) * [V₀ * sin(α) + √((V₀ * sin(α))² + 2 * g * h)] / g

        return (int) flyingDistance - this.landingDist;

    }

    public double distance() {

        double y = this.height + this.landX * Math.tan(Math.toRadians(Math.abs(this.R))) - this.varG * Math.pow(this.landX, 2) / (2 * Math.pow(this.speed, 2) * Math.pow(Math.toRadians(Math.abs(this.R)), 2));
        return y;
    }

    public double distance2() {
        double d = this.speed - Math.pow(this.P, 2) / 2;
        return d;
    }

    public void tiltDecc() {
        if (this.fromLeft) {
            this.setR(this.R + 10);
        } else {
            this.setR(this.R - 10);
        }
    }

    public void tiltAcc() {
        if (this.fromLeft) {
            this.setR(this.R - 10);
        } else {
            this.setR(this.R + 10);
        }
    }

    public void landingRotation() {
        int corr = this.fromLeft ? 90 : -90;
        this.setR((int) Math.round(this.vector) + corr);
    }

    public void maintainSpeedApp() {

    }

    public void maintainSpeedLand() {
        //empty
    }
    //setters

    public void setR(int r) {
        if (r < -90) {
            r = -90;
        } else if (r > 90) {
            r = 90;
        }
        R = r;
    }

    public void setP(int p) {
        P = p;
    }


    //getters


    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getxFlatStart() {
        return xFlatStart;
    }

    public int getLandX() {
        return landX;
    }

    public int getLandY() {
        return landY;
    }

    public int getVS() {
        return VS;
    }

    public int getHS() {
        return HS;
    }

    public int getR() {
        return R;
    }

    public int getP() {
        return P;
    }

    public int getHeight() {
        return height;
    }

    public double getVector() {
        return vector;
    }

    public double getLandAlpha() {
        return landAlpha;
    }

    public double getSpeed() {
        return speed;
    }

    public double getVarG() {
        return varG;
    }

    public boolean isFromLeft() {
        return fromLeft;
    }

    public int getLandingDist() {
        return landingDist;
    }

    public int getDiagonalDistance() {
        return diagonalDistance;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getStartHS() {
        return startHS;
    }

    public int getStartVS() {
        return startVS;
    }

    public int getStartP() {
        return startP;
    }

    public int getStartR() {
        return startR;
    }
}

class Surface {

    int[] mainPointsX;
    int[] mainPointsY;
    double[] surfaceY;

    public Surface(int[] mainPointsX, int[] mainPointsY) {

        //põhimõtteliselt töötab, aga kusagil arvutab valesti
        this.mainPointsX = mainPointsX;
        this.mainPointsY = mainPointsY;
        this.surfaceY = new double[mainPointsX[mainPointsX.length - 1] + 1]; // 0 index vaja ka arvesse võtta ja array suurusele liita

        for (int i = 0; i < mainPointsX.length; i++) {

            surfaceY[mainPointsX[i]] = mainPointsY[i]; //suures arrays on index X koordinaat ja selles kohas Y vaja teada.

            //alpha leidmiseks
            int x = 0;
            if (i > 0) {
                x = mainPointsX[i] - mainPointsX[i - 1];
            }
            //TODO y arvuamisel on viga. mitte esimese punkti kõrgus, vaid eelmise punkti kõrgus tuleb maha arvutada
            int y = mainPointsY[i] - mainPointsY[0]; //esimese punkti kõrgus tuleb maha lahutada, et õige kaateti kõrgus trig arvutusele tuleks
            if (i > 0 && mainPointsY[i] < mainPointsY[i - 1]) {
                y = mainPointsY[i - 1];
            }
            double alpha = Math.atan2(y, x); //nurga arvutamiseks

            if (i > 0 && mainPointsY[i] == mainPointsY[i - 1] || x == 0) {
                alpha = 0;
            }

            if (i != 0 && mainPointsY[i] > mainPointsY[i - 1]) {
                for (int j = mainPointsX[i - 1]; j < mainPointsX[i]; j++) {
                    //debug(j);
                    if (j > 0) {
                        this.surfaceY[j] = this.surfaceY[j - 1] + Math.tan(alpha); //siin ei pea x läbi kordama, sest x on alati 1
                    }

                }

            } else if (i != 0 && mainPointsY[i] < mainPointsY[i - 1]) {


                for (int j = mainPointsX[i - 1]; j < mainPointsX[i]; j++) {

                    if (j > 0) {
                        this.surfaceY[j] = this.surfaceY[j - 1] - Math.tan(alpha); //siin ei pea x läbi kordama, sest x on alati 1
                    }
                }

            } else if (i != 0 && mainPointsY[i] == mainPointsY[i-1]) {
                for (int j = mainPointsX[i-1]; j < mainPointsX[i]; j++){
                    this.surfaceY[j] = this.surfaceY[j-1];
                }

            }


        }

    }

    public double getSurfaceY(int x) {
        return surfaceY[x];
    }

    public double[] getSurfaceY() {
        return surfaceY;
    }
}
