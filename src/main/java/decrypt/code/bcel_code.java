package decrypt.code;

import core.CryptInterface;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import me.gv7.woodpecker.bcel.classfile.Utility;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences;
import utils.Methods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class bcel_code implements CryptInterface {
    private static File file = null;
    private static String temp = "R4gd0ll";
    private static String content = null;
    @Override
    public Boolean decrypt(String text, String type , TextArea textArea_decrypt, TextArea textArea_info) {
        return dec(text,type,textArea_decrypt,textArea_info);
    }

    @Override
    public Boolean encrypt(String text,String type, TextArea textArea_encrypt,TextArea textArea_info) {
        return enc(text,type,textArea_encrypt,textArea_info);
    }

    private Boolean dec(String text,String type,TextArea textArea_decrypt,TextArea textArea_info){
        try {
            String sourcetype = text.startsWith("$$BCEL$$")?"BCEL":"CLASS";
            String flag = bcelDecode(text);
            if(flag!=null && flag!="" && !flag.isEmpty()){

                Platform.runLater(() -> {
                    textArea_info.appendText(getAllEncode.getTime()+"\n[+]"+type+",源码为"+sourcetype+"编码 解密成功!\n");
                    textArea_decrypt.appendText(flag);
                });
                return true;
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                textArea_info.appendText(getAllEncode.getTime()+"\n[-]"+type+" 解密失败!\n");
            });
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            textArea_info.appendText(getAllEncode.getTime()+"\n[-]"+type+" 解密失败!\n");
        });
        return false;
    }

    private Boolean enc(String text,String type ,TextArea textArea_encrypt,TextArea textArea_info){
        try {
            String flag = bcelEncode(text);
            if(flag!=null && flag!="" && !flag.isEmpty()){
                Platform.runLater(() -> {
                    textArea_info.appendText(getAllEncode.getTime()+"\n[+]"+type+" 加密成功!\n");
                    textArea_encrypt.appendText(flag);
                });
                return true;
            }
        } catch (Exception e) {
            Platform.runLater(() -> {
                textArea_info.appendText(getAllEncode.getTime()+"\n[-]"+type+" 加密失败!\n");
            });
            e.printStackTrace();
        }
        return false;
    }
    public static String bcelDecode(String passwd){

        Map<String, Object> options = new HashMap<>();
        options.put(IFernflowerPreferences.DECOMPILE_INNER, "1");
        options.put(IFernflowerPreferences.REMOVE_SYNTHETIC, "1");
        options.put(IFernflowerPreferences.ASCII_STRING_CHARACTERS, "1");
        if(passwd.startsWith("$$BCEL$$")){
            String bin = passwd.substring(8);
            passwd = temp+".class";
            file = new File(passwd);
            try {
                FileOutputStream fos = null;
                FileChannel channel = null;
                fos = new FileOutputStream(passwd);
                channel = fos.getChannel();
                byte[] array =  Utility.decode(bin,true);
                ByteBuffer buffer = ByteBuffer.wrap(array);
                channel.write(buffer);
                channel.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            file = new File(passwd);
        }
        ConsoleDecompiler decompiler = new ConsoleDecompiler(null, options);

        try {
            decompiler.addSpace(file, true);
            decompiler.decompileContext();
            String javapwd = "";
            if(passwd == temp+".class"){
                javapwd = temp+".java";
                file.delete();
            }else{
                javapwd = file.getName().replace(".class","")+".java";
            }
            file = new File(javapwd);
            content = Methods.readFile(javapwd);
            new File(javapwd).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new File(temp+".class").delete();
        return content;
    }

    public static String bcelEncode(String input) {

        try {
            File file = new File(input);
            URL url = file.toURI().toURL();
            URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
            Class<?> clazz = classLoader.loadClass(file.getName().replace(".class",""));
            byte[] var1 = yso.payloads.util.Methods.classAsBytes(clazz);
            String s = Utility.encode(var1, true);
            return "$$BCEL$$" + s;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}