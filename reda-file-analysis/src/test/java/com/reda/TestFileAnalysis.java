package com.reda;

import com.reda.utils.FileAnalysisUtils;
import scala.collection.immutable.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class TestFileAnalysis {
    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        File file = new File("C:\\test\\cc71e78133d1368f5f73b604d94ae231.reda");
        Map<String, List<Object>> listMap = FileAnalysisUtils.analysisFile(new FileInputStream(file), "redacrossone");
        long endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;
        System.out.println("执行时间：" + excTime + "s");
        System.out.println("listMap:{}" + listMap);
        System.out.println("listSize:{}" + listMap.size());
    }
}
