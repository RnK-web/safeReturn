package fr.uge.api.safeReturn.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import fr.uge.api.safeReturn.model.Payment;

@RestController
public class PaymentController {

	@PostMapping("/v1/payments")
	public Payment createPayment(@RequestBody Payment Payment) {
		return null;
	}
	
	@GetMapping("/v1/Payments")
	public List<Payment> getPayments() {
		return null;
	}
	
	@GetMapping("/v1/payments/{id}")
	public Payment getPaymentById(@PathVariable long id) {
		return null;
	}

	@DeleteMapping("/v1/payments/{id}")
	public void deletePaymentById(@PathVariable long id) {
	}

	@PatchMapping("/v1/payments/{id}")
	public Payment updatePayment(@PathVariable long id, @RequestBody Payment payment) {
		return null;
	}
	
}
