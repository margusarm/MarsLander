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

        for (int i = 0; i < N; i++) {

            int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            if (previousLandY - landY == 0) {
                flatPointXstart = previousLandX;
                flatPointXend = landX;
                landingPointY = landY;
            }
            previousLandY = landY;
            previousLandX = landX;
            System.err.println(i + ". " + landX);
        }
        int landingPointX = flatPointXstart + (flatPointXend - flatPointXstart) / 2;


        System.err.println(flatPointXstart + " - " + flatPointXend);
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

           if (lander.landAlpha < 52){
                lander.R = lander.fromLeft ? 15 : -15; //TODO peab valemi tegema, mis igal korral on erinev nurk, mis ohutu on
                lander.P = 4;
            }
            if (lander.landAlpha >= 52 || lander.height < 1000){
                lander.landingRotation();
                if (lander.VS<40){
                    lander.P = 3;
                } else if (lander.VS > 40) {
                    lander.P = 4;

                }
            }

            //kolmanda taseme jaoks
            if (lander.landingDist < 1050 && lander.HS >40){
                lander.P = 4;
            }



            if (lander.height<100){
                lander.R = 0;
            }


            //debugging info
            debug("speed: " + lander.speed);
            //debug("findY: " + lander.findLandY());
            //debug("vector: " + lander.vector);
            //debug("acc distance: " + lander.pDistLandX());
            debug("when landingspot X: " + lander.landX + " Y: " + lander.distance() + ", but should be: " + lander.landY);
            //debug("rotation: "+ lander.R);
            //debug("varG: "+ lander.varG);
            //debug("landX: "+ lander.landX);
            //debug("R: "+lander.R + " P: "+lander.P);
            //debug(lander.HS);
            //debug("direct distance: " + lander.diagonalDistance);
            debug(" angle of impact: " + lander.landAlpha);
            debug(lander.distance2());




            System.out.println(lander.R + " " + lander.P);

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
    int X;
    int Y;
    int xFlatStart;
    int landX;
    int landY;
    int VS;
    int HS;
    int R;
    int P;
    int height;

    double vector;
    double landAlpha;

    double speed;
    double varG;

    boolean fromLeft;
    int landingDist;
    int diagonalDistance;

    int startX;
    int startY;

    int startHS;
    int startVS;
    int startP;
    int startR;

    public Lander(){
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
        int x = Math.max(this.X,this.landX) - Math.min(this.X,this.landX);
        int y = Math.max(this.Y,this.landY) - Math.min(this.Y,this.landY);
        this.diagonalDistance = (int) Math.sqrt(Math.pow(x,2) + Math.pow(y,2));

        this.landAlpha = Math.toDegrees(Math.acos((double) this.landingDist / this.diagonalDistance)) ;


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

        double y = this.height + this.landX * Math.tan(Math.toRadians(Math.abs(this.R))) - this.varG * Math.pow(this.landX, 2) /
                (2 * Math.pow(this.speed, 2) * Math.pow(Math.toRadians(Math.abs(this.R)), 2));
        return y;
    }

    public double distance2(){
        double d = this.speed - Math.pow(this.P,2)/2;
        return d;
    }

    public void tiltDecc() {
        if (this.fromLeft) {
            this.R += 10;
        } else {
            this.R -= 10;
        }

        if (this.R > 90) {
            this.R = 90;
        } else if (this.R < -90) {
            this.R = -90;
        }
    }

    public void tiltAcc() {
        if (this.fromLeft) {
            this.R -= 10;
        } else {
            this.R += 10;
        }

        if (this.R > 90) {
            this.R = 90;
        } else if (this.R < -90) {
            this.R = -90;
        }
    }

    public void landingRotation() {
        int corr = this.fromLeft ? 90 : -90;
        this.R = ((int) Math.round(this.vector) + corr);

        if (this.R > 90) {
            this.R = 90;
        } else if (this.R < -90) {
            this.R = -90;
        }
    }

    public void maintainSpeedApp() {

    }

    public void maintainSpeedLand() {
        //empty
    }

}