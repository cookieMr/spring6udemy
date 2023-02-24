package mr.cookie.spring6udemy.model.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Publisher {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;

}
