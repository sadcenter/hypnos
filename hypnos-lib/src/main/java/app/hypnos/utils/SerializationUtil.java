package app.hypnos.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializationUtil
{

    public static byte[] serializeList(List<String> stringList) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            for (String element : stringList) {
                out.writeUTF(element);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static List<String> deserializeList(byte[] bytes) {
        List<String> strings = new ArrayList<>();
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(bais);
            while (in.available() > 0) {
                String element = in.readUTF();
                strings.add(element);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }
}