package test;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import java.time.LocalDate;

@GraphQLApi
public class Users {
    @Query public User user(int id) {
        var birthDay = LocalDate.now().minusDays(id + 3).minusYears(73);
        User jane = User.builder().id(id).name("Jane").birthDate(birthDay).build();
        User jannet = User.builder().id(id + 1).name("Jannet").build();
        User james = User.builder().id(id + 2).name("James").build();
        jane.addFriend(jannet).addFriend(james);
        jannet.addFriend(jane);
        james.addFriend(jane);
        return jane;
    }

    public Integer age(@Source User user) {return user.getAge();}
}
