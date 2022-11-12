package cs107;

import java.util.ArrayList;

/**
 * Utility class to manipulate arrays.
 * @apiNote First Task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class ArrayUtils {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private ArrayUtils(){}

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[]) - First array
     * @param a2 (byte[]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[] a1, byte[] a2){

        if ( a1.length != a2.length){
            return false;
        }

        for(int i = 0 ; i < a1.length;++i){
            if(a1[i] != a2[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the content of both arrays is the same
     * @param a1 (byte[][]) - First array
     * @param a2 (byte[][]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[][] a1, byte[][] a2){
        if((a1.length != a2.length) || (a1[1].length != a2[1].length)){
            return false;
        }
        for(int i = 0 ; i < a1.length; ++i){
            if(!equals(a1[i], a2[i])){
                return false;
            }
        }
        return true;
    }

    // ==================================================================================
    // ============================ ARRAY WRAPPING METHODS ==============================
    // ==================================================================================

    /**
     * Wrap the given value in an array
     * @param value (byte) - value to wrap
     * @return (byte[]) - array with one element (value)
     */
    public static byte[] wrap(byte value){
        byte[] arrayMono = {value};

        return arrayMono;
    }

    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    /**
     * Create an Integer using the given array. The input needs to be considered
     * as "Big Endian"
     * (See handout for the definition of "Big Endian")
     * @param bytes (byte[]) - Array of 4 bytes
     * @return (int) - Integer representation of the array
     * @throws AssertionError if the input is null or the input's length is different from 4
     */
    public static int toInt(byte[] bytes){
        assert bytes.length == 4 ;
        assert bytes != null ;

        int number = 0;
        for(int i = 0 ; i < bytes.length; ++i){
            int b = bytes[bytes.length-1-i];
            if (b < 0) {
                b = 256 + b ;
                };
            int k = b << (i*8);

            number += k;
        }


        return number;
    }

    /**
     * Separate the Integer (word) to 4 bytes. The Memory layout of this integer is "Big Endian"
     * (See handout for the definition of "Big Endian")
     * @param value (int) - The integer
     * @return (byte[]) - Big Endian representation of the integer
     */
    public static byte[] fromInt(int value){
        byte[] encoding= {0,0,0,0};
        for(int i = 0 ; i < 4  ; ++i){
            int q = (value >> i*8) & (0b11111111) ;
            encoding[3-i] = (byte) q ;
        }

        return encoding ;
    }

    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    /**
     * Concatenate a given sequence of bytes and stores them in an array
     * @param bytes (byte ...) - Sequence of bytes to store in the array
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     */
    public static byte[] concat(byte ... bytes){

        assert bytes != null ;

        return bytes ;
    }



    /**
     * Concatenate a given sequence of arrays into one array
     * @param tabs (byte[] ...) - Sequence of arrays
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null.
     */
    public static byte[] concat(byte[] ... tabs){
        assert tabs != null;

        int taille = 0 ;
        int compteur = 0  ;
        for (int i = 0 ; i < tabs.length ; ++i){
            taille += tabs[i].length ;
            assert tabs[i] != null;
        }
        byte[] byteTab = new byte[taille] ;
        for (int i = 0 ; i < tabs.length ; ++i) {
            for (int j = 0 ; j < tabs[i].length ; ++j){
                byteTab[compteur] = tabs[i][j] ;
                compteur++;
            }
        }
        return byteTab;
    }

    // ==================================================================================
    // =========================== ARRAY EXTRACTION METHODS =============================
    // ==================================================================================

    /**
     * Extract an array from another array
     * @param input (byte[]) - Array to extract from
     * @param start (int) - Index in the input array to start the extract from
     * @param length (int) - The number of bytes to extract
     * @return (byte[]) - The extracted array
     * @throws AssertionError if the input is null or start and length are invalid.
     * start + length should also be smaller than the input's length
     */
    public static byte[] extract(byte[] input, int start, int length){
        assert input != null;
        assert (start < input.length) && (start >= 0) ;
        assert length >= 0 ;
        assert start + length <= input.length;
        byte[] extracted = new byte[length];
        for (int i = 0 ; i < length ; ++i){
            extracted[i] = input[start + i] ;
        }
        return extracted;
    }

    /**
     * Create a partition of the input array.
     * (See handout for more information on how this method works)
     * @param input (byte[]) - The original array
     * @param sizes (int ...) - Sizes of the partitions
     * @return (byte[][]) - Array of input's partitions.
     * The order of the partition is the same as the order in sizes
     * @throws AssertionError if one of the parameters is null
     * or the sum of the elements in sizes is different from the input's length
     */
    public static byte[][] partition(byte[] input, int ... sizes) {
        assert input != null;
        assert sizes != null;
        int sum = 0 ;
        for (int i = 0 ; i < sizes.length ; ++i) {
            sum += sizes[i];
        }
        assert sum == input.length ;

        int maxsize = 0 ;
        int start = 0;
        //tableau remplis de 0 !!!
        for (int i = 0 ; i < sizes.length ; ++i){
            if (maxsize < sizes[i]) {
                maxsize = sizes[i] ;
            }
        }
        byte[][] byteDoubleTab = new byte[sizes.length][maxsize];

        for (int j = 0 ; j < sizes.length ; ++j){
            byteDoubleTab[j]= extract(input, start, sizes[j]);
            start += sizes[j] ;
        }
        return  byteDoubleTab;
    }

    // ==================================================================================
    // ============================== ARRAY FORMATTING METHODS ==========================
    // ==================================================================================

    /**
     * Format a 2-dim integer array
     * where each dimension is a direction in the image to
     * a 2-dim byte array where the first dimension is the pixel
     * and the second dimension is the channel.
     * See handouts for more information on the format.
     * @param input (int[][]) - image data
     * @return (byte [][]) - formatted image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     */
    public static byte[][] imageToChannels(int[][] input){
        assert input != null;
        for( int i = 0 ; i < input.length; ++i ) {
            assert input[i] != null;
            assert input[i].length == input[0].length;
        }
        byte[][] channels = new byte[input.length*input[0].length][4];
        int a = 0; //tracker
        for(int i = 0; i < input.length; ++i){
            for(int k = 0; k < input[i].length; ++k){
                byte[] b = fromInt(input[i][k]);
                channels[a][0] = b[1]; //r
                channels[a][1] = b[2]; //g
                channels[a][2] = b[3]; //b
                channels[a][3] = b[0]; //a
                a++ ;
            }
        }
        return channels;
    }

    /**
     * Format a 2-dim byte array where the first dimension is the pixel
     * and the second is the channel to a 2-dim int array where the first
     * dimension is the height and the second is the width
     * @param input (byte[][]) : linear representation of the image
     * @param height (int) - Height of the resulting image
     * @param width (int) - Width of the resulting image
     * @return (int[][]) - the image data
     * @throws AssertionError if the input is null
     * or one of the inner arrays of input is null
     * or input's length differs from width * height
     * or height is invalid
     * or width is invalid
     */
    public static int[][] channelsToImage(byte[][] input, int height, int width){
        assert input != null;
        assert input.length != height;
        assert input[0].length != width;
        for( int i = 0 ; i < input.length; ++i ) {
            assert input[i] != null;
            assert input[i].length == input[0].length;
        }


        int[][] image = new int[height][width];
        int a = 0; //tracker
        for(int i = 0; i < height; ++i){
            for(int k = 0; k < width; ++k){
                byte[] listPassage = input[a];
                byte varPassage = listPassage[3]; //les prochaines lignes servent à replacer
                listPassage[3] = listPassage[2];  //le A devant.
                listPassage[2] = listPassage[1];
                listPassage[1] = varPassage;
                image[i][k] = toInt(listPassage);
                a++ ;
            }

        }
        return image ;
    }
    public static void printdoublebyte(byte[][] bytes){

        for (int i = 0; i < bytes.length ; ++i){
            for( int j = 0; j < bytes[i].length; ++j){
                System.out.print(bytes[i][j]+ " ");
            }
            System.out.println("  |  ");
        }

    }

}
