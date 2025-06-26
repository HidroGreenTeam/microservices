package com.ayni.notification_service.notifications.interfaces.rest;

import com.ayni.notification_service.notifications.domain.model.aggregates.Reminder;
import com.ayni.notification_service.notifications.domain.model.commands.ScheduleReminderCommand;
import com.ayni.notification_service.notifications.domain.model.queries.GetPendingRemindersQuery;
import com.ayni.notification_service.notifications.domain.services.ReminderCommandService;
import com.ayni.notification_service.notifications.application.internal.queryservices.ReminderQueryServiceImpl;
import com.ayni.notification_service.notifications.interfaces.rest.resources.ReminderResource;
import com.ayni.notification_service.notifications.interfaces.rest.resources.CreateReminderResource;
import com.ayni.notification_service.notifications.interfaces.rest.transform.ReminderResourceFromEntityAssembler;
import com.ayni.notification_service.notifications.interfaces.rest.transform.CreateReminderCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RemindersController
 */
@RestController
@RequestMapping(value = "/api/v1/reminders", produces = "application/json")
@Tag(name = "Reminders", description = "Reminder Management Endpoints")
public class RemindersController {
    
    private final ReminderCommandService reminderCommandService;
    private final ReminderQueryServiceImpl reminderQueryService;
    
    public RemindersController(ReminderCommandService reminderCommandService,
                             ReminderQueryServiceImpl reminderQueryService) {
        this.reminderCommandService = reminderCommandService;
        this.reminderQueryService = reminderQueryService;
    }

    @PostMapping
    public ResponseEntity<ReminderResource> createReminder(@RequestBody CreateReminderResource resource) {
        try {
            ScheduleReminderCommand command = CreateReminderCommandFromResourceAssembler.toCommandFromResource(resource);
            Long reminderId = reminderCommandService.handle(command);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ReminderResource(reminderId, "Reminder scheduled successfully"));
                    
        } catch (Exception e) {
            throw e;
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<ReminderResource>> getPendingReminders() {
        try {
            GetPendingRemindersQuery query = new GetPendingRemindersQuery();
            List<Reminder> reminders = reminderQueryService.handle(query);
            List<ReminderResource> reminderResources = reminders.stream()
                    .map(ReminderResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(reminderResources);
            
        } catch (Exception e) {
            throw e;
        }
    }
    
    @GetMapping("/profile/{farmerId}")
    public ResponseEntity<List<ReminderResource>> getRemindersByfarmerId(@PathVariable Long farmerId) {
        try {
            List<Reminder> reminders = reminderQueryService.getRemindersByfarmerId(farmerId);
            List<ReminderResource> reminderResources = reminders.stream()
                    .map(ReminderResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(reminderResources);
            
        } catch (Exception e) {
            throw e;
        }
    }
    
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> cancelReminder(@PathVariable Long reminderId) {
        try {
            reminderCommandService.cancelReminder(reminderId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            throw e;
        }
    }
}
