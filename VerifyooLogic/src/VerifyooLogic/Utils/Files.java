package VerifyooLogic.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by roy on 12/28/2015.
 */
public class Files {
    public static String GetFileName(String userName) {
        String fileName = String.format("%s-%s", userName, Consts.STORAGE_FILE);
        return fileName;
    }

    public static void writeToFile(String data, OutputStreamWriter outputStreamWriter) {
        try {
            //File file = new File(Environment.getExternalStorageDirectory(), "/shapeRecognition/user.txt");
            //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("shapesrec.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            
        }
        catch (Exception e) {
            
        }
    }

    public static String readFromFile(InputStream inputStream) {

        String ret = "";

        try {
            //InputStream inputStream = openFileInput("shapesrec.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            
        } catch (IOException e) {
            
        }
        catch (Exception e) {
            
        }
        return ret;
    }
}
