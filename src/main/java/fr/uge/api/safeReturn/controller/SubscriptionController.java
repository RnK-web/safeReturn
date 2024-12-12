package fr.uge.api.safeReturn.controller;

import fr.uge.api.safeReturn.model.PaginatedItems;
import fr.uge.api.safeReturn.model.Payment;
import fr.uge.api.safeReturn.model.Subscription;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class SubscriptionController {
    private static final int PAGE_SIZE = 5;
    private Long nextSubscriptionId = 0l;
    private final HashMap<Long, Subscription> subscriptionStore = new HashMap<>();

    @PostMapping("/v1/subscriptions")
    public Subscription createPayment(@RequestBody Subscription subscription) {
        return null;
    }

    @GetMapping("/v1/subscriptions")
    public PaginatedItems<Subscription> getSubscriptions(@RequestParam(name = "page", required = false) String page) {
        return null;
    }

    @GetMapping("/v1/subscriptions/{id}")
    public Subscription getSubscriptionById(@PathVariable long id) {
        return null;
    }

    @DeleteMapping("/v1/subscriptions/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteSubscriptionById(@PathVariable long id) {

    }

    @PatchMapping("/v1/subscriptions/{id}")
    public Subscription updateSubscription(@PathVariable long id, @RequestBody Subscription updateValues) {
        return null;
    }


}
