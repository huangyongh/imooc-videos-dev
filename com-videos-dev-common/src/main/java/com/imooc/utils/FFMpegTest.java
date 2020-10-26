package com.imooc.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {
    private String ffmpegExE;

    public FFMpegTest(String ffmpegExE) {
        this.ffmpegExE = ffmpegExE;
    }

    public static void main(String[] args){
        FFMpegTest ffMpegTest = new FFMpegTest("F:\\ffmpge\\ffmpeg-20190614-dd357d7-win64-static\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("D:\\test\\20190626190034.mp4","D:\\test\\20190626190034.avi");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void convertor(String videoInputPath,String videoOutputPath) throws Exception{

        List<String> command = new ArrayList<>();
        command.add(ffmpegExE);

        command.add("-i");

        command.add(videoInputPath);

        command.add(videoOutputPath);

        ProcessBuilder builder = new ProcessBuilder(command);

        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();

        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);

        BufferedReader br = new BufferedReader(inputStreamReader);
        String line = "";

        while((line = br.readLine()) != null){

        }
        if(br != null){
            br.close();
        }
        if( errorStream != null){
            errorStream.close();
        }
        if(inputStreamReader != null){
            inputStreamReader.close();
        }
    }
}
