package bg.tuvarna.lab;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.nio.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Vault {
    private final SecretKey key;
    private final SecureRandom random=new SecureRandom();
    public Vault(@Value("${lab.key-file:}") String path) throws Exception {
        byte[] bytes;
        if(path.isBlank()) { bytes=new byte[32]; random.nextBytes(bytes); }
        else bytes=Base64.getDecoder().decode(Files.readString(Path.of(path)).trim());
        if(bytes.length!=32) throw new IllegalArgumentException("AES key must contain 32 bytes");
        key=new SecretKeySpec(bytes,"AES");
    }
    public String encrypt(String value, String owner) {
        if(value==null || value.isBlank() || value.length()>200) throw new IllegalArgumentException("Invalid field");
        try {
            byte[] iv=new byte[12]; random.nextBytes(iv);
            Cipher c=Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.ENCRYPT_MODE,key,new GCMParameterSpec(128,iv));
            c.updateAAD(owner.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted=c.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return "v1:"+Base64.getEncoder().encodeToString(ByteBuffer.allocate(iv.length+encrypted.length).put(iv).put(encrypted).array());
        } catch(GeneralSecurityException e) { throw new IllegalStateException("Encryption failed"); }
    }
    public String decrypt(String envelope, String owner) {
        try {
            if(envelope==null || !envelope.startsWith("v1:") || envelope.length()>2048) throw new IllegalArgumentException();
            byte[] bytes=Base64.getDecoder().decode(envelope.substring(3));
            if(bytes.length<28) throw new IllegalArgumentException();
            Cipher c=Cipher.getInstance("AES/GCM/NoPadding"); c.init(Cipher.DECRYPT_MODE,key,new GCMParameterSpec(128,Arrays.copyOf(bytes,12)));
            c.updateAAD(owner.getBytes(StandardCharsets.UTF_8));
            return new String(c.doFinal(bytes,12,bytes.length-12),StandardCharsets.UTF_8);
        } catch(GeneralSecurityException | IllegalArgumentException e) { throw new IllegalArgumentException("Invalid encrypted field"); }
    }
}
