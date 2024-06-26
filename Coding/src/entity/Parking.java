package entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parking {
    private Integer recordId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Student student;
    private Vehicle vehicle;
}
