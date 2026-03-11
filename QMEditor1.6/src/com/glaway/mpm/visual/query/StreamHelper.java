/**
 * SVN Id:        $Id: StreamHelper.java 25 2008-05-27 08:02:10Z mvonhasselbach $
 * SVN Date:      $Date: 2008-05-27 10:02:10 +0200 (Tue, 27 May 2008) $
 * SVN Revision:  $Revision: 25 $
 * SVN Author:    $Author: mvonhasselbach $
 * SVN URL:       $HeadURL: http://autodev.ptc.com/eecRepos/Components/iex/trunk/overwrite/src/ext/webject/StreamHelper.java $
 *
 * bcwti
 * 
 * Copyright (c) 1998-2008 Parametric Technology. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Parametric Technology. You shall not disclose such
 * confidential information and shall use it only in accordance with the terms of the license agreement you entered into
 * with Parametric Technology.
 * 
 * ecwti
 */
package com.glaway.mpm.visual.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * @author mvonhasselbach
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StreamHelper
{
    /**
     * Method copyReader2Writer.
     * @param bufferSize int
     * @param reader Reader
     * @param writer Writer
     * @throws IOException
     */
    public static void copyReader2Writer( int bufferSize, Reader reader, Writer writer) throws IOException{
         char acharBuf[] = new char[bufferSize];
         do{
             int i = reader.read(acharBuf);
             if(i != -1)
                 writer.write(acharBuf, 0, i);
             else
                 return;
         } while(true);
    }
    /**
     * Method copyIs2Os.
     * @param bufferSize int
     * @param reader InputStream
     * @param writer OutputStream
     * @throws IOException
     */
    public static void copyIs2Os( int bufferSize, InputStream reader, OutputStream writer) throws IOException{
        /*            
                    char acharBuf[] = new char[bufferSize];
                    do{
                        int i = reader.read(acharBuf);
                        if(i != -1)
                            writer.write(acharBuf, 0, i);
                        else
                            return;
                    } while(true);
        */
                 byte acharBuf[] = new byte[bufferSize];
                 do{
                     int i = reader.read(acharBuf);
                     if(i != -1){
                         //System.out.write(acharBuf, 0, i);
                         writer.write(acharBuf, 0, i);
                     }else
                         return;
                 } while(true);                
            }

    /**
     * this is fundamentally different: it opens a 2. thread and a pipe and the OS is read asap bytes are taken from the IS (and not before)
     * this mechanism allows minimal memory usage (oterwise you'll have to cache the OS content to a buffer in memory
     * 
     * @param writer
     * @param reader
     * @throws IOException
     */
    /*
    public static InputStream copyOs2Is( OutputStream writer) throws IOException{

        //PipedInputStream pis = new PipedInputStream();
        PipeReadThread prt = new PipeReadThread(writer,reader){
            PipedInputStream pis = null;
            PipedOutputSream pos = null;
            PipeReadThread( OutputStream writer, InputStream reader){
                pis = new PipedInputStream();
                pos = new PipedOutputSream(pis);
            }
        };
        prt.start();        
        
        do{
             int i = reader.read(acharBuf);
             if(i != -1){
                 //System.out.write(acharBuf, 0, i);
                 writer.write(acharBuf, 0, i);
             }else
                 return;
         } while(true);                
    }
    */
}


