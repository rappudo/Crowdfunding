package com.eseg.pagamentos.service;

import com.eseg.pagamentos.model.Pagamento;
import com.eseg.pagamentos.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    // 1. Listar todos os pagamentos
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    // 2. Buscar pagamento por Id
    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
    }

    // 3. Criar pagamento
    public Pagamento criarPagamento(Pagamento novoPagamento) {
        return pagamentoRepository.save(novoPagamento);
    }

    // 4. Editar pagamento
    public Pagamento editarPagamento(Long id, Pagamento pagamentoAtualizado) {
        pagamentoRepository.update(id, pagamentoAtualizado);
        return buscarPorId(id);
    }

    // 5. Deletar campanha
    public void deletarPorId(Long id) {
        pagamentoRepository.deleteById(id);
    }
}
