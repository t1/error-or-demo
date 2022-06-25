package test;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.graphql.client.typesafe.api.ErrorOr;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

@QuarkusTest
class UserTest {

    @GraphQLClientApi interface Users {
        @Query User user(int id);
    }

    @Data @SuperBuilder @NoArgsConstructor
    public static class User {
        String name;
        ErrorOr<Integer> age;
        List<User2> friends;
    }

    @Data @SuperBuilder @NoArgsConstructor
    public static class User2 {
        String name;
        ErrorOr<Integer> age;
        List<User3> friends;
    }

    @Data @SuperBuilder @NoArgsConstructor
    public static class User3 {
        String name;
        ErrorOr<Integer> age;
    }

    @TestHTTPResource("graphql")
    URI uri;

    Users users() {return TypesafeGraphQLClientBuilder.newBuilder().endpoint(uri).build(Users.class);}

    @Test void shouldGetJane() {
        var jane = users().user(1);

        then(jane.name).isEqualTo("Jane");
        then(jane.age.get()).isEqualTo(73);
        then(jane.friends).hasSize(2)
            .map(User2::getName).containsExactly("Jannet", "James");
        then(jane.friends.get(0).friends).hasSize(1)
            .map(User3::getName).containsExactly("Jane");
    }

    @Test void shouldGetMethuselah() {
        var me = users().user(34567);

        then(me.name).isEqualTo("Jane");
        then(me.age.isPresent()).isFalse();
        var error = me.age.getErrors().get(0);
        then(error.getCode()).isEqualTo("invalid-age");
        then(error.getMessage()).isEqualTo("invalid age: 167");
    }

    @Test void shouldGetUnborn() {
        var me = users().user(-34567);

        then(me.name).isEqualTo("Jane");
        then(me.age.isPresent()).isFalse();
        var error = me.age.getErrors().get(0);
        then(error.getCode()).isEqualTo("invalid-age");
        then(error.getMessage()).isEqualTo("invalid age: -21");
    }
}
