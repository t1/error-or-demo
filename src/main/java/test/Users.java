package test;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import java.time.LocalDate;

@GraphQLApi
public class Users {
    @Query public User user(int id) {
        var birthDay = LocalDate.now().minusDays(id + 3).minusYears(73);
        return User.builder().id(id).name("Jane Doe").birthDate(birthDay).build();
    }

    public Integer age(@Source User user) {return user.getAge();}
}
