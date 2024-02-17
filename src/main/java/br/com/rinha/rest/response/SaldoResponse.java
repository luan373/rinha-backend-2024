package br.com.rinha.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class SaldoResponse {
    
    private int total;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private LocalDateTime data_extrato = LocalDateTime.now();
	
	private int limite;
	
	public SaldoResponse(int total, int limite) {
		super();
		this.total = total;
		this.limite = limite;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public LocalDateTime getData_extrato() {
		return data_extrato;
	}

	public void setData_extrato(LocalDateTime	 data_extrato) {
		this.data_extrato = data_extrato;
	}

	public int getLimite() {
		return limite;
	}

	public void setLimite(int limite) {
		this.limite = limite;
	}
	
	
}
