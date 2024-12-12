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
    public Payment createPayment(@RequestBody Payment payment) {
        return null;
    }

    @GetMapping("/v1/subscriptions")
    public PaginatedItems<Payment> getPayments(@RequestParam(name = "page", required = false) String page) {
        return null;
    }

    @GetMapping("/v1/subscriptions/{id}")
    public Payment getPaymentById(@PathVariable long id) {
        return null;
    }

    @DeleteMapping("/v1/subscriptions/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deletePaymentById(@PathVariable long id) {

    }

    @PatchMapping("/v1/subscriptions/{id}")
    public Payment updatePayment(@PathVariable long id, @RequestBody Payment updateValues) {
        return null;
    }


}
