package cs107;

import static cs107.Helper.Image;
import static cs107.Helper.generateImage;

/**
 * "Quite Ok Image" Decoder
 * @apiNote Third task of the 2022 Mini Project
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder(){}

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header){
        //========assert==================
        assert header != null;
        assert header.length == QOISpecification.HEADER_SIZE;
        byte[] QOIF =ArrayUtils.extract(header,0,4) ;
        assert ArrayUtils.equals(QOISpecification.QOI_MAGIC,QOIF);
        assert header[12] == QOISpecification.RGB || header[12] == QOISpecification.RGBA;
        assert header[13] == QOISpecification.ALL || header[13] == QOISpecification.sRGB;
        //========code====================
        int[] decoded = new int[4];
        decoded[0] = ArrayUtils.toInt(ArrayUtils.extract(header,4,4));
        decoded[1] = ArrayUtils.toInt(ArrayUtils.extract(header,8,4));
        decoded[2] = (int) header[12];
        decoded[3] = (int)header[13];
        return decoded;
    }

    // ==================================================================================
    // =========================== ATOMIC DECODING METHODS ==============================
    // ==================================================================================

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param alpha (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx){
        assert buffer != null && input!= null;
        assert buffer.length > position & input.length > idx;
        assert input.length-position > 4;

        byte[] pixel = ArrayUtils.concat(ArrayUtils.extract(input,idx,QOISpecification.RGB),ArrayUtils.wrap(alpha));
        buffer[position] = pixel;
        return QOISpecification.RGB;

    }

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param input (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx){
        assert buffer != null && input!= null;
        assert buffer.length > position & input.length > idx;
        assert input.length - position > 4;

        byte[] pixel = ArrayUtils.extract(input,idx,QOISpecification.RGBA);
        buffer[position] = pixel;
        return QOISpecification.RGBA;
    }

    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk){
        assert previousPixel != null;
        assert previousPixel.length ==4;
        assert (byte)(chunk >>> 6) == QOISpecification.QOI_OP_DIFF_TAG>>>6;

        byte[] chunkList = {
                (byte)(((chunk >>>4) & 0b11)-2), //extraire dr
                (byte)(((chunk >>>2) & 0b11)-2), // extraire dg
                (byte)((chunk & 0b11)-2)         //extraire db
        };
        byte[] toReturn = new byte[4];


        for(int i = 0 ; i < chunkList.length; ++i){
            toReturn[i] += previousPixel[i] + chunkList[i];
        }
        toReturn[3] = previousPixel[3];


        return toReturn;
    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     * @param previousPixel (byte[]) - The previous pixel
     * @param data (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data){
        assert previousPixel != null;
        assert data != null;
        assert (byte)((data[0])&0b11000000) >>>6 == QOISpecification.QOI_OP_LUMA_TAG >>>6;


        byte[] chunkList = new byte[3];
        byte dg = (byte)((data[0] & 0b111111)-32);
        chunkList[1] = dg;
        chunkList[0] = (byte)(((data[1]&0b11110000)>>>4)+dg-8);
        chunkList[2] = (byte)(((data[1])& 0b1111)+dg-8);


        byte[] toReturn = new byte[4];


        for(int i = 0 ; i < chunkList.length; ++i){
            toReturn[i] += previousPixel[i] + chunkList[i];
        }
        toReturn[3] = previousPixel[3];

        return toReturn;
    }

    /**
     * Store the given pixel in the buffer multiple times
     * @param buffer (byte[][]) - Buffer where to store the pixel
     * @param pixel (byte[]) - The pixel to store
     * @param chunk (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position){
        assert buffer != null;
        assert buffer.length > position;
        assert pixel != null;
        assert pixel.length == 4;



        int iteration = chunk&0b111111;
        //assert buffer.length > iteration + 1;


        for(int i = 0 ; i<= iteration;++i){
            buffer[position+i] = pixel;
        }
        return iteration;


    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     * @param data (byte[]) - Data to decode
     * @param width (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height){
        assert data != null;
        assert (width > 0) && (height>0);



        //=========Variable============================
        byte[][] imageDecode = new byte[width*height][4];

        byte[] prevPixel = QOISpecification.START_PIXEL;
        imageDecode[0] = prevPixel;
        byte[][] hachtable = new byte[64][4];
        int idata = 0;

        for (int idx = 0; idx < imageDecode.length ; ++idx){
            //========HachTable================
            hachtable[QOISpecification.hash(prevPixel)] = prevPixel ;

            //=========for=====================
            byte tag = (byte)( data[idata] >>>6);
            switch (tag) {
                case 1: //OPdiff
                    imageDecode[idx] = decodeQoiOpDiff(prevPixel, data[idata]) ;
                    prevPixel = imageDecode[idx];
                    idata += 1;
                    continue;
                case (-2): //OPLuma
                    byte[] dataLuma = ArrayUtils.extract(data,idata,2);
                    imageDecode[idx] = decodeQoiOpLuma(prevPixel, dataLuma) ;
                    prevPixel = imageDecode[idx];
                    idata += 2;
                    continue;
                case 0://OpIndex
                    imageDecode[idx] = hachtable[(0b00111111 & data[idata])];
                    prevPixel = imageDecode[idx];

                    idata += 1;
                    continue;
                case -1 :
                    if (data[idata] == (byte)0b11111111) { //RGBA
                        decodeQoiOpRGBA(imageDecode, data, idx, idata+1);
                        prevPixel = imageDecode[idx];
                        idata += 5;
                        continue;
                    }
                    if (data[idata] == (byte)0b11111110) { //RGB
                        decodeQoiOpRGB(imageDecode, data, prevPixel[3], idx, idata+1);
                        prevPixel = imageDecode[idx];
                        idata += 4;

                    }
                    else { //RUN
                        idx += decodeQoiOpRun(imageDecode, prevPixel, data[idata], idx);
                        prevPixel = imageDecode[idx];
                        idata += 1;

                    }
            }

        }
        assert idata == data.length ;
        return imageDecode;
    }

    /**
     * Decode a file using the "Quite Ok Image" Protocol
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content){
        byte[] encodedHeader = ArrayUtils.extract(content,0,14);


        int[] decodedHeader = decodeHeader(encodedHeader);
        int w = decodedHeader[0];
        int h = decodedHeader[1];

        byte[][] imageDecoded = decodeData(ArrayUtils.extract(content,14,content.length-22),w,h );
        Image image = generateImage(ArrayUtils.channelsToImage(imageDecoded,w,h),(byte)decodedHeader[2],(byte)decodedHeader[3]);
        return image;
    }

}