package com.sp.poc.rsm.controller;

import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.Event;
import com.sp.poc.rsm.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class EmployeeController {

    Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "Add employee", description = "Add employee", tags = "Employee API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)
    })
    @PostMapping(value = "/employees", name = "addEmployee")
    public ResponseEntity<Employee> addEmployee(@Parameter @Valid @RequestBody Employee employee) {
        Employee createdEmployee = employeeService.addEmployee(employee);
        return new ResponseEntity<>(createdEmployee, HttpStatus.CREATED);
    }

    @Operation(summary = "Get employee", description = "Get employee by id", tags = "Employee API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Employee not found",
                    content = @Content)
    })

    @GetMapping(value = "/employees/{id}", name = "getEmployee")
    public ResponseEntity<Employee> getEmployee(@Parameter @PathVariable("id") Long employeeId) {
        logger.trace("EmployeeController:getEmployee:Id : " + employeeId);
        Employee employee = employeeService.getEmployee(employeeId);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Update employee", description = "Update employee", tags = "Employee API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)
    })
    @PutMapping(value = "/employees", name = "updateEmployee")
    public ResponseEntity<Employee> updateEmployee(@Parameter @Valid @RequestBody Employee employee) {
        Employee updatedEmployee = employeeService.updateEmployee(employee);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Operation(summary = "Update employee state", description = "Update employee state", tags = "Employee API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content)
    })
    @PutMapping(value = "/employees/{id}/{event}", name = "updateEmployeeState")
    public ResponseEntity<Employee> updateEmployeeState(@Parameter @PathVariable(value = "id") Long empId,
                                                        @Parameter @Schema(type = "string", allowableValues = {"BEGIN_CHECK", "APPROVE", "UN_APPROVE", "ACTIVATE"})
                                                        @Valid @PathVariable(value = "event") Event event) {
        Employee employee = employeeService.updateEmployeeState(empId, event);
        return ResponseEntity.accepted().body(employee);
    }
}
