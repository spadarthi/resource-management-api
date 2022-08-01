package com.sp.poc.rsm.service;

import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.Event;
import com.sp.poc.rsm.enums.State;
import com.sp.poc.rsm.exceptions.EmployeeStateChangeException;
import com.sp.poc.rsm.exceptions.InvalidInputException;
import com.sp.poc.rsm.exceptions.ResourceNotFoundException;
import com.sp.poc.rsm.persistence.EmployeeRepository;
import com.sp.poc.rsm.statemachine.RSMEnumStateMachineHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
public class EmployeeServiceImpl implements EmployeeService {
    Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    @Autowired
    private EmployeeRepository empRepository;

    @Autowired
    StateMachineFactory<State, Event> factory;

    @Autowired
    RSMEnumStateMachineHandler stateMachineHandler;

    @Override
    public Employee addEmployee(Employee employee) {
        Optional.of(employee).orElseThrow(() -> new InvalidInputException("Employee details is required"));

        if (employee.getState() == null || employee.getState().equals(State.ADDED)) {
            employee.setState(State.ADDED);
            return empRepository.save(employee);
        } else {
            throw new InvalidInputException("State should be empty or only 'ADDED' is allowed for adding employee");
        }
    }

    @Override
    public Employee getEmployee(Long employeeId) {
        Employee employee = empRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee not Found by ID : " + employeeId)
        );
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        // Employee state cannot be changed here
        Optional<Employee> existingEmployee = empRepository.findById(employee.getId());
        existingEmployee.orElseThrow(() -> new ResourceNotFoundException("Employee not Found : " + employee));
        existingEmployee.ifPresent(employee1 -> {
            employee1.setEmpName(employee.getEmpName());
            employee1.setContractInfo(employee.getContractInfo());
            employee1.setAge(employee.getAge());
            empRepository.save(employee1);
        });
        return this.getEmployee(employee.getId());
    }

    @Override
    public Employee updateEmployeeState(Long employeeId, Event event) {
        StateMachine stateMachine = stateMachineHandler.handle(employeeId, event);
        Object state = stateMachine.getState().getId();
        if (state == null) {
            String errorMessage = "Unable to update employee state with employee Id: " + employeeId + " and event:" + event;
            throw new EmployeeStateChangeException(errorMessage);
        } else {
            logger.info("updateEmployeeState with EmployeeId:" + employeeId + ", Event: " + event + " The result : " + state);
            return this.getEmployee(employeeId);
        }
    }
}
