package fr.uge.api.safeReturn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.api.safeReturn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import fr.uge.api.safeReturn.model.Payment;
import fr.uge.api.safeReturn.model.PaginatedItems;

@RestController
public class PaymentController {

	private static final int PAGE_SIZE = 5;
	private Long nextPaymentId = 0l;
	private final HashMap < Long, Payment > paymentStore = new HashMap < > ();

	@Autowired
	private UserService userService;

	@PostMapping("/v1/payments")
	public Payment createPayment(@RequestBody Payment payment) {
		var createdPayment = new Payment(nextPaymentId, payment.rewarderId(), payment.receiverId(), payment.correlationId(), payment.amount(), payment.currency(), payment.method(), payment.status());
		paymentStore.put(nextPaymentId, createdPayment);
		nextPaymentId++;
		return createdPayment;
	}

	@GetMapping("/v1/payments")
	public PaginatedItems < Payment > getPayments(@RequestParam(name = "page", required = false) String page, @RequestHeader("Authorization") String authorizationHeader) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}

		if (paymentStore.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No payments were found !");
		}
		if (page == null) {
			var payments = paymentStore.values().stream().toList();
			return new PaginatedItems < Payment > (payments, payments.size(), 0, 0);
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
			var totalPages = paymentStore.size() / PAGE_SIZE;
			var totalItems = paymentStore.size();
			var currentPage = pageNum;
			var toIndex = Math.min((pageNum + 1) * PAGE_SIZE, paymentStore.size());
			var payments = paymentStore.values().stream().toList().subList(pageNum * PAGE_SIZE, toIndex);
			return new PaginatedItems < Payment > (payments, totalItems, totalPages, currentPage);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page is not a valid number");
		}
	}

	@GetMapping("/v1/payments/{id}")
	public Payment getPaymentById(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader) {
		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		var payment = paymentStore.get(id);
		if (payment == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment with id {" + id + "} not found !");
		}
		return payment;
	}

	@DeleteMapping("/v1/payments/{id}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deletePaymentById(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader) {
		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		var removedPayment = paymentStore.remove(id);
		if (removedPayment == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment with id {" + id + "} not found !");
		}
	}

	@PatchMapping("/v1/payments/{id}")
	public Payment updatePayment(@PathVariable long id, @RequestBody Payment updateValues, @RequestHeader("Authorization") String authorizationHeader) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		var previousPayment = paymentStore.get(id);
		if (previousPayment == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment with id {" + id + "} not found !");
		}
		var amount = updateValues.amount() == null ? previousPayment.amount() : updateValues.amount();
		var currency = updateValues.currency() == null ? previousPayment.currency() : updateValues.currency();
		var method = updateValues.method() == null ? previousPayment.method() : updateValues.method();
		var status = updateValues.status() == null ? previousPayment.status() : updateValues.status();
		var patchedPayment = new Payment(id, previousPayment.rewarderId(), previousPayment.receiverId(), previousPayment.correlationId(), amount, currency, method, status);
		paymentStore.replace(id, patchedPayment);
		return patchedPayment;
	}

}