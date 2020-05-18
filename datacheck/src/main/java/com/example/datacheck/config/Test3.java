package com.example.datacheck.config;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;

/**
 * created by yuanjunjie on 2020/5/9 6:26 PM
 */
public class Test3 {
    public static void tt(String[] args) throws Exception {
//        writeByObject();
//        System.out.println(Double.toHexString(0.99f));
//        System.out.println(Double.valueOf(Double.toHexString(0.123f)));

        String path1 = "/Users/yuanjunjie/works/JavaPro/samples/properties-demo/test_file_data1";
        String path2 = "/Users/yuanjunjie/works/JavaPro/samples/properties-demo/test_file_data2";
        writeByData(path1);
        System.out.println("---------");
        writeByData(path2);
        System.out.println("---------");
        String md51 = DigestUtils.md5Hex(new FileInputStream(path1));
        String md52 = DigestUtils.md5Hex(new FileInputStream(path2));
        System.out.println("md51:"+md51);
        System.out.println("md52:"+md52);
        System.out.println(md51.equals(md52));

    }

    private static void writeByData(String path) throws Exception {
        double a = 99912344.123f;
        System.out.println(String.format("%.3f", a));

        File file = new File(path);

        FileOutputStream outputStream = new FileOutputStream(file);
        DataOutputStream objectOutputStream = new DataOutputStream(outputStream);
        objectOutputStream.writeDouble(a);
        objectOutputStream.write("HelloWorld".getBytes());
        objectOutputStream.writeBoolean(true);
        objectOutputStream.writeInt(9000);
        objectOutputStream.writeFloat(0.1f);
        objectOutputStream.writeUTF("nihao");
        objectOutputStream.writeLong(1000000L);


        objectOutputStream.close();
        outputStream.close();


        DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
        double data = inputStream.readDouble();
        System.out.println(String.format("%.3f", data));
        byte str[] = new byte[10];
        int read = inputStream.read(str, 0, 10);
        System.out.println(new String(str, "utf-8"));


    }


    private static void writeByObject() throws Exception {
        double a = 99.9999f;

        File file = new File("/Users/yuanjunjie/works/JavaPro/samples/properties-demo/test_file_obj");

        FileOutputStream outputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeDouble(a);
        objectOutputStream.close();
        outputStream.close();


    }

    private static void writeBy2() throws Exception {
        double a = 99.9999f;

        File file = new File("/Users/yuanjunjie/works/JavaPro/samples/properties-demo/test_file");
//        if (!file.exists()) {
//            file.createNewFile();
//    }
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(double2Bytes(a));
        outputStream.close();

        FileInputStream inputStream = new FileInputStream(file);
        byte buffer[] = new byte[128];
        int read = inputStream.read(buffer);

        System.out.println(read);
        inputStream.close();
    }

    public static byte[] double2Bytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

}
