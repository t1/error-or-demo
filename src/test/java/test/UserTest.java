package test;

import io.smallrye.graphql.client.typesafe.api.ErrorOr;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class UserTest {

    @GraphQLClientApi interface Users {
        @Query User user(int id);
    }

    @Data @SuperBuilder @NoArgsConstructor
    public static class User {
        String name;
        ErrorOr<Integer> age;
    }

    Users users = TypesafeGraphQLClientBuilder.newBuilder().endpoint("http://localhost:8080/graphql").build(Users.class);

    @Test void shouldGetMe() {
        var me = users.user(1);

        then(me.name).isEqualTo("Jane Doe");
        then(me.age.get()).isEqualTo(73);
    }

    @Test void shouldGetMethuselah() {
        var me = users.user(34567);

        then(me.name).isEqualTo("Jane Doe");
        then(me.age.isPresent()).isFalse();
        var error = me.age.getErrors().get(0);
        then(error.getCode()).isEqualTo("invalid-age");
        then(error.getMessage()).isEqualTo("invalid age: 167");
    }

    @Test void shouldGetUnborn() {
        var me = users.user(-34567);

        then(me.name).isEqualTo("Jane Doe");
        then(me.age.isPresent()).isFalse();
        var error = me.age.getErrors().get(0);
        then(error.getCode()).isEqualTo("invalid-age");
        then(error.getMessage()).isEqualTo("invalid age: -21");
    }
}
