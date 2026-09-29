package bg.tuvarna.lab;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class VaultTest {
    @Test void roundtripUniqueNonceAndAuthenticatedOwner() throws Exception {
        Vault v=new Vault("");String a=v.encrypt("данни","alice"), b=v.encrypt("данни","alice");
        assertThat(a).isNotEqualTo(b); assertThat(v.decrypt(a,"alice")).isEqualTo("данни");
        assertThatThrownBy(()->v.decrypt(a,"bob")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void wrongKeyCorruptionAndMalformedInput() throws Exception {
        Vault v=new Vault(""),wrong=new Vault("");String a=v.encrypt("SYNTHETIC","alice");
        assertThatThrownBy(()->wrong.decrypt(a,"alice")).hasMessage("Invalid encrypted field");
        byte[] bytes=java.util.Base64.getDecoder().decode(a.substring(3));bytes[15]^=1;
        String damaged="v1:"+java.util.Base64.getEncoder().encodeToString(bytes);
        assertThatThrownBy(()->v.decrypt(damaged,"alice")).hasMessage("Invalid encrypted field");
        for(String s:new String[]{"", "v1:!", "v2:AAAA", "v1:AA=="}) assertThatThrownBy(()->v.decrypt(s,"alice")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->v.encrypt("","alice")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->v.encrypt(null,"alice")).isInstanceOf(IllegalArgumentException.class);
    }
}
