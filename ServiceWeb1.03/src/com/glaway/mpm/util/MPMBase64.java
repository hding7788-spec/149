/* bcwti
 *
 * Copyright (c) 2008 Parametric Technology Corporation (PTC). All Rights Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package com.glaway.mpm.util;

/**
 *  Utility class providing static methods for encoding and decoding
 *  data using the Base64 algorithm specified in Internet Standard RFC2045.
 */
 public  class MPMBase64  {
    // RFC2045: Base64 Alphabet
    private static byte [] Base64EncMap = {
        (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F',
        (byte)'G', (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L',
        (byte)'M', (byte)'N', (byte)'O', (byte)'P', (byte)'Q', (byte)'R',
        (byte)'S', (byte)'T', (byte)'U', (byte)'V', (byte)'W', (byte)'X',
        (byte)'Y', (byte)'Z',
        (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f',
        (byte)'g', (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l',
        (byte)'m', (byte)'n', (byte)'o', (byte)'p', (byte)'q', (byte)'r',
        (byte)'s', (byte)'t', (byte)'u', (byte)'v', (byte)'w', (byte)'x',
        (byte)'y', (byte)'z',
        (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5',
        (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'+', (byte)'/' 
    };

    private static byte [] Base64DecMap;

    static {

	Base64DecMap = new byte[128];

	for ( int i = 0; i < Base64EncMap.length; ++i )
	    Base64DecMap[Base64EncMap[i]] = (byte)i;
    }

    /**
     *  Decode a Base64-encoded string.  This method should be called only
     *  when it is known that the decoded result will be a valid string and
     *  not binary data.
     *
     *  @param base64str The Base64-encoded string.
     *  @return The decoded result.
     */

    public final static String decode (String base64str) {

        byte[] encoded = base64str.getBytes ();
        return new String (decode (encoded));
    }

    /**
     *  Decode a Base64-encoded byte array containing single-byte characters
     *  from the Base64 alphabet.
     *
     *  @param encoded The Base64-encoded array of single-byte characters.
     *  @return The decoded result.
     */

    public final static byte [] decode (byte [] encoded) {

        return fromBytes (encoded, 0, encoded.length);          
    }

    /**
     *  Decode a subset of a Base64-encoded byte array containing single-byte
     *  characters from the Base64 alphabet.
     *
     *  @param encoded The Base64-encoded array of single-byte characters.
     *  @param offset The index of the first character to be decoded.
     *  @param len The number of characters to be decoded.
     *  @return The decoded result.
     */

    public final static byte [] decode (byte [] encoded, int offset, int len) {

        return fromBytes (encoded, offset, len);
    }

    /**
     *  Base64-encode an array of bytes.
     *
     *  @param bytes The array of bytes to be encoded.
     *  @return The Base64-encoded result, as an array of single-byte
     *          characters from the Base64 alphabet.
     */

    public final static byte [] encode (byte [] bytes) {

        return toBytes (bytes);
    }

    /**
     *  Base64-encode a string.
     *
     *  @param str The string to be encoded.
     *  @return The Base64-encoded result, as a string.
     */

    public final static String encode (String str) {

        return new String (encode (str.getBytes()));
    }
   
    private final static byte [] fromBytes (byte[] encoded,
                                            int offset,
                                            int len) {

        int length = 3 * (len / 4);
        byte [] decoded = new byte[length];
        int didx = 0;
        int numChars = 0;

        int sidx = 0;

        while ( sidx < len ) {
	    
	    // Evaluate groups of 4 chars at a time.
	    
            long accum = 0;
            numChars = 3;

            for ( int quadidx = 0; quadidx < 4; ++quadidx ) {

	        byte b = encoded [offset + sidx++];

                if ( b == '=' ) {

                    if ( quadidx == 2 ) {
                        accum >>>= 4;
                        numChars = 1;
                        sidx = len;
                        break;
                    }
	                
                    if ( quadidx == 3 ) {
                        accum >>>= 2;
                        numChars = 2;
                        sidx = len;
                        break;
                    }
                }
	        
                if ( quadidx > 0 ) accum <<= 6;

                accum += Base64DecMap[b];
            }

            // Write out the decoded chars.
	    
            if ( numChars > 2 ) {
                decoded[didx + 2] = (byte) (accum & 0xFF);
                accum >>= 8;
            }
	    
            if ( numChars > 1 ) {
                decoded[didx + 1] = (byte) (accum & 0xFF);
                accum >>= 8;
            }
	    
            decoded[didx] = (byte)(accum & 0xFF);
	    
            didx += numChars;	    
        }

        byte [] results = new byte [didx];

        for ( int i = 0; i < didx; ++i ) results [i] = decoded [i];

        return results;
    }

    private final static byte [] toBytes (byte[] data) {

        int sidx;
        int didx;

        if ( data == null ) return null;

        byte [] encoded = new byte[((data.length + 2) / 3) * 4];


        // 3-byte to 4-byte conversion + 0-63 to ascii printable conversion

        for ( sidx=0, didx=0; sidx < data.length - 2; sidx += 3 ) {
            encoded[didx++] = Base64EncMap[(data[sidx] >>> 2) & 0x3f];
            encoded[didx++] = Base64EncMap[(data[sidx+1] >>> 4) & 0x0f
                                           | (data[sidx] << 4) & 0x3f];
            encoded[didx++] = Base64EncMap[(data[sidx+2] >>> 6) & 0x03
                                           | (data[sidx+1] << 2) & 0x3f];
            encoded[didx++] = Base64EncMap[data[sidx+2] & 0x3f];
        }

        if ( sidx < data.length ) {

            encoded[didx++] = Base64EncMap[(data[sidx] >>> 2) & 0x3f];

            if ( sidx < data.length - 1 ) {
                encoded[didx++] = Base64EncMap[(data[sidx+1] >>> 4) & 0x0f
                                               | (data[sidx] << 4) & 0x3f];
                encoded[didx++] = Base64EncMap[(data[sidx+1] << 2) & 0x3f];
            }
            else
                encoded[didx++] = Base64EncMap[(data[sidx] << 4) & 0x3f];
        }

        // add padding

        while ( didx < encoded.length ) encoded[didx++] = (byte) '=';

        return encoded;
    }
}
