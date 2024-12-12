package fr.uge.api.safeReturn.controller;

import fr.uge.api.safeReturn.model.*;
import fr.uge.api.safeReturn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

@RestController
public class SubscriptionController {
    private static final int PAGE_SIZE = 5;
    private Long nextSubscriptionId = 0L;
    private final HashMap < Long, Subscription > subscriptionStore = new HashMap < > ();

    @Autowired
    private UserService userService;

    @PostMapping("/v1/subscriptions")
    @ResponseStatus(value = HttpStatus.CREATED)
    public Subscription createSubscriptions(@RequestBody SubscriptionRequest subscription, @RequestHeader("Authorization") String authorizationHeader) {
	    	if (!userService.isValidToken(authorizationHeader)) {
	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
	    	}


	      Instant now = Instant.now();

	      // convert Instant to ZonedDateTime
	      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd");
	      ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, ZoneId.systemDefault());
	      var startDate = dtf.format(zonedDateTime);
	      var endDate = dtf.format( zonedDateTime.plusMonths(subscription.subscriptionDuration()));

        var createdSubscription = new Subscription(nextSubscriptionId, subscription.userId(), startDate, endDate, "active");
        subscriptionStore.put(nextSubscriptionId, createdSubscription);
        nextSubscriptionId++;
        return createdSubscription;
    }

    @GetMapping("/v1/subscriptions")
    public PaginatedItems < Subscription > getSubscriptions(@RequestParam(name = "page", required = false) String page, @RequestHeader("Authorization") String authorizationHeader) {

        if (!userService.isValidToken(authorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
        }

        if (subscriptionStore.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No subscriptions were found !");
        }
        if (page == null) {
            var subscriptions = subscriptionStore.values().stream().toList();
            return new PaginatedItems < > (subscriptions, subscriptions.size(), 0, 0);
        }
        int pageNum;
        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page must be an int");
        }
        if (pageNum < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page can't be negative");
        }
        try {
            var totalPages = subscriptionStore.size() / PAGE_SIZE;
            var totalItems = subscriptionStore.size();
            var currentPage = pageNum;
            var toIndex = Math.min((pageNum + 1) * PAGE_SIZE, subscriptionStore.size());
            var subscriptions = subscriptionStore.values().stream().toList().subList(pageNum * PAGE_SIZE, toIndex);
            return new PaginatedItems < > (subscriptions, totalItems, totalPages, currentPage);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page is not a valid number");
        }
    }

    @GetMapping("/v1/subscriptions/{id}")
    public Subscription getSubscriptionById(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader) {

        if (!userService.isValidToken(authorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
        }

        var subscription = subscriptionStore.get(id);
        if (subscription == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription with id {" + id + "} not found !");
        }
        return subscription;
    }

    @DeleteMapping("/v1/subscriptions/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteSubscriptionById(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader) {

        if (!userService.isValidToken(authorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
        }

        if (subscriptionStore.remove(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription with id {" + id + "} not found !");
        }
    }

    @PatchMapping("/v1/subscriptions/{id}")
    public Subscription updateSubscription(@PathVariable long id, @RequestBody Status updateValues, @RequestHeader("Authorization") String authorizationHeader) {

        if (!userService.isValidToken(authorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
        }

        var previousSubscription = subscriptionStore.get(id);
        if (previousSubscription == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment with id {" + id + "} not found !");
        }

        var status = updateValues.status() == null ? previousSubscription.status() : updateValues.status();
        var patchedSubscription = new Subscription(previousSubscription.id(), previousSubscription.userId(), previousSubscription.startDate(), previousSubscription.endDate(), status);
        subscriptionStore.replace(id, patchedSubscription);
        return patchedSubscription;
    }

}