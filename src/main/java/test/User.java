package test;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.graphql.Ignore;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.YEARS;

@Data @SuperBuilder @NoArgsConstructor
@Slf4j
public class User {
    int id;
    String name;
    LocalDate birthDate;
    List<User> friends;

    public User addFriend(User friend) {
        if (friends == null) friends = new ArrayList<>();
        friends.add(friend);
        return this;
    }

    @Ignore public Integer getAge() {
        var age = (int) Period.between(birthDate, LocalDate.now()).get(YEARS);
        log.info("{} (*{}): age {}", name, birthDate, age);
        if (age < 0 || age > 150) throw new InvalidAgeException(age);
        return age;
    }

    private static class InvalidAgeException extends RuntimeException {
        public InvalidAgeException(int age) {super("invalid age: " + age);}
    }
}
