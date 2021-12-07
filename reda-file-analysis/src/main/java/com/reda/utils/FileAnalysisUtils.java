package com.reda.utils;


import com.reda.constant.Constants;
import com.reda.model.Param;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;


import java.io.*;

import java.util.*;
import java.util.stream.Collectors;


/**
 * reda文件解析工具类
 */

public class FileAnalysisUtils {
    private volatile static FileAnalysisUtils fileAnalysisUtils;


    /**
     * 解压密码
     */
    //private static final String password = "redacrossone";
    private FileAnalysisUtils() {

    }

    public static FileAnalysisUtils getInstance() {
        if (fileAnalysisUtils == null) {
            synchronized (FileAnalysisUtils.class) {
                if (fileAnalysisUtils == null) {
                    fileAnalysisUtils = new FileAnalysisUtils();
                }
            }
        }
        return fileAnalysisUtils;
    }

    /**
     * @param inputStream 文件流
     * @Return Map<String,scala.collection.immutable.List<Object>> key 为参数名称   value为参数值
     * @throws IOException
     */
    public static Map<String,scala.collection.immutable.List<Object>> analysisFile(InputStream inputStream, String password) {

        ZipInputStream zipInputStream = new ZipInputStream(inputStream, password.toCharArray());
        LocalFileHeader localFileHeader;
        Map<String,scala.collection.immutable.List<Object>> listMap=new HashMap<>();
        try {
            //map的key为文件名称  value值为文件字节流
            Map<String,byte[]> map=new HashMap<>();
              List<Map<String,byte[]>> mapList=new ArrayList<>();
            ByteArrayOutputStream outputStream=null;
            while ((localFileHeader = zipInputStream.getNextEntry()) != null) {
                if(localFileHeader.getFileName().endsWith(Constants.BIN_FILE_POSTFIX)){
                    //获取文件名称
                    String fileName = localFileHeader.getFileName();
                    outputStream = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int temp;
                    while ((temp = zipInputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, temp);
                    }
                    map.put(fileName.replace(".bin",""),outputStream.toByteArray());
                     mapList.add(map);
                }
            }
            if(outputStream!=null){
                outputStream.close();
            }
            for (Map<String, byte[]> stringMap : mapList) {
                List<Param> params = stringMap.entrySet().stream()
                        .filter(t -> t.getValue().length > 0)
                        .map(t -> {
                            return new Param(t.getValue(),t.getKey());
                        })
                        .collect(Collectors.toList());

                for (Param param : params) {
                    scala.collection.immutable.List<Object> allValue = param.getAllValue(3000,3020);
                        listMap.put(param.name(),allValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }
}
