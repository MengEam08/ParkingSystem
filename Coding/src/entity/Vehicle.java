package entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehicle {
    private Integer id;
    private VehicleType vehicleType;
    public enum VehicleType {
        BIKE,
        MOTORBIKE,
        CAR
    }
    //private Student student;
}
