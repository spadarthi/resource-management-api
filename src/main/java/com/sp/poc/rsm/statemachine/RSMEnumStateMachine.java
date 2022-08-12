package com.sp.poc.rsm.statemachine;

import com.sp.poc.rsm.enums.Event;
import com.sp.poc.rsm.enums.State;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.transition.Transition;

import java.util.Optional;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class RSMEnumStateMachine extends StateMachineConfigurerAdapter<State, Event> {

    Logger logger = LoggerFactory.getLogger(RSMEnumStateMachine.class);

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions.withExternal().source(State.ADDED).target(State.IN_CHECK).event(Event.BEGIN_CHECK)
                .and()
                .withExternal().source(State.IN_CHECK).target(State.APPROVED).event(Event.APPROVE)
                .and()
                .withExternal().source(State.APPROVED).target(State.IN_CHECK).event(Event.UN_APPROVE)
                .and()
                .withExternal().source(State.APPROVED).target(State.ACTIVE).event(Event.ACTIVATE);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.ADDED)
                .state(State.IN_CHECK)
                .state(State.APPROVED)
                .end(State.ACTIVE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        StateMachineListenerAdapter<State, Event> adapter = new StateMachineListenerAdapter<State, Event>() {
            @Override
            public void eventNotAccepted(Message<Event> event) {
                logger.warn("Not accepted event: {}", event);
            }

            @Override
            public void transition(Transition<State, Event> transition) {
                logger.warn("MOVE from: {}, to: {}",
                        ofNullableState(transition.getSource().getId()),
                        ofNullableState(transition.getTarget().getId()));
            }

            @Override
            public void stateChanged(org.springframework.statemachine.state.State<State, Event> from, org.springframework.statemachine.state.State<State, Event> to) {
                logger.info(String.format("stateChanged(from: %s, to: %s)", from + "", to + ""));
            }
        };
        config.withConfiguration().autoStartup(false).listener(adapter);
    }

    private Object ofNullableState(State s) {
        return Optional.ofNullable(s)
                .map(State::name)
                .orElse(null);
    }
}
