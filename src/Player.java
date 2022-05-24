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
            lander.setPos(X, Y, landingPointX, landingPointY);
            lander.setMove(HS, VS, R, P);
            lander.setStartAttr(startX, startY, startHS, startVS, startP, startR); //krt seda pole vaja vist nii mahukalt

            // lander objekti kasutamine
            lander.tiltAcc();


            debug("speed: " + lander.speed);
            debug("findY: " + lander.findLandY());
            //debug("vector: " + lander.vector);
            debug("acc distance: " + lander.pDistLandX());
            debug("acc distance2: " + lander.distance());
            //debug("rotation: "+ lander.R);
            //debug("varG: "+ lander.varG);
            //debug("landX: "+ lander.landX);
            //debug("R: "+lander.R + " P: "+lander.P);
            //debug(lander.HS);


            /**
             if( Math.abs(vectorSpeed)  > 10 && distance< initialDistance * 0.2 && heightFromLandingPoint < 500){
             int corrLand = fromLeft ? 90 : -90;
             R = (int) headingVector + corrLand;
             P = 4;
             System.err.println("land");
             } else if (distance < initialDistance * 0.75 && Math.abs(vectorSpeed)  > 10) {
             int corrApp = fromLeft ? 60 : -60;
             R = (int) headingVector + corrApp;
             P = 4;
             System.err.println("app");

             } else if(Math.abs(HS)>60) {
             int corrSpeedy = fromLeft ? 60 : -60;
             R = (int) headingVector + corrSpeedy;
             P = 3;
             } else if (Math.abs(vectorSpeed)<10){
             R = fromLeft ? -45 : 45;
             P = 4;
             }
             if(Y - landingPointY <100){ // usually not needed but just in case
             R = 0;
             }
             if (Math.abs(HS)>40){
             int corrSpeedy = fromLeft ? 90 : -90;
             R = (int) headingVector + corrSpeedy;
             P = 4;
             }
             //R = -45;
             //P = 4;

             */


            System.out.println(lander.R + " " +lander.P);

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
    int landX;
    int landY;
    int VS;
    int HS;
    int R;
    int P;
    int height;

    double vector;

    double speed;
    double varG;

    boolean fromLeft;
    int landingDist;

    int startX;
    int startY;

    int startHS;
    int startVS;
    int startP;
    int startR;


    public void setPos(int X, int Y, int landX, int landY) {
        this.X = X;
        this.Y = Y;
        this.landX = landX;
        this.landY = landY;
        this.height = Y - landY;
        this.fromLeft = X < landX ? true : false;
        this.landingDist = fromLeft ? landX - X : X - landX;

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

    public double distance(){
        double y = this.height + this.landX * Math.tan(Math.toRadians(Math.abs(this.R))) - this.varG * Math.pow(this.landX,2) /
                (2 * Math.pow(this.speed,2) * Math.pow(Math.toRadians(Math.abs(this.R)),2));
        return y;
    }

    public void tiltDecc(){
        if (this.fromLeft){
            this.R +=10;
        } else {
            this.R -=10;
        }

        if(this.R > 90){
            this.R =90;
        } else if (this.R < -90) {
            this.R = -90;
        }
    }
    public void tiltAcc(){
        if (this.fromLeft){
            this.R -=10;
        } else {
            this.R +=10;
        }

        if(this.R > 90){
            this.R =90;
        } else if (this.R < -90) {
            this.R = -90;
        }
    }

}