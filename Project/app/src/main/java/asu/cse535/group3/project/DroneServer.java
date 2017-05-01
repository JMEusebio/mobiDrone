package asu.cse535.group3.project;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telecom.Call;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DroneServer
{
    private DroneServer() {}
    private static final String SERVICE_URL = "http://neptune.fulton.ad.asu.edu/TempUse/DroneServer/Service.svc/";

    /**
     * Reset the server if you are done with it.
     */
    public static void ResetServer()
    {
        CallService(SERVICE_URL + "ResetServer");
    }

    /**
     * Creates the initial request to a drone.
     */
    public static void RequestDrone(Double originLat, Double originLon, Double targetLat, Double targetLon)
    {
        CallService(SERVICE_URL + "RequestDrone?originLat=" + originLat +
            "&originLon=" + originLon +
            "&targetLat=" + targetLat +
            "&targetLon=" + targetLon);
    }

    /**
     * Tells the server to send a drone to your location.
     */

    public static void ScheduleDrone(Double originLat, Double originLon, Double targetLat, Double targetLon, String time)
    {
        CallService(SERVICE_URL + "ScheduleDrone?originLat=" + originLat +
                "&originLon=" + originLon +
                "&targetLat=" + targetLat +
                "&targetLon=" + targetLon +
                "&time=" + time);
    }
    /**
    *   Tells server to send a drone at the requested time
    */

    public static void PanicRequest(Double lat, Double lon)
    {
        CallService(SERVICE_URL + "PanicRequest?lat=" + lat + "&lon=" + lon);
    }

    /**
     * Get the best path from your current location to the target location specified
     * when you requested the drone.
     */
    public static String GetBestPath(Double currentLat, Double currentLon)
    {
        return CallService(SERVICE_URL + "GetBestPath?lat=" + currentLat + "&lon=" + currentLon);
    }

    /**
     * Get the best path from the current location to the target location entered
     * when you requested the drone.
     */
    public static String GetBestPath()
    {
        return CallService(SERVICE_URL + "GetPreviousBestPath");
    }

    /**
     * Tell the server to pause the drone's movement.
     */
    public static void RequestPause()
    {
        CallService(SERVICE_URL + "RequestPause");
    }

    /**
     * Tell the server to unpause (resume) the drone's movement.
     */
    public static void RequestResume()
    {
        CallService(SERVICE_URL + "RequestResume");
    }

    /**
     * Tell the server you're done with the drone and it can leave.
     */
    public static void RequestStop()
    {
        CallService(SERVICE_URL + "RequestStop");
    }

    /**
     * Get the ETA from the last set of requested locations.
     */
    public static String GetEta()
    {
        return CallService(SERVICE_URL + "GetEta");
    }

    /**
     * Get the ETA from the input location to the previously entered target coordinates.
     */
    public static String GetEta(Double currentLat, Double currentLon)
    {
        return CallService(SERVICE_URL + "GetEta?lat=" + currentLat + "&lon=" + currentLon);
    }

    /**
     * Turn the light on or off.
     */
    public static void EnableLight(boolean on)
    {
        CallService(SERVICE_URL + "EnableLight?on=" + on);
    }

    public static Bitmap GetDroneImage()
    {
        InputStream input = null;
        StringBuilder total = new StringBuilder();
        HttpURLConnection connection = null;
        Bitmap toReturn;
        try
        {
            URL url = new URL(SERVICE_URL + "GetDroneImage");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                System.out.println("Server gave message: " + connection.getResponseCode() + " " + connection.getResponseMessage());
                return null;
            }

            input = connection.getInputStream();

            toReturn = BitmapFactory.decodeStream(input);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        finally
        {
            try
            {
                if (input != null)
                    input.close();
            }
            catch (IOException ex) {}

            if (connection != null)
                connection.disconnect();
        }

        return toReturn;
    }

    private static String CallService(String uri)
    {
        InputStream input = null;
        BufferedReader br;
        StringBuilder total = new StringBuilder();
        String line;
        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                System.out.println("Server gave message: " + connection.getResponseCode() + " " + connection.getResponseMessage());
                return null;
            }

            input = connection.getInputStream();

            br = new BufferedReader(new InputStreamReader(input));
            while ((line = br.readLine()) != null)
            {
                total.append(line).append('\n');
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
        finally
        {
            try
            {
                if (input != null)
                    input.close();
            }
            catch (IOException ex) {}

            if (connection != null)
                connection.disconnect();
        }

        return total.toString();
    }
}
