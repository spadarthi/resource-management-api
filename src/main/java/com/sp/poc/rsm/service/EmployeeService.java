package com.sp.poc.rsm.service;

import com.sp.poc.rsm.dtos.EmployeeDTO;
import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.Event;
import org.springframework.stereotype.Service;
@Service
public interface EmployeeService {
    Employee addEmployee(Employee employee);

    Employee getEmployee(Long employeeId);

    Employee updateEmployee(Employee employee);

    Employee updateEmployeeState(Long employeeId, Event employeeEvent);

}
