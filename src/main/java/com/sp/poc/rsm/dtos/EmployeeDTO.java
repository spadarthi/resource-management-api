package com.sp.poc.rsm.dtos;

import com.sp.poc.rsm.enums.State;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    @Nullable
    private Long id;
    private String empName;
    private String contractInfo;
    private State state;
    private Integer age;

}
