package com.sp.poc.rsm.Integration;

import com.sp.poc.rsm.ResourceManagementApiApplication;
import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.Event;
import com.sp.poc.rsm.enums.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.test.util.AssertionErrors.assertNotEquals;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ResourceManagementApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerITTest {
    @LocalServerPort
    private int port;
    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Test
    void testRetrieveEmployeeById() {

        HttpEntity<Employee> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Employee> response = restTemplate.exchange(createURLWithPort("/api/v1/employees/{id}"), HttpMethod.GET, entity, Employee.class, 1L);

        Employee expected = new Employee(1L, "Michael Jackson", "APAC", State.ADDED, 29);
        assertEquals("Should be equal", expected.getEmpName(), response.getBody().getEmpName());
    }

    @Test
    void testUpdateEmployee() {
        HttpEntity<Employee> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Employee> response = restTemplate.exchange(createURLWithPort("/api/v1/employees/{id}"), HttpMethod.GET, entity, Employee.class, 1L);
        Employee employee = response.getBody();
        employee.setAge(36);
        employee.setContractInfo("EUROPE");

        HttpEntity<Employee> entity2 = new HttpEntity<>(employee, headers);

        ResponseEntity<Employee> response2 = restTemplate.exchange(createURLWithPort("/api/v1/employees"), HttpMethod.PUT, entity2, Employee.class);

        Employee expected = new Employee(1L, "Michael Jackson", "EUROPE", State.ADDED, 36);
        assertEquals("Should be equal", expected.getContractInfo(), response2.getBody().getContractInfo());
    }

    @Test
    void testUpdateEmployeeShouldFail() {
        HttpEntity<Employee> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Employee> response = restTemplate.exchange(createURLWithPort("/api/v1/employees/{id}"), HttpMethod.GET, entity, Employee.class, 1L);
        Employee employee = response.getBody();
        employee.setAge(36);
        employee.setContractInfo("EUROPE");
        employee.setState(State.APPROVED);
        //Here we can observe that state is not changed after update employee from this endpoint.

        HttpEntity<Employee> entity2 = new HttpEntity<>(employee, headers);
        ResponseEntity<Employee> response2 = restTemplate.exchange(createURLWithPort("/api/v1/employees"), HttpMethod.PUT, entity2, Employee.class);

        Employee expected = new Employee(1L, "Michael Jackson", "EUROPE", State.ADDED, 36);
        assertEquals("Should be equal", expected.getContractInfo(), response2.getBody().getContractInfo());
        assertEquals("Should be equal", expected.getState(), response2.getBody().getState());
    }

    @Test
    void testUpdateEmployeeStateEventFailCase() {
        // Trying to send event ACTIVATE when State is ADDED

        HttpEntity<Employee> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Employee> response = restTemplate.exchange(createURLWithPort("/api/v1/employees"), HttpMethod.GET, entity, Employee.class);
        Employee employee = response.getBody();
        HttpEntity<Employee> entity2 = new HttpEntity<>(employee, headers);
        ResponseEntity<Employee> response2 = restTemplate.exchange(createURLWithPort("/api/v1/employees/{id}/{event}"), HttpMethod.PUT, entity2, Employee.class, 1L, Event.ACTIVATE);

        Employee expected = new Employee(1L, "Michael Jackson", "APAC", State.ACTIVE, 36);
        assertNotEquals("Should not equal", expected.getState(), response2.getBody().getState());
    }

    @Test
    void testUpdateEmployeeStateEventSuccessCase() {
        // Trying to send event BEGIN_CHECK when State is ADDED

        HttpEntity<Employee> entity = new HttpEntity<>(null, headers);
        ResponseEntity<Employee> response = restTemplate.exchange(createURLWithPort("/api/v1/employees"), HttpMethod.GET, entity, Employee.class);
        Employee employee = response.getBody();
        HttpEntity<Employee> entity2 = new HttpEntity<>(employee, headers);
        ResponseEntity<Employee> response2 = restTemplate.exchange(createURLWithPort("/api/v1/employees/{id}/{event}"), HttpMethod.PUT, entity2, Employee.class, 1L, Event.BEGIN_CHECK);

        Employee expected = new Employee(1L, "Michael Jackson", "APAC", State.IN_CHECK, 36);
        assertEquals("Should be equal", expected.getState(), response2.getBody().getState());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
