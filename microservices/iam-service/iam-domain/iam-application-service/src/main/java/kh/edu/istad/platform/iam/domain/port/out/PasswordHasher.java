package kh.edu.istad.platform.iam.domain.port.out;

public interface PasswordHasher {

    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}
