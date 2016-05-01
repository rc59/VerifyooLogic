package VerifyooLogic.Utils;

/**
 * Created by roy on 1/14/2016.
 */
public class UtilsInstructions {
    public static String GetInstruction(int idx) {
        String instruction = "Input a letter, digit and/or shape";

        switch (idx) {
            case 1:
                instruction = "Input two shapes";
                break;
            case 2:
                instruction = "Input two digits";
                break;
        }

        return instruction;
    }
}
