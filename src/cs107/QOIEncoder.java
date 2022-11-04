package cs107;

/**
 * "Quite Ok Image" Encoder
 * @apiNote Second task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class QOIEncoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder(){}

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     * @param image (Helper.Image) - Image to use
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *  (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     */
    public static byte[] qoiHeader(Helper.Image image){
        assert !image.equals(null);
        assert image.channels() == QOISpecification.RGB || image.channels() == QOISpecification.RGBA;
        assert image.color_space() == QOISpecification.sRGB || image.color_space() == QOISpecification.ALL;
        byte[] header = {
                113,111,105,102,//nombre magique
                ArrayUtils.fromInt(image.data()[0].length)[0], //Largeur
                ArrayUtils.fromInt(image.data()[0].length)[1], //Largeur
                ArrayUtils.fromInt(image.data()[0].length)[2], //Largeur
                ArrayUtils.fromInt(image.data()[0].length)[3], //Largeur
                ArrayUtils.fromInt(image.data().length)[0], //Hauteur
                ArrayUtils.fromInt(image.data().length)[1], //Hauteur
                ArrayUtils.fromInt(image.data().length)[2], //Hauteur
                ArrayUtils.fromInt(image.data().length)[3], //Hauteur
                image.channels(),
                image.color_space()
        };
        return header;
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     * @param pixel (byte[]) - The Pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     */
    public static byte[] qoiOpRGB(byte[] pixel){
        assert pixel.length == 4;

        byte[] arrayReturn = {QOISpecification.QOI_OP_RGB_TAG, pixel[0],pixel[1],pixel[2]};
        return arrayReturn;
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     * @param pixel (byte[]) - The pixel to encode
     * @throws AssertionError if the pixel's length is not 4
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     */
    public static byte[] qoiOpRGBA(byte[] pixel){
        assert pixel.length == 4;
        byte[] returned = new byte[5];
        returned[0] = QOISpecification.QOI_OP_RGBA_TAG;
        for(int i = 0; i < pixel.length;++i){
            returned[1+i] = pixel[i];
        }
        return returned;
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     * @param index (byte) - Index of the pixel
     * @throws AssertionError if the index is outside the range of all possible indices
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     */
    public static byte[] qoiOpIndex(byte index){
        assert index >= 0 && index <64;
        byte encoding = (byte) (QOISpecification.QOI_OP_INDEX_TAG | index) ;
        byte[] encodingArray = ArrayUtils.wrap(encoding);
        return encodingArray;
    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpDiff(byte[] diff){
        assert diff.length == 3;
        assert diff != null;
        for(int i = 0 ; i < 3; ++i){
            assert(diff[i]<2)&&(diff[i]>-3);
        }
        byte encoding = (byte)((QOISpecification.QOI_OP_DIFF_TAG | (diff[0]+2 <<4)) | ((diff[1]+2 << 2) | (diff[2]+2)));
        byte[] encodingWrap = ArrayUtils.wrap(encoding);
        return encodingWrap;

    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     * @param diff (byte[]) - The difference between 2 pixels
     * @throws AssertionError if diff doesn't respect the constraints
     * or diff's length is not 3
     * (See the handout for the constraints)
     * @return (byte[]) - Encoding of the given difference
     */
    public static byte[] qoiOpLuma(byte[] diff){
        assert diff != null;
        assert diff.length == 3;
        assert diff[1]< 32 && diff[1]> -33;
        assert (diff[0]-diff[1]) < 8 && (diff[0]-diff[1]) >-9;
        assert (diff[2]-diff[1]) < 8 && (diff[2]-diff[1]) >-9;

        byte encoding = (byte)(QOISpecification.QOI_OP_LUMA_TAG | (diff[1]+32));
        byte encoding2 = (byte)((diff[0]-diff[1] + 8 <<4)|(diff[2]-diff[1]+8));
        byte[] lumaArray = ArrayUtils.concat(encoding,encoding2);
        return lumaArray;
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     * @param count (byte) - Number of similar pixels
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     * @return (byte[]) - Encoding of count
     */
    public static byte[] qoiOpRun(byte count){
        assert count >= 1 && count <=62;

        byte encoding = (byte) (QOISpecification.QOI_OP_RUN_TAG | (count-1)) ;
        byte[] encodingArray = ArrayUtils.wrap(encoding);
        return encodingArray;
    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
    public static byte[] encodeData(byte[][] image){
        return Helper.fail("Not Implemented");
    }

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     */
    public static byte[] qoiFile(Helper.Image image){
        return Helper.fail("Not Implemented");
    }

}