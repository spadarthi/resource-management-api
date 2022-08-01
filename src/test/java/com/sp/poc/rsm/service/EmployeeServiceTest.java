package com.sp.poc.rsm.service;

import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.State;
import com.sp.poc.rsm.persistence.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository repository;

    @Test
    public void getEmployeeById() {
        long id = 1L;
        Employee employee1 = new Employee(1L, "Test Root", "APAC", State.ADDED, 29);
        when(repository.findById(id)).thenReturn(Optional.of(employee1));
        Employee employee2 = employeeService.getEmployee(id);
        assertEquals(29, employee2.getAge());
        assertEquals(State.ADDED, employee2.getState());
    }
}
