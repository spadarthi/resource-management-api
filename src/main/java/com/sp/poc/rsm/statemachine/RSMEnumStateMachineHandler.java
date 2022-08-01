package com.sp.poc.rsm.statemachine;

import com.sp.poc.rsm.entity.Employee;
import com.sp.poc.rsm.enums.Event;
import com.sp.poc.rsm.enums.State;
import com.sp.poc.rsm.persistence.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Service
public class RSMEnumStateMachineHandler {
    private static final String EMPLOYEE_ID_HEADER = "employeeId";
    @Autowired
    private EmployeeRepository empRepository;

    @Autowired
    private StateMachineFactory<State, Event> factory;

    public StateMachine<State, Event> handle(@NotEmpty Long employeeId,@NotEmpty Event triggeredEvent) {
        StateMachine<State, Event> sm = this.build(employeeId);

        Message<Event> eventMessage = MessageBuilder.withPayload(triggeredEvent)
                .setHeader(EMPLOYEE_ID_HEADER, employeeId)
                .setHeader("event", triggeredEvent)
                .build();
        sm.sendEvent(eventMessage);
        return sm;
    }

    public StateMachine<State, Event> build(@NotEmpty Long id) {
        Employee employee = empRepository.findById(id).get();
        String employeeIdKey = Long.toString(employee.getId());
        StateMachine<State, Event> sm = factory.getStateMachine(employeeIdKey);
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(
                sma -> {
                    sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<State, Event>() {
                        @Override
                        public void preStateChange(org.springframework.statemachine.state.State<State, Event> state, Message<Event> message, Transition<State, Event> transition, StateMachine<State, Event> stateMachine, StateMachine<State, Event> rootStateMachine) {
                            Optional.ofNullable(message).ifPresent(msg -> {

                                Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(EMPLOYEE_ID_HEADER, -1L))).ifPresent(employeeId1 -> {
                                    Employee employeeToUpdate = empRepository.findById(employeeId1).get();
                                    employeeToUpdate.setState(state.getId());
                                    empRepository.save(employeeToUpdate);
                                });
                            });
                        }
                    });
                    sma.resetStateMachine(new DefaultStateMachineContext<>(employee.getState(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
