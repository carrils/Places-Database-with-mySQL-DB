//Sam Carrillo
//1.29.18
//CSC 346 Homework 02

import java.sql.*;
import java.util.Scanner;

public class Main {
    static Connection conn;
    static ResultSet rsp;//resultset for places variables
    static ResultSet rsd;//resultset for distance variables
    static ResultSet rshu;//resultset for housing unit variables
    static Statement stmt, stmt2, stmt3;

    //to do list:
    //solve multiple populations from same place
    //solve multiple zipcodes returning back same place problem
    //match housing units to their correct place.

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String host = "jdbc:mysql://turing.cs.missouriwestern.edu:3306/misc";
        String user = "csc254";
        String password = "age126";

        System.out.println("Enter in a ZIP code and a radius in miles.");
        System.out.print("ZIP: ");
        int inputZipCode = scan.nextInt();
        System.out.print("Radius: ");
        int radius = scan.nextInt();

        //this string should be all zips in an area
        String queryStringPlaces = "SELECT DISTINCT city, zipcode,state, lat, `long`, estimatedpopulation" +
                " FROM zips2  WHERE locationtype LIKE 'PRIMARY' " +
                "ORDER BY city";

        //this string is only the primary lat and long
        String queryStringDistances = "SELECT DISTINCT lat, `long` "+
                "FROM zips2 WHERE zipcode LIKE "+inputZipCode+" LIMIT 1";

        //this string is used for housing units info
        String queryStringHousing = "SELECT DISTINCT housingunits, city, state_prefix, zip_code" +
                " FROM zips ORDER BY city";

        try{
            conn = DriverManager.getConnection(host,user,password);
            if(conn == null){
                System.out.println("Connection to database failed");
            }else{
                System.out.println("Connection to database successful");
            }
            stmt = conn.createStatement();//prepares packet of information to be sent
            stmt2 = conn.createStatement();
            stmt3 = conn.createStatement();

            rsp = stmt.executeQuery(queryStringPlaces);
            rsd = stmt2.executeQuery(queryStringDistances);
            rshu = stmt3.executeQuery(queryStringHousing);

            ResultSetMetaData rsMetaData = rsp.getMetaData();
            ResultSetMetaData rsMetaDataDistance = rsd.getMetaData();

            while(rsd.next()) {//primary lat and lon found here
                while (rshu.next()){//housing units found here
                    if (rsp.next()) {//rest of data found here
                        //'IF' is important here because it cycles through this code once then gets the next
                        //rshu resultset values (housing units), 'WHILE' does not. (this is the fruit of hours of being stumped)
                        String name = rsp.getString("city");
                        String state = rsp.getString("state");
                        double lat2 = rsp.getDouble("lat");//end lat
                        double lon2 = rsp.getDouble("long");//end lon
                        int zip = rsp.getInt("zipcode");
                        int population = rsp.getInt("estimatedpopulation");

                        double lat = rsd.getDouble("lat");//primary lat
                        double lon = rsd.getDouble("long");//primary lon

                        int housingunits = rshu.getInt("housingunits");//value needed
                        int housingzip = rshu.getInt("zip_code");//for matching purposes only

                        double distanceKM = place.distanceKM(lat, lon, lat2, lon2);
                        double distanceMiles = place.distanceMiles(lat, lon, lat2, lon2);

                        //return places within the radius, this if goes into a loop of some kind that matches the zips
                        if (distanceMiles <= radius) {
                            place place = new place(name, zip,state, housingunits, population, distanceKM, distanceMiles);//the placeholder "place" Object
                            System.out.println(place);//prints out all places with toString
                        }

                    }
                }
            }

            conn.close();

        }catch(SQLException e){
            //e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
